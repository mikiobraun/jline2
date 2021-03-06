/*
 * Copyright (c) 2002-2007, Marc Prud'hommeaux. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */

package jline.console.completers;

import jline.console.Completer;

import java.io.File;
import java.util.List;

/**
 * A file name completer takes the buffer and issues a list of
 * potential completions.
 *
 * <p>
 * This completer tries to behave as similar as possible to
 * <i>bash</i>'s file name completion (using GNU readline)
 * with the following exceptions:
 * <p/>
 * <ul>
 * <li>Candidates that are directories will end with "/"</li>
 * <li>Wildcard regular expressions are not evaluated or replaced</li>
 * <li>The "~" character can be used to represent the user's home,
 * but it cannot complete to other users' homes, since java does
 * not provide any way of determining that easily</li>
 * </ul>
 * <p/>
 *
 * @author <a href="mailto:mwp1@cornell.edu">Marc Prud'hommeaux</a>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class FileNameCompleter
    implements Completer
{
    // TODO: Handle files with spaces in them

    public int complete(final String buf, final int cursor, final List<CharSequence> candidates) {
        String buffer = (buf == null) ? "" : buf;
        String translated = buffer;

        File homeDir = getUserHome();

        // Special character: ~ maps to the user's home directory
        if (translated.startsWith("~" + File.separator)) {
            translated = homeDir.getPath() + translated.substring(1);
        }
        else if (translated.startsWith("~")) {
            translated = homeDir.getParentFile().getAbsolutePath();
        }
        else if (!(translated.startsWith(File.separator))) {
            String cwd = getUserDir().getPath();
            translated = cwd + File.separator + translated;
        }

        File file = new File(translated);

        final File dir;

        if (translated.endsWith(File.separator)) {
            dir = file;
        }
        else {
            dir = file.getParentFile();
        }

        File[] entries = dir == null ? new File[0] : dir.listFiles();

        return matchFiles(buffer, translated, entries, candidates);
    }

    protected File getUserHome() {
        return new File(System.getProperty("user.home"));
    }

    protected File getUserDir() {
        return new File(".");
    }

    public int matchFiles(final String buffer, final String translated, final File[] files, final List<CharSequence> candidates) {
        if (files == null) {
            return -1;
        }

        int matches = 0;

        // first pass: just count the matches
        for (File file : files) {
            if (file.getAbsolutePath().startsWith(translated)) {
                matches++;
            }
        }
        for (File file : files) {
            if (file.getAbsolutePath().startsWith(translated)) {
                CharSequence name = file.getName() + (matches == 1 && file.isDirectory() ? File.separator : " ");
                candidates.add(render(file, name).toString());
            }
        }

        final int index = buffer.lastIndexOf(File.separator);

        return index + File.separator.length();
    }

    protected CharSequence render(final File file, final CharSequence name) {
        assert file != null;
        assert name != null;

        return name;
    }
}
