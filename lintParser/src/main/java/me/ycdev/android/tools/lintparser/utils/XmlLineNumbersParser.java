package me.ycdev.android.tools.lintparser.utils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class XmlLineNumbersParser {
    public static int[] parse(String xmlFilePath, int[] nodeLines) throws Exception {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(xmlFilePath);
            return parse(new InputSource(fis), nodeLines);
        } finally {
            IoUtils.closeQuietly(fis);
        }
    }

    static int[] parse(InputSource source, int[] nodeLines) throws Exception {
        LinesHandler handler = new LinesHandler(nodeLines);
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(handler);
        xmlReader.parse(source);
        return handler.getAllLines();
    }

    private static class LinesHandler extends DefaultHandler {
        private int[] mNodeLines;
        private HashSet<Integer> mAllLines = new HashSet<>();
        private Locator mLocator;
        private Stack<String> mNodesToRemove = new Stack<>();
        private int mLineRemoveStart;

        LinesHandler(int[] nodeLines) {
            mNodeLines = nodeLines;
            Arrays.sort(mNodeLines);
        }

        int[] getAllLines() {
            int[] result = new int[mAllLines.size()];
            Iterator<Integer> iterator = mAllLines.iterator();
            for (int i = 0; i < result.length; i++) {
                result[i] = iterator.next();
            }
            Arrays.sort(result);
            return result;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            mLocator = locator;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            int lineNumber = mLocator.getLineNumber();
            Logger.log("startElement: %s, lineNumber: %s", localName, lineNumber);
            if (!mNodesToRemove.empty()) {
                mNodesToRemove.push(localName);
            } else if (Arrays.binarySearch(mNodeLines, lineNumber) >= 0) {
                mLineRemoveStart = lineNumber;
                mNodesToRemove.push(localName);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            int lineNumber = mLocator.getLineNumber();
            Logger.log("endElement: %s, lineNumber: %s", localName, lineNumber);
            if (!mNodesToRemove.empty()) {
                mNodesToRemove.pop();
                if (mNodesToRemove.empty()) {
                    addRemoveRange(mLineRemoveStart, lineNumber);
                }
            }
        }

        private void addRemoveRange(int lineStart, int lineEnd) {
            for (int lineNumber = lineStart; lineNumber <= lineEnd; lineNumber++) {
                mAllLines.add(lineNumber);
            }
        }
    }
}
