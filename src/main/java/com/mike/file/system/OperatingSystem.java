package com.mike.file.system;

import com.mike.enums.OsType;
import com.mike.exceptions.IllegalFileSystemOperationException;
import com.mike.exceptions.PathAlreadyExistsException;
import com.mike.exceptions.PathNotFoundException;
import com.mike.exceptions.TextFileException;
import com.mike.service.OperatingSystemService;
import com.mike.service.OperatingSystemServiceImpl;
import com.mike.util.PathUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class OperatingSystem {

    private final Logger lgr = Logger.getLogger(this.getClass().getName());

    private final OperatingSystemService systemService;
    private final OsValidator validator;
    private Map<String, Drive> drives;
    private final PathUtil pathUtil = new PathUtil();
    private static final String TXT = ".txt";
    private static final String ZIP = ".zip";

    public OperatingSystem() {
        this.validator = new OsValidator();
        this.systemService = new OperatingSystemServiceImpl();
        drives = new HashMap<>();
    }

    public Map<String, Drive> getDrives() {
        return drives;
    }

    void setDrives(HashMap<String, Drive> drives) {
        this.drives = drives;
    }

    public void create(String name, Class<FsGen> mft, String pathOfParent) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException {
        OsType type = systemService.typeExtractor(mft);
        validator.globalValidate(drives, type, pathOfParent, name);

        systemService.create(name, type, pathOfParent, drives);

        lgr.info("Created " + systemService.typeExtractor(mft) + " \"" + name + "\" successfully.");

    }

    public void delete(String path) throws PathNotFoundException, IllegalFileSystemOperationException, PathAlreadyExistsException, TextFileException {
        if (path == null) {
            throw new IllegalFileSystemOperationException("Please enter path to delete, path cannot be null");
        }
        validator.validatePathString(path);
        List<String> pathAsList = pathUtil.getPathAsList(path);
        String fileName;
        String folderName;
        String desiredPath;
        if (pathAsList.size() > 1) {//if path list is greater than 1 we know that it is either a zip, txt, or folder
            String fileOrFoldertoDelete = pathAsList.get(pathAsList.size() - 1);
            if (fileOrFoldertoDelete.endsWith(TXT)) {
                fileName = StringUtils.removeEnd(fileOrFoldertoDelete, TXT);
                desiredPath = StringUtils.removeEnd(path, fileName + TXT);
                systemService.delete(fileName, OsType.TEXTFILE, desiredPath, drives);
            } else if (fileOrFoldertoDelete.endsWith(ZIP)) {
                fileName = StringUtils.removeEnd(fileOrFoldertoDelete, ZIP);
                desiredPath = StringUtils.removeEnd(path, fileName + ZIP);
                systemService.delete(fileName, OsType.ZIPFILE, desiredPath, drives);
            } else if (StringUtils.isEmpty(FilenameUtils.getExtension(path))) {
                folderName = StringUtils.substringAfterLast(path, "/");
                desiredPath = StringUtils.removeEnd(path, "/" + folderName);
                systemService.delete(folderName, OsType.FOLDER, desiredPath, drives);
            } else {//no other extension are allowed
                throw new IllegalFileSystemOperationException("Invalid path");
            }
        } else {  //if there is only one node in the path we know that we are creating a drive
            systemService.delete(path, OsType.DRIVE, null, drives);
        }

    }

    public void move(String sourcePath, String destination) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException {
        boolean destinationHasExtension = StringUtils.isNotEmpty(FilenameUtils.getExtension(destination));
        if (destinationHasExtension) {  //destination cannot be a file, must be a folder or drive
            throw new IllegalFileSystemOperationException("Path is not valid, Please select a folder or drive to navigation into");
        } else {
            List<String> sourcePathAsList = pathUtil.getPathAsList(sourcePath);  //get the path as a list
            if (sourcePathAsList.size() == 1) {
                throw new IllegalFileSystemOperationException("cannot move drive");  //make sure the list is greater than 1
            }
            if (pathUtil.removeBeginningOrTrailingSlashes(sourcePath).equals(pathUtil.removeBeginningOrTrailingSlashes(destination))) {//if the paths are equal then do nothing
                if (destination.equals(sourcePath)) {
                    lgr.info("Destination and source path are equal, file or folder will remain");
                }
            } else {
                String extension = FilenameUtils.getExtension(sourcePath);
                extension = StringUtils.isEmpty(extension) ? "folder" : extension;  //if there is not extension then it must be a folder
                String fileFolderName = sourcePathAsList.get(sourcePathAsList.size() - 1);
                sourcePathAsList.remove(sourcePathAsList.size() - 1);
                switch (extension) {
                    case "txt":
                        systemService.move(StringUtils.removeEnd(fileFolderName, ".txt"), OsType.TEXTFILE, StringUtils.substringBeforeLast(sourcePath, "/"), destination, drives, sourcePathAsList);
                        break;
                    case "zip":
                        systemService.move(StringUtils.removeEnd(fileFolderName, ".zip"), OsType.ZIPFILE, StringUtils.substringBeforeLast(sourcePath, "/"), destination, drives, sourcePathAsList);
                        break;
                    default:
                        systemService.move(StringUtils.substringAfterLast(sourcePath, "/"), OsType.FOLDER, StringUtils.substringBeforeLast(sourcePath, "/"), destination, drives, sourcePathAsList);
                }
            }
        }

    }

    public void writeTofile(String path, String content) throws PathNotFoundException, TextFileException, IllegalFileSystemOperationException, PathAlreadyExistsException {

        if (path == null || content == null) {
            throw new TextFileException("path or content cannot be null");
        }
        path = pathUtil.removeBeginningOrTrailingSlashes(path);
        validator.validatePathString(path);
        List<String> pathAsList = pathUtil.getPathAsList(path);
        if (pathAsList.size() < 2) {
            throw new TextFileException("invalid path");
        }

        String textfile = FilenameUtils.getExtension(path);
        pathAsList.remove(pathAsList.size() - 1);
        if (StringUtils.isNotEmpty(textfile) && textfile.equals("txt")) {
            systemService.writeTofile(StringUtils.removeEnd(StringUtils.substringAfterLast(path, "/"), ".txt"), StringUtils.substringBeforeLast(path, "/"), drives, pathAsList, content);
        } else {
            throw new TextFileException("invalid input to create text file");
        }

    }


}
