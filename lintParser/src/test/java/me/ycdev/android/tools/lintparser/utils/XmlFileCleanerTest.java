package me.ycdev.android.tools.lintparser.utils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class XmlFileCleanerTest {
    @Test
    public void test_matchXmlBeginTag_expected() {
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");

        assertThat(cleaner.matchXmlBeginTag("    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>"),
                notNullValue());
        assertThat(cleaner.matchXmlBeginTag("  <string name=\"navigation_drawer_open\">"),
                notNullValue());
        assertThat(cleaner.matchXmlBeginTag(" <item"),
                notNullValue());

        assertThat(cleaner.matchXmlBeginTag("\t \t<dimen name=\"nav_header_vertical_spacing\">16dp</dimen>"),
                notNullValue());
        assertThat(cleaner.matchXmlBeginTag("\t \t <string name=\"navigation_drawer_open\">"),
                notNullValue());
        assertThat(cleaner.matchXmlBeginTag(" \t \t <dimen"),
                notNullValue());

        assertThat(cleaner.matchXmlBeginTag("<dimen name=\"nav_header_vertical_spacing\">16dp</dimen>"),
                notNullValue());
        assertThat(cleaner.matchXmlBeginTag("<string name=\"navigation_drawer_open\">"),
                notNullValue());
        assertThat(cleaner.matchXmlBeginTag("<dimen"),
                notNullValue());
    }

    @Test
    public void test_matchXmlBeginTag_unexpected() {
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");

        assertThat(cleaner.matchXmlBeginTag("< dimen name=\"nav_header_vertical_spacing\">16dp</dimen>"),
                nullValue());
        assertThat(cleaner.matchXmlBeginTag("<\tdimen name=\"nav_header_vertical_spacing\">16dp</dimen>"),
                nullValue());
        assertThat(cleaner.matchXmlBeginTag("<<string name=\"navigation_drawer_open\">"),
                nullValue());
        assertThat(cleaner.matchXmlBeginTag("< \tdimen"),
                nullValue());

        assertThat(cleaner.matchXmlBeginTag("x <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>"),
                nullValue());
        assertThat(cleaner.matchXmlBeginTag("< <string name=\"navigation_drawer_open\">"),
                nullValue());
        assertThat(cleaner.matchXmlBeginTag("> <dimen"),
                nullValue());

        assertThat(cleaner.matchXmlBeginTag("<!-- Default screen margins, per the Android Design guidelines. -->"),
                nullValue());
    }

    @Test
    public void test_matchXmlEndTag_expected() {
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");

        assertThat(cleaner.matchXmlEndTag("    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>"),
                notNullValue());
        assertThat(cleaner.matchXmlEndTag(" Open navigation drawer</string>"),
                notNullValue());
        assertThat(cleaner.matchXmlEndTag(" </item>"),
                notNullValue());

        assertThat(cleaner.matchXmlEndTag("    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>   "),
                notNullValue());
        assertThat(cleaner.matchXmlEndTag(" Open navigation drawer</string>  "),
                notNullValue());
        assertThat(cleaner.matchXmlEndTag(" </item> "),
                notNullValue());

        assertThat(cleaner.matchXmlEndTag("    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>\t"),
                notNullValue());
        assertThat(cleaner.matchXmlEndTag(" Open navigation drawer</string>\t\t"),
                notNullValue());
        assertThat(cleaner.matchXmlEndTag(" </item> \t \t "),
                notNullValue());

        assertThat(cleaner.matchXmlEndTag(" />"), notNullValue());
        assertThat(cleaner.matchXmlEndTag("/> "), notNullValue());
        assertThat(cleaner.matchXmlEndTag("\t/>  "), notNullValue());
        assertThat(cleaner.matchXmlEndTag(" />\t\t"), notNullValue());
        assertThat(cleaner.matchXmlEndTag(" /> \t \t "), notNullValue());
    }

    @Test
    public void test_matchXmlEndTag_unexpected() {
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");

        assertThat(cleaner.matchXmlEndTag("    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>>"),
                nullValue());
        assertThat(cleaner.matchXmlEndTag(" Open navigation drawer</string><"),
                nullValue());
        assertThat(cleaner.matchXmlEndTag(" </item>aa"),
                nullValue());

        assertThat(cleaner.matchXmlEndTag("    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>  >"),
                nullValue());
        assertThat(cleaner.matchXmlEndTag(" Open navigation drawer</string>  <"),
                nullValue());
        assertThat(cleaner.matchXmlEndTag(" </item>\t<"),
                nullValue());

        assertThat(cleaner.matchXmlEndTag("    <dimen name=\"nav_header_vertical_spacing\">"),
                nullValue());
        assertThat(cleaner.matchXmlEndTag("    android:layout_width=\"match_parent\""),
                nullValue());
        assertThat(cleaner.matchXmlEndTag("    <dimen name=\"nav_header_vertical_spacing\">"),
                nullValue());

        assertThat(cleaner.matchXmlEndTag(" /><"), nullValue());
        assertThat(cleaner.matchXmlEndTag(" /> <"), nullValue());
        assertThat(cleaner.matchXmlEndTag("\t/>\t<"), nullValue());
        assertThat(cleaner.matchXmlEndTag(" />\t\t<"), nullValue());
        assertThat(cleaner.matchXmlEndTag(" /> \t \t aaa"), nullValue());
    }

    @Test
    public void test_isEmptyResourceFile_empty() {
        String xmlContent = "<resources>\n" +
                "\n" +
                "</resources>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(true));

        xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "</resources>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(true));

        xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "\n" +
                "</resources>";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(true));

        xmlContent = "<resources>\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "</resources>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(true));
    }

    @Test
    public void test_isEmptyResourceFile_noneEmpty() {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "\n" +
                "    <string name=\"navigation_drawer_open\">Open navigation drawer</string>\n" +
                "    <string name=\"navigation_drawer_close\">Close navigation drawer</string>\n" +
                "\n" +
                "</resources>";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));

        xmlContent = "<resources>\n" +
                "    <string name=\"app_name\">AndroidLintTools</string>\n" +
                "\n" +
                "    <string name=\"action_settings\">Settings</string>\n" +
                "</resources>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));
    }

    @Test
    public void test_isEmptyResourceFile_nonResourceXml() {
        String xmlContent = "<aaa>\n" +
                "    <string name=\"app_name\">AndroidLintTools</string>\n" +
                "\n" +
                "    <string name=\"action_settings\">Settings</string>\n" +
                "</aaa>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));

        xmlContent = "<aaa>\n" +
                "</aaa>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));

        xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<bbb>\n" +
                "</bbb>";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));
    }

    @Test
    public void test_isEmptyResourceFile_badXml() {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "\n" +
                "    <string name=\"navigation_drawer_open\">Open navigation drawer</string>\n" +
                "    <string name=\"navigation_drawer_close\">Close navigation drawer</string>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));

        xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<aaaa>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));

        xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));

        xmlContent = "hahaaaaaaaaaa";
        assertThat(XmlFileCleaner.isEmptyResourceFile(xmlContent), is(false));
    }

    @Test
    public void test_clean_nonEmpty() throws IOException {
        String xmlContent = "<resources xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
                "    <item name=\"ic_menu_camera\" type=\"drawable\">@android:drawable/ic_menu_camera</item>\n" +
                "    <item name=\"ic_menu_gallery\" type=\"drawable\">@android:drawable/ic_menu_gallery</item>\n" +
                "    <item name=\"ic_menu_slideshow\" type=\"drawable\">@android:drawable/ic_menu_slideshow</item>\n" +
                "    <item name=\"ic_menu_manage\" type=\"drawable\">@android:drawable/ic_menu_manage</item>\n" +
                "    <item name=\"ic_menu_share\" type=\"drawable\">@android:drawable/ic_menu_share</item>\n" +
                "    <item name=\"ic_menu_send\" type=\"drawable\">@android:drawable/ic_menu_send</item>\n" +
                "</resources>\n";
        BufferedReader reader = new BufferedReader(new StringReader(xmlContent));
        int[] linesToRemove = new int[] {3, 6};
        String resultExpected = "<resources xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
                "    <item name=\"ic_menu_camera\" type=\"drawable\">@android:drawable/ic_menu_camera</item>\n" +
                "    <item name=\"ic_menu_slideshow\" type=\"drawable\">@android:drawable/ic_menu_slideshow</item>\n" +
                "    <item name=\"ic_menu_manage\" type=\"drawable\">@android:drawable/ic_menu_manage</item>\n" +
                "    <item name=\"ic_menu_send\" type=\"drawable\">@android:drawable/ic_menu_send</item>\n" +
                "</resources>\n";
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");
        String result = cleaner.doClean(reader, linesToRemove);
        assertThat(result, equalTo(resultExpected));
    }

    @Test
    public void test_clean_nonEmptySkip() throws IOException {
        String xmlContent = "<resources>\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>\n" +
                "    <dimen name=\"nav_header_height\">160dp</dimen>\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "    <dimen name=\"activity_horizontal_margin\">16dp</dimen>\n" +
                "    <dimen name=\"activity_vertical_margin\">16dp</dimen>\n" +
                "    <dimen name=\"fab_margin\">16dp</dimen>\n" +
                "</resources>\n";
        BufferedReader reader = new BufferedReader(new StringReader(xmlContent));
        int[] linesToRemove = new int[] {3, 4, 5};
        String resultExpected = "<resources>\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "    <dimen name=\"activity_horizontal_margin\">16dp</dimen>\n" +
                "    <dimen name=\"activity_vertical_margin\">16dp</dimen>\n" +
                "    <dimen name=\"fab_margin\">16dp</dimen>\n" +
                "</resources>\n";
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");
        String result = cleaner.doClean(reader, linesToRemove);
        assertThat(result, equalTo(resultExpected));
    }

    @Test
    public void test_clean_empty() throws IOException {
        String xmlContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "    <color name=\"colorPrimary\">#3F51B5</color>\n" +
                "    <color name=\"colorPrimaryDark\">#303F9F</color>\n" +
                "    <color name=\"colorAccent\">#FF4081</color>\n" +
                "</resources>\n";
        BufferedReader reader = new BufferedReader(new StringReader(xmlContent));
        int[] linesToRemove = new int[] {3, 4, 5};
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");
        String result = cleaner.doClean(reader, linesToRemove);
        assertThat(result, nullValue());
    }

    @Test
    public void test_clean_emptyWithComment() throws IOException {
        String xmlContent = "<resources>\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "    <dimen name=\"nav_header_vertical_spacing\">16dp</dimen>\n" +
                "    <dimen name=\"nav_header_height\">160dp</dimen>\n" +
                "    <!-- Default screen margins, per the Android Design guidelines. -->\n" +
                "    <dimen name=\"activity_horizontal_margin\">16dp</dimen>\n" +
                "    <dimen name=\"activity_vertical_margin\">16dp</dimen>\n" +
                "    <dimen name=\"fab_margin\">16dp</dimen>\n" +
                "</resources>\n";
        BufferedReader reader = new BufferedReader(new StringReader(xmlContent));
        int[] linesToRemove = new int[] {3, 4, 6, 7, 8};
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");
        String result = cleaner.doClean(reader, linesToRemove);
        assertThat(result, nullValue());
    }

    @Test
    public void test_clean_stringArray() throws IOException {
        String xmlContent = "<resources>\n" +
                "    <string name=\"app_name\">AndroidLintTools</string>\n" +
                "\n" +
                "    <string name=\"action_settings\">Settings</string>\n" +
                "\n" +
                "    <string-array name=\"timeout_values\">\n" +
                "        <item>15分钟</item>\n" +
                "        <item>30分钟</item>\n" +
                "        <item>1小时 </item>\n" +
                "        <item>不限制</item>\n" +
                "    </string-array>\n" +
                "\n" +
                "</resources>\n";

        BufferedReader reader = new BufferedReader(new StringReader(xmlContent));
        int[] linesToRemove = new int[] {6};
        String resultExpected = "<resources>\n" +
                "    <string name=\"app_name\">AndroidLintTools</string>\n" +
                "\n" +
                "    <string name=\"action_settings\">Settings</string>\n" +
                "\n" +
                "\n" +
                "</resources>\n";
        XmlFileCleaner cleaner = new XmlFileCleaner("fake_filepath");
        String result = cleaner.doClean(reader, linesToRemove);
        assertThat(result, equalTo(resultExpected));
    }
}
