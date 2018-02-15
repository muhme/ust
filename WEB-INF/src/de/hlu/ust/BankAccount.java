/*
 * ust - my VAT calculating project
 * BankAccount,java - implements the bank account object
 * hlu, Sep 22 2003 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.InputStreamReader;

/**
 * The <code>BankAccount</code> object stores all information about a bank
 * from this programs view.
 * 
 * @author Heiko Lübbe
 */
public class BankAccount extends Finance implements Comparable {

    /**
     * Getter for the banking account number.
     * 
     * @return A copy of the banking account number.
     * 
     * @see #setAccountNumber(String)
     */
    public String getAccountNumber() {
        return new String(accountNumber);
    }

    /**
     * Getter for the long (e.g. instituts) name of the bank.
     * 
     * @return A copy of the long name of the bank, e.g. "Dresdner Bank".
     * 
     * @see #setBankName(String)
     * @see #getNickname()
     */
    public String getBankName() {
        return new String(bankName);
    }

    /**
     * Getter for the short name of the bank.
     * 
     * @return A copy of the short name of the bank, e.g. "DreBa".
     * 
     * @see #setNickname(String)
     * @see #getBankName()
     */
    public String getNickname() {
        return new String(nickname);
    }

    /**
     * Getter for this bank entry description.
     * 
     * @return A copy of the description.
     * 
     * @see #setDescription(String)
     */
    public String getDescription() {
        return new String(description);
    }

    /**
     * Getter for the bank code (German BLZ).
     * 
     * @return A copy of the bank code.
     * 
     * @see #setBankCode(String)
     */
    public String getBankCode() {
        return new String(bankCode);
    }

    /**
     * Getter for the International Bank Account Number.
     * 
     * @return A copy of the IBAN.
     * 
     * @see #setIBAN(String)
     */
    public String getIBAN() {
        return new String(iban);
    }

    /**
     * Setter for the bank account number.
     * 
     * @param accountNumber
     *            The bank account number to copy.
     * 
     * @see #getAccountNumber()
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = new String(accountNumber);
    }

    /**
     * Setter for the long bank name.
     * 
     * @param bankName
     *            The long bank name to copy.
     * 
     * @see #getBankName()
     */
    public void setBankName(String bankName) {
        this.bankName = new String(bankName);
    }

    /**
     * Setter for the short bank name.
     * 
     * @param nickname
     *            The short bank name to copy.
     * 
     * @see #getNickname()
     */
    public void setNickname(String nickname) {
        this.nickname = new String(nickname);
    }

    /**
     * Setter for the description of this bank entry.
     * 
     * @param description
     *            The description to copy.
     * 
     * @see #getDescription()
     */
    public void setDescription(String description) {
        this.description = new String(description);
    }

    /**
     * Setter for the bank code (German BLZ).
     * 
     * @param bankCode
     *            The bank code to copy.
     * 
     * @see #getBankCode()
     */
    public void setBankCode(String bankCode) {
        this.bankCode = new String(bankCode);
    }

    /**
     * Setter for the International Bank Account Number.
     * 
     * @param iban
     *            The IBAN to copy.
     * 
     * @see #getIBAN()
     */
    public void setIBAN(String iban) {
        this.iban = new String(iban);
    }


