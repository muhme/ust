/*
 * ust - my VAT calculating project
 * Booking,java - implements a booking object
 * hlu, Jan 30 2000 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Booking object stores all informations about a single booking entry.
 * 
 * For all calculations the bookings gross value is taken as base. The vat and
 * the net values are calculated from the gross value and the vat rate.
 * 
 * @author Heiko Lübbe
 */
public class Booking extends Finance implements Comparable {

    /**
     * For interface {@link Serializable}.
     */
    private static final long serialVersionUID = 1000L;

    /**
     * Get this bookings gross in Euro Cents.
     * 
     * @return the gross in Euro Cents, rounded mercantile.
     */
    public int getGross() {

        if (gross == null) {
            return (0);
        }
        return gross.getInCents(Money.ROUND_MERCANTILE);
    }

    /**
     * Get this bookings gross as String.
     * 
     * @return the gross, e.g. "90,40"
     */
    public String getGrossAsString() {

        if (gross == null) {
            return ("0,00");
        }
        return gross.toString();
    }

    /**
     * Getter for the <tt>description</tt> field.
     * 
     * @return a copy of the bookings description.
     * 
     * @see #setDescription(String)
     */
    public String getDescription() {
        return new String(description);
    }

    /**
     * Getter for the <tt>bankStatementId</tt> field.
     * 
     * @return the bank statements id or 0 for a cash booking.
     * 
     * @see #setBankStatementId(int)
     */
    public int getBankStatementId() {
        return bankStatementId;
    }

    /**
     * Getter for the <tt>accountId</tt> field.
     * 
     * @return the accounts id.
     * 
     * @see #setAccountId(int)
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Getter for the <tt>bookingDate</tt> field.
     * 
     * @return the bookings date.
     * 
     * @see #setBookingDate(GregorianCalendar)
     */
    public GregorianCalendar getBookingDate() {
        return (GregorianCalendar) bookingDate.clone();
    }

    /**
     * Get the bookings date as {@link String}.
     * 
     * @return the bookings date or the actual date if no booking date is set.
     */
    public String getBookingDateAsString() {

        GregorianCalendar gc = bookingDate;

        if (gc == null) {
            gc = new GregorianCalendar();
        }

        return Finance.gregorianToString(gc);
    }

    /**
     * Calculating this bookings VAT value. The calculation depends on the
     * bookings gross value and the vat rate.
     * 
     * @return the bookings vat value as Euro Cent.
     * 
     * @see #calcNet()
     */
    public int calcVat() {
        return Money.calcVat(getGross(), vat);
    }

    /**
     * Calculating this bookings net value. The calculation depends on the
     * bookings gross value and the vat rate.
     * 
     * @return the bookings vat value as Euro Cent.
     * 
     * @see #calcVat()
     */
    public int calcNet() {
        return Money.calcNet(getGross(), vat);
    }

    /**
     * Setter for the <tt>gross</tt> field.
     * 
     * @param gross
     *            this bookings gross in Cent.
     * 
     * @see #getGross()
     */
    public void setGross(int gross) {
        this.gross = new Money(gross);
    }

    /**
     * Setter for the <tt>description</tt> field. The given parameter is
     * copied.
     * 
     * @param description
     *            this bookings description.
     * 
     * @see #getDescription()
     */
    public void setDescription(String description) {
        this.description = new String(description);
    }

    /**
     * Setter for the <tt>bankStatementId</tt> field.
     * 
     * @param bankStatementId
     *            the bank statements id to set.
     * 
     * @see #getBankStatementId()
     */
    public void setBankStatementId(int bankStatementId) {
        this.bankStatementId = bankStatementId;
    }

    /**
     * Setter for the <tt>accountId</tt> field.
     * 
     * @param accountId
     *            the account id to set.
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    /**
     * Setter for the <tt>bookingDate</tt> field. The given parameter is
     * copied.
     * 
     * @param bookingDate
     *            the booking date to set.
     * 
     * @see #getBookingDate()
     * @see #setBookingDate(String)
     */
    public void setBookingDate(GregorianCalendar bookingDate) {
        this.bookingDate = (GregorianCalendar) bookingDate.clone();
    }

