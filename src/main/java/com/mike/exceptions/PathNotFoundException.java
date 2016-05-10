package com.mike.exceptions;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class PathNotFoundException extends Exception {

    public PathNotFoundException(String path) {
        super("Path not found for path " + path );
    }
}
