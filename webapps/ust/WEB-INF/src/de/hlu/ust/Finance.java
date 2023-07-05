/*
 * ust - my VAT calculating project
 * Finance.java - an parent object for the Account, Booking and BankAccount objects
 * hlu, Jan 30 2000 - Jul 5 2023
 */

package de.hlu.ust;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * The base class for {@link Account},{@link BankAccount}and {@link Booking}.
 * <p>
 * It stores:
 * <ul>
 * <li>an internal identifier,</li>
 * <li>the creating or last changing date and time,</li>
 * <li>the creating or last changing user name and</li>
 * <li>the value added tax percent value.</li>
 * </ul>
 * It defines the following account types:
 * <ul>
 * <li><code>IN</code>,</li>
 * <li><code>OUT</code>,</li>
 * <li><code>NEUTRAL</code>,</li>
 * <li><code>PREVAT</code>,</li>
 * <li><code>IN_CASH_BOX</code> and</li>
 * <li><code>OUT_CASH_BOX</code>.</li>
 * </ul>
 * 
 * @author Heiko Lübbe
 */
public abstract class Finance implements Serializable {

    /** Program version. */
    public static final String VERSION = "0.2.7";

    /** Program date. */
    public static final String DATE = "5. Juli 2023";

    /** The incomming account type. */
    public static final int IN = 0;

    /** The outgoing account type. */
    public static final int OUT = 1;

    /** The neutral account type. */
    public static final int NEUTRAL = 2;

    /** The prevat (Vorsteuer) account type. */
    public static final int PREVAT = 3;

    /** The incomming cash box account type. */
    public static final int IN_CASH_BOX = 4;

    /** The outgoing cash box account type. */
    public static final int OUT_CASH_BOX = 5;

    /** Month selection. */
    public static final String[] months = { "Alle", "Januar", "Februar",
            "März", "April", "Mai", "Juni", "Juli", "August", "September",
            "Oktober", "November", "Dezember", "1. Quartal", "2. Quartal",
            "3. Quartal", "4. Quartal" };

    /**
     * Constructor with the given VAT percent value.
     * 
     * @param vat
     *            Value added tax percents.
     */
    public Finance(double vat) {
        super();
        this.vat = vat;
    }

    /**
     * Standard constructor sets <code>date</code>.
     */
    public Finance() {
        this.date = new GregorianCalendar();
    }

    /**
     * Getter for the date field. If the field isn't set, the actual date and
     * time is returned.
     * 
     * @return An copy of the objects date field.
     * @see #setDate(GregorianCalendar)
     * @see #getDateAsString()
     */
    public GregorianCalendar getDate() {
        if (date == null) {
            return new GregorianCalendar();
        }
        return (GregorianCalendar) date.clone();
    }

    /**
     * Get the date field as string. If the field isn't set, the actual date and
     * time is returned.
     * 
     * @return A string representation of the objects date field.
     * @see #getDate()
     */
    public String getDateAsString() {
        if (date == null) {
            return (gregorianToString(new GregorianCalendar()));
        }
        return gregorianToString(date);
    }

    /**
     * Getter for the users name field.
     * 
     * @return The users name field or <code>null</code>.
     * @see #setUser(String)
     */
    public String getUser() {
        return user;
    }

    /**
     * Get the value added tax field as {@link String}.
     * <p>
     * An appending ".0" is removed and American '.' is converted to German ','.
     * 
     * @return VAT value as String.
     */
    public String getVatAsString() {
        String str = "" + vat;

        if (str.endsWith(".0")) {
            str = str.substring(0, str.length() - 2);
        }
        return str.replace('.', ',');
    }

    /**
     * Getter for the value added tax field.
     * 
     * @return The value added tax percent value.
     * @see #setVat(double)
     */
    public double getVat() {
        return vat;
    }

    /**
     * Checks if this Finance has a VAT value.
     * 
     * @return true if this Finance has a VAT value.
     */
    public boolean hasVat() {
        return this.vat != 0;
    }

    /**
     * Getter for the internal identifier field.
     * 
     * @return The internal identifier.
     * @see #setId(int)
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the internal identifier field.
     * 
     * @param id
     *            The internal identifier to set.
     * @see #getId()
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter for the vat field.
     * 
     * @param vat
     *            The value added tax in percent to set.
     * @see #getVat()
     */
    public void setVat(double vat) {
        this.vat = vat;
    }

