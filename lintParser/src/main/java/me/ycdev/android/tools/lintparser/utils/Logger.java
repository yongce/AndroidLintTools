package me.ycdev.android.tools.lintparser.utils;

public class Logger {
    public static final boolean DEBUG = false;

    public static void log(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }

    public static void log(String format, Object... args) {
        if (DEBUG) {
            System.out.println(String.format(format, args));
        }
    }
}
