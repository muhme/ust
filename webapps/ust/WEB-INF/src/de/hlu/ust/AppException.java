/*
 * AppException.java - an exception as logical application error representation 
 *
 * ust web application, Copyright (c) 2008 Heiko Lübbe, MIT License, https://github.com/muhme/ust
 */

package de.hlu.ust;

/**
 * AppException is an logical error (means the user is giving some wrong input)
 * exception
 *
 * @author Heiko Lübbe
 */
public class AppException extends Exception {

    /**
     * The one and only Constructor.
     * 
     * @param msg The logical error message.
     */
    public AppException(String msg) {
        super(msg);
    }

    /**
     * One simple test.
     * 
     * @param args Unused program arguments.
     */
    public static void main(String[] args) {
        try {
            throw new AppException("test (thrown in try-block)");
        } catch (AppException a) {
            System.out.println("AppException: " + a);
        }
    }
    
    /** For serialization. */
    private static final long serialVersionUID = 1L;
}
