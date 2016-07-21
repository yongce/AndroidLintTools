package me.ycdev.android.tools.lintparser.impl;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import me.ycdev.android.tools.lintparser.LintConstants;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XmlIssueNodeTest {
    @Test
    public void test_matchNode() {
        assertThat(XmlIssueNode.matchNode("issue"), is(true));

        assertThat(XmlIssueNode.matchNode(null), is(false));
        assertThat(XmlIssueNode.matchNode(""), is(false));
        assertThat(XmlIssueNode.matchNode("xissue"), is(false));
        assertThat(XmlIssueNode.matchNode("issuey"), is(false));
    }

    @Test(expected = SAXException.class)
    public void test_unknownNode() throws SAXException {
        Attributes attributes = mock(Attributes.class);
        new XmlIssueNode("aissue", attributes);
    }

    @Test
    public void test_singleLocation1() throws SAXException {
        /*
    <issue
        id="UnusedResources"
        severity="Warning"
        message="The resource `R.drawable.side_nav_bar` appears to be unused"
        category="Performance"
        priority="3"
        summary="Unused resources"
        explanation="Unused resources make applications larger and slow down builds."
        errorLine1="&lt;shape xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;"
        errorLine2="^">
        <location
            file="/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/demo/src/main/res/drawable/side_nav_bar.xml"
            line="1"
            column="1"/>
    </issue>
         */
        Attributes issueAttributes = mock(Attributes.class);
        when(issueAttributes.getValue("category")).thenReturn("Performance");
        when(issueAttributes.getValue("id")).thenReturn("UnusedResources");
        when(issueAttributes.getValue("severity")).thenReturn("Warning");

        Attributes locAttrs1 = mock(Attributes.class);
        when(locAttrs1.getValue("file")).thenReturn("/Users/yctu/work/tyc/ycdev/tools/" +
                "AndroidLintTools/demo/src/main/res/drawable/side_nav_bar.xml");
        when(locAttrs1.getValue("line")).thenReturn("1");
        when(locAttrs1.getValue("column")).thenReturn("1");

        XmlIssueNode issueNode = new XmlIssueNode("issue", issueAttributes);
        XmlLocationNode locationNode = new XmlLocationNode("location", locAttrs1);
        issueNode.addLocation(locationNode);

        assertThat(issueNode.getIssueCategory(), equalTo("Performance"));
        assertThat(issueNode.getIssueId(), equalTo("UnusedResources"));
        assertThat(issueNode.getIssueSeverity(), equalTo("Warning"));
        assertThat(issueNode.isWholeFileIssue(), equalTo(true));
        assertThat(issueNode.getIssueFileType(), equalTo(LintConstants.IssueFileType.RES_XML));
        assertThat(issueNode.getLocations().size(), equalTo(1));
        assertThat(issueNode.getLocations().get(0), sameInstance(locationNode));
    }

    @Test
    public void test_singleLocation2() throws SAXException {
        /*
    <issue
        id="GradleDependency"
        severity="Warning"
        message="A newer version of com.android.support:appcompat-v7 than 23.2.1 is available: 24.0.0"
        category="Correctness"
        priority="4"
        summary="Obsolete Gradle Dependency"
        explanation="This detector looks for usages of libraries where the version you are using is not the current stable release. Using older versions is fine, and there are cases where you deliberately want to stick with an older version. However, you may simply not be aware that a more recent version is available, and that is what this lint check helps find."
        errorLine1="    compile &apos;com.android.support:appcompat-v7:23.2.1&apos;"
        errorLine2="    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
        quickfix="studio">
        <location
            file="/Users/yctu/work/tyc/ycdev/tools/AndroidLintTools/demo/build.gradle"
            line="31"
            column="5"/>
    </issue>
         */
        Attributes issueAttributes = mock(Attributes.class);
        when(issueAttributes.getValue("category")).thenReturn("Correctness");
        when(issueAttributes.getValue("id")).thenReturn("GradleDependency");
        when(issueAttributes.getValue("severity")).thenReturn("Warning");

        Attributes locAttrs1 = mock(Attributes.class);
        when(locAttrs1.getValue("file")).thenReturn("/Users/yctu/work/tyc/ycdev/tools/" +
                "AndroidLintTools/demo/build.gradle");
        when(locAttrs1.getValue("line")).thenReturn("31");
        when(locAttrs1.getValue("column")).thenReturn("5");

        XmlIssueNode issueNode = new XmlIssueNode("issue", issueAttributes);
        XmlLocationNode locationNode = new XmlLocationNode("location", locAttrs1);
        issueNode.addLocation(locationNode);

        assertThat(issueNode.getIssueCategory(), equalTo("Correctness"));
        assertThat(issueNode.getIssueId(), equalTo("GradleDependency"));
        assertThat(issueNode.getIssueSeverity(), equalTo("Warning"));
        assertThat(issueNode.isWholeFileIssue(), equalTo(false));
        assertThat(issueNode.getIssueFileType(), equalTo(LintConstants.IssueFileType.BUILD_GRADLE));
        assertThat(issueNode.getLocations().size(), equalTo(1));
        assertThat(issueNode.getLocations().get(0), sameInstance(locationNode));
    }

    @Test
    public void test_twoLocations() throws SAXException {
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
        Attributes issueAttributes = mock(Attributes.class);
        when(issueAttributes.getValue("category")).thenReturn("Performance");
        when(issueAttributes.getValue("id")).thenReturn("UnusedResources");
        when(issueAttributes.getValue("severity")).thenReturn("Warning");

        Attributes locAttrs1 = mock(Attributes.class);
        when(locAttrs1.getValue("file")).thenReturn("/Users/yctu/work/tyc/ycdev/tools/" +
                "AndroidLintTools/demo/src/main/res/drawable-v21/ic_menu_camera.xml");
        when(locAttrs1.getValue("line")).thenReturn("1");
        when(locAttrs1.getValue("column")).thenReturn("1");

        Attributes locAttrs2 = mock(Attributes.class);
        when(locAttrs2.getValue("file")).thenReturn("/Users/yctu/work/tyc/ycdev/tools/" +
                "AndroidLintTools/demo/src/main/res/values/drawables.xml");
        when(locAttrs2.getValue("line")).thenReturn("2");
        when(locAttrs2.getValue("column")).thenReturn("11");

        XmlLocationNode locationNode1 = new XmlLocationNode("location", locAttrs1);
        XmlLocationNode locationNode2 = new XmlLocationNode("location", locAttrs2);

        {
            XmlIssueNode issueNode = new XmlIssueNode("issue", issueAttributes);
            issueNode.addLocation(locationNode1);
            issueNode.addLocation(locationNode2);

            assertThat(issueNode.getIssueCategory(), equalTo("Performance"));
            assertThat(issueNode.getIssueId(), equalTo("UnusedResources"));
            assertThat(issueNode.getIssueSeverity(), equalTo("Warning"));
            assertThat(issueNode.isWholeFileIssue(), equalTo(false));
            assertThat(issueNode.getIssueFileType(), equalTo(LintConstants.IssueFileType.RES_XML));
            assertThat(issueNode.getLocations().size(), equalTo(2));
            assertThat(issueNode.getLocations(), hasItem(locationNode1));
            assertThat(issueNode.getLocations(), hasItem(locationNode2));
        }

        {
            XmlIssueNode issueNode = new XmlIssueNode("issue", issueAttributes);
            // exchange the order
            issueNode.addLocation(locationNode2);
            issueNode.addLocation(locationNode1);

            assertThat(issueNode.getIssueCategory(), equalTo("Performance"));
            assertThat(issueNode.getIssueId(), equalTo("UnusedResources"));
            assertThat(issueNode.getIssueSeverity(), equalTo("Warning"));
            assertThat(issueNode.isWholeFileIssue(), equalTo(false));
            assertThat(issueNode.getIssueFileType(), equalTo(LintConstants.IssueFileType.RES_XML));
            assertThat(issueNode.getLocations().size(), equalTo(2));
            assertThat(issueNode.getLocations(), hasItem(locationNode1));
            assertThat(issueNode.getLocations(), hasItem(locationNode2));
        }
    }
}
