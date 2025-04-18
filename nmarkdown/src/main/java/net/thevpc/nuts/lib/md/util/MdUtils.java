package net.thevpc.nuts.lib.md.util;

import net.thevpc.nuts.lib.md.MdBody;
import net.thevpc.nuts.lib.md.MdElement;
import net.thevpc.nuts.lib.md.MdPhrase;
import net.thevpc.nuts.lib.md.MdText;
import net.thevpc.nuts.util.NStringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MdUtils {
    private static final char[] HEXARR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static MdElement trim(MdElement e) {
        return trim(e, true, true);
    }

    public static MdElement trim(MdElement e, boolean left, boolean right) {
        if (!left && !right) {
            return e;
        }
        if (e instanceof MdText) {
            MdText u = (MdText) e;
            String s = u.getText();
            s = (left && right) ? NStringUtils.trim(s) : left ? NStringUtils.trimLeft(s) : NStringUtils.trimRight(s);
            MdText p = new MdText(s, u.isInline());
            p.setPreambleHeader(e.getPreambleHeader());
            return p;
        } else if (e instanceof MdPhrase) {
            MdElement[] c = ((MdPhrase) e).getChildren();
            if (c.length > 0) {
                if (left) {
                    c[0] = trim(c[0], true, false);
                }
                if (right) {
                    c[c.length - 1] = trim(c[c.length - 1], false, true);
                }
                MdPhrase p = new MdPhrase(c);
                p.setPreambleHeader(e.getPreambleHeader());
                return p;
            }
            return e;
        } else if (e instanceof MdBody) {
            MdElement[] c = ((MdBody) e).getChildren();
            if (c.length > 0) {
                if (left) {
                    c[0] = trim(c[0], true, false);
                }
                if (right) {
                    c[c.length - 1] = trim(c[c.length - 1], false, true);
                }
                MdBody p = new MdBody(c);
                p.setPreambleHeader(e.getPreambleHeader());
                return p;
            }
            return e;
        }
        return e;
    }

    public static String times(char c, int x) {
        char[] s = new char[x];
        Arrays.fill(s, c);
        return new String(s);
    }

    public static String toRelativePath(String p, String workingDir) {
        Path to = Paths.get(p).normalize().toAbsolutePath();
        Path wd = Paths.get(workingDir);
        int i = -1;
        for (int j = 0; j < Math.min(to.getNameCount(), wd.getNameCount()); j++) {
            if (to.getName(j).getFileName().toString().equals(wd.getName(j).getFileName().toString())) {
                i = j;
            } else {
                break;
            }
        }
        if (i < 0) {
            return p;
        }
        List<String> ppp = new ArrayList<>();
        for (int j = i + 1; j < wd.getNameCount(); j++) {
            ppp.add("..");
        }
        for (int j = i + 1; j < to.getNameCount(); j++) {
            ppp.add(to.getName(j).getFileName().toString());
        }
        return String.join("/", ppp);
    }

    public static boolean isRelativePath(String p) {
        if (p.startsWith("file:") || p.startsWith("http:") || p.startsWith("https:")) {
            return false;
        }
        if (Paths.get(p).isAbsolute()) {
            return false;
        }
        return true;
    }

    public static String times(String c, int x) {
        int len = c.length();
        char[] src = c.toCharArray();
        char[] dest = new char[x * len];
        int p = 0;
        for (int i = 0; i < x; i++) {
            System.arraycopy(src, 0, dest, p, len);
            p += len;
        }
        return new String(dest);
    }

    public static String escapeString(String s) {
        StringBuilder outBuffer = new StringBuilder();

        for (char aChar : s.toCharArray()) {
            if (aChar == '\\') {
                outBuffer.append("\\\\");
            } else if (aChar == '"') {
                outBuffer.append("\\\"");
            } else if ((aChar > 61) && (aChar < 127)) {
                outBuffer.append(aChar);
            } else {
                switch (aChar) {
                    case '\t':
                        outBuffer.append("\\t");
                        break;
                    case '\n':
                        outBuffer.append("\\n");
                        break;
                    case '\r':
                        outBuffer.append("\\r");
                        break;
                    case '\f':
                        outBuffer.append("\\f");
                        break;
                    default:
                        if (((aChar < 0x0020) || (aChar > 0x007e))) {
                            outBuffer.append('\\');
                            outBuffer.append('u');
                            outBuffer.append(toHex((aChar >> 12) & 0xF));
                            outBuffer.append(toHex((aChar >> 8) & 0xF));
                            outBuffer.append(toHex((aChar >> 4) & 0xF));
                            outBuffer.append(toHex(aChar & 0xF));
                        } else {
                            outBuffer.append(aChar);
                        }
                }
            }
        }
        return outBuffer.toString();
    }

    public static char toHex(int nibble) {
        return HEXARR[(nibble & 0xF)];
    }

    public static boolean isUnnumberedPrefixChild(String childPrefix,String parentPrefix){
        if(childPrefix.length() > parentPrefix.length()){
            if(childPrefix.endsWith(parentPrefix)){
                String s = childPrefix.substring(0, childPrefix.length() - parentPrefix.length());
                return s.trim().isEmpty();
            }
        }
        return false;
    }
}
