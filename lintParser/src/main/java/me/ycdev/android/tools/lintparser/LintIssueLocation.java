package me.ycdev.android.tools.lintparser;

import me.ycdev.android.tools.lintparser.impl.XmlLocationNode;

public class LintIssueLocation {
    private XmlLocationNode mLocationNode;

    LintIssueLocation(XmlLocationNode locationNode) {
        mLocationNode = locationNode;
    }

    public String getFilePath() {
        return mLocationNode.getFilePath();
    }

    /**
     * @return -1 if no line number
     */
    public int getLineNumber() {
        return mLocationNode.getLineNumber();
    }

    /**
     * @return -1 if no column number
     */
    public int getColumnNumber() {
        return mLocationNode.getColumnNumber();
    }

    public boolean isWholeFile() {
        return mLocationNode.isWholeFile();
    }
}