    /**
     * Set the booking date from a string like my birthday "19.12.1961".
     * 
     * @param date
     *            the booking date to set.
     * 
     * @throws AppException
     *             if the date parsing fails.
     */
    public void setBookingDate(String date) throws AppException {
        int day, month, year;
        StringTokenizer tokens = new StringTokenizer(date, ".");

        try {
            day = Integer.parseInt(tokens.nextToken());
            month = Integer.parseInt(tokens.nextToken()) - 1;
            year = Integer.parseInt(tokens.nextToken());

            if (year < 100) {
                year += 2000;
            }
            // check if the date correct, e.g. is day or month not over-running
            GregorianCalendar gc = new GregorianCalendar(year, month, day);
            String compare = toTwo(day) + "." + toTwo(month + 1) + "." + year;
            if (!gregorianToString(gc).startsWith(compare)) {
                throw new AppException("Datum \"" + date + "\" ergibt erst \""
                        + compare + "\" und dann \"" + gregorianToString(gc)
                        + "\"");
            }
            this.bookingDate = gc;
        }
        // catch all exceptions to one error message
        catch (Throwable t) {
            throw new AppException("Das Datum \"" + date
                    + "\" hat nicht die Form 19.12.1961!<br> " + t);
        }
    }

    /**
     * Get all existing bookings in an array, sorted first by booking date and
     * second after the bank statement.
     * 
     * @return An array of all bookings.
     * @throws AppException
     *             In case of read errors.
     */
    public static Booking[] getAllBookings() throws AppException {
        return getAllBookings(1, 12, 0);
    }

    /**
     * Selecting bookings from a starting month, until to an ending month and a
     * accounting number. Getting the result in an array, sorted first by
     * booking date and second after the bank statement.
     * 
     * @param from
     *            From that month (1...12).
     * @param to
     *            Until this month (1...12).
     * @param accountNumber
     *            Only for this accounting number, or 0 for all accounts.
     * @return An array of the selected bookings.
     * @throws AppException
     *             In case of read errors.
     */
    public static Booking[] getAllBookings(int from, int to, int accountNumber)
            throws AppException {
        int month; // bookindDate month as 1 ... 12

        readBookings();

        Vector list = new Vector();

        for (int i = 0; i < allEntries.size(); ++i) {
            month = ((Booking) allEntries.elementAt(i)).getBookingDate().get(
                    Calendar.MONTH) + 1;
            if (month >= from && month <= to) {
                if (accountNumber == 0) {
                    list.add(allEntries.elementAt(i));
                } else {
                    Account account = Account
                            .getAccountById(((Booking) allEntries.elementAt(i))
                                    .getAccountId());
                    if (account.getNumber() == accountNumber) {
                        list.add(allEntries.elementAt(i));
                    }
                }
            }
        }

        Booking[] aa = new Booking[list.size()];
        list.copyInto(aa);

        // sort after booking date
        Arrays.sort(aa);

        return aa;

    }

    /**
     * Get an booking entry by the given booking identifier.
     * 
     * @param id
     *            The booking identifier.
     * @return A reference to the booking entry found.
     * @throws AppException
     *             In case of read errors or no such entry exists.
     */
    public static Booking getBookingById(int id) throws AppException {
        Booking booking = new Booking();

        readBookings();

        for (int i = 0; i < allEntries.size(); ++i) {
            if (((Booking) allEntries.elementAt(i)).getId() == id) {
                booking = (Booking) allEntries.elementAt(i);
                return booking;
            }
        }
        throw new AppException("Kann keine Buchung mit der ID " + id
                + " finden!");

    }

    /**
     * Get an booking entry by the given bank statement identifier.
     * 
     * @param bankStatementId
     *            The bank statement identifier.
     * @return A reference to the booking entry found.
     * @throws AppException
     *             In case of read errors or no such entry exists.
     */
    public static Booking getBookingByBankStatementId(int bankStatementId)
            throws AppException {
        Booking booking = new Booking();

        readBookings();

        for (int i = 0; i < allEntries.size(); ++i) {
            if (((Booking) allEntries.elementAt(i)).getBankStatementId() == bankStatementId) {
                booking = (Booking) allEntries.elementAt(i);
                return booking;
            }
        }
        throw new AppException("Kann keine Buchung mit der bankStatementId "
                + bankStatementId + " finden!");

    }

    /**
     * Create a new booking entry by making the new booking entry persistent.
     * Saving the file as .old, setting the id, append *this and write all
     * bookings.
     * 
     * @throws AppException
     *             In case of read or write errors.
     */
    public void create() throws AppException {

        int highestId = 0; // highest existing id

        readBookings();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the highest used id
            if (((Booking) allEntries.elementAt(i)).getId() > highestId) {
                highestId = ((Booking) allEntries.elementAt(i)).getId();
            }
        }

        // set id for the new booking entry
        setId(++highestId);

        // add the new booking entry
        allEntries.add(this);

