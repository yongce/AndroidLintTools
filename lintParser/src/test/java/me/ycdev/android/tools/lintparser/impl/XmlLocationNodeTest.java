package me.ycdev.android.tools.lintparser.impl;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XmlLocationNodeTest {
    @Test
    public void test_matchNode() {
        assertThat(XmlLocationNode.matchNode("location"), is(true));

        assertThat(XmlLocationNode.matchNode(null), is(false));
        assertThat(XmlLocationNode.matchNode(""), is(false));
        assertThat(XmlLocationNode.matchNode("alocation"), is(false));
        assertThat(XmlLocationNode.matchNode("locationa"), is(false));
    }

    @Test(expected = SAXException.class)
    public void test_unknownNode() throws SAXException {
        Attributes attributes = mock(Attributes.class);
        new XmlLocationNode("alocation", attributes);
    }

    @Test
    public void test_wholeFile_withLineColumn() throws SAXException {
        final String FILEPATH = "/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/" +
                "demo/src/main/res/drawable-v21/ic_menu_camera.xml";
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("file")).thenReturn(FILEPATH);
        when(attributes.getValue("line")).thenReturn("1");
        when(attributes.getValue("column")).thenReturn("1");

        XmlLocationNode location = new XmlLocationNode("location", attributes);
        assertThat(location.getFilePath(), equalTo(FILEPATH));
        assertThat(location.getLineNumber(), equalTo(1));
        assertThat(location.getColumnNumber(), equalTo(1));
        assertThat(location.isWholeFile(), equalTo(true));
    }

    @Test
    public void test_wholeFile_noLineColumn() throws SAXException {
        final String FILEPATH = "/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/" +
                "demo/src/main/res/drawable-v21/ic_menu_camera.xml";
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("file")).thenReturn(FILEPATH);
        when(attributes.getValue("line")).thenReturn(null);
        when(attributes.getValue("column")).thenReturn(null);

        XmlLocationNode location = new XmlLocationNode("location", attributes);
        assertThat(location.getFilePath(), equalTo(FILEPATH));
        assertThat(location.getLineNumber(), equalTo(-1));
        assertThat(location.getColumnNumber(), equalTo(-1));
        assertThat(location.isWholeFile(), equalTo(true));
    }

    @Test
    public void test_inFile() throws SAXException {
        final String FILEPATH = "/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/" +
                "demo/src/main/res/values/navigation_strings.xml";
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("file")).thenReturn(FILEPATH);
        when(attributes.getValue("line")).thenReturn("4");
        when(attributes.getValue("column")).thenReturn("13");

        XmlLocationNode location = new XmlLocationNode("location", attributes);
        assertThat(location.getFilePath(), equalTo(FILEPATH));
        assertThat(location.getLineNumber(), equalTo(4));
        assertThat(location.getColumnNumber(), equalTo(13));
        assertThat(location.isWholeFile(), equalTo(false));
    }

    @Test
    public void test_lineNotNumber() throws SAXException {
        final String FILEPATH = "/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/" +
                "demo/src/main/res/values/navigation_strings.xml";
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("file")).thenReturn(FILEPATH);
        when(attributes.getValue("line")).thenReturn("a4");
        when(attributes.getValue("column")).thenReturn("13");

        try {
            new XmlLocationNode("location", attributes);
            fail("failed to process invalid number");
        } catch (SAXException e) {
            assertThat(e.toString(), containsString("Line number not number"));
        }
    }

    @Test
    public void test_columnNotNumber() throws SAXException {
        final String FILEPATH = "/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/" +
                "demo/src/main/res/values/navigation_strings.xml";
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("file")).thenReturn(FILEPATH);
        when(attributes.getValue("line")).thenReturn("4");
        when(attributes.getValue("column")).thenReturn("13b");

        try {
            new XmlLocationNode("location", attributes);
            fail("failed to process invalid number");
        } catch (SAXException e) {
            assertThat(e.toString(), containsString("Column number not number"));
        }
    }

}
