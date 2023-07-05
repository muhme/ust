/*
 * ust - my VAT calculating project
 * BankStatement.java - implements a bank statement object
 * hlu, Feb 6 2000 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.InputStreamReader;

/**
 * The BankStatement object stores all information about one line from the bank
 * statement.
 * 
 * @author Heiko Lübbe
 */
public class BankStatement extends Finance implements Comparable {

    /**
     * Getter for the bank statment number (usually the counting start with 1 in
     * January).
     * 
     * @return The bank statements number.
     * 
     * @see #setNumber(int)
     */
    public int getNumber() {
        return number;
    }

    /**
     * Getter for the bank statements page. Each bank statement number starts
     * with page 1.
     * 
     * @return The bank statements page.
     * 
     * @see #setPage(int)
     */
    public int getPage() {
        return page;
    }

    /**
     * Getter for the bank statements position on the page. Each page starts
     * with position 1.
     * 
     * @return The bank statements page position.
     * 
     * @see #getPosition()
     */
    public int getPosition() {
        return position;
    }

    /**
     * Getter for the refernce to the bank account. The bank account is
     * referenced by the unique bank account internal identifier.
     * 
     * @return The bank accounts unique internal identifier for this bank
     *         statement.
     */
    public int getBankAccountId() {
        return bankAccountId;
    }

    /**
     * Setter for the bank statements number in the year.
     * 
     * @param number
     *            The bank statement number in the year to set.
     * 
     * @see #getNumber()
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Setter for the bank statements page for the given number.
     * 
     * @param page
     *            The page for the given number to set.
     * 
     * @see #getPage()
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Setter for the bank statements position on the page. Each page starts
     * with a position 1 bank statement.
     * 
     * @param position
     *            The position for the given page to set.
     * 
     * @see #getPosition()
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Setter for the bank account unique internal identifier.
     * 
     * @param bankAccountId
     *            The bank account unique internal identifier to set for this
     *            bank statement.
     * 
     * @see #getBankAccountId()
     */
    public void setBankAccountId(int bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    /**
     * Cerate this bank statement. Making the new BankStatement persistent by
     * saving the actual file as .old, set the id, append *this and write all
     * bank statemnts.
     * 
     * @throws AppException
     *             If the bank statement exists already or in case of read or
     *             write errors.
     * 
     * @see #create(int, int, int, int)
     */
    public void create() throws AppException {

        int highestId = 0; // highest existing id

        BankStatement[] bs = getAllBankStatements();

        for (int i = 0; i < bs.length; ++i) {
            // find the highest used id
            if (bs[i].getId() > highestId) {
                highestId = bs[i].getId();
            }
            // check if the bank statement is already in use
            // no "Sammelbuchungen" at the moment
            if ((bs[i].getBankAccountId() == bankAccountId)
                    && (bs[i].getNumber() == number)
                    && (bs[i].getPage() == page)
                    && (bs[i].getPosition() == position)) {
                throw new AppException("Den Kontoauszug " + number
                        + ", Seite " + page + " und Position " + position
                        + " gibt es bereits!");
            }
        }

        // set id for the new bank statement entry
        setId(++highestId);

        // add the new bank statement entry
        allEntries.add(this);

        // write all bank statement entries
        save();

    }

    /**
     * Wrapper for {@link #create()}with bank account id, number, page and
     * position.
     * 
     * @param bankAccountId
     *            The bank statments bank account identifier.
     * @param number
     *            The bank statements number.
     * @param page
     *            The bank statements page.
     * @param position
     *            The bank statements position on the page.
     * 
     * @throws AppException
     *             If the bank statement exists already or in case of read or
     *             write errors.
     * 
     * @see #create()
     */
    public void create(int bankAccountId, int number, int page, int position)
            throws AppException {
        this.bankAccountId = bankAccountId;
        this.number = number;
        this.page = page;
        this.position = position;
        create();
    }

    /**
     * Remove this bank statement.
     * 
     * @throws AppException
     *             If the bank statement isn't found or in case of read or write
     *             errors.
     */
    public void remove() throws AppException {

        read();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the bank statement by number
            if (((BankStatement) allEntries.elementAt(i)).getId() == getId()) {
                allEntries.remove(i);
                // write down the reduced account list
                save();
                return;
            }
        }
        throw new AppException("Keinen Bankeintrag mit der internen ID "
                + getId() + " gefunden!");

    }

