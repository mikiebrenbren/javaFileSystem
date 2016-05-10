package com.mike.exceptions;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class IllegalFileSystemOperationException extends Exception {

    public IllegalFileSystemOperationException(String message) {
        super(message);
    }

    public IllegalFileSystemOperationException() {
        super("File System Operation Error");
    }
}
