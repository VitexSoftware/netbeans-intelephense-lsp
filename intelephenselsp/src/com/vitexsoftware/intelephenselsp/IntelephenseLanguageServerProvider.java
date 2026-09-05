package com.vitexsoftware.intelephenselsp;

import java.io.IOException;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.lsp.client.spi.LanguageIdResolver;
import org.netbeans.modules.lsp.client.spi.LanguageServerProvider;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

@MimeRegistration(mimeType = "text/x-php5", service = LanguageServerProvider.class)
public class IntelephenseLanguageServerProvider implements LanguageServerProvider {

    @Override
    public LanguageServerDescription startServer(Lookup lkp) {
        try {
            ProcessBuilder pb = new ProcessBuilder("intelephense", "--stdio");
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();
            Lookup serverLookup = Lookups.fixed((LanguageIdResolver) fo -> "php");
            return LanguageServerDescription.create(p.getInputStream(), p.getOutputStream(), p, serverLookup);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }
}
