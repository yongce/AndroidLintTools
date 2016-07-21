package me.ycdev.android.tools.lintparser.impl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.ycdev.android.tools.lintparser.LintConstants.IssueFileType;


public class XmlIssueNode {
    /*
    <issue
        id="UnusedResources"
        severity="Warning"
        message="The resource `R.drawable.ic_menu_camera` appears to be unused"
        category="Performance"
        priority="3"
        summary="Unused resources"
        explanation="Unused resources make applications larger and slow down builds."
        errorLine1="&lt;vector xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;"
        errorLine2="^">
        <location
            file="/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/demo/src/main/res/drawable-v21/ic_menu_camera.xml"
            line="1"
            column="1"/>
        <location
            file="/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/demo/src/main/res/values/drawables.xml"
            line="2"
            column="11"/>
    </issue>
     */
    private static final String NODE_NAME = "issue";
    private static final String ATTRIBUTE_CATEGORY = "category";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_SEVERITY = "severity";

    private String mIssueCategory;
    private String mIssueId;
    private String mIssueSeverity;
    private int mIssueFileType;
    private List<XmlLocationNode> mLocationNodes = new ArrayList<>();

    public static boolean matchNode(String nodeName) {
        return NODE_NAME.equals(nodeName);
    }

    public XmlIssueNode(String nodeName, Attributes attributes) throws SAXException {
        if (!NODE_NAME.equals(nodeName)) {
            throw new SAXException("Unexpected node name: " + nodeName);
        }

        mIssueCategory = attributes.getValue(ATTRIBUTE_CATEGORY);
        mIssueId = attributes.getValue(ATTRIBUTE_ID);
        mIssueSeverity = attributes.getValue(ATTRIBUTE_SEVERITY);
    }

    public void addLocation(XmlLocationNode location) {
        mLocationNodes.add(location);

        if (mLocationNodes.size() == 1) {
            File file = new File(location.getFilePath());
            String fileName = file.getName();
            if (fileName.equals("AndroidManifest.xml")) {
                mIssueFileType = IssueFileType.ANDROID_MANIFEST;
            } else if (fileName.endsWith(".java")) {
                mIssueFileType = IssueFileType.CODE_JAVA;
            } else if (fileName.endsWith(".xml")) {
                mIssueFileType = IssueFileType.RES_XML;
            } else if (fileName.equals("build.gradle")) {
                mIssueFileType = IssueFileType.BUILD_GRADLE;
            } else {
                mIssueFileType = IssueFileType.UNKNOWN;
            }
        }
    }

    public String getIssueCategory() {
        return mIssueCategory;
    }

    public String getIssueId() {
        return mIssueId;
    }

    public String getIssueSeverity() {
        return mIssueSeverity;
    }

    public List<XmlLocationNode> getLocations() {
        return mLocationNodes;
    }

    public int getIssueFileType() {
        return mIssueFileType;
    }

    public boolean isWholeFileIssue() {
        for (XmlLocationNode location : mLocationNodes) {
            if (!location.isWholeFile()) {
                return false;
            }
        }
        return true;
    }
}
