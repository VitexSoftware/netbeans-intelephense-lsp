package com.vitexsoftware.intelephenselsp;

import com.vitexsoftware.intelephenselsp.options.IntelephenseOptions;
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

    private static volatile ServerRestarter restarter;

    @Override
    public LanguageServerDescription startServer(Lookup lkp) {
        restarter = lkp.lookup(ServerRestarter.class);
        try {
            String command = IntelephenseOptions.getInstance().getPath();
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
