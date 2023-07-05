/*
 * ust - my VAT calculating project
 * TableData.java - some simple HTML table support
 * hlu, May 12 2001 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

/**
 * A little bit support to create HTML table data tags and HTML colorize numbers.
 * Most functionality is given by the static methods.
 *
 * @author Heiko Lübbe
 */
public class TableData {

    /**
     * Constructor with table data content as {@link String}.
     * 
     * @param content
     *            The table data content.
     */
    public TableData(String content) {
        setContent(content);
    }

    /**
     * Constructor with table data content as <code>int</code>.
     * 
     * @param i
     *            The table data content.
     */
    public TableData(int i) {
        setContent("" + i);
    }

    /**
     * Getter for the table data content.
     * 
     * @return This table data content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the table data content.
     * Changes <code>null</code>, empty strings or one single space to <code>&amp;nbsp;</code>.
     * 
     * @param content
     *            The table data content.
     */
    public void setContent(String content) {

        this.content = fmt(content);
    }

    /**
     * Changing <code>null</code>, empty strings or one single space to <code>&amp;nbsp;</code>.
     * All other strings are returned unchanged.
     * @param str The input {@link String}.
     * @return The unchanged string or <code>&amp;nbsp;</code>.
     */
    public static String fmt(String str) {
        if ((str == null) || (str.length() == 0)) {
            // use an HTML space
            return "&nbsp;";
        } else if ((str.length() == 1) && (str.charAt(0) == ' ')) {
            // same (coming from one space for tokenizer), use an HTML space
            return "&nbsp;";
        }

        return str;
    }

    /**
     * Gives the given String as an HTML table data element, left aligned.
     * @param str The input String.
     * @return The HTML data element.
     */
    public static String left(String str) {
        return "<td>" + fmt(str) + "</td>";
    }

    /**
     * Gives the given integer as an HTML table data element, left aligned.
     * @param i The input int.
     * @return The HTML data element.
     */    
    public static String left(int i) {
        return "<td>" + fmt("" + i) + "</td>";
    }

    /**
     * Gives the given String as an HTML table data element, right aligned.
     * @param str The input String.
     * @return The HTML data element.
     */
    public static String right(String str) {
        return "<td align=right>" + fmt(str) + "</td>";
    }
    
    /**
     * Gives the given integer as an HTML table data element, right aligned.
     * @param i The input int.
     * @return The HTML data element.
     */ 
    public static String right(int i) {
        return "<td align=right>" + fmt("" + i) + "</td>";
    }

    /**
     * Gives the given String as an HTML table data element, centered.
     * @param str The input String.
     * @return The HTML data element.
     */ 
    public static String center(String str) {
        return "<td align=center>" + fmt(str) + "</td>";
    }

    /**
     * Gives the given integer as an HTML table data element, centered.
     * @param i The input int.
     * @return The HTML data element.
     */
    public static String center(int i) {
        return "<td align=center>" + fmt("" + i) + "</td>";
    }

    /**
     * Surround the given number with an HTML font tag in dependency of a number sign.
     * <ul>
     *   <li>Gives minus signed numbers red.</li>
     *   <li>Gives plus signed numbers green.</li>
     *   <li>Gives zeros values gray.</li>
     *   <li>Convertes "+-" to "-".</li>
     *   <li>Convertes "--" to "+".</li>
     * </ul>
     * @param str The input string with a possible sign.
     * @return HTML tagged output string.
     */
    public static String colorize(String str) {
        if ((str == null) || (str.length() == 0)) {
            return "&nbsp;";
        }
        if (str.startsWith("+-")) {
            str = str.substring(1);
        } else if (str.startsWith("--")) {
            str = "+" + str.substring(2);
        }
        if (str.startsWith("+0,00 ") || str.startsWith("-0,00 ")) {
            str = str.substring(1);
        }
        if (str.startsWith("0,00 ")) {
            return "<font color=gray>" + str + "</font>";
        } else if (str.startsWith("+")) {
            return "<font color=green>" + str + "</font>";
        } else if (str.startsWith("-")) {
            return "<font color=red>" + str + "</font>";
        } else {
            return str;
        }
    }

    /**
     * Gives the table data content as HTML table data tagged {@link String}.
     * @return The table data content as HTML table data tagged.
     */
    public String toString() {
        return "<TD>" + content + "</TD>";
    }

    private String content;
}