    /**
     * Making the new bank account persistent by saving the actual file as .old, set the id,
     * append *this and write all bank accounts.
     * 
     * @throws AppException If an bank account with this nickname exists already, or in case of read or write errors.
     */
    public void create() throws AppException {

        int highestId = 0; // highest existing id

        readBankAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the highest used id
            if (((BankAccount) allEntries.elementAt(i)).getId() > highestId) {
                highestId = ((BankAccount) allEntries.elementAt(i)).getId();
            }
            // check if the bank accounts nickname is already in use
            if (((BankAccount) allEntries.elementAt(i)).getNickname().equals(
                    nickname)) {
                throw new AppException("Ein Bankkonto mit der Abkürzung "
                        + nickname + " gibt es bereits!");
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
     * Remove the bank account.
     * 
     * @throws AppException If no bank account entry exists with the given id, or in case of read or write errors.
     */
    public void remove() throws AppException {

        readBankAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the bank account by id
            if (((BankAccount) allEntries.elementAt(i)).getId() == getId()) {
                allEntries.remove(i);
                // write down the reduced bank account list
                save();
                return;
            }
        }
        throw new AppException("Kein Bankkonto mit der internen ID " + getId()
                + " vorhanden!");

    }

    /**
     * Actualize the given account by id in the persistent storage.
     * The date field is set to the actual date and time.
     * 
     * @param accountNumber The new bank account number.
     * @param bankName The new long bank name.
     * @param nickname The new short bank name.
     * @param description The new bank entry description.
     * @param bankCode The new bank code (German BLZ).
     * @param iban The new International Bank Account Number.
     * @param user The user, who is changing the entry.
     * 
     * @throws AppException If no entry found with this id, or in case of read or write errors.
     */
    public void update(String accountNumber, String bankName, String nickname,
            String description, String bankCode, String iban, String user)
            throws AppException {

        readBankAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            // find the bank account by the internal id
            if (((BankAccount) allEntries.elementAt(i)).getId() == getId()) {
                ((BankAccount) allEntries.elementAt(i))
                        .setAccountNumber(accountNumber);
                ((BankAccount) allEntries.elementAt(i)).setBankName(bankName);
                ((BankAccount) allEntries.elementAt(i)).setNickname(nickname);
                ((BankAccount) allEntries.elementAt(i))
                        .setDescription(description);
                ((BankAccount) allEntries.elementAt(i)).setBankCode(bankCode);
                ((BankAccount) allEntries.elementAt(i)).setIBAN(iban);
                ((BankAccount) allEntries.elementAt(i))
                        .setAccountNumber(accountNumber);
                ((BankAccount) allEntries.elementAt(i)).setUser(user);
                ((BankAccount) allEntries.elementAt(i)).setDate( new GregorianCalendar());

                // write down the updated bank account list
                save();
                return;
            }
        }
        throw new AppException("Kein Bankkonto mit der internen ID " + getId()
                + " vorhanden!");

    } // update();

    /**
     * Compares an bank account entry after the internal identifier.
     * 
     * @param o
     *            the BankAccount object to compare
     * @return -1, 0 or 1, see {@link Comparable}
     */
    public int compareTo(Object o) {

        Integer ai = new Integer(((BankAccount) o).getId());
        Integer bi = new Integer(id);

        return bi.compareTo(ai);
    }
    
    /**
     * Print the bank account with all fields separated by '|'.
     * 
     * @return The String that contains all fields.
     * 
     * @see #fromString(String)
     */
    public String toString() {

        return getId() + "|" + cfs(accountNumber) + "|" + cfs(bankName) + "|"
                + cfs(nickname) + "|" + cfs(description) + "|" + cfs(bankCode)
                + "|" + cfs(iban) + "|" + getDateAsString() + "|" + cfs(user);
    }

    /**
     * Get all existing bank accounts in an array, sorted by the account number.
     * 
     * @return An array of all bank account entries.
     * 
     * @throws AppException
     *             in case of problems with reading the bankAccounts file.
     */
    public static BankAccount[] getAllBankAccounts() throws AppException {

        readBankAccounts();

        BankAccount[] aa = new BankAccount[allEntries.size()];
        allEntries.copyInto(aa);

        // sort after account number
        Arrays.sort(aa);

        return aa;
    }

    /**
     * Returns the short bank name for an BankAccount internal identifier.
     * 
     * For the backward compatibility to version 0.1 it gives "?" if the ID is 0
     * and no bank account exists.
     * 
     * @param id
     *            The internal bank account identifier.
     * 
     * @return The short bank name.
     * 
     * @throws AppException
     *             If no such entry exists or in case of read errors.
     */
    public static String getBankAccountNickname(int id) throws AppException {
        String nickname = "?";
        BankAccount bankAccount;

        try {
            bankAccount = getBankAccountById(id);
            nickname = bankAccount.getNickname();
        } catch (Exception e) {
            if (id != 0) {
                throw new AppException(
                        "Kann kein Bankkonto für die interne ID " + id
                                + " finden!" + e);
            }
        }
        return nickname;
    }

