package me.ycdev.android.tools.lintparser.impl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.ycdev.android.tools.lintparser.LintXmlParser;
import me.ycdev.android.tools.lintparser.LintXmlParser.Builder;

public class XmlRootNode {
    /*
     * <issues format="4" by="lint 25.1.7">
     */
    private static final String NODE_NAME = "issues";
    private static final String ATTRIBUTE_FORMAT = "format";

    private List<XmlIssueNode> mIssues = new ArrayList<>();

    public XmlRootNode(String nodeName, Attributes attributes) throws SAXException {
        if (!NODE_NAME.equals(nodeName)) {
            throw new SAXException("Unexpected node name: " + nodeName);
        }

        String numberStr = attributes.getValue(ATTRIBUTE_FORMAT);
        int formatVersion;
        try {
            formatVersion = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            throw new SAXException("Format version not number: " + numberStr);
        }

        if (formatVersion != 4) {
            throw new SAXException("Not supported format version: " + numberStr);
        }
    }

    public void addIssue(Builder builder, XmlIssueNode issueNode) {
        if (builder.isWholeFileOnly() && !issueNode.isWholeFileIssue()) {
            return; // skip
        }

        Set<String> watchedIssueIds = builder.getIssuesIds();
        if (watchedIssueIds != null && !watchedIssueIds.contains(issueNode.getIssueId())) {
            return; // skip
        }

        if ((builder.getIssueFileTypes() & issueNode.getIssueFileType()) == 0) {
            return; // skip
        }

        LintXmlParser.FileFilter fileFilter = builder.getFileFilter();
        if (fileFilter != null) {
            for (XmlLocationNode location : issueNode.getLocations()) {
                if (!fileFilter.accept(location.getFilePath())) {
                    return; // skip
                }
            }
        }

        mIssues.add(issueNode);
    }

    public List<XmlIssueNode> getIssues() {
        return mIssues;
    }
}
