package com.vitexsoftware.intelephenselsp.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import org.openide.util.RequestProcessor;

final class IntelephenseOptionsPanel extends JPanel {

    private static final RequestProcessor RP = new RequestProcessor(IntelephenseOptionsPanel.class);
    private static final int VERSION_DETECTION_DELAY_MS = 500;

    private final JTextField pathField = new JTextField();
    private final JLabel versionValueLabel = new JLabel();
    private final EventListenerList listenerList = new EventListenerList();
    private final Timer versionDetectionTimer;

    IntelephenseOptionsPanel() {
        setLayout(new GridBagLayout());

        JLabel pathLabel = new JLabel("Intelephense Path:");
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> browse());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0;
        c.gridy = 0;
        add(pathLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(pathField, c);

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        add(browseButton, c);

        JLabel versionLabel = new JLabel("Version:");
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        add(versionLabel, c);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(versionValueLabel, c);
        c.gridwidth = 1;

        JLabel note = new JLabel("<html>Full path to the Intelephense executable, or just <code>intelephense</code> "
                + "if it is installed globally and on your system PATH (<code>npm i intelephense -g</code>).</html>");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.weightx = 1;
        c.insets = new Insets(12, 6, 6, 6);
        add(note, c);

        versionDetectionTimer = new Timer(VERSION_DETECTION_DELAY_MS, e -> detectVersion());
        versionDetectionTimer.setRepeats(false);

        pathField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            private void changed() {
                fireChange();
                versionDetectionTimer.restart();
            }
        });
    }

    private void browse() {
        JFileChooser chooser = new JFileChooser();
        String current = pathField.getText().trim();
        if (!current.isEmpty()) {
            File currentFile = new File(current);
            File parent = currentFile.getParentFile();
            if (parent != null && parent.isDirectory()) {
                chooser.setCurrentDirectory(parent);
            }
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void detectVersion() {
        String command = pathField.getText().trim();
        versionValueLabel.setText("detecting…");
        RP.post(() -> {
            String version = IntelephenseVersionDetector.detectVersion(command);
            SwingUtilities.invokeLater(() -> {
                if (!command.equals(pathField.getText().trim())) {
                    return; // stale result, path changed again meanwhile
                }
                versionValueLabel.setText(version != null ? version : "not found");
            });
        });
    }

    String getPathValue() {
        return pathField.getText().trim();
    }

    void setPathValue(String path) {
        pathField.setText(path);
        detectVersion();
    }

    void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    private void fireChange() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
            listener.stateChanged(event);
        }
    }
}
