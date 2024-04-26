package io.github.itzispyder.improperui.util;

import java.io.*;

public final class FileValidationUtils {

    public static boolean validate(File file) {
        try {
            if (!file.getParentFile().exists())
                if (!file.getParentFile().mkdirs())
                    return false;
            if (!file.exists())
                if (!file.createNewFile())
                    return false;
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static boolean quickWrite(File file, String string) {
        if (file == null || !validate(file)) {
            return false;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(string);
            bw.close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static String quickRead(File file, boolean inline) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            br.lines().forEach(line -> sb.append(line.trim()).append(inline ? " " : "\n"));
            br.close();
            return sb.toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
}
