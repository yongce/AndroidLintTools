package me.ycdev.android.tools.lintparser;

import me.ycdev.android.tools.lintparser.impl.XmlIssueNode;
import me.ycdev.android.tools.lintparser.impl.XmlLocationNode;

import java.util.ArrayList;
import java.util.List;

public class LintIssue {
    private XmlIssueNode mIssueNode;
    private List<LintIssueLocation> mLocationNodes;

    LintIssue(XmlIssueNode issueNode) {
        mIssueNode = issueNode;
    }

    public String getIssueCategory() {
        return mIssueNode.getIssueCategory();
    }

    public String getIssueId() {
        return mIssueNode.getIssueId();
    }

    public String getIssueSeverity() {
        return mIssueNode.getIssueSeverity();
    }

    public int getIssueFileType() {
        return mIssueNode.getIssueFileType();
    }

    public boolean isWholeFileIssue() {
        return mIssueNode.isWholeFileIssue();
    }

    public List<LintIssueLocation> getLocations() {
        if (mLocationNodes == null) {
            List<LintIssueLocation>  locationNodes = new ArrayList<>();
            for (XmlLocationNode xmlLocation : mIssueNode.getLocations()) {
                locationNodes.add(new LintIssueLocation(xmlLocation));
            }
            mLocationNodes = locationNodes;
        }
        return mLocationNodes;
    }
}
