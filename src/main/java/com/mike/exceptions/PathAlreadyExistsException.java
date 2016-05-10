package com.mike.exceptions;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class PathAlreadyExistsException extends Exception {

    public PathAlreadyExistsException(String path) {
        super("Path already exists for path " + path);
    }

}
