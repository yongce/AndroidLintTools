package me.ycdev.android.tools.lintparser;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class LintXmlParserTest {
    @Test
    public void test_parse_allIssues() throws LintParserException {
        URL url =  getClass().getResource("/lint-results-01.xml");
        File lintResultXmlFile = new File(url.getFile());
        LintXmlParser parser = new LintXmlParser.Builder().build();
        LintReport report = parser.parse(lintResultXmlFile.getAbsolutePath());
        List<LintIssue> issues = report.getLintIssues();
        assertThat(issues.size(), equalTo(33));
    }

    @Test
    public void test_parse_someIssues() throws Exception {
        URL url =  getClass().getResource("/lint-results-01.xml");
        File lintResultXmlFile = new File(url.getFile());
        LintXmlParser parser = new LintXmlParser.Builder()
                .watchIssues("OldTargetApi", "GoogleAppIndexingWarning")
                .build();
        LintReport report = parser.parse(lintResultXmlFile.getAbsolutePath());
        List<LintIssue> issues = report.getLintIssues();
        assertThat(issues.size(), equalTo(2));

        {
            LintIssue issue1 = issues.get(0);
            assertThat(issue1.getIssueCategory(), equalTo("Correctness"));
            assertThat(issue1.getIssueId(), equalTo("OldTargetApi"));
            assertThat(issue1.getIssueSeverity(), equalTo("Warning"));
            assertThat(issue1.getIssueFileType(), equalTo(LintConstants.IssueFileType.BUILD_GRADLE));
            assertThat(issue1.isWholeFileIssue(), equalTo(false));
            assertThat(issue1.getLocations().size(), equalTo(1));

            LintIssueLocation location1 = issue1.getLocations().get(0);
            assertThat(location1.getFilePath(), equalTo("/Users/yctu/work/tyc/ycdev/tools/" +
                    "AndroidLintTools/demo/build.gradle"));
            assertThat(location1.getLineNumber(), equalTo(10));
            assertThat(location1.getColumnNumber(), equalTo(9));
        }

        {
            LintIssue issue2 = issues.get(1);
            assertThat(issue2.getIssueCategory(), equalTo("Usability"));
            assertThat(issue2.getIssueId(), equalTo("GoogleAppIndexingWarning"));
            assertThat(issue2.getIssueSeverity(), equalTo("Warning"));
            assertThat(issue2.getIssueFileType(), equalTo(LintConstants.IssueFileType.ANDROID_MANIFEST));
            assertThat(issue2.isWholeFileIssue(), equalTo(false));
            assertThat(issue2.getLocations().size(), equalTo(1));

            LintIssueLocation location1 = issue2.getLocations().get(0);
            assertThat(location1.getFilePath(), equalTo("/Users/yctu/work/tyc/ycdev/tools/" +
                    "AndroidLintTools/demo/src/main/AndroidManifest.xml"));
            assertThat(location1.getLineNumber(), equalTo(5));
            assertThat(location1.getColumnNumber(), equalTo(5));
        }

    }

    @Test
    public void testParse_v5() throws Exception {
        URL url =  getClass().getResource("/lint-results-02.xml");
        File lintResultXmlFile = new File(url.getFile());
        LintXmlParser parser = new LintXmlParser.Builder()
                .watchIssues("OldTargetApi", "GoogleAppIndexingWarning")
                .build();
        LintReport report = parser.parse(lintResultXmlFile.getAbsolutePath());
        List<LintIssue> issues = report.getLintIssues();
        assertThat(issues.size(), equalTo(2));

        {
            LintIssue issue1 = issues.get(0);
            assertThat(issue1.getIssueCategory(), equalTo("Correctness"));
            assertThat(issue1.getIssueId(), equalTo("OldTargetApi"));
            assertThat(issue1.getIssueSeverity(), equalTo("Warning"));
            assertThat(issue1.getIssueFileType(), equalTo(LintConstants.IssueFileType.BUILD_GRADLE));
            assertThat(issue1.isWholeFileIssue(), equalTo(false));
            assertThat(issue1.getLocations().size(), equalTo(1));

            LintIssueLocation location1 = issue1.getLocations().get(0);
            assertThat(location1.getFilePath(), equalTo("/Users/yctu/bin/" +
                    "AndroidLintTools/demo/build.gradle"));
            assertThat(location1.getLineNumber(), equalTo(8));
            assertThat(location1.getColumnNumber(), equalTo(9));
        }

        {
            LintIssue issue2 = issues.get(1);
            assertThat(issue2.getIssueCategory(), equalTo("Usability"));
            assertThat(issue2.getIssueId(), equalTo("GoogleAppIndexingWarning"));
            assertThat(issue2.getIssueSeverity(), equalTo("Warning"));
            assertThat(issue2.getIssueFileType(), equalTo(LintConstants.IssueFileType.ANDROID_MANIFEST));
            assertThat(issue2.isWholeFileIssue(), equalTo(false));
            assertThat(issue2.getLocations().size(), equalTo(1));

            LintIssueLocation location1 = issue2.getLocations().get(0);
            assertThat(location1.getFilePath(), equalTo("/Users/yctu/bin/" +
                    "AndroidLintTools/demo/src/main/AndroidManifest.xml"));
            assertThat(location1.getLineNumber(), equalTo(5));
            assertThat(location1.getColumnNumber(), equalTo(5));
        }

    }

    @Test
    public void testParse_v5_moreLocations() throws Exception {
        URL url =  getClass().getResource("/lint-results-02.xml");
        File lintResultXmlFile = new File(url.getFile());
        LintXmlParser parser = new LintXmlParser.Builder()
                .watchIssues("UnusedResources")
                .build();
        LintReport report = parser.parse(lintResultXmlFile.getAbsolutePath());
        List<LintIssue> issues = report.getLintIssues();
        assertThat(issues.size(), equalTo(2));

        {
            LintIssue issue1 = issues.get(0);
            assertThat(issue1.getIssueCategory(), equalTo("Performance"));
            assertThat(issue1.getIssueId(), equalTo("UnusedResources"));
            assertThat(issue1.getIssueSeverity(), equalTo("Warning"));
            assertThat(issue1.getIssueFileType(), equalTo(LintConstants.IssueFileType.RES_XML));
            assertThat(issue1.isWholeFileIssue(), equalTo(false));
            assertThat(issue1.getLocations().size(), equalTo(1));

            LintIssueLocation location1 = issue1.getLocations().get(0);
            assertThat(location1.getFilePath(), equalTo("/Users/yctu/bin/" +
                    "AndroidLintTools/demo/src/main/res/values/strings.xml"));
            assertThat(location1.getLineNumber(), equalTo(4));
            assertThat(location1.getColumnNumber(), equalTo(13));
        }

        {
            LintIssue issue2 = issues.get(1);
            assertThat(issue2.getIssueCategory(), equalTo("Performance"));
            assertThat(issue2.getIssueId(), equalTo("UnusedResources"));
            assertThat(issue2.getIssueSeverity(), equalTo("Warning"));
            assertThat(issue2.getIssueFileType(), equalTo(LintConstants.IssueFileType.RES_XML));
            assertThat(issue2.isWholeFileIssue(), equalTo(false));
            assertThat(issue2.getLocations().size(), equalTo(2));

            LintIssueLocation location1 = issue2.getLocations().get(0);
            assertThat(location1.getFilePath(), equalTo("/Users/yctu/bin/" +
                    "AndroidLintTools/demo/src/main/res/values/strings.xml"));
            assertThat(location1.getLineNumber(), equalTo(6));
            assertThat(location1.getColumnNumber(), equalTo(13));

            LintIssueLocation location2 = issue2.getLocations().get(1);
            assertThat(location2.getFilePath(), equalTo("/Users/yctu/bin/" +
                    "AndroidLintTools/demo/src/main/res/values-zh-rCN/strings.xml"));
            assertThat(location2.getLineNumber(), equalTo(3));
            assertThat(location2.getColumnNumber(), equalTo(13));
        }
    }
}