    /**
     * Setter for the users name field.
     * 
     * @param user
     *            User name as first and family name or <code>null</code>.
     * @see #getUser()
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Setter for the date and time field.
     * <p>
     * The field presents the the date and time of creating or last changing the
     * entry.
     * 
     * @param date
     *            The date field to copy or <code>null</code>.
     * @see #getDate()
     * @see #setDate(String)
     */
    public void setDate(GregorianCalendar date) {
        if (date == null) {
            this.date = null;
        }
        this.date = (GregorianCalendar) date.clone();
    }

    /**
     * Set the date field from a String.
     * 
     * @param date
     *            The date and time in the a format like "19.12.1961 12:00".
     * @throws AppException
     *             In case of parsing errors.
     * @see #setDate(GregorianCalendar)
     */
    public void setDate(String date) throws AppException {
        this.date = getGregorianCalendarFromString(date);
    }

    /**
     * Parse a {@link String}for date and time in the format of "19.12.1961
     * 12:00".
     * 
     * @param date
     *            The date and time to parse.
     * @return The parsed date and time.
     * @throws AppException
     *             In case of parsing errors.
     * @see #gregorianToString(GregorianCalendar)
     */
    public static GregorianCalendar getGregorianCalendarFromString(String date)
            throws AppException {

        StringTokenizer tokens;
        GregorianCalendar gc = new GregorianCalendar();

        int mday;
        int month;
        int year;

        try {
            // split in date and time
            tokens = new StringTokenizer(date, " ");
            if (tokens.countTokens() != 2) {
                throw new Exception();
            }
            String day = tokens.nextToken();
            String time = tokens.nextToken();

            // split in day, month and year
            tokens = new StringTokenizer(day, ".");
            if (tokens.countTokens() != 3) {
                throw new Exception();
            }
            mday = Integer.parseInt(tokens.nextToken());
            month = Integer.parseInt(tokens.nextToken()) - 1;
            year = Integer.parseInt(tokens.nextToken());

            // split in hour and minute
            tokens = new StringTokenizer(time, ":");
            if (tokens.countTokens() != 2) {
                throw new Exception();
            }
            int hour = Integer.parseInt(tokens.nextToken());
            int minute = Integer.parseInt(tokens.nextToken());

            // set GregorianCalendar
            gc.set(year, month, mday, hour, minute);

        }
        // catch all errors to one error message
        catch (Exception e) {
            throw new AppException("Datum \"" + date
                    + "\" ist nicht im Format \"19.12.1961 12:00\"");
        }

        // check if the date correct, e.g. is day or month not over-running
        String compare = toTwo(mday) + "." + toTwo(month + 1) + "." + year;
        if (!gregorianToString(gc).startsWith(compare)) {
            throw new AppException("Datum \"" + date + "\" ergibt erst \""
                    + compare + "\" und dann \"" + gregorianToString(gc) + "\"");
        }

        return gc;

    }

    /**
     * Converts date and time from a {@link GregorianCalendar}to a
     * {@link String}in the format of e.g. "19.12.1961 12:00".
     * 
     * @param date
     *            {@link GregorianCalendar}to convert.
     * @return Converted date and time.
     * @see #getGregorianCalendarFromString(String)
     * @see #gregorianDateToString(GregorianCalendar)
     */
    public static String gregorianToString(GregorianCalendar date) {

        return (toTwo(date.get(Calendar.DAY_OF_MONTH)) + "."
                + toTwo((date.get(Calendar.MONTH) + 1)) + "."
                + date.get(Calendar.YEAR) + " "
                + toTwo(date.get(Calendar.HOUR_OF_DAY)) + ":" + toTwo(date
                .get(Calendar.MINUTE)));
    }

    /**
     * Converts date from a {@link GregorianCalendar}to a {@link String}in the
     * format of e.g. "19.12.1961".
     * 
     * @param date
     *            {@link GregorianCalendar}to convert.
     * @return Converted date and time.
     * @see #getGregorianCalendarFromString(String)
     * @see #gregorianToString(GregorianCalendar)
     */
    public String gregorianDateToString(GregorianCalendar date) {

        return (toTwo(date.get(Calendar.DAY_OF_MONTH)) + "."
                + toTwo((date.get(Calendar.MONTH) + 1)) + "." + date
                .get(Calendar.YEAR));
    }

    /**
     * Adds a leading zero to a single digit.
     * 
     * @param i
     *            The digit.
     * @return <code>"09"</code> for 9,<code>"10"</code> for 10 and so on.
     */
    public static String toTwo(int i) {
        return (i < 10 ? "0" + i : "" + i);
    }

    /** The internal identifier 1, 2, 3 ... */
    protected int id;

    /** The date and time creating or last change this entry */
    protected GregorianCalendar date = null;

