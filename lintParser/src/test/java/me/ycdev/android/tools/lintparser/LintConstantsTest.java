package me.ycdev.android.tools.lintparser;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class LintConstantsTest {
    @Test
    public void test_issueFileTypes() {
        int andAll = LintConstants.IssueFileType.UNKNOWN
                & LintConstants.IssueFileType.ANDROID_MANIFEST
                & LintConstants.IssueFileType.CODE_JAVA
                & LintConstants.IssueFileType.RES_XML
                & LintConstants.IssueFileType.BUILD_GRADLE;
        assertThat(andAll, equalTo(0));

        int orAll = LintConstants.IssueFileType.UNKNOWN
                | LintConstants.IssueFileType.ANDROID_MANIFEST
                | LintConstants.IssueFileType.CODE_JAVA
                | LintConstants.IssueFileType.RES_XML
                | LintConstants.IssueFileType.BUILD_GRADLE;
        assertThat(orAll, equalTo(LintConstants.IssueFileType.ALL));
    }
}
