package me.ycdev.android.tools.lintparser;

import org.junit.Test;

import java.util.Arrays;

import me.ycdev.android.tools.lintparser.impl.XmlIssueNode;
import me.ycdev.android.tools.lintparser.impl.XmlLocationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LintIssueTest {
    @Test
    public void test_allMethods() {
        XmlIssueNode issueNode = mock(XmlIssueNode.class);
        when(issueNode.getIssueCategory()).thenReturn("fsfkdsjf");
        when(issueNode.getIssueId()).thenReturn("dfkldfksld");
        when(issueNode.getIssueFileType()).thenReturn(LintConstants.IssueFileType.RES_XML);
        when(issueNode.getIssueSeverity()).thenReturn("dkldld");
        {
            XmlLocationNode locNode1 = mock(XmlLocationNode.class);
            when(locNode1.getFilePath()).thenReturn("sfjldsjfkdsf");
            when(locNode1.getLineNumber()).thenReturn(23);
            when(locNode1.getColumnNumber()).thenReturn(54);
            when(locNode1.isWholeFile()).thenReturn(true);

            XmlLocationNode locNode2 = mock(XmlLocationNode.class);
            when(locNode2.getFilePath()).thenReturn("dklsaflsf");
            when(locNode2.getLineNumber()).thenReturn(833);
            when(locNode2.getColumnNumber()).thenReturn(63);
            when(locNode2.isWholeFile()).thenReturn(false);

            when(issueNode.getLocations()).thenReturn(Arrays.asList(locNode1, locNode2));
        }

        LintIssue lintIssue = new LintIssue(issueNode);
        assertThat(lintIssue.getIssueCategory(), equalTo(issueNode.getIssueCategory()));
        assertThat(lintIssue.getIssueId(), equalTo(issueNode.getIssueId()));
        assertThat(lintIssue.getIssueFileType(), equalTo(issueNode.getIssueFileType()));
        assertThat(lintIssue.getIssueSeverity(), equalTo(issueNode.getIssueSeverity()));

        assertThat(lintIssue.getLocations().size(), equalTo(issueNode.getLocations().size()));
        for (int i = 0; i < lintIssue.getLocations().size(); i++) {
            LintIssueLocation lintLocation = lintIssue.getLocations().get(i);
            XmlLocationNode locationNode = issueNode.getLocations().get(i);
            assertThat(lintLocation.getFilePath(), equalTo(locationNode.getFilePath()));
            assertThat(lintLocation.getLineNumber(), equalTo(locationNode.getLineNumber()));
            assertThat(lintLocation.getColumnNumber(), equalTo(locationNode.getColumnNumber()));
            assertThat(lintLocation.isWholeFile(), equalTo(locationNode.isWholeFile()));
        }
    }
}
