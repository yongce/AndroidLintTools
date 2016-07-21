package me.ycdev.android.tools.lintparser;

import java.util.ArrayList;
import java.util.List;

import me.ycdev.android.tools.lintparser.impl.XmlIssueNode;
import me.ycdev.android.tools.lintparser.impl.XmlRootNode;

public class LintReport {
    private XmlRootNode mRootNode;

    LintReport(XmlRootNode rootNode) {
        mRootNode = rootNode;
    }

    public List<LintIssue> getLintIssues() {
        List<LintIssue> results = new ArrayList<>();
        for (XmlIssueNode issueNode : mRootNode.getIssues()) {
            results.add(new LintIssue(issueNode));
        }
        return results;
    }
}