    /**
     * Get all bank statements from January until December.
     * 
     * @return BankStatement[] All bank statements they are available.
     * 
     * @throws AppException
     *             In case of read errors.
     */
    public static BankStatement[] getAllBankStatements() throws AppException {
        return getAllBankStatements(1, 12);
    }

    /**
     * Get all bank statements from <tt>from</tt> month until <tt>to</tt>
     * month in an array, sorted first by booking date and second by the bank
     * statement.
     * 
     * @param from
     *            int beginning month (1...12)
     * @param to
     *            int until this month (1...12)
     * 
     * @return BankStatement[] All bank statements found.
     * 
     * @throws AppException
     *             Ic case of read errors.
     */
    public static BankStatement[] getAllBankStatements(int from, int to)
            throws AppException {

        int month; // bookingDate month as 1 ... 12
        Booking booking; // get the BankStatement Booking for the booking
        // date

        read();

        Vector list = new Vector();

        for (int i = 0; i < allEntries.size(); ++i) {
            booking = Booking
                    .getBookingByBankStatementId(((BankStatement) allEntries
                            .elementAt(i)).getId());
            month = booking.getBookingDate().get(Calendar.MONTH) + 1;
            if (month >= from && month <= to) {
                list.add(allEntries.elementAt(i));
            }
        }

        BankStatement[] bs = new BankStatement[list.size()];
        list.copyInto(bs);

        // sort after bankAccountId, number, page and position
        Arrays.sort(bs);

        return bs;
    }

    /**
     * Get a <code>BankStatement</code> object by given id.
     * 
     * @param id
     *            The bank statement internal identifier looking for.
     * 
     * @return The completely filled object.
     * 
     * @throws AppException
     *             If no such entry exists or in case of read errors.
     */
    public static BankStatement getBankStatementById(int id)
            throws AppException {

        read();

        for (int i = 0; i < allEntries.size(); ++i) {
            if (((BankStatement) allEntries.elementAt(i)).getId() == id) {
                return (BankStatement) allEntries.elementAt(i);
            }
        }
        throw new AppException("Kann keinen Bankeintrag mit der ID " + id
                + " finden!");

    }

    /**
     * Get a <code>BankStatement</code> object by all fields.
     * 
     * @param bankAccountId
     *            The bank statements bank account identifier.
     * @param number
     *            The bank statements number.
     * @param page
     *            The bank statements page.
     * @param position
     *            The bank statements position in the page.
     * 
     * @return The completely filled object or <code>null</code> if no such
     *         bank statement exists.
     * 
     * @throws AppException
     *             In case of read errors.
     */
    public static BankStatement getBankStatement(int bankAccountId,
            int number, int page, int position) throws AppException {

        read();

        for (int i = 0; i < allEntries.size(); ++i) {
            BankStatement bs = (BankStatement) allEntries.elementAt(i);
            if (bs.getBankAccountId() == bankAccountId &&
                    bs.getNumber() == number &&
                    bs.getPage() == page &&
                    bs.getPosition() == position) {
                return bs;
            }
        }
        
        // nothing found
        return null;

    }

