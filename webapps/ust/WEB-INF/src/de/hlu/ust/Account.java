/*
 * ust - my VAT calculating project
 * Account.java - implements the account object
 * hlu, Jan 30 2000 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.InputStreamReader;

/**
 * The <code>Account</code> object stores all information about an ust
 * account. An <code>Account</code> collects bookings of the same type, e.g.
 * all outgoings for stamps. It differs from the {@link BankAccount}which makes
 * an entry for a bank.
 * 
 * @author Heiko Lübbe
 */
public class Account extends Finance implements Comparable {

    /**
     * Getter for the accounts number.
     * 
     * @return The accounts number.
     * 
     * @see #setNumber(int)
     */
    public int getNumber() {
        return number;
    }

    /**
     * Getter for the accounts name.
     * 
     * @return This accounts name.
     * 
     * @see #setName(String)
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the accounts description.
     * 
     * @return This accounts description.
     * 
     * @see #setDescription(String)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the accounts type. The type is importend for the VAT
     * calculation. Possible types are:
     * <li>
     * <ul>
     * {@link Finance#IN}
     * </ul>
     * <ul>
     * {@link Finance#OUT}
     * </ul>
     * <ul>
     * {@link Finance#NEUTRAL}
     * </ul>
     * <ul>
     * {@link Finance#PREVAT}
     * </ul>
     * <ul>
     * {@link Finance#IN_CASH_BOX}
     * </ul>
     * <ul>
     * {@link Finance#OUT_CASH_BOX}
     * </ul>
     * </li>
     * 
     * @return This accounts type.
     * 
     * @see #setKind(int)
     */
    public int getKind() {
        return kind;
    }

    /**
     * Getter of the accounts flag about the bank statement.
     * 
     * @return <code>true</code> if bookings of this account have usually an
     *         bank statement.
     * 
     * @see #setHasBankStatement(boolean)
     */
    public boolean getHasBankStatement() {
        return hasBankStatement;
    }

    /**
     * Setter for the accounts number.
     * 
     * @param number
     *            The account number to set.
     * 
     * @see #getNumber()
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Setter for the accounts name.
     * 
     * @param name
     *            The accounts name to set.
     * 
     * @see #getName()
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the accounts description.
     * 
     * @param description
     *            The accounts description to set.
     * 
     * @see #getDescription()
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for the accounts type.
     * 
     * @param kind
     *            The accounts type, for meaningful values see
     *            {@link #getKind()}.
     * 
     * @see #getKind()
     */
    public void setKind(int kind) {
        this.kind = kind;
    }

    /**
     * Setter for the "booking of this account have usually an bank entry" flag.
     * 
     * @param hasBankStatement
     *            <code>true</code> if bookings of this account have usually
     *            an bank entry.
     * 
     * @see #getHasBankStatement()
     */
    public void setHasBankStatement(boolean hasBankStatement) {
        this.hasBankStatement = hasBankStatement;
    }

    /**
     * Add the account to the list of accounts. Make the new account
     * persistence, save the file as .old, set the id, append *this and write
     * all accounts.
     * 
     * @throws AppException
     *             If the account number exists already or some read or write
     *             problems.
     */
    public void create() throws AppException {

        int highestId = 0; // highest existing id

        readAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the highest used id
            if (((Account) allEntries.elementAt(i)).getId() > highestId) {
                highestId = ((Account) allEntries.elementAt(i)).getId();
            }
            // check if the account number already in use
            if (((Account) allEntries.elementAt(i)).getNumber() == number) {
                throw new AppException("Die Kontonummer " + number
                        + " gibt es bereits!");
            }
        }

        // set id for the new account entry
        setId(++highestId);

        // add the new account entry
        allEntries.add(this);