        // write all booking entries
        save();

    }

    /**
     * Remove this booking entry.
     * 
     * @throws AppException
     *             In case of read or write errors, or if the entry isn't found.
     */
    public void remove() throws AppException {

        readBookings();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the booking by number
            if (((Booking) allEntries.elementAt(i)).getId() == getId()) {
                allEntries.remove(i);
                // write down the reduced booking list
                save();
                return;
            }
        }
        throw new AppException("Kein Konto mit der internen ID " + getId()
                + " vorhanden!");

    }

    /**
     * Actualize the given booking by id in the persistence.
     * 
     * @param gross
     *            The new gross value.
     * @param description
     *            The new description.
     * @param bankStatementId
     *            The new bank statement identifier or <code>0</code> if no
     *            bank statement exists.
     * @param accountId
     *            The new account identifier.
     * @param bookingDate
     *            The new last booking date.
     * @param date
     *            Thew new last changed date and time.
     * @param user
     *            The new last changed user.
     * @param vat
     *            The new value added tax percents or <code>0</code> if the
     *            booking VAT free.
     * @throws AppException
     *             In case of read or write errors, or if no booking entry
     *             exists for this identifier.
     */
    public void update(int gross, String description, int bankStatementId,
            int accountId, String bookingDate, GregorianCalendar date,
            String user, double vat) throws AppException {

        readBookings();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the booking by the internal id
            if (((Booking) allEntries.elementAt(i)).getId() == getId()) {
                ((Booking) allEntries.elementAt(i)).setGross(gross);
                ((Booking) allEntries.elementAt(i)).setDescription(description);
                ((Booking) allEntries.elementAt(i))
                        .setBankStatementId(bankStatementId);
                ((Booking) allEntries.elementAt(i)).setAccountId(accountId);
                ((Booking) allEntries.elementAt(i)).setBookingDate(bookingDate);
                ((Booking) allEntries.elementAt(i)).setUser(user);
                ((Booking) allEntries.elementAt(i)).setVat(vat);
                ((Booking) allEntries.elementAt(i)).setDate(date);

                // write down the booking list with the modified entry
                save();
                return;
            }
        }
        throw new AppException("Kein Buchung mit der internen ID " + getId()
                + " vorhanden!");

    }
    
    /**
     * This method determines the booking year by average booking entries year.
     * <p>
     * If less then three booking entries exist or in case of read errors, use simple this year.
     * 
     * @return The guessed booking year.
     */
    public static int guessBookingYear() {
        
        int i;
        int sum = 0;
        
        try {
            readBookings();
        }
        catch (AppException ae) {
            /* do nothing, use simple this year */
        }
        
        for (i = 0; i < allEntries.size(); ++i) {
            sum += ((Booking) allEntries.elementAt(i)).getBookingDate().get(Calendar.YEAR);
        }
        
        if (i < 3) {
            return Calendar.getInstance().get(Calendar.YEAR);
        }
        
        return sum / i;
    }

    /**
     * Print the booking entry with all fields.
     * 
     * @return The booking entry with all fields separated by '|'.
     * @see #fromString(String)
     */
    public String toString() {

        // append at least one space to the possible empty string fields
        // to fight against StringTokenizer cannot have empty fields
        String d = description.length() == 0 ? " " : description;
        String u = user.length() == 0 ? " " : user;

        return (gross.getInCents(Money.ROUND_MERCANTILE) + "|" + d + "|"
                + bankStatementId + "|" + accountId + "|"
                + getBookingDateAsString() + "|" + getId() + "|"
                + getDateAsString() + "|" + u + "|" + getVat());
    }

    /**
     * Parsing an booking entry from a {@link String}.
     * 
     * @param from
     *            The booking entry with all fields separated by '|'.
     * @return The parsed booking entry.
     * @throws AppException
     *             In case of parsing errors.
     * @see #toString()
     */
    public static Booking fromString(String from) throws AppException {
        final int TOKENS = 9;
        Booking booking = new Booking();
        String token;
        StringTokenizer tokens;

        tokens = new StringTokenizer(from, "|");
        if (tokens.countTokens() != TOKENS) {
            throw new AppException(tokens.countTokens() + " und nicht "
                    + TOKENS + " Felder in \"" + from + "\"!");
        }
        // 1
        token = tokens.nextToken();
        try {
            booking.setGross(Integer.parseInt(token));
        } catch (NumberFormatException e) {
            throw new AppException("Das erste Feld \"" + token
                    + "\" sollte den Bruttobetrag enthalten!");
        }
        // 2
        booking.description = tokens.nextToken();
        if (booking.description.equals(" ")) {
            booking.setDescription(""); // against StringTokenizer
        }
        // 3
        token = tokens.nextToken();
        try {
            booking.bankStatementId = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das dritte Feld \"" + token
                    + "\" sollte die Id der Banküberweisung enthalten!");
        }
        // 4
        token = tokens.nextToken();
        try {
            booking.accountId = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das vierte Feld \"" + token
                    + "\" sollte die Id des Bankkontos enthalten!");
        }
        // 5
        booking.bookingDate = Finance.getGregorianCalendarFromString(tokens
                .nextToken());
        // 6
        token = tokens.nextToken();
        try {
            booking.id = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das sechste Feld \"" + token
                    + "\" sollte die interne Id enthalten!");
        }
        // 7
        booking.date = Finance.getGregorianCalendarFromString(tokens
                .nextToken());
        // 8
        booking.user = tokens.nextToken();
        if (booking.user.equals(" ")) {
            booking.setUser(""); // against StringTokenizer
        }
        // 9
        token = tokens.nextToken();
        try {
            booking.vat = Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das neunte Feld \"" + token
                    + "\" sollte den Mehrwertsteuer-Prozentsatz enthalten!");
        }

        return booking;

    }
    
    /**
     * Getter for all booking entries.
     * @see de.hlu.ust.Finance#getAllEntries()
     */
    protected Vector getAllEntries() {
        return allEntries;
    }

    /**
     * Getter for the bookings file name.
     * @return The relative file name.
     */
    public String getDataFileName() {
        return BOOKINGS;
    }
    
    /** This bookings gross value */
    private Money gross;

    /** This bookings description */
    private String description;

    /**
     * This bookings bank statement identifier or <code>0</code> if this
     * booking hasn't no bank statement.
     */
    private int bankStatementId;

    /** This booking account identifier */
    private int accountId;

    /** This bookings date. The date of the booking, not the entry made date. */
    private GregorianCalendar bookingDate;

    /** The relative file name of the bookings data file. */
    private static final String BOOKINGS = "bookings";

    /** Flag, if the bookings data file already read */
    static boolean fileIsRead = false;
    
    /** All booking entries. */
    private static Vector allEntries;

    /**
     * Read in all bookings from the data file.
     * <p>
     * Not as static initializer to have the posibility to throw exceptions.
     * 
     * @throws AppException
     *             In case of read errors.
     */
    private static void readBookings() throws AppException {

        if (fileIsRead) {
            return;
        }

        allEntries = new Vector();
        String line;
        int lineNumber = 0;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(Config
                    .getFileName(BOOKINGS)), "UTF-8"));
            while ((line = in.readLine()) != null) {
                ++lineNumber;
                allEntries.add(fromString(line));
            }
            fileIsRead = true;
        } catch (IOException e) {
            // no exception at initialize
            throw new AppException("Probleme beim Einlesen der Datei \""
                    + Config.getFileName(BOOKINGS) + "\" in der Zeile "
                    + lineNumber + ": " + e);
        } catch (AppException ae) {
            throw new AppException("Probleme beim Einlesen der Datei \""
                    + Config.getFileName(BOOKINGS) + "\" in der Zeile "
                    + lineNumber + ": " + ae.getMessage());
        }

        return;
    }

    /**
     * Compares Bookings first after the booking date and second after the bank
     * statement.
     * 
     * @param object
     *            The second Booking object to compare with.
     * @return -1, 0 or 1, see {@link Comparable}
     */
    public int compareTo(Object object) {

        Booking booking = (Booking) object;
        if (booking == null) {
            return -1;
        }

        GregorianCalendar a = getBookingDate();
        GregorianCalendar b = booking.getBookingDate();

        if (a.get(Calendar.YEAR) < b.get(Calendar.YEAR)) {
            return -1;
        } else if (a.get(Calendar.YEAR) > b.get(Calendar.YEAR)) {
            return 1;
        }

        if (a.get(Calendar.MONTH) < b.get(Calendar.MONTH)) {
            return -1;
        } else if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)) {
            return 1;
        }

        if (a.get(Calendar.DAY_OF_MONTH) < b.get(Calendar.DAY_OF_MONTH)) {
            return -1;
        } else if (a.get(Calendar.DAY_OF_MONTH) > b.get(Calendar.DAY_OF_MONTH)) {
            return 1;
        }

        int ida = getBankStatementId();
        int idb = booking.getBankStatementId();

        if (ida == 0 && idb == 0) {
            return 0;
        } else if (ida == 0) {
            return -1;
        } else if (idb == 0) {
            return 1;
        }

        BankStatement bankStatementA;
        BankStatement bankStatementB;
        try {
            bankStatementA = BankStatement.getBankStatementById(ida);
            bankStatementB = BankStatement.getBankStatementById(idb);
        } catch (Exception e) {
            // ignore errors
            return 0;
        }
        return bankStatementA.compareTo(bankStatementB);

    } // compareTo()

    /**
     * Test case.
     * 
     * @param args
     */
    public static void main(String[] args) {

        try {
            /*
            Booking booking = new Booking();
            System.out.println(booking.getGrossAsString());
            */
            System.out.println("Jahr: " + guessBookingYear());
        } catch (Throwable t) {
            System.err.println(t);
            t.printStackTrace();
        }

    }

}
