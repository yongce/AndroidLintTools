package me.ycdev.android.tools.arcleaner;

import me.ycdev.android.tools.lintparser.LintIssue;
import me.ycdev.android.tools.lintparser.LintIssueLocation;
import me.ycdev.android.tools.lintparser.LintReport;
import me.ycdev.android.tools.lintparser.LintXmlParser;
import me.ycdev.android.tools.lintparser.LintConstants.IssueId;
import me.ycdev.android.tools.lintparser.LintParserException;
import me.ycdev.android.tools.lintparser.utils.Logger;
import me.ycdev.android.tools.lintparser.utils.XmlFileCleaner;
import me.ycdev.android.tools.lintparser.utils.XmlLineNumbersParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidResourceCleaner {
    private static final String ARC_VERSION = "1.1.0";

    public static void main(String[] args) {
        System.out.println("ARCleaner version: " + ARC_VERSION);
        if (args.length < 1) {
            System.out.println("No cmd name specified in shell script!");
            return;
        }
        if (args.length == 1) {
            String cmdName = args[0];
            System.out.println(String.format("Usage: %s <lint result XML file> [<true|false> <fileMatchReg>]", cmdName));
            return;
        }

        System.out.println("args: " + Arrays.toString(args));
        System.out.println("working...");

        String lintResultXmlFilepath = args[1];
        boolean cleanWholeFileOnly = false;
        if (args.length > 2) {
            cleanWholeFileOnly = Boolean.parseBoolean(args[2]);
        }
        String fileMatchReg = null;
        if (args.length > 3) {
            fileMatchReg = args[3];
        }

        AndroidResourceCleaner cleaner = new AndroidResourceCleaner(lintResultXmlFilepath,
                cleanWholeFileOnly, fileMatchReg);
        cleaner.execute();

        System.out.println("done");
    }

    private String mLintResultXmlFilepath;
    private boolean mCleanWholeFileOnly;
    private String mFileMatchReg;

    @SuppressWarnings("WeakerAccess")
    public AndroidResourceCleaner(String lintResultXmlFilepath, boolean cleanWholeFileOnly,
            String fileMatchReg) {
        mLintResultXmlFilepath = lintResultXmlFilepath;
        mCleanWholeFileOnly = cleanWholeFileOnly;
        mFileMatchReg = fileMatchReg;
    }

    private void execute() {
        LintXmlParser.Builder builder = new LintXmlParser.Builder()
                .watchIssues(IssueId.UNUSED_RESOURCES);
        if (mCleanWholeFileOnly) {
            builder.enableWholeFileOnly();
        }
        if (mFileMatchReg != null && mFileMatchReg.length() > 0) {
            builder.setFileFilter(filepath -> filepath.matches(mFileMatchReg));
        }

        LintXmlParser parser = builder.build();
        try {
            LintReport result = parser.parse(mLintResultXmlFilepath);
            List<LintIssue> issuesList = result.getLintIssues();
            Logger.log("count of issued found: %d", issuesList.size());;

            cleanResourceFiles(issuesList);
            if (!mCleanWholeFileOnly) {
                cleanResourceItems(issuesList);
            }
        } catch (LintParserException e) {
            e.printStackTrace();
        }
    }

    private void cleanResourceFiles(List<LintIssue> issuesList) {
        for (LintIssue issue : issuesList) {
            if (mCleanWholeFileOnly && !issue.isWholeFileIssue()) {
                continue;
            }

            for (LintIssueLocation location : issue.getLocations()) {
                if (location.isWholeFile()) {
                    //noinspection ResultOfMethodCallIgnored
                    new File(location.getFilePath()).delete();
                }
            }
        }
    }

    private void cleanResourceItems(List<LintIssue> issuesList) {
        Map<String, List<LintIssueLocation>> issuesByFile = collectIssueLocationsByFile(issuesList);
        for (List<LintIssueLocation> fileLocations : issuesByFile.values()) {
            cleanFileItems(fileLocations);
        }
    }

    private Map<String, List<LintIssueLocation>> collectIssueLocationsByFile(List<LintIssue> issuesList) {
        Map<String, List<LintIssueLocation>> locationsByFile = new HashMap<>();
        for (LintIssue issue : issuesList) {
            for (LintIssueLocation location : issue.getLocations()) {
                if (!location.isWholeFile()) {
                    List<LintIssueLocation> fileLocations = locationsByFile.get(location.getFilePath());
                    if (fileLocations == null) {
                        fileLocations = new ArrayList<>();
                        locationsByFile.put(location.getFilePath(), fileLocations);
                    }
                    fileLocations.add(location);
                }
            }
        }
        return locationsByFile;
    }

    private void cleanFileItems(List<LintIssueLocation> fileLocations) {
        int[] linesToRemove = new int[fileLocations.size()];
        int index = 0;
        for (LintIssueLocation location : fileLocations) {
            linesToRemove[index++] = location.getLineNumber();
        }

        try {
            linesToRemove = XmlLineNumbersParser.parse(fileLocations.get(0).getFilePath(), linesToRemove);
            XmlFileCleaner xmlFileCleaner = new XmlFileCleaner(fileLocations.get(0).getFilePath());
            xmlFileCleaner.clean(linesToRemove);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
