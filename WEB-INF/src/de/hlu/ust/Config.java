/*
 * ust - my VAT calculating project
 * Config.java - handles the configuration values
 * hlu, Feb 8 2002 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;

/**
 * Config values, set by web-xml' context-param, java properties or hard coded
 * defaults.
 * 
 * @author Heiko Lübbe
 */
public class Config {

    /** The currency sign, used overall */
    protected static String iCurrency;

    /** The file path to the data files */
    protected static String iFilePath;

    /** The tax number, used in the headlines. */
    protected static String iTaxNumber;

    /** The user name, used for all new entries and in the headlines */
    protected static String iUserName;

    /** The booking year. */
    protected static int iBookingYear;
    
    /** The instance tag, e.g. the year. */
    protected static String iTag;

    /** Flag to set web.xml' context-param's once. */
    protected static boolean iContextParamRead = false;

    /**
     * Class constructor to set defaults from environment properties or
     * hardwired defaults.
     * <p>
     * <table>
     * <tr>
     * <th>Property</th>
     * <th>hard-wired default</th>
     * </tr>
     * <tr>
     * <td>UST_CURRENCY</td>
     * <td>EUR</td>
     * </tr>
     * <tr>
     * <td>UST_DATA</td>
     * <td>/tmp</td>
     * </tr>
     * <tr>
     * <td>UST_TAX_NUMBER</td>
     * <td>123/45678</td>
     * </tr>
     * <tr>
     * <td>user.name</td>
     * <td>James Brown</td>
     * </tr>
     * </table>
     */
    static {

        if ((iCurrency = System.getProperty("UST_CURRENCY")) == null) {
            iCurrency = "EUR";
        }

        if ((iFilePath = System.getProperty("UST_DATA")) == null) {
            iFilePath = "/tmp";
        }

        if ((iTaxNumber = System.getProperty("UST_TAX_NUMBER")) == null) {
            iTaxNumber = "123/45678";
        }

        if ((iUserName = System.getProperty("user.name")) == null) {
            iUserName = "James Brown";
        }

        // initialize at first Getter call
        iBookingYear = 0;

    }

    /**
     * Get an absolute file name for the given relative file name.
     * 
     * @param relativeFileName
     *            The relative file name.
     * @return The absolute filename with the path.
     */
    public static String getFileName(String relativeFileName) {

        return iFilePath + "/" + relativeFileName;
    }

    /**
     * Get the currency or <code>null</code>.
     * 
     * @return The configured currency sign.
     */
    public static String getCurrency() {
        return iCurrency;
    }

    /**
     * Get the users name or <code>null</code>.
     * 
     * @return The configured user name.
     */
    public static String getUserName() {
        return iUserName;
    }

    /**
     * Getter for the file path.
     * 
     * @return The configured file path or <code>null</code>.
     * @see #setFilePath(String)
     */
    public static String getFilePath() {
        return iFilePath;
    }

    /**
     * Get the tax number or <code>null</code>.
     * 
     * @return The configured tax number.
     */
    public static String getTaxNumber() {
        return iTaxNumber;
    }

    /**
     * Getter for the booking year.
     * 
     * @return The booking year.
     * @see #setBookingYear(int)
     */
    public static int getBookingYear() {
        // initialized?
        if (iBookingYear == 0) {
            iBookingYear = Booking.guessBookingYear();
        }
        return iBookingYear;
    }
    
    /**
     * Getter for the instance tag.
     * @return Returns the tag or "ust" if no tag is set.
     * @see #setTag(String)
     */
    public static String getTag() {
        return iTag == null ? "ust" : iTag;
    }
    
    /**
     * Setter for the instance tag.
     * @param tag The instance tag to set or <code>null</code>.
     */
    public static void setTag(String tag) {
        iTag = tag;
    }
    
    /**
     * Setter for the booking year.
     * 
     * @param year
     *            The year to set as booking year.
     * @see #getBookingYear()
     */
    public static void setBookingYear(int year) {
        iBookingYear = year;
    }

    /**
     * Set the currency sign or <code>null</code>.
     * 
     * @param currency
     *            The currency sign to set or <code>null</code>.
     */
    public static void setCurrency(String currency) {

        iCurrency = currency;
    }

    /**
     * Set the file path for the data files.
     * 
     * @param filePath
     *            The file path to set or <code>null</code>.
     * @see #getFilePath()
     */
    public static void setFilePath(String filePath) {
        iFilePath = filePath;
    }

