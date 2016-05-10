package com.mike.exceptions;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class TextFileException extends Exception {

    public TextFileException() {
        super("Not a text file");
    }

    public TextFileException(String s) {
        super(s);
    }
}
