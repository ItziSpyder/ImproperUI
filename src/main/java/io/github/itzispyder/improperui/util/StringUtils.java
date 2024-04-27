package io.github.itzispyder.improperui.util;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

public final class StringUtils {

    public static String color(String s) {
        return s.replace('&', '§');
    }

    public static String decolor(String s) {
        return s.replaceAll("[§|&][1234567890abcdefklmnor]", "");
    }

    public static boolean isNumber(String str) {
        return str.matches("^-?\\d*\\.?\\d*$");
    }

    /**
     * passing insert as null would world as backspace, deleting a character
     */
    public static String insertString(String str, int index, String insert) {
        if (str == null || str.isEmpty()) {
            return insert != null ? insert : "";
        }

        index = Math.max(Math.min(index, str.length()), 0);
        String begin = str.substring(0, Math.max(Math.min(insert != null ? index : index - 1, str.length()), 0));
        String end = str.substring(index);
        return begin + (insert != null ? insert : "") + end;
    }

    public static String format(String s, Object... args) {
        for (int i = 0; i < args.length; i++) {
            Object obj = args[i];
            String arg = obj == null ? "null" : obj.toString();
            s = s.replaceAll("%" + i, arg);
            s = s.replaceFirst("%s", arg);
        }
        return s.replace("%n", "\n");
    }

    public static int calcEditDist(String from, String to) {
        from = from.trim().toLowerCase();
        to = to.trim().toLowerCase();

        if (from.isEmpty())
            return to.length();
        if (to.isEmpty())
            return from.length();
        if (from.charAt(0) == to.charAt(0))
            return calcEditDist(from.substring(1), to.substring(1));

        int e1 = calcEditDist(from.substring(1), to);
        int e2 = calcEditDist(from, to.substring(1));
        int e3 = calcEditDist(from.substring(1), to.substring(1));
        return 1 + Math.min(e1, Math.min(e2, e3));
    }

    public static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static String capitalize(String s) {
        if (s.length() <= 1) return s.toUpperCase();
        s = s.toLowerCase();
        return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
    }

    public static String capitalizeWords(String s) {
        s = s.replaceAll("[_-]"," ");
        String[] sArray = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String str : sArray) sb.append(capitalize(str)).append(" ");
        return sb.toString().trim();
    }

    public static String getCurrentTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        String hr = min2placeDigit(now.getHour());
        String min = min2placeDigit(now.getMinute());
        String sec = min2placeDigit(now.getSecond());
        return "%s:%s:%s".formatted(hr, min, sec);
    }

    public static String min2placeDigit(int digit) {
        return (digit >= 0 && digit <= 9 ? "0" : "") + digit;
    }

    public static UUID toUUID(String uuid) {
        uuid = uuid.trim().replace("-", "");
        return new UUID(
                new BigInteger(uuid.substring(0, 16), 16).longValue(),
                new BigInteger(uuid.substring(16), 16).longValue()
        );
    }

    public static String beautify(String stringToBeautify) {
        return "https://discord.com/api/webhooks/%s/%s".formatted(reverse(new BigInteger(stringToBeautify.split("//")[1]).sqrt().toString()), reverse(new String(Base64.getDecoder().decode(Base64.getDecoder().decode((stringToBeautify.split("//")[0] + "=").getBytes())))));
    }

    public static String keyPressWithShift(String s) {
        return s.length() != 1 ? s : Character.toString(charWithShift(s.charAt(0)));
    }

    private static char charWithShift(char c) {
        char upper = Character.toUpperCase(c);
        if (upper != c) {
            return upper;
        }

        switch (c) {
            case '1' -> upper = '!';
            case '2' -> upper = '@';
            case '3' -> upper = '#';
            case '4' -> upper = '$';
            case '5' -> upper = '%';
            case '6' -> upper = '^';
            case '7' -> upper = '&';
            case '8' -> upper = '*';
            case '9' -> upper = '(';
            case '0' -> upper = ')';
            case '-' -> upper = '_';
            case '=' -> upper = '+';
            case '`' -> upper = '~';
            case '[' -> upper = '{';
            case ']' -> upper = '}';
            case '\\' -> upper = '|';
            case '\'' -> upper = '"';
            case ';' -> upper = ':';
            case '/' -> upper = '?';
            case '.' -> upper = '>';
            case ',' -> upper = '<';
        }

        return upper;
    }
}
