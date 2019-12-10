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

public class XmlFileCleaner {
    private String mFilepath;

    public XmlFileCleaner(String filepath) {
        mFilepath = filepath;
    }

    public void clean(int[] linesToRemove) {
        Logger.log("Clean files %d items in %s", linesToRemove.length, mFilepath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(mFilepath));
            String content = doClean(reader, linesToRemove);
            if (content == null) {
                //noinspection ResultOfMethodCallIgnored
                new File(mFilepath).delete();
            } else {
                IoUtils.saveAsFile(content, mFilepath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(reader);
        }
    }

    /**
     * Visible for testing
     */
    String doClean(BufferedReader reader, int[] linesToRemove) throws IOException {
        // first, sort the node lines to remove
        Arrays.sort(linesToRemove);
        Logger.log("lines to remove: " + Arrays.toString(linesToRemove));

        StringBuilder resultContent = new StringBuilder();
        String line;
        int lineNumber = 0;
        int index = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if (index == linesToRemove.length) {
                // no lines to remove anymore
                resultContent.append(line).append("\n");
                continue;
            }

            if (lineNumber == linesToRemove[index]) {
                // remove the line
                index++; // prepare for next line to remove
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
        public void endDocument() {
            mIsEmpty = true;
        }

        boolean isEmpty() {
            return mIsEmpty;
        }
    }

    private static class BreakParsingException extends SAXException {
        // empty
    }
}
