package me.ycdev.android.tools.lintparser;

public class LintConstants {
    public static class IssueFileType {
        public static final int UNKNOWN = 1;
        public static final int ANDROID_MANIFEST = 2;
        public static final int CODE_JAVA = 4;
        public static final int RES_XML = 8;
        public static final int BUILD_GRADLE = 16;

        public static final int ALL = UNKNOWN | ANDROID_MANIFEST | CODE_JAVA
                | RES_XML | BUILD_GRADLE;
    }

    public static class IssueCategory {
        public static final String CORRECTNESS = "Correctness";
    }

    public static class IssueId {
        public static final String UNUSED_RESOURCES = "UnusedResources";
    }

}
