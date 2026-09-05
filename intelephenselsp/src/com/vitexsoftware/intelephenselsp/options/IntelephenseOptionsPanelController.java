package com.vitexsoftware.intelephenselsp.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * Registers a plain sibling tab ("Intelephense") directly under the PHP
 * options category, next to General/Debugging/Annotations/Code
 * Analysis/Jenkins/Frameworks &amp; Tools.
 *
 * <p>The PHP module's own "Frameworks &amp; Tools" sub-list (ApiGen, atoum,
 * Composer, ...) is implemented with {@code org.netbeans.modules.php.api.util.UiUtils}
 * from the {@code org.netbeans.modules.php.api.phpmodule} module, whose
 * package is restricted to an explicit list of "friend" module code name
 * bases baked into that module's own manifest by the Apache NetBeans project.
 * Third-party plugins cannot add themselves to that list, so this uses the
 * plain public {@link OptionsPanelController.SubRegistration} API instead,
 * targeting the PHP category's options folder id directly by its literal
 * string (the same id the PHP module itself registers under).</p>
 */
@OptionsPanelController.SubRegistration(
        displayName = "#LBL_IntelephenseOptionsName",
        id = "Intelephense",
        location = "org-netbeans-modules-php-project-ui-options-PHPOptionsCategory",
        position = 700
)
public final class IntelephenseOptionsPanelController extends OptionsPanelController implements ChangeListener {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private IntelephenseOptionsPanel panel;
    private volatile boolean changed = false;

    @Override
    public void update() {
        getPanel().setPathValue(IntelephenseOptions.getInstance().getPath());
        changed = false;
    }

    @Override
    public void applyChanges() {
        IntelephenseOptions.getInstance().setPath(getPanel().getPathValue());
        changed = false;
    }

    @Override
    public void cancel() {
        // options are only persisted in applyChanges(), nothing to roll back
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isChanged() {
        return !IntelephenseOptions.getInstance().getPath().equals(getPanel().getPathValue());
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("com.vitexsoftware.intelephenselsp.options.Options"); // NOI18N
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private IntelephenseOptionsPanel getPanel() {
        if (panel == null) {
            panel = new IntelephenseOptionsPanel();
            panel.addChangeListener(this);
        }
        return panel;
    }
}