    /**
     * Read the bank statement from a {@link String}.
     * 
     * @param from
     *            The <code>String</code> with the bank statement to read.
     * 
     * @return An complete bank statement object.
     * 
     * @throws AppException
     *             If the bank statement cannot be parsed.
     * 
     * @see #toString()
     */
    public static BankStatement fromString(String from) throws AppException {
        final int TOKENS = 6;
        BankStatement bankStatement = new BankStatement();
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
            bankStatement.id = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das erste Feld \"" + token
                    + "\" sollte die ID enthalten!");
        }
        // 2
        bankStatement.date = Finance.getGregorianCalendarFromString(tokens
                .nextToken());
        // 3
        token = tokens.nextToken();
        try {
            bankStatement.number = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das erste Feld \"" + token
                    + "\" sollte die Nummer enthalten!");
        }
        // 4
        token = tokens.nextToken();
        try {
            bankStatement.page = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das vierte Feld \"" + token
                    + "\" sollte die Seite enthalten!");
        }
        // 5
        token = tokens.nextToken();
        try {
            bankStatement.position = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das fünfte Feld \"" + token
                    + "\" sollte die Position enthalten!");
        }
        // 6
        token = tokens.nextToken();
        try {
            bankStatement.bankAccountId = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das sechste Feld \"" + token
                    + "\" sollte die ID der Bankverbindung enthalten!");
        }
        return bankStatement;

    }

    /**
     * Print the bank statement object with all fields to a {@link String}.
     * 
     * @return The <code>String</code> which contains all fields.
     * 
     * @see #fromString(String)
     */
    public String toString() {
        return (id + "|" + getDateAsString() + "|" + number + "|" + page + "|"
                + position + "|" + bankAccountId);
    }
    
    /**
     * Getter for all bank statement entries.
     * @see de.hlu.ust.Finance#getAllEntries()
     */
    protected Vector getAllEntries() {
        return allEntries;
    }
    
    /**
     * Getter for the bank statements file name.
     * @return The relative file name.
     */
    public String getDataFileName() {
        return BANKSTATEMENTS;
    }
    
    /** The bank statements number. Starting with 1 each year. */
    private int number;

    /** The page for a bank statement number. Starting with 1. */
    private int page;

    /** The position in the bank statements page. Starting with 1. */
    private int position;

    /** The reference to this bank statements bank account. */
    private int bankAccountId;

    /** The bank statements file name */
    private static final String BANKSTATEMENTS = "bankStatements";
    
    /** To fullfill the {@link Serializable}interface. */
    private static final long serialVersionUID = 1L;
    
    /** All bank statement entries. */
    private static Vector allEntries;

    /**
     * Read in all bank statements from the data file.
     * 
     * Not as static initializer to have the posibility to throw exceptions.
     * 
     * @throws AppException
     *             In case of read errors.
     */
    private static void read() throws AppException {

        if (allEntries != null) {
            return;
        }

        allEntries = new Vector();
        String line;
        int lineNumber = 0;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(Config
                    .getFileName(BANKSTATEMENTS)), "UTF-8"));

            while ((line = in.readLine()) != null) {
                ++lineNumber;
                allEntries.add(fromString(line));
            }
        } catch (IOException e) {
            // no exception at initialize
            throw new AppException("Probleme beim Einlesen der Datei \""
                    + Config.getFileName(BANKSTATEMENTS) + "\" in der Zeile "
                    + lineNumber + ": " + e);
        } catch (AppException ae) {
            throw new AppException("Probleme beim Einlesen der Datei \""
                    + Config.getFileName(BANKSTATEMENTS) + "\" in der Zeile "
                    + lineNumber + ": " + ae.getMessage());
        }
    }

    /**
     * Compare a bank statement first after the bank account identifier, second
     * after the number, third after the page and fourth after the position.
     * 
     * @param o
     *            The bank statement object to compare to.
     * @return -1, 0 or 1, see {@link Comparable}
     */
    public int compareTo(Object o) {

        Integer ai = new Integer(((BankStatement) o).getBankAccountId());
        Integer bi = new Integer(bankAccountId);

        if (bi.compareTo(ai) != 0) {
            return bi.compareTo(ai);
        }

        ai = new Integer(((BankStatement) o).getNumber());
        bi = new Integer(number);

        if (bi.compareTo(ai) != 0) {
            return bi.compareTo(ai);
        }

        ai = new Integer(((BankStatement) o).getPage());
        bi = new Integer(page);

        if (bi.compareTo(ai) != 0) {
            return bi.compareTo(ai);
        }

        ai = new Integer(((BankStatement) o).getPosition());
        bi = new Integer(position);

        return bi.compareTo(ai);

    }

} // class BankStatement