    /** The user who has created or last changed this entry. */
    protected String user;

    /** The value added tax as percent value. */
    protected double vat;

    /**
     * Getter for the in-memory double of the persistent list of all entries.
     * 
     * @return All entries as vector.
     */
    protected abstract Vector getAllEntries();

    /**
     * Getter for the relative data file name (w/o the path).
     * 
     * @return The relative data file name.
     */
    protected abstract String getDataFileName();

    /** To fullfill the {@link Serializable}interface. */
    static final long serialVersionUID = 1000L;

    /**
     * Prepare a {@link String}for file storage.
     * <p>
     * Append at least one space to the possible empty string fields to fight
     * against {@link StringTokenizer}cannot have empty fields.
     * <p>
     * Replace all occurences of '|' whith an space, because it is used as field
     * separator.
     * 
     * @param str
     *            The input String.
     * @return A new String.
     * @see #sfc(String)
     */
    public static String cfs(String str) {
        if ((str == null) || (str.length() == 0)) {
            return " ";
        }
        return str.replace('|', ' ');
    }

    /**
     * Get an {@link String}from the file storage.
     * <p>
     * Removing single spaces (they are inserted for the {@link StringTokenizer}).
     * 
     * @param str
     *            The input String.
     * @return An empty string or the given string..
     * @see #cfs(String)
     */
    public static String sfc(String str) {

        return str.equals(" ") ? "" : str;
    }

    /**
     * Indicating the data file is readable.
     * 
     * @return <code>true</code> if the data file is readable.
     */
    public boolean isReadable() {
        File file = new File(Config.getFileName(getDataFileName()));
        return file.canRead();
    }

    /**
     * Indicating the data file is read-only.
     * 
     * @return <code>true</code> if the data file is read-only.
     */
    public boolean isReadOnly() {
        File file = new File(Config.getFileName(getDataFileName()));
        return !file.canWrite();
    }

    /**
     * Creates an empty data file.
     * 
     * @throws AppException
     *             If the new data file cannot be created.
     */
    public void createDataFile() throws AppException {

        final String fullFileName = Config.getFileName(getDataFileName());

        try {
            FileWriter out = new FileWriter(fullFileName);
            PrintWriter printWriter = new PrintWriter(out);
            printWriter.close();
        } catch (IOException ioe) {
            throw new AppException("Die Datei \"" + fullFileName
                    + "\" kann nicht zum Schreiben geöffnet werden!");
        }
    }

    /**
     * Save the all entries Vector to a file.
     * 
     * @throws AppException
     *             In case of write errors etc.
     */
    protected void save() throws AppException {

        OutputStreamWriter out;
        final String fullFileName = Config.getFileName(getDataFileName());
        final String fullFileNameOld = fullFileName + ".old";
        final File before = new File(fullFileName);
        final File save = new File(fullFileNameOld);

        if (!before.canWrite()) {
            throw new AppException("Kann die Datei \"" + before
                    + "\" nicht schreiben!");
        }
        long oldDate = before.lastModified();
        if (before.exists()) {
            if (!before.renameTo(save)) {
                new AppException("Kann die Datei " + fullFileName
                        + " nicht in die Datei " + fullFileNameOld
                        + " umbennen!");
            }
            /*
             * Das renameTo() gibt keinen Fehler unter Linux, wenn das
             * Verzeichnis nicht schreibbar ist
             */
            if (oldDate != save.lastModified()) {
                throw new AppException("Die Sicherung in die Datei " + save
                        + " war nicht erfolgreich!");
            }
        }

        try {

            try {
                out = new OutputStreamWriter(
                        new FileOutputStream(fullFileName), "UTF-8");
            } catch (FileNotFoundException fnfe) {
                throw new AppException("Die Datei \"" + fullFileName
                        + "\" kann nicht zum Schreiben geöffnet werden!");
            }
            PrintWriter p = new PrintWriter(out);

            // write all account entries
            for (int i = 0; i < getAllEntries().size(); ++i) {
                p.println(getAllEntries().elementAt(i));
            }

            // check for I/O errors
            if (p.checkError()) {
                throw new AppException("Fehler beim Schreiben der Datei "
                        + fullFileName);
            }
            p.close();

        } catch (Throwable t) {
            // catch all errors during writing the new file
            String msg = "Probleme beim Erstellen der Datei \"" + fullFileName
                    + "\"! " + t;
            t.printStackTrace();
            if (save.exists()) {
                // go back to the old file
                save.renameTo(before);
                msg += " Datei \"" + fullFileName + "\" zurückgeschrieben.";
                throw new AppException(msg);
            }
        }

    }

}
