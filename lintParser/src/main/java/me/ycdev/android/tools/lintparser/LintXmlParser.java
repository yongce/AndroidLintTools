package me.ycdev.android.tools.lintparser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.ycdev.android.tools.lintparser.LintConstants.IssueFileType;
import me.ycdev.android.tools.lintparser.impl.XmlIssueNode;
import me.ycdev.android.tools.lintparser.impl.XmlLocationNode;
import me.ycdev.android.tools.lintparser.impl.XmlRootNode;
import me.ycdev.android.tools.lintparser.utils.IoUtils;
import me.ycdev.android.tools.lintparser.utils.Logger;

public class LintXmlParser {
    private Builder mBuilder;
    private String mXmlFilePath;

    private LintXmlParser(Builder builder) {
        mBuilder = builder;
    }

    public LintReport parse(String xmlFilePath) throws LintParserException {
        mXmlFilePath = xmlFilePath;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(xmlFilePath);
            LintIssuesHandler issuesHandler = new LintIssuesHandler();
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(issuesHandler);
            xmlReader.parse(new InputSource(fis));
            return new LintReport(issuesHandler.getRootNode());
        } catch (SAXException | IOException e) {
            throw new LintParserException(e);
        } finally {
            IoUtils.closeQuietly(fis);
        }
    }

    public interface FileFilter {
        boolean accept(String filepath);
    }

    public static class Builder {
        private boolean mBuildDone = false;

        private Set<String> mIssueIds;
        private FileFilter mFileFilter;
        private int mIssueFileTypes = IssueFileType.ALL;
        private boolean mWholeFileOnly = false;

        public LintXmlParser build() {
            mBuildDone = true;
            return new LintXmlParser(this);
        }

        private void checkForChange() {
            if (mBuildDone) {
                throw new RuntimeException("builder already locked, cannot change");
            }
        }

        public Set<String> getIssuesIds() {
            return mIssueIds;
        }

        public Builder watchIssues(String... issues) {
            return watchIssues(Arrays.asList(issues));
        }

        public Builder watchIssues(List<String> issues) {
            checkForChange();
            if (mIssueIds == null) {
                mIssueIds = new HashSet<>();
            }
            mIssueIds.addAll(issues);
            return this;
        }

        public FileFilter getFileFilter() {
            return mFileFilter;
        }

        public Builder setFileFilter(FileFilter filter) {
            checkForChange();
            mFileFilter = filter;
            return this;
        }

        public int getIssueFileTypes() {
            return mIssueFileTypes;
        }

        /**
         * @see LintConstants.IssueFileType
         */
        public Builder setIssueFileTypes(int issueFileTypes) {
            checkForChange();
            mIssueFileTypes = issueFileTypes;
            return this;
        }

        public boolean isWholeFileOnly() {
            return mWholeFileOnly;
        }

        public Builder enableWholeFileOnly() {
            checkForChange();
            mWholeFileOnly = true;
            return this;
        }
    }

    private class LintIssuesHandler extends DefaultHandler {
        private XmlRootNode mRootNode;
        private XmlIssueNode mCurIssueNode;

        @Override
        public void startDocument() throws SAXException {
            Logger.log("start document");
        }

        @Override
        public void endDocument() throws SAXException {
            Logger.log("end document");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            Logger.log("start element: " + localName);
            if (mRootNode == null) {
                // expect the first element is the root node "issues"
                mRootNode = new XmlRootNode(localName, attributes);
            } else if (XmlIssueNode.matchNode(localName)) {
                mCurIssueNode = new XmlIssueNode(localName, attributes);
            } else if (XmlLocationNode.matchNode(localName)) {
                XmlLocationNode locationNode = new XmlLocationNode(localName, attributes);
                mCurIssueNode.addLocation(locationNode);
                mRootNode.addIssue(mBuilder, mCurIssueNode);
            } else {
                throw new SAXException("unexpected node name: " + localName);
            }
        }

        public XmlRootNode getRootNode() {
            return mRootNode;
        }
    }
}
