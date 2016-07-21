package me.ycdev.android.tools.lintparser;

import org.junit.Test;

import me.ycdev.android.tools.lintparser.impl.XmlLocationNode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LintIssueLocationTest {
    @Test
    public void test_allMethods() {
        XmlLocationNode locationNode = mock(XmlLocationNode.class);
        when(locationNode.getFilePath()).thenReturn("sljflksjfdls");
        when(locationNode.getLineNumber()).thenReturn(843);
        when(locationNode.getColumnNumber()).thenReturn(23);
        when(locationNode.isWholeFile()).thenReturn(true);

        LintIssueLocation issueLocation = new LintIssueLocation(locationNode);
        assertThat(issueLocation.getFilePath(), equalTo(locationNode.getFilePath()));
        assertThat(issueLocation.getLineNumber(), equalTo(locationNode.getLineNumber()));
        assertThat(issueLocation.getColumnNumber(), equalTo(locationNode.getColumnNumber()));
        assertThat(issueLocation.isWholeFile(), equalTo(locationNode.isWholeFile()));
    }
}
