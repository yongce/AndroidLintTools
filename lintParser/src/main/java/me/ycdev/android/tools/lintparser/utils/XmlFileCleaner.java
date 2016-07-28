package me.ycdev.android.tools.lintparser.utils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlFileCleaner {
    private String mFilepath;
    private Pattern mTagBeginPattern;
    private Pattern mTagEndPattern1;
    private Pattern mTagEndPattern2;

    public XmlFileCleaner(String filepath) {
        mFilepath = filepath;
        mTagBeginPattern = Pattern.compile("^[ \t]*<([^ \t<!]+)");
        mTagEndPattern1 = Pattern.compile("</([^ \t>]+)>[ \t]*$");
        mTagEndPattern2 = Pattern.compile("(/>)[ \t]*$");
    }

    public void clean(int[] nodeLinesToRemove) {
        Logger.log("Clean files %d items in %s", nodeLinesToRemove.length, mFilepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(mFilepath));
            String content = doClean(reader, nodeLinesToRemove);
            if (content == null) {
                //noinspection ResultOfMethodCallIgnored
                new File(mFilepath).delete();
            } else {
                IoUtils.saveAsFile(content, mFilepath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(reader);
        }
    }

    /**
     * Visible for testing
     */
    String doClean(BufferedReader reader, int[] nodeLinesToRemove) throws IOException {
        // first, sort the node lines to remove
        Arrays.sort(nodeLinesToRemove);
        Logger.log("lines to remove: " + Arrays.toString(nodeLinesToRemove));

        StringBuilder resultContent = new StringBuilder();
        String line;
        int lineNumber = 0;
        int index = 0;
        boolean removeInProgress = false;
        String beginTag = null;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (removeInProgress) {
                String endTag = matchXmlEndTag(line);
                if (endTag != null && endTag.equals(beginTag)) {
                    removeInProgress = false;
                }
                continue;
            }

            if (index == nodeLinesToRemove.length) {
                // no lines to remove anymore
                resultContent.append(line).append("\n");
                continue;
            }

            if (lineNumber == nodeLinesToRemove[index]) {
                index++; // prepare for next line to remove

                // found the node line to remove
                beginTag = matchXmlBeginTag(line);
                if (beginTag != null) {
                    // remove the line
                    String endTag = matchXmlEndTag(line);
                    if (endTag == null) {
                        // need to remove more lines
                        removeInProgress = true;
                    }
                } else {
                    System.out.println("Cannot remove the line: [" + line + "] at "
                            + lineNumber + " in " + mFilepath);
                    resultContent.append(line).append("\n");
                }
            } else {
                resultContent.append(line).append("\n");
            }
        }

        String content = resultContent.toString();
        if (isEmptyResourceFile(content)) {
            content = null;
        }
        return content;
    }

    /**
     * Visible for testing
     */
    String matchXmlBeginTag(String line) {
        Matcher matcher = mTagBeginPattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Visible for testing
     */
    String matchXmlEndTag(String line) {
        Matcher matcher = mTagEndPattern1.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        matcher = mTagEndPattern2.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Visible for testing
     */
    static boolean isEmptyResourceFile(String content) {
        EmptyResourcesChecker checker = new EmptyResourcesChecker();

        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(checker);
            xmlReader.parse(new InputSource(new StringReader(content)));
        } catch (BreakParsingException e) {
            // expected
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        return checker.isEmpty();
    }

    private static class EmptyResourcesChecker extends DefaultHandler {
        private boolean mIsEmpty = false;

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
            if (!localName.equals("resources")) {
                mIsEmpty = false;
                throw new BreakParsingException();
            }
        }

        @Override
        public void endDocument() throws SAXException {
            mIsEmpty = true;
        }

        public boolean isEmpty() {
            return mIsEmpty;
        }
    }

    private static class BreakParsingException extends SAXException {
        // empty
    }
}