        // write all account entries
        save();

    }

    /**
     * Remove the account entry.
     * 
     * @throws AppException
     *             If the accounts internal id isn't found or in case of read
     *             or write problems.
     */
    public void remove() throws AppException {

        readAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the account by internal id
            if (((Account) allEntries.elementAt(i)).getId() == getId()) {
                allEntries.remove(i);
                // write down the reduced account list
                save();
                return;
            }
        }
        throw new AppException("Kein Konto mit der internen ID " + getId()
                + " vorhanden!");

    }

    /**
     * Actualize the given account by the internal id and persist the change.
     * The date field is set to the actual date and time.
     * 
     * @param number
     *            accounts new number.
     * @param name
     *            accounts new name.
     * @param description
     *            accounts new description.
     * @param kind
     *            accounts new type.
     * @param hasBankStatement
     *            accounts new idea about bookings of this account have
     *            usually an bank entry.
     * @param vat
     *            accounts default for the VAT that have bookings of this
     *            account.
     * @param user
     *            changing users name.
     * 
     * @throws AppException
     *             If no account with the given internal id is found or in case
     *             of read or write problems.
     */
    public void update(int number, String name, String description, int kind,
            boolean hasBankStatement, double vat, String user)
            throws AppException {

        readAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the account by the internal id
            if (((Account) allEntries.elementAt(i)).getId() == getId()) {
                ((Account) allEntries.elementAt(i)).setNumber(number);
                ((Account) allEntries.elementAt(i)).setName(name);
                ((Account) allEntries.elementAt(i)).setDescription(description);
                ((Account) allEntries.elementAt(i)).setKind(kind);
                ((Account) allEntries.elementAt(i))
                        .setHasBankStatement(hasBankStatement);
                ((Account) allEntries.elementAt(i)).setVat(vat);
                ((Account) allEntries.elementAt(i)).setUser(user);
                // set the date to the actual date and time
                ((Account) allEntries.elementAt(i))
                        .setDate(new GregorianCalendar());

                // write down the reduced account list
                save();
                return;
            }
        }
        throw new AppException("Kein Konto mit der internen ID " + getId()
                + " vorhanden!");

    }

    /**
     * Print the account with all fields.
     * 
     * @return With all fields of this account, separated by '|'.
     */
    public String toString() {

        return number + "|" + cfs(name) + "|" + cfs(description) + "|" + kind
                + "|" + (hasBankStatement ? "true" : "false") + "|" + getId()
                + "|" + getDateAsString() + "|" + cfs(user) + "|" + getVat();
    }

    /**
     * Compares an account entry after the account number.
     * 
     * @param o
     *            the Account object to compare
     * @return -1, 0 or 1, see {@link Comparable}
     */
    public int compareTo(Object o) {

        Integer ai = new Integer(((Account) o).getNumber());
        Integer bi = new Integer(number);

        return bi.compareTo(ai);

    }

    /**
     * Get all existing accounts in an array, sorted by the account number.
     * 
     * @return All account entries.
     * 
     * @throws AppException
     *             in case of problems with reading the accounts file.
     */
    public static Account[] getAllAccounts() throws AppException {

        readAccounts();

        Account[] aa = new Account[allEntries.size()];
        allEntries.copyInto(aa);

        // sort after account number
        Arrays.sort(aa);

        return aa;

    }

    /**
     * Get an <code>Account</code> object for a given account number.
     * 
     * @param number
     *            The account number, looking for.
     * 
     * @return Reference to the account entry for the given number.
     * 
     * @throws AppException
     *             In case of problems reading accounts or if for the given
     *             account number no account exists.
     */
    public static Account getAccountByNumber(int number) throws AppException {

        readAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            if (((Account) allEntries.elementAt(i)).getNumber() == number) {
                return (Account) allEntries.elementAt(i);
            }
        }
        throw new AppException("Kann kein Konto mit der Nummer " + number
                + " finden!");

    }

    /**
     * Get an <code>Account</code> object for a given internal id.
     * 
     * @param id
     *            The internal id, looking for.
     * 
     * @return Reference to the account entry for the given internal id.
     * 
     * @throws AppException
     *             In case of problems reading accounts or if for the given
     *             internal id no account exists.
     */
    public static Account getAccountById(int id) throws AppException {

        readAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            if (((Account) allEntries.elementAt(i)).getId() == id) {
                return (Account) allEntries.elementAt(i);
            }
        }
        throw new AppException("Kann kein Konto mit der ID " + id + " finden!");

    }

    /**
     * Read the account from a string.
     * 
     * @param from
     *            The line where the account entry is read for.
     * 
     * @return The completly filled account entry.
     * 
     * @throws AppException
     *             If the format cannot be parsed.
     */
    private static Account fromString(String from) throws AppException {
        final int TOKENS = 9;
        Account account = new Account();
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
            account.number = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das erste Feld \"" + token
                    + "\" sollte die Kontonummer enthalten!");
        }
        // 2
        account.name = sfc(tokens.nextToken());
        // 3
        account.description = sfc(tokens.nextToken());
        // 4
        token = tokens.nextToken();
        try {
            account.kind = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das vierte Feld \"" + token
                    + "\" sollte die Art (0,1,2 ...) enthalten!");
        }
        // 5
        token = tokens.nextToken();
        if (token.equals("true")) {
            account.hasBankStatement = true;
        }
        // 6
        token = tokens.nextToken();
        try {
            account.id = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das sechste Feld \"" + token
                    + "\" sollte die interne Id enthalten!");
        }
        // 7
        account.date = Finance.getGregorianCalendarFromString(tokens
                .nextToken());
        // 8
        account.user = sfc(tokens.nextToken());
        // 9
        token = tokens.nextToken();
        try {
            account.vat = Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das neunte Feld \"" + token
                    + "\" sollte den Mehrwertsteuer-Prozentsatz enthalten!");
        }

        return account;

    }

    /**
     * Read in the account list. Not as static initializer to have the
     * posibility to throw exceptions.
     * 
     * @throws AppException
     *             In case of read problems.
     */
    private static void readAccounts() throws AppException {

        if (allEntries != null) {
            return;
        }

        allEntries = new Vector();
        String line;
        int lineNumber = 0;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(Config
                    .getFileName(ACCOUNTS)), "UTF-8"));

            while ((line = in.readLine()) != null) {
                ++lineNumber;
                allEntries.add(fromString(line));
            }
        } catch (Exception e) {
            throw new AppException("Probleme beim Einlesen der Datei \""
                    + Config.getFileName(ACCOUNTS) + "\" in der Zeile "
                    + lineNumber + ": " + e);
        }
    }

    /**
     * Getter for all account entries.
     * 
     * @see de.hlu.ust.Finance#getAllEntries()
     */
    protected Vector getAllEntries() {
        return allEntries;
    }

    /**
     * Getter for the accounts file name.
     * @return The relative file name.
     */
    public String getDataFileName() {
        return ACCOUNTS;
    }

    /** Field to store accounts number. */
    private int number;

    /** Field to store accounts name. */
    private String name;

    /** Field to store accounts description */
    private String description;

    /** Field to store accounts type (IN, OUT, NEUTRAL ...). */
    private int kind;

    /** Flag to store 'bookings of this account have usually a bank statement'. */
    private boolean hasBankStatement;

    /** The accounts file name. */
    private static final String ACCOUNTS = "accounts";

    /** All account entries. */
    private static Vector allEntries;

    /** To fullfill the {@link Serializable}interface. */
    private static final long serialVersionUID = 1000L;

    /**
     * Test case.
     * 
     * @param args
     */
    public static void main(String[] args) {

        try {
            readAccounts();
            (new Account()).save();
            /*
             * Account[] accounts = getAllAccounts();
             * 
             * for (int i = 0; i < accounts.length; ++i) {
             * System.out.println(accounts[i]);
             * System.out.println(accounts[i].getName()); }
             */

        } catch (Throwable t) {
            System.err.println(t);
            t.printStackTrace();
        }

    }

}
