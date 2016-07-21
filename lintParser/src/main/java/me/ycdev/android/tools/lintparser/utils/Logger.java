package me.ycdev.android.tools.lintparser.utils;

public class Logger {
    public static final boolean DEBUG = false;

    public static void log(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }
}
