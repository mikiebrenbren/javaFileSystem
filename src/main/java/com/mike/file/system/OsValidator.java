package com.mike.file.system;

import com.mike.enums.OsType;
import com.mike.exceptions.IllegalFileSystemOperationException;
import com.mike.util.PathUtil;
import com.sun.javafx.scene.shape.PathUtils;
import javafx.scene.shape.Path;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class OsValidator {

    private static final String DRIVE_ERR = "You must create a drive before performing any other operating system procedures";

    /**
     * Validation for all operating system procedures
     * @param d
     * @param type
     * @throws IllegalFileSystemOperationException
     */
    public void globalValidate(Map<String, Drive> d, OsType type, String path, String name) throws IllegalFileSystemOperationException {

        //drive, path, or name cannot be null, if the drive is empty then the only type you can create is a drive type
        if (d == null || path == null || name == null || (d.isEmpty() && !OsType.DRIVE.equals(type))) {

            throw new IllegalFileSystemOperationException(DRIVE_ERR);
        }

        PathUtil pathutil = new PathUtil();
        List<String> pathList = pathutil.getPathAsList(path);

        if (pathList.size() > 1 && !OsType.DRIVE.equals(type)) {
            throw new IllegalFileSystemOperationException(DRIVE_ERR);
        }

    }

    /**
     * Validates string, ensures string is alphanumeric with underscores or dots
     * @param s
     * @throws IllegalFileSystemOperationException
     */
    public void validatePathNode(String s) throws IllegalFileSystemOperationException {
        if (!s.matches("^[A-Za-z0-9_.]+$")) {
            throw new IllegalFileSystemOperationException("path can only contain alphanumeric characters and underscores");
        }
    }

    /**
     * Validates full path string
     * @param s
     * @throws IllegalFileSystemOperationException
     */
    public void validatePathString(String s) throws IllegalFileSystemOperationException {
        if (!s.matches("^[A-Za-z0-9_./]+$")) {
            throw new IllegalFileSystemOperationException("path can only contain alphanumeric characters and underscores");
        }
    }

    /**
     * validates zip file name
     * @param fileName
     * @throws IllegalFileSystemOperationException
     */
    public void validateFileName(String fileName) throws IllegalFileSystemOperationException {
        validatePathNode(fileName);
    }

    /**
     * Validates full path string and desired file name
     * @param path
     * @param name
     * @throws IllegalFileSystemOperationException
     */
    public void validatePathAndName(String path, String name) throws IllegalFileSystemOperationException {
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(name)) {
            throw new IllegalFileSystemOperationException("Path and name cannot be bull");
        }

        validatePathString(path);
        validateFileName(name);

    }

//    public void validateMovingPath(String path, String destination) throws IllegalFileSystemOperationException {
//        if (path == null || destination == null) {
//            throw new IllegalFileSystemOperationException("Current or destinatinon path cannot be null");
//        }
//        if (StringUtils.con)
//    }
}
