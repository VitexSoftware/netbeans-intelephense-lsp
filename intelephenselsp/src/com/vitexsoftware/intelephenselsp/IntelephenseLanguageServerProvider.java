package com.vitexsoftware.intelephenselsp;

import java.io.IOException;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.lsp.client.spi.LanguageIdResolver;
import org.netbeans.modules.lsp.client.spi.LanguageServerProvider;
import org.netbeans.modules.lsp.client.spi.ServerRestarter;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

@MimeRegistration(mimeType = "text/x-php5", service = LanguageServerProvider.class)
public class IntelephenseLanguageServerProvider implements LanguageServerProvider {

    /**
     * Path or executable name for the Intelephense CLI. Override with
     * {@code -J-Dcom.vitexsoftware.intelephenselsp.path=/custom/path/intelephense}
     * in netbeans.conf if it is not installed globally on PATH.
     */
    private static final String PATH_PROPERTY = "com.vitexsoftware.intelephenselsp.path";

    private static volatile ServerRestarter restarter;

    @Override
    public LanguageServerDescription startServer(Lookup lkp) {
        restarter = lkp.lookup(ServerRestarter.class);
        try {
            String command = System.getProperty(PATH_PROPERTY, "intelephense");
            ProcessBuilder pb = new ProcessBuilder(command, "--stdio");
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();
            Lookup serverLookup = Lookups.fixed((LanguageIdResolver) fo -> "php");
            return LanguageServerDescription.create(p.getInputStream(), p.getOutputStream(), p, serverLookup);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    /**
     * Force NetBeans to stop and restart the currently running Intelephense
     * server, e.g. after editing its licence file or configuration.
     *
     * @return true if a running server was found and asked to restart
     */
    static boolean restart() {
        ServerRestarter r = restarter;
        if (r == null) {
            return false;
        }
        r.restart();
        return true;
    }
}
