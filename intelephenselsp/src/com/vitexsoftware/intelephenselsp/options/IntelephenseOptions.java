package com.vitexsoftware.intelephenselsp.options;

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 * Persistent settings for the Intelephense LSP plugin, editable via
 * Tools &gt; Options &gt; PHP &gt; Intelephense.
 */
public final class IntelephenseOptions {

    /**
     * System property kept as a fallback default so the JVM-wide override
     * documented for netbeans.conf keeps working even if no path was ever
     * saved in the Options panel.
     */
    private static final String PATH_PROPERTY = "com.vitexsoftware.intelephenselsp.path";
    private static final String KEY_PATH = "path";
    private static final String DEFAULT_PATH = "intelephense";

    private static final IntelephenseOptions INSTANCE = new IntelephenseOptions();

    private IntelephenseOptions() {
    }

    public static IntelephenseOptions getInstance() {
        return INSTANCE;
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(IntelephenseOptions.class);
    }

    public String getPath() {
        return getPreferences().get(KEY_PATH, System.getProperty(PATH_PROPERTY, DEFAULT_PATH));
    }

    public void setPath(String path) {
        if (path == null || path.isBlank()) {
            getPreferences().remove(KEY_PATH);
        } else {
            getPreferences().put(KEY_PATH, path.trim());
        }
    }
}
