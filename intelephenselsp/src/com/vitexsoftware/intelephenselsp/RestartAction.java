package com.vitexsoftware.intelephenselsp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;

@ActionID(category = "Tools", id = "com.vitexsoftware.intelephenselsp.RestartAction")
@ActionRegistration(displayName = "#CTL_RestartAction")
@ActionReference(path = "Menu/Tools", position = 1500)
public final class RestartAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (IntelephenseLanguageServerProvider.restart()) {
            StatusDisplayer.getDefault().setStatusText("Restarting Intelephense language server…");
        } else {
            StatusDisplayer.getDefault().setStatusText("Intelephense is not currently running.");
        }
    }
}
