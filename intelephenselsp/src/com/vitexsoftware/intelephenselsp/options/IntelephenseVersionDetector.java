package com.vitexsoftware.intelephenselsp.options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects the installed Intelephense version.
 *
 * <p>The {@code intelephense} CLI does not support a {@code --version} flag
 * (it crashes trying to start as a language server), so this instead resolves
 * the configured executable (following PATH lookup and symlinks, since a
 * global npm install is typically a symlink such as
 * {@code /usr/local/bin/intelephense -> ../lib/node_modules/intelephense/lib/intelephense.js})
 * and reads the {@code version} field from the npm package's
 * {@code package.json}, walked up from the resolved file.</p>
 */
final class IntelephenseVersionDetector {

    private static final Pattern VERSION_PATTERN = Pattern.compile("\"version\"\\s*:\\s*\"([^\"]+)\"");
    private static final int MAX_PARENT_LEVELS = 6;

    private IntelephenseVersionDetector() {
    }

    /**
     * @param command the configured path or bare command name
     * @return the detected version, or {@code null} if it could not be determined
     */
    static String detectVersion(String command) {
        if (command == null || command.isBlank()) {
            return null;
        }
        File resolved = resolveExecutable(command.trim());
        if (resolved == null || !resolved.isFile()) {
            return null;
        }
        try {
            Path realPath = resolved.toPath().toRealPath();
            Path dir = realPath.getParent();
            for (int i = 0; dir != null && i < MAX_PARENT_LEVELS; i++) {
                Path packageJson = dir.resolve("package.json");
                if (Files.isRegularFile(packageJson)) {
                    String content = Files.readString(packageJson);
                    Matcher m = VERSION_PATTERN.matcher(content);
                    if (m.find()) {
                        return m.group(1);
                    }
                }
                dir = dir.getParent();
            }
        } catch (IOException ex) {
            return null;
        }
        return null;
    }

    private static File resolveExecutable(String command) {
        File direct = new File(command);
        if (direct.isAbsolute() || command.contains(File.separator)) {
            return direct.isFile() ? direct : null;
        }
        String path = System.getenv("PATH");
        if (path == null) {
            return null;
        }
        for (String dir : path.split(File.pathSeparator)) {
            File candidate = new File(dir, command);
            if (candidate.isFile()) {
                return candidate;
            }
        }
        return null;
    }
}