    /**
     * Return the BankAccount object for the given internal identifier.
     * 
     * @param id The internal identifier for the BankAccount.
     * 
     * @return The BankAccount found.
     * 
     * @throws AppException If no BankAccount is found for the internal identifier or in case of read errors.
     */
    public static BankAccount getBankAccountById(int id) throws AppException {

        readBankAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            if (((BankAccount) allEntries.elementAt(i)).getId() == id) {
                return (BankAccount) allEntries.elementAt(i);
            }
        }
        throw new AppException("Kann kein Bankkonto mit der ID " + id
                + " finden!");

    } 
    
    /**
     * Return the BankAccount object for the given short bank name.
     * 
     * @param nickname The short bank name for the BankAccount.
     * 
     * @return The BankAccount found.
     * 
     * @throws AppException If no BankAccount is found with this nickname or in case of read errors.
     */
    public static BankAccount getBankAccountByNickname(String nickname)
            throws AppException {

        readBankAccounts();

        for (int i = 0; i < allEntries.size(); ++i) {
            if (((BankAccount) allEntries.elementAt(i)).getNickname().equals(
                    nickname)) {
                return (BankAccount) allEntries.elementAt(i);
            }
        }
        throw new AppException("Kann kein Bankkonto mit der Abkürzung "
                + nickname + " finden!");

    }
    
    /**
     * Read the bank account from a string.
     * 
     * @param from The line, from this the entry should be read.
     * 
     * @return A complete filled BankAccount object.
     * 
     * @throws AppException On parsing errors.
     * 
     * @see #toString()
     */
    private static BankAccount fromString(String from) throws AppException {
        final int TOKENS = 9;
        BankAccount bankAccount = new BankAccount();
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
            bankAccount.id = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AppException("Das erste Feld \"" + token
                    + "\" sollte die interne ID enthalten!");
        }
        // 2
        bankAccount.accountNumber = sfc(tokens.nextToken());
        // 3
        bankAccount.bankName = sfc(tokens.nextToken());
        // 4
        bankAccount.nickname = sfc(tokens.nextToken());
        // 5
        bankAccount.description = sfc(tokens.nextToken());
        // 6
        bankAccount.bankCode = sfc(tokens.nextToken());
        // 7
        bankAccount.iban = sfc(tokens.nextToken());
        // 8
        bankAccount.date = Finance.getGregorianCalendarFromString(tokens
                .nextToken());
        // 9
        bankAccount.user = sfc(tokens.nextToken());

        return bankAccount;

    } // fromString()

    /**
     * Read in the bank accounts list.
     * Not as static initializer to have the posibility to throw exceptions.
     * 
     * @throws AppException In case of read errors.
     */
    private static void readBankAccounts() throws AppException {

        if (allEntries != null) {
            return;
        }

        allEntries = new Vector();
        String line;
        int lineNumber = 0;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(Config
                    .getFileName(BANK_ACCOUNTS)), "UTF-8"));

            while ((line = in.readLine()) != null) {
                ++lineNumber;
                allEntries.add(fromString(line));
            }
        } catch (IOException e) {
            // no exception at initialize
            throw new AppException("Probleme beim Einlesen der Datei \""
                    + Config.getFileName(BANK_ACCOUNTS) + "\" in der Zeile "
                    + lineNumber + ": " + e);
        } catch (AppException ae) {
            throw new AppException("Probleme beim Einlesen der Datei \""
                    + Config.getFileName(BANK_ACCOUNTS) + "\" in der Zeile "
                    + lineNumber + ": " + ae.getMessage());
        }
    }
    
    /**
     * Getter for all bank account entries.
     * @see de.hlu.ust.Finance#getAllEntries()
     */
    protected Vector getAllEntries() {
        return allEntries;
    }
    
    /**
     * Getter for the bank accounts file name.
     * @return The relative file name.
     */
    public String getDataFileName() {
        return BANK_ACCOUNTS;
    }
    
    /** The bank account number */
    private String accountNumber;

    /** The banks long bank. */
    private String bankName;

    /** The banks short name */
    private String nickname;

    /** The description for this BankAccount entry. */
    private String description;

    /** The bank code (German BLZ). */
    private String bankCode;

    /** The International Bank Account Number. */
    private String iban;

    /** The bank accounts file name */
    private static final String BANK_ACCOUNTS = "bankAccounts";
    
    /** To fullfill the {@link Serializable} interface. */
    private static final long serialVersionUID = 1L;
    
    /** All bank account entries. */
    private static Vector allEntries;    
}
