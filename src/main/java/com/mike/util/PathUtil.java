package com.mike.util;

import com.mike.exceptions.IllegalFileSystemOperationException;
import com.mike.file.system.OsValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class PathUtil {

    private OsValidator validator = new OsValidator();

    /**
     * Gets path string and puts it into a list
     *
     * @param path
     * @return
     * @throws IllegalFileSystemOperationException
     */
    public List<String> getPathAsList(String path) throws IllegalFileSystemOperationException {
        if (path == null) {
            throw new IllegalFileSystemOperationException("path cannot be null");
        }
        path = removeBeginningOrTrailingSlashes(path);

        List<String> pathList = new LinkedList<>(Arrays.asList(path.split("/")));

        for (String s : pathList) {
            validator.validatePathNode(s);
        }

        return pathList;
    }

    /**
     * checks to see if path contains trailing slashes if so it removes them
     * @param path
     * @return
     * @throws IllegalFileSystemOperationException
     */
    public String removeBeginningOrTrailingSlashes(String path) throws IllegalFileSystemOperationException {

        if (path == null) {
            throw new IllegalFileSystemOperationException("path cannot be null");
        }
        if (path.startsWith("/")) {
            path = StringUtils.removeStart(path, "/");
        }
        if (path.endsWith("/")) {
            path = StringUtils.removeEnd(path, "/");
        }
        return path;
    }

}
