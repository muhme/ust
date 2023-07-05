/*
 * ust - my VAT calculating project
 * Money.java - the VAT calculating projects thinking about money
 * hlu, Apr 1 2000 - $Date: 2008-03-09 15:04:49 +0100 (Sun, 09 Mar 2008) $
 */

package de.hlu.ust;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * The Money class stores the Money value in Cent * 10.
 * <p>
 * The class implements some methods für parsing, printing, rounding and
 * calculating VAT ant net values.
 * <p>
 * There is no check for overrun because the intern presentation as long alows
 * to store 92.233.720.368.547.758 Euros.
 *
 * @author Heiko Lübbe
 */
public class Money {

    /**
     * <code>ROUND_DOWN_TENTH_CENTS</code> rounds the last Cent digit to zero,
     * e.g. "1.17" to "1.10".
     */
    public static final int ROUND_DOWN_TENTH_CENTS = 1;

    /**
     * <code>ROUND_MERCANTILE</code> (kaufmännisches Runden) rounds 1...5 to
     * this digit and 6...9 to the next one. E.g. "1.125" to "1.12"
     */
    public static final int ROUND_MERCANTILE = 2;

    private static DecimalFormat df;
    /**
     * Initialize at class loading the decimal format symbol dfs to present the
     * Money like "1.020,40".
     */
    static {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df = new DecimalFormat("#,###,##0.00", dfs);
    }

    /**
     * Constructor with zero money value.
     */
    public Money() {
        value = 0;
    }

    /**
     * Constructor with the given money value in Cent.
     * 
     * @param cent
     *            The initial money value in Cent.
     */
    public Money(int cent) {
        value = cent * 10;
    }

    /**
     * Constructor with the given money value in Euro and Cent.
     * 
     * @param euro
     *            The initial money value in Euro
     * @param cent
     *            The initial money value in Cent.
     */
    public Money(int euro, int cent) {
        value = (euro * 100 + cent) * 10;
    }

    /**
     * Constructor, which sets the Money value from strings like "1.024,99",
     * "42,00" or "42".
     * 
     * @param money
     *            The money value as string prasentation.
     * @throws AppException
     *             In case of parse errors.
     */
    public Money(String money) throws AppException {
        Number N;
        if (money.indexOf(',') == -1) {
            money += ",00";
        }
        try {
            N = df.parse(money);
        } catch (java.text.ParseException pe) {
            throw new AppException("Der Geldbetrag \"" + money
                    + "\" kann nicht parsiert werden! (" + pe + ")");
        }
        value = N.doubleValue() * 1000;
    }

    /**
     * Add the given cent-value, which can be negative.
     * 
     * @param cent
     *            Cents to add.
     */
    public void add(int cent) {
        value += cent * 10;
    }

    /**
     * Getter for the money value as Cent * 10.
     * 
     * @return The money value as Cent * 10.
     */
    public long getValue() {
        Double D = new Double(value);
        return D.longValue();
    }

    /**
     * Setter for the money value in Cent * 10.
     * 
     * @param value
     *            The money value as Cent * 10.
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * Gets the money value in Cents rounded by the given parameter.
     * 
     * @param round
     *            <code>ROUND_DOWN_TENTH_CENTS</code> or
     *            <code>ROUND_MERCANTILE</code>.
     * @return The money value rounded in Cents.
     */
    public int getInCents(int round) {
        double i = value;
        if (round == ROUND_DOWN_TENTH_CENTS) {
            i -= (i % 100);
        } else if (round == ROUND_MERCANTILE) {
            if (i < 0) {
                if ((-i % 1000) % 10 > 5) {
                    i -= 5;
                }
            } else {
                if ((i % 1000) % 10 > 5) {
                    i += 5;
                }
            }
        }
        Double D = new Double(i / 10);
        return D.intValue();
    }

    /**
     * Gets the money values Euro part.
     * 
     * @return The money values Euro part.
     */
    public int getEuro() {
        Double D = new Double(value / 1000);
        return D.intValue();
    }

    /**
     * Gets the money values Cent part.
     * 
     * @return The money values Cent part.
     */
    public int getCent() {
        Double D = new Double((value % 1000) / 10);
        return D.intValue();
    }

    /**
     * Gets the money value rounded mercantile and added with the currency.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return toString(ROUND_MERCANTILE) + "&nbsp;" + Config.getCurrency();
    }

    /**
     * Get the moneys value as string, rounded by the given parameter.
     * 
     * @param round
     *            <code>ROUND_DOWN_TENTH_CENTS</code> or
     *            <code>ROUND_MERCANTILE</code>.
     * @return The money value.
     */
    public String toString(int round) {
        return df.format((double)this.getInCents(round) / 100);
    }