    /**
     * Set the tax number.
     * 
     * @param taxNumber
     *            The tax number to set or <code>null</code>.
     */
    public static void setTaxNumber(String taxNumber) {

        iTaxNumber = taxNumber;
    }

    /**
     * Set the user name for all new entries.
     * 
     * @param userName
     *            The users name to set or <code>null</code>.
     */
    public static void setUserName(String userName) {
        iUserName = userName;
    }

    /**
     * Getter to check if web.xml's context-param already set.
     * 
     * @return Returns true or false.
     */
    public static boolean isContextParamRead() {
        return iContextParamRead;
    }

    /**
     * Mark that web.xml's context-param ist taken over.
     */
    public static void setContextParamRead() {
        iContextParamRead = true;
    }

    // ************************ version code
    // ***************************************

    /** Versions file relative file name */
    public static final String VERSION = "version";

    /** the data files version as read from file version */
    public static final String version = "DATA_VERSION:1";

    static boolean versionOK = false;

    /**
     * Creates the version file with the actual version.
     * @throws AppException In case of write problems.
     */
    public static void createVersionFile() throws AppException {
        
        final String fullFileName = getFileName(VERSION);
        
        try {
            FileWriter out = new FileWriter(fullFileName);
            PrintWriter printWriter = new PrintWriter(out);
            printWriter.println(version);
            printWriter.close();
        } catch (IOException ioe) {
            throw new AppException("Die Datei \"" + fullFileName
                    + "\" kann nicht geschrieben werden!");
        }
    }

    /**
     * Read in the data version number.
     * 
     * @throws AppException
     *             If the version isn't supported.
     * 
     */
    public static void checkDataFilesVersionUpdates() throws AppException {

        final String fileName = getFileName(VERSION);

        if (versionOK == true) {
            // already read
            return;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

            String line = in.readLine();
            if (!line.equals(version)) {
                throw new AppException("Die in der Datei \"" + fileName
                        + "\" gefundene Version ist \"" + line
                        + "\" und nicht \"" + version + "\"!");
            }
            if (in.readLine() != null) {
                throw new AppException("Mehr als eine Zeile in der Datei \""
                        + fileName + "\"!");
            }
        } catch (FileNotFoundException fne) {
            // update from old versions to 0.2.3
            updateTo023();
            // throw new AppException (fne.toString());
        } catch (IOException e) {
            throw new AppException(e.toString());
        }

        return;
    }

    /**
     * Version converting code from 0.2.3 to any later version.
     * 
     * @throws AppException
     *             Ever. In case of read or write problems. Or to mark the
     *             converting.
     */
    public static void updateTo023() throws AppException {
        FileOutputStream out = null;
        final String fileName = getFileName(VERSION);
        File old = new File(fileName);
        File save = new File(fileName + ".old");
        int prevat = 0;

        Booking[] allBookings = Booking.getAllBookings();

        for (int i = 0; i < allBookings.length; ++i) {
            if (Account.getAccountById(allBookings[i].getAccountId()).getKind() == Finance.PREVAT) {
                allBookings[i].update(allBookings[i].getGross() * -1,
                        allBookings[i].getDescription(), allBookings[i]
                                .getBankStatementId(), allBookings[i]
                                .getAccountId(), allBookings[i]
                                .getBookingDateAsString().substring(0, 10),
                        allBookings[i].getDate(), allBookings[i].getUser(),
                        allBookings[i].getVat());
                ++prevat;
            }
        }

        if (old.exists()) {
            old.renameTo(save);
        }
        try {
            out = new FileOutputStream(getFileName(VERSION));
        } catch (FileNotFoundException fnfe) {
            throw new AppException("Die Datei \"" + fileName
                    + "\" existiert nicht!");
        }
        PrintWriter p = new PrintWriter(out);
        p.println(version);
        p.close();

        // check for I/O errors
        if (p.checkError()) {
            throw new AppException("Fehler beim Schreiben der Datei "
                    + fileName);
        }
        versionOK = true;

        throw new AppException("Einmalig die Version auf " + version
                + " aktualisiert. Datei \"" + fileName + "\" angelegt. Für "
                + prevat + " Vorsteuer-Einträge das Vorzeichen gewechselt.");
    }

}
