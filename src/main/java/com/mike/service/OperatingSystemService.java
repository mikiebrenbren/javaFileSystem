package com.mike.service;

import com.mike.enums.OsType;
import com.mike.exceptions.IllegalFileSystemOperationException;
import com.mike.exceptions.PathAlreadyExistsException;
import com.mike.exceptions.PathNotFoundException;
import com.mike.exceptions.TextFileException;
import com.mike.file.system.Drive;
import com.mike.file.system.FsGen;

import java.util.List;
import java.util.Map;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public interface OperatingSystemService {

    void create(String name, OsType type, String pathOfParent, Map<String, Drive> drives) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException;

    void delete(String name, OsType type, String pathOfParent, Map<String, Drive> drives) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException;

    void move(String name, OsType type, String pathOfParent, String destinationPath, Map<String, Drive> drives, List<String> sourcePathAsList) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException;

    void writeTofile(String name, String pathOfParent, Map<String, Drive> drives, List<String> pathAsString, String content) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException;

    public OsType typeExtractor(Class<FsGen> mft);

}