    /**
     * Calculate the net value from a given gross value and the VAT percents and
     * return the result as int.
     * 
     * @param gross
     *            The gross value in Cent.
     * @param vatPercent
     *            The value added tax percents.
     * @return The net money value in Cents.
     */
    public static int calcNet(int gross, double vatPercent) {
        Money m = new Money();
        if (vatPercent == 100.0) {
            // 100 % VAT means only VAT
            m.setValue(0);
        } else {
            Double D = new Double((double) gross * 1000 / (100 + vatPercent));
            m.setValue(D.longValue());
        }
        return m.getInCents(ROUND_MERCANTILE);
    }

    /**
     * Calculate the net value from a given gross value and VAT percents and
     * return the result as string.
     * 
     * @param gross
     *            The gross value in Cent.
     * @param vatPercent
     *            The value added tax percents.
     * @return The net money value e.g. "19,20"
     */
    public static String calcNetAsString(int gross, double vatPercent) {
        Money m = new Money(calcNet(gross, vatPercent));
        return m.toString();
    }

    /**
     * Calculate the value added tax value from a given gross value and the VAT
     * percents and return the result as int.
     * 
     * @param gross
     *            The gross value in Cent.
     * @param vatPercent
     *            The value added tax percents.
     * @return The VAT money value in Cents.
     */
    public static int calcVat(int gross, double vatPercent) {
        Money m = new Money(gross - calcNet(gross, vatPercent));
        return m.getInCents(ROUND_MERCANTILE);
    }

    /**
     * Calculate the value added tax value from a given gross value and VAT
     * percents and return the result as string.
     * 
     * @param gross
     *            The gross value in Cent.
     * @param vatPercent
     *            The value added tax percents.
     * @return The VAT money value e.g. "19,20"
     */
    public static String calcVatAsString(int gross, double vatPercent) {
        Money m = new Money(calcVat(gross, vatPercent));
        return m.toString();
    }

    // This money value in Cent * 10.
    private double value;

    /**
     * some test cases
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            String currency = "&nbsp;" + Config.getCurrency();
            // cases 1 - 5
            Money money = new Money(999);
            test(money.toString().equals("9,99" + currency));
            test(money.getValue() == 9990);
            test(money.getInCents(ROUND_MERCANTILE) == 999);
            test(money.toString(ROUND_MERCANTILE).equals("9,99"));
            test(money.toString(ROUND_DOWN_TENTH_CENTS).equals("9,90"));
            // cases 6 - 9
            test(Money.calcVat(11600, 16) == 1600);
            test(Money.calcNet(11600, 16) == 10000);
            test(Money.calcVatAsString(11600, 16).equals(
                    "16,00" + currency));
            test(Money.calcNetAsString(11600, 16).equals(
                    "100,00" + currency));
            // cases 10 - 12 (inserting one zero in the Cents if cents < 10)
            money = new Money(4805);
            test(money.toString().equals("48,05" + currency));
            test(Money.calcNetAsString(4805, 16).equals(
                    "41,42" + currency));
            test(Money.calcVatAsString(4805, 16).equals(
                    "6,63" + currency));
            // cases 13 - 14 (greater thousend e.g. 32.010,99)
            money = new Money(3201099);
            test(money.toString().equals("32.010,99" + currency));
            money = new Money("32010,99" + currency);
            test(money.toString().equals("32.010,99" + currency));
            // cases 15 - 16 (test String with and w/o Cents)
            money = new Money("42");
            test(money.getValue() == 42000);
            money = new Money("42,00 " + Config.getCurrency());
            test(money.getValue() == 42000);
            // cases 17 - 19 (test large numbers)
            money = new Money(4640000);
            test(money.toString().equals("46.400,00" + currency));
            test(Money.calcNetAsString(4640000, 16).equals(
                    "40.000,00" + currency));
            test(Money.calcVatAsString(4640000, 16).equals(
                    "6.400,00" + currency));
            // case 8,19 as input gives 8,20 back
            money = new Money(819);
            test(money.toString().equals("8,19" + currency));
            money = new Money("8,19");
            test(money.toString().equals("8,19" + currency));
            money = new Money("-8,19");
            test(money.toString().equals("-8,19" + currency));
            System.out.println(money.toString());
            System.out.println(money.value);
            System.out.println(money.value / 100);
            System.out.println(money.getInCents(2));
            System.out.println((double) money.getInCents(2));
        } catch (AppException ae) {
            System.err.println(ae);
        }
    } // main()

    private static int testCase = 0; // test case counter

    /**
     * check that this test is true increment the test case number, give ok or
     * failed message
     * 
     * @param result
     */
    private static void test(boolean result) {
        ++testCase;
        if (result) {
            System.out.println("case " + testCase + " ok");
        } else {
            System.out.println("case " + testCase + " FAILED!");
        }
    }
}
