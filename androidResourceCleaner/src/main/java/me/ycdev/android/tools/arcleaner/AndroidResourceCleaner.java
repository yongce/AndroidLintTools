package me.ycdev.android.tools.arcleaner;

import me.ycdev.android.tools.lintparser.LintIssue;
import me.ycdev.android.tools.lintparser.LintIssueLocation;
import me.ycdev.android.tools.lintparser.LintReport;
import me.ycdev.android.tools.lintparser.LintXmlParser;
import me.ycdev.android.tools.lintparser.LintConstants.IssueId;
import me.ycdev.android.tools.lintparser.LintParserException;
import me.ycdev.android.tools.lintparser.utils.Logger;
import me.ycdev.android.tools.lintparser.utils.XmlFileCleaner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidResourceCleaner {
    public static void main(String[] args) {
        System.out.println("ARCleaner args: " + Arrays.toString(args));

        String lintResultXmlFilepath = args[0];
        boolean cleanWholeFileOnly = false;
        if (args.length > 1) {
            cleanWholeFileOnly = Boolean.parseBoolean(args[1]);
        }
        String fileMatchReg = null;
        if (args.length > 2) {
            fileMatchReg = args[2];
        }

        AndroidResourceCleaner cleaner = new AndroidResourceCleaner(lintResultXmlFilepath,
                cleanWholeFileOnly, fileMatchReg);
        cleaner.execute();
    }

    private String mLintResultXmlFilepath;
    private boolean mCleanWholeFileOnly;
    private String mFileMatchReg;

    public AndroidResourceCleaner(String lintResultXmlFilepath, boolean cleanWholeFileOnly,
            String fileMatchReg) {
        mLintResultXmlFilepath = lintResultXmlFilepath;
        mCleanWholeFileOnly = cleanWholeFileOnly;
        mFileMatchReg = fileMatchReg;
    }

    public void execute() {
        LintXmlParser.Builder builder = new LintXmlParser.Builder()
                .watchIssues(IssueId.UNUSED_RESOURCES);
        if (mCleanWholeFileOnly) {
            builder.enableWholeFileOnly();
        }
        if (mFileMatchReg != null && mFileMatchReg.length() > 0) {
            builder.setFileFilter(new LintXmlParser.FileFilter() {
                @Override
                public boolean accept(String filepath) {
                    return filepath.matches(mFileMatchReg);
                }
            });
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

        XmlFileCleaner xmlFileCleaner = new XmlFileCleaner(fileLocations.get(0).getFilePath());
        xmlFileCleaner.clean(linesToRemove);
    }

}
