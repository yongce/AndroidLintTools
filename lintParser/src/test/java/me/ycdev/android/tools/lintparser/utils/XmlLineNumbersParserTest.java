package me.ycdev.android.tools.lintparser.utils;

import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.StringReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class XmlLineNumbersParserTest {
    @Test
    public void normalCase() throws Exception {
        String xmlContent = "<resources>\n" +
                "    <string name=\"demo_strings1\">demo string 1</string>\n" +
                "    <string name=\"demo_strings2\" />\n" +
                "    <string name=\"used_strings3\">\n" +
                "        <large>%s</large>\n" +
                "        <small>:%s</small>\n" +
                "    </string>\n" +
                "    <string name=\"demo_strings4\">demo string 4</string>\n" +
                "</resources>\n";

        int[] allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{2});
        assertThat(allLines, equalTo(new int[]{2}));

        allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{3});
        assertThat(allLines, equalTo(new int[]{3}));

        allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{4});
        assertThat(allLines, equalTo(new int[]{4, 5, 6, 7}));

        allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{8});
        assertThat(allLines, equalTo(new int[]{8}));

        allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{2, 4});
        assertThat(allLines, equalTo(new int[]{2, 4, 5, 6, 7}));

        allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{3, 4});
        assertThat(allLines, equalTo(new int[]{3, 4, 5, 6, 7}));

        allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{2, 3, 4, 8});
        assertThat(allLines, equalTo(new int[]{2, 3, 4, 5, 6, 7, 8}));
    }

    @Test
    public void embeddedEmptyLines() throws Exception {
        String xmlContent = "<resources>\n" +
                "    <string name=\"app_name\">AndroidLintTools</string>\n" +
                "\n" +
                "    <string name=\"action_settings\">Settings</string>\n" +
                "\n" +
                "    <string-array name=\"timeout_values\">\n" +
                "        <item>15分钟</item>\n" +
                "        <item>30分钟</item>\n" +
                "\n" +
                "        <item>1小时 </item>\n" +
                "\n" +
                "        <item>不限制</item>\n" +
                "    </string-array>\n" +
                "\n" +
                "</resources>\n";
        int[] allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{6});
        assertThat(allLines, equalTo(new int[]{6, 7, 8, 9, 10, 11, 12, 13}));

        allLines = XmlLineNumbersParser.parse(new InputSource(new StringReader(xmlContent)), new int[]{2, 6});
        assertThat(allLines, equalTo(new int[]{2, 6, 7, 8, 9, 10, 11, 12, 13}));
    }
}
