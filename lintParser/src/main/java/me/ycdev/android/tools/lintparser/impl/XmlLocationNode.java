package me.ycdev.android.tools.lintparser.impl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XmlLocationNode {
    /*
        <location
            file="/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/demo/src/main/res/drawable-v21/ic_menu_camera.xml"
            line="1"
            column="1"/>
     */
    private static final String NODE_NAME = "location";
    private static final String ATTRIBUTE_FILE = "file";
    private static final String ATTRIBUTE_LINE = "line";
    private static final String ATTRIBUTE_COLUMN = "column";

    private String mFilePath;
    private int mLineNumber;
    private int mColumnNumber;

    public static boolean matchNode(String nodeName) {
        return NODE_NAME.equals(nodeName);
    }

    public XmlLocationNode(String nodeName, Attributes attributes) throws SAXException {
        if (!NODE_NAME.equals(nodeName)) {
            throw new SAXException("Unexpected node name: " + nodeName);
        }

        mFilePath = attributes.getValue(ATTRIBUTE_FILE);

        String numberStr = attributes.getValue(ATTRIBUTE_LINE);
        try {
            if (numberStr == null || numberStr.length() == 0) {
                mLineNumber = -1;
            } else {
                mLineNumber = Integer.parseInt(numberStr);
            }
        } catch (NumberFormatException e) {
            throw new SAXException("Line number not number: " + numberStr);
        }

        numberStr = attributes.getValue(ATTRIBUTE_COLUMN);
        try {
            if (numberStr == null || numberStr.length() == 0) {
                mColumnNumber = -1;
            } else {
                mColumnNumber = Integer.parseInt(numberStr);
            }
        } catch (NumberFormatException e) {
            throw new SAXException("Column number not number: " + numberStr);
        }
    }

    public String getFilePath() {
        return mFilePath;
    }

    /**
     * @return -1 if no line number
     */
    public int getLineNumber() {
        return mLineNumber;
    }

    /**
     * @return -1 if no column number
     */
    public int getColumnNumber() {
        return mColumnNumber;
    }

    public boolean isWholeFile() {
        return mLineNumber < 0 || (mFilePath.contains("/res/") && !mFilePath.contains("/res/values"));
    }
}
