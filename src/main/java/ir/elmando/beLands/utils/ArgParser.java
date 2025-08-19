package ir.elmando.beLands.utils;

public class ArgParser {

    /**
     * Parses an integer safely.
     *
     * @param args     command arguments array
     * @param index    index to read from
     * @param fallback value if parsing fails or index is out of bounds
     * @return parsed int or fallback
     */
    public static int getInt(String[] args, int index, int fallback) {
        if (index < 0 || index >= args.length) {
            return fallback;
        }
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * Gets a string safely.
     *
     * @param args     command arguments array
     * @param index    index to read from
     * @param fallback value if index is out of bounds
     * @return string at index or fallback
     */
    public static String getString(String[] args, int index, String fallback) {
        if (index < 0 || index >= args.length) {
            return fallback;
        }
        return args[index];
    }
}
