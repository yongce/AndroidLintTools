package me.ycdev.android.tools.lintparser.impl;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Arrays;
import java.util.Collections;

import me.ycdev.android.tools.lintparser.LintConstants;
import me.ycdev.android.tools.lintparser.LintXmlParser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XmlRootNodeTest {

    @Test(expected = SAXException.class)
    public void test_unknownNode() throws SAXException {
        Attributes attributes = mock(Attributes.class);
        new XmlRootNode("aissues", attributes);
    }

    @Test
    public void test_invalidFormat() {
                /*
        <issues format="4" by="lint 25.1.7">
         */
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("format")).thenReturn("a3");
        try {
            new XmlRootNode("issues", attributes);
        } catch (SAXException e) {
            assertThat(e.getMessage(), containsString("Format version not number"));
        }
    }

    @Test
    public void test_unsupportedFormatVersion() {
        /*
        <issues format="4" by="lint 25.1.7">
         */
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("format")).thenReturn("3");
        try {
            new XmlRootNode("issues", attributes);
        } catch (SAXException e) {
            assertThat(e.getMessage(), containsString("Not supported format version"));
        }
    }

    @Test
    public void test_enableWholeFileOnly() throws SAXException {
        /*
        <issues format="4" by="lint 25.1.7">
         */
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("format")).thenReturn("4");

        XmlIssueNode issueNode1 = mock(XmlIssueNode.class);
        when(issueNode1.getIssueId()).thenReturn("UnusedResources");
        when(issueNode1.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode1.isWholeFileIssue()).thenReturn(false);

        XmlIssueNode issueNode2 = mock(XmlIssueNode.class);
        when(issueNode2.getIssueId()).thenReturn("UnusedResources");
        when(issueNode2.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode2.isWholeFileIssue()).thenReturn(true);

        XmlIssueNode issueNode3 = mock(XmlIssueNode.class);
        when(issueNode3.getIssueId()).thenReturn("UnusedResources");
        when(issueNode3.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode3.isWholeFileIssue()).thenReturn(false);

        {
            LintXmlParser.Builder builder = new LintXmlParser.Builder()
                    .enableWholeFileOnly();
            XmlRootNode rootNode = new XmlRootNode("issues", attributes);
            rootNode.addIssue(builder, issueNode1);
            rootNode.addIssue(builder, issueNode2);
            rootNode.addIssue(builder, issueNode3);

            assertThat(rootNode.getIssues().size(), equalTo(1));
            assertThat(rootNode.getIssues(), hasItem(issueNode2));
        }

        {
            LintXmlParser.Builder builder = new LintXmlParser.Builder();
            XmlRootNode rootNode = new XmlRootNode("issues", attributes);
            rootNode.addIssue(builder, issueNode1);
            rootNode.addIssue(builder, issueNode2);
            rootNode.addIssue(builder, issueNode3);

            assertThat(rootNode.getIssues().size(), equalTo(3));
            assertThat(rootNode.getIssues(), hasItem(issueNode1));
            assertThat(rootNode.getIssues(), hasItem(issueNode2));
            assertThat(rootNode.getIssues(), hasItem(issueNode3));
        }
    }

    @Test
    public void test_watchIssues() throws SAXException {
        /*
        <issues format="4" by="lint 25.1.7">
         */
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("format")).thenReturn("4");

        XmlIssueNode issueNode1 = mock(XmlIssueNode.class);
        when(issueNode1.getIssueId()).thenReturn("HardcodedText");
        when(issueNode1.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode1.isWholeFileIssue()).thenReturn(false);

        XmlIssueNode issueNode2 = mock(XmlIssueNode.class);
        when(issueNode2.getIssueId()).thenReturn("UnusedResources");
        when(issueNode2.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode2.isWholeFileIssue()).thenReturn(true);

        XmlIssueNode issueNode3 = mock(XmlIssueNode.class);
        when(issueNode3.getIssueId()).thenReturn("GradleDependency");
        when(issueNode3.getIssueFileType()).thenReturn(LintConstants.IssueFileType.BUILD_GRADLE);
        when(issueNode3.isWholeFileIssue()).thenReturn(false);

        LintXmlParser.Builder builder = new LintXmlParser.Builder()
                .watchIssues("HardcodedText", "UnusedResources");
        XmlRootNode rootNode = new XmlRootNode("issues", attributes);
        rootNode.addIssue(builder, issueNode1);
        rootNode.addIssue(builder, issueNode2);
        rootNode.addIssue(builder, issueNode3);

        assertThat(rootNode.getIssues().size(), equalTo(2));
        assertThat(rootNode.getIssues(), hasItem(issueNode1));
        assertThat(rootNode.getIssues(), hasItem(issueNode2));
    }

    @Test
    public void test_setIssueFileTypes() throws SAXException {
        /*
        <issues format="4" by="lint 25.1.7">
         */
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("format")).thenReturn("4");

        XmlIssueNode issueNode1 = mock(XmlIssueNode.class);
        when(issueNode1.getIssueId()).thenReturn("HardcodedText");
        when(issueNode1.getIssueFileType()).thenReturn(LintConstants.IssueFileType.CODE_JAVA);
        when(issueNode1.isWholeFileIssue()).thenReturn(false);

        XmlIssueNode issueNode2 = mock(XmlIssueNode.class);
        when(issueNode2.getIssueId()).thenReturn("UnusedResources");
        when(issueNode2.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode2.isWholeFileIssue()).thenReturn(true);

        XmlIssueNode issueNode3 = mock(XmlIssueNode.class);
        when(issueNode3.getIssueId()).thenReturn("GradleDependency");
        when(issueNode3.getIssueFileType()).thenReturn(LintConstants.IssueFileType.BUILD_GRADLE);
        when(issueNode3.isWholeFileIssue()).thenReturn(false);

        LintXmlParser.Builder builder = new LintXmlParser.Builder()
                .setIssueFileTypes(LintConstants.IssueFileType.CODE_JAVA
                        | LintConstants.IssueFileType.RES_XML);
        XmlRootNode rootNode = new XmlRootNode("issues", attributes);
        rootNode.addIssue(builder, issueNode1);
        rootNode.addIssue(builder, issueNode2);
        rootNode.addIssue(builder, issueNode3);

        assertThat(rootNode.getIssues().size(), equalTo(2));
        assertThat(rootNode.getIssues(), hasItem(issueNode1));
        assertThat(rootNode.getIssues(), hasItem(issueNode2));
    }

    @Test
    public void test_setFileFilter() throws SAXException {
        /*
        <issues format="4" by="lint 25.1.7">
         */
        Attributes attributes = mock(Attributes.class);
        when(attributes.getValue("format")).thenReturn("4");

        XmlIssueNode issueNode1 = mock(XmlIssueNode.class);
        when(issueNode1.getIssueId()).thenReturn("HardcodedText");
        when(issueNode1.getIssueFileType()).thenReturn(LintConstants.IssueFileType.CODE_JAVA);
        when(issueNode1.isWholeFileIssue()).thenReturn(false);
        {
            XmlLocationNode loc1 = mock(XmlLocationNode.class);
            when(loc1.getFilePath()).thenReturn("/home/xxx/yyy/src/main/java/com/third/party/Foo.java");
            when(issueNode1.getLocations()).thenReturn(Collections.singletonList(loc1));
        }

        XmlIssueNode issueNode2 = mock(XmlIssueNode.class);
        when(issueNode2.getIssueId()).thenReturn("UnusedResources");
        when(issueNode2.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode2.isWholeFileIssue()).thenReturn(true);
        {
            XmlLocationNode loc1 = mock(XmlLocationNode.class);
            when(loc1.getFilePath()).thenReturn("/home/xxx/yyy/src/main/res/drawable/third_party_abc.xml");
            XmlLocationNode loc2 = mock(XmlLocationNode.class);
            when(loc2.getFilePath()).thenReturn("/home/xxx/yyy/src/main/res/values/drawables.xml");
            when(issueNode2.getLocations()).thenReturn(Arrays.asList(loc1, loc2));
        }

        XmlIssueNode issueNode3 = mock(XmlIssueNode.class);
        when(issueNode3.getIssueId()).thenReturn("GradleDependency");
        when(issueNode3.getIssueFileType()).thenReturn(LintConstants.IssueFileType.BUILD_GRADLE);
        when(issueNode3.isWholeFileIssue()).thenReturn(false);
        {
            XmlLocationNode loc1 = mock(XmlLocationNode.class);
            when(loc1.getFilePath()).thenReturn("/home/xxx/yyy/src/main/java/com/self/Bar.java");
            when(issueNode3.getLocations()).thenReturn(Collections.singletonList(loc1));
        }

        LintXmlParser.Builder builder = new LintXmlParser.Builder()
                .setFileFilter(new LintXmlParser.FileFilter() {
                    @Override
                    public boolean accept(String filepath) {
                        return !filepath.contains("java/com/third/party")
                                && !filepath.contains("res/drawable/third_party_");
                    }
                });
        XmlRootNode rootNode = new XmlRootNode("issues", attributes);
        rootNode.addIssue(builder, issueNode1);
        rootNode.addIssue(builder, issueNode2);
        rootNode.addIssue(builder, issueNode3);

        assertThat(rootNode.getIssues().size(), equalTo(1));
        assertThat(rootNode.getIssues(), hasItem(issueNode3));
    }
}
