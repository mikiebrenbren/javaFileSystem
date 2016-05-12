package com.mike.service;

import com.mike.enums.Crud;
import com.mike.enums.OsType;
import com.mike.exceptions.IllegalFileSystemOperationException;
import com.mike.exceptions.PathAlreadyExistsException;
import com.mike.exceptions.PathNotFoundException;
import com.mike.exceptions.TextFileException;
import com.mike.file.system.*;
import com.mike.util.PathUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by michaelbrennan on 5/7/16.
 */
public class OperatingSystemServiceImpl implements OperatingSystemService {

    private final Logger lgr = Logger.getLogger(this.getClass().getName());
    private final OsValidator validator = new OsValidator();
    private final PathUtil pathUtil = new PathUtil();

    @Override
    public void create(String name, OsType type, String pathOfParent, Map<String, Drive> drives) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException {
        nullCheckDrives(drives);
        if (type.equals(OsType.DRIVE)) {
            if (StringUtils.isNotEmpty(pathOfParent)) {
                throw new IllegalFileSystemOperationException("Drives can only be created at the root level of the file system, please check your path");
            }
            putDrive(pathOfParent, name, drives);
        } else {
            validator.validatePathString(pathOfParent);
            createEntity(drives, pathOfParent, name, type, Crud.CREATE);
        }

    }

    @Override
    public void delete(String name, OsType type, String path, Map<String, Drive> drives) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException {
        nullCheckDrives(drives);

        if (type.equals(OsType.DRIVE)) {
            if (StringUtils.isNotEmpty(path)) {  //null value must be passed in for the path to be delete drive
                throw new IllegalFileSystemOperationException("Drives can only be delete at the root level of the file system, please check your path");
            }
            deleteDrive(name, drives);
        } else {
            validator.validatePathString(path);
            deleteEntity(drives, path, name, type, Crud.DELETE);
        }

    }

    @Override
    public void move(String name, OsType type, String pathOfParent, String destinationPath, Map<String, Drive> drives, List<String> sourcePathAsList) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException {
        nullCheckDrives(drives);
        TextFile textFile;
        ZipFile zipFile;
        Folder folderToMove;
        Folder folder = getCurrentFolder(drives, pathOfParent, sourcePathAsList);
        switch (type) {
            case FOLDER:
                folderToMove = (Folder) navigateMove(sourcePathAsList.subList(1, sourcePathAsList.size()), folder, name, pathOfParent, 0, OsType.FOLDER, null);
                sendToDestination(destinationPath, drives, folderToMove, type, name);
                delete(name, type, pathOfParent, drives);
                break;
            case ZIPFILE:
                zipFile = (ZipFile) navigateMove(sourcePathAsList.subList(1, sourcePathAsList.size()), folder, name, pathOfParent, 0, OsType.ZIPFILE, null);
                sendToDestination(destinationPath, drives, zipFile, type, name);
                delete(FilenameUtils.removeExtension(name), type, pathOfParent, drives);
                break;
            case TEXTFILE:
                textFile = (TextFile) navigateMove(sourcePathAsList.subList(1, sourcePathAsList.size()), folder, name, pathOfParent, 0, OsType.TEXTFILE, null);
                sendToDestination(destinationPath, drives, textFile, type, name);
                delete(FilenameUtils.removeExtension(name), type, pathOfParent, drives);
                break;
        }
    }

    @Override
    public void writeTofile(String name, String pathOfParent, Map<String, Drive> drives, List<String> pathAsList, String content) throws PathNotFoundException, PathAlreadyExistsException, IllegalFileSystemOperationException, TextFileException {
        nullCheckDrives(drives);

        if (pathAsList.size() == 1) {
            Map<String, TextFile> textFiles = drives.get(pathAsList.get(0)).getTextFiles();
            doCrudTypeOnOsType(pathOfParent, name, textFiles, OsType.TEXTFILE, Crud.WRITETOFILE, content);
        } else {
            getCurrentNodeBeginNav(drives, pathOfParent, name, OsType.TEXTFILE, Crud.WRITETOFILE, pathAsList, content);
        }
    }

    public void sendToDestination(String destinationPath, Map<String, Drive> drives, FsGen mft, OsType type, String name) throws IllegalFileSystemOperationException, PathNotFoundException, PathAlreadyExistsException, TextFileException {
        List<String> destinationPathAsList = pathUtil.getPathAsList(destinationPath);
        if (destinationPathAsList.size() == 1) {
            Drive drive = drives.get(destinationPathAsList.get(0));
            if (drive == null) {
                throw new PathNotFoundException("Path destination does not exists");
            }
            if (type == OsType.TEXTFILE) {
                if (drive.getTextFiles().containsKey(name)) {
                    throw new PathAlreadyExistsException("text file already exists");
                } else {
                    drive.getTextFiles().put(name, (TextFile) mft);
                }
            } else if (type == OsType.ZIPFILE) {
                if (drive.getZipFiles().containsKey(name)) {
                    throw new PathAlreadyExistsException("zip file already exists");
                } else {
                    drive.getZipFiles().put(name, (ZipFile) mft);
                }
            } else if (type == OsType.FOLDER) {
                if (drive.getFolders().containsKey(name)) {
                    throw new PathAlreadyExistsException(" folder already exists");
                } else {
                    drive.getFolders().put(name, (Folder) mft);
                }
            }
        } else {
            Folder currentFolder = getCurrentFolder(drives, destinationPath, destinationPathAsList);
            navigateMove(destinationPathAsList.subList(1, destinationPathAsList.size()), currentFolder, name, destinationPath, 0, type, mft);
        }
    }

    /**
     * Recursive method that navigates file system
     *
     * @param pathAsList
     * @param folder
     * @param name
     * @param path
     * @param iterator
     * @param mft
     * @return
     * @throws PathAlreadyExistsException
     * @throws PathNotFoundException
     */
    private FsGen navigateMove(List<String> pathAsList, Folder folder, String name, String path, int iterator, OsType type, FsGen mft) throws PathAlreadyExistsException, PathNotFoundException, TextFileException {

        //if the iteration is on the last node then create file, folder, or drive and return
        if (iterator == pathAsList.size() - 1) {
            switch (type) {
                case FOLDER:
                    if (mft != null) {
                        moveFolder(folder, name, path, type, mft);
                        return null;
                    } else {
                        return folder.getFolders().get(name);
                    }
                case ZIPFILE:
                    if (mft != null) {
                        moveZip(folder, name, path, type, mft);
                        return null;
                    } else {
                        return folder.getZipFiles().get(name);
                    }
                case TEXTFILE:
                    if (mft != null) {
                        moveText(folder, name, path, type, mft);
                        return null;
                    } else {
                        return folder.getTextFiles().get(name);
                    }
            }
        }

        //get the current folder name
        String nextFolderName = pathAsList.get(iterator + 1);  //get the next folder name
        if (nextFolderName != null) {
            folder = folder.getFolders().get(nextFolderName);
        }
        if (folder == null) {  //at this point, the folder should exist
            throw new PathNotFoundException(path);
        }

        return navigateMove(pathAsList, folder, name, path, ++iterator, type, mft);
    }

    private void moveFolder(Folder currentFolder, String name, String path, OsType type, FsGen content) throws PathAlreadyExistsException, PathNotFoundException, TextFileException {
        if (currentFolder.getFolders().containsKey(name)) {
            throw new PathAlreadyExistsException("path already exists for destination");
        }
        Folder content1 = (Folder) content;
        currentFolder.setPath(path);
        currentFolder.getFolders().put(name, content1);
    }

    private void moveZip(Folder currentFolder, String name, String path, OsType type, FsGen content) throws PathAlreadyExistsException, PathNotFoundException, TextFileException {
        if (currentFolder.getZipFiles().containsKey(name)) {
            throw new PathAlreadyExistsException("path already exists for destination");
        }
        ZipFile zipFile = (ZipFile) content;
        zipFile.setPath(path);
        currentFolder.getZipFiles().put(name, (ZipFile) content);
    }

    private void moveText(Folder folder, String name, String path, OsType type, FsGen content) throws PathAlreadyExistsException, PathNotFoundException, TextFileException {
        if (folder.getTextFiles().containsKey(name)) {
            throw new PathAlreadyExistsException("path already exists for destination");
        }
        TextFile textFile = (TextFile) content;
        textFile.setPath(path);
        folder.getTextFiles().put(name, (TextFile) content);
    }


    private void deleteEntity(Map<String, Drive> drives, String path, String name, OsType type, Crud crudType) throws IllegalFileSystemOperationException, PathNotFoundException, PathAlreadyExistsException, TextFileException {

        validator.validatePathAndName(path, name);
        List<String> pathAsList = pathUtil.getPathAsList(path);
        Drive currentDrive = null;

        if (pathAsList.size() == 1) { //we know we are deleting something at root level in drive,
            if (OsType.FOLDER.equals(type)) {
                currentDrive = getCurrentDrive(drives, path, pathAsList);  //if current drive is null throw exception
                doCrudTypeOnOsType(path, name, currentDrive.getFolders(), type, crudType, null);
            } else if (OsType.ZIPFILE.equals(type)) {
                doCrudTypeOnOsType(path, name, currentDrive.getZipFiles(), type, crudType, null);
            } else if (OsType.TEXTFILE.equals(type)) {
                doCrudTypeOnOsType(path, name, currentDrive.getTextFiles(), type, crudType, null);
            }
        } else {
            getCurrentNodeBeginNav(drives, path, name, type, crudType, pathAsList, null);
        }


    }

    private void createEntity(Map<String, Drive> drives, String path, String name, OsType type, Crud crudType) throws IllegalFileSystemOperationException, PathNotFoundException, PathAlreadyExistsException, TextFileException {
        List<String> pathAsList = pathUtil.getPathAsList(path);
        Drive currentDrive;

        if (pathAsList.size() == 1) { //we know we are creating something at root level in drive
            currentDrive = getCurrentDrive(drives, path, pathAsList);  //if current drive is null throw exception
            //if the size of the drive is empty and the path is at root in the drive then add file or folder to drive if not contained
            if (OsType.FOLDER.equals(type)) {
                doCrudTypeOnOsType(path, name, currentDrive.getFolders(), type, crudType, null);
            } else if (OsType.ZIPFILE.equals(type)) {
                doCrudTypeOnOsType(path, name, currentDrive.getZipFiles(), type, crudType, null);
            } else if (OsType.TEXTFILE.equals(type)) {
                doCrudTypeOnOsType(path, name, currentDrive.getTextFiles(), type, crudType, null);
            }

        } else {//if drive is not empty begin navigation through the folders
            getCurrentNodeBeginNav(drives, path, name, type, crudType, pathAsList, null);
        }
    }

    /**
     * gets current folder
     *
     * @param drives
     * @param path
     * @param pathAsList
     * @throws PathNotFoundException
     * @throws PathAlreadyExistsException
     * @throws TextFileException
     */
    private Folder getCurrentFolder(Map<String, Drive> drives, String path, List<String> pathAsList) throws PathNotFoundException, PathAlreadyExistsException, TextFileException {
        Drive currentDrive;
        currentDrive = getCurrentDrive(drives, path, pathAsList);  //get the current drive
        String currentNode = pathAsList.get(1);
        if (!currentDrive.getFolders().containsKey(currentNode)) { //there must be at least one folder
            throw new PathNotFoundException(path);
        }
        return currentDrive.getFolders().get(currentNode); //get the current folder
    }

    /**
     * begin navigation through file system
     *
     * @param drives
     * @param path
     * @param name
     * @param type
     * @param crudType
     * @param pathAsList
     * @param content    -- this will be null unless writing to file
     * @throws PathNotFoundException
     * @throws PathAlreadyExistsException
     * @throws TextFileException
     */
    private void getCurrentNodeBeginNav(Map<String, Drive> drives, String path, String name, OsType type, Crud crudType, List<String> pathAsList, String content) throws PathNotFoundException, PathAlreadyExistsException, TextFileException {
        Folder currentFolder = getCurrentFolder(drives, path, pathAsList); //get the current folder
        navigate(pathAsList.subList(1, pathAsList.size()), currentFolder, name, path, 0, type, crudType, content);
    }


    /**
     * Recursive method that navigates file system
     *
     * @param pathAsList
     * @param folder
     * @param name
     * @param path
     * @param iterator
     * @return
     * @throws PathAlreadyExistsException
     * @throws PathNotFoundException
     */
    private int navigate(List<String> pathAsList, Folder folder, String name, String path, int iterator, OsType type, Crud crudType, String content) throws PathAlreadyExistsException, PathNotFoundException, TextFileException {

        //if the iteration is on the last node then create file, folder, or drive and return
        if (iterator == pathAsList.size() - 1) {
            doByType(folder, name, path, type, crudType, content);
            return 0;
        }

        //get the current folder name
        String nextFolderName = pathAsList.get(iterator + 1);  //get the next folder name
        if (nextFolderName != null) {
            folder = folder.getFolders().get(nextFolderName);
        }
        if (folder == null) {  //at this point, the folder should exist
            throw new PathNotFoundException(path);
        }

        return navigate(pathAsList, folder, name, path, ++iterator, type, crudType, content);
    }

    private void doByType(Folder folder, String name, String path, OsType type, Crud crud, String content) throws PathAlreadyExistsException, PathNotFoundException, TextFileException {
        switch (type) {
            case FOLDER:
                doCrudTypeOnOsType(path, name, folder.getFolders(), type, crud, content);
                break;
            case ZIPFILE:
                doCrudTypeOnOsType(path, name, folder.getZipFiles(), type, crud, content);
                break;
            case TEXTFILE:
                doCrudTypeOnOsType(path, name, folder.getTextFiles(), type, crud, content);
        }
    }

    private void doCrudTypeOnOsType(String path, String name, Map<String, ? extends FsGen> current, OsType type, Crud crud, String content) throws PathAlreadyExistsException, PathNotFoundException, TextFileException {

        switch (type) {
            case FOLDER:
                switch (crud) {
                    case CREATE:
                        putFolder(path, name, (Map<String, Folder>) current);
                        break;
                    case DELETE:
                        deleteFolder(name, (Map<String, Folder>) current);
                        break;
                }
                break;
            case ZIPFILE:
                switch (crud) {
                    case CREATE:
                        putZipFile(path, name, (Map<String, ZipFile>) current);
                        break;
                    case DELETE:
                        deleteZip(name, (Map<String, ZipFile>) current);
                        break;
                }
                break;
            case TEXTFILE:
                switch (crud) {
                    case CREATE:
                        putTextFile(path, name, (Map<String, TextFile>) current);
                        break;
                    case DELETE:
                        deleteTextFile(name, (Map<String, TextFile>) current);
                        break;
                    case WRITETOFILE:
                        addContentIfFileExists(path, name, (Map<String, TextFile>) current, content);
                        break;
                }
        }
    }

    private void addContentIfFileExists(String path, String name, Map<String, TextFile> current, String content) throws TextFileException {
        if (!current.containsKey(name)) {
            throw new TextFileException("cannot write content, text file does not exist");
        }
        TextFile textFile = current.get(name);
        textFile.setContent(content);
        lgr.info("added content " + "\"" + content + "\"" + " to file " + "\"" + name + "\"");
    }

    private void deleteDrive(String name, Map<String, Drive> drives) throws PathNotFoundException {
        if (!drives.containsKey(name)) {
            throw new PathNotFoundException("deletion failed, drive does not exist in given path");
        }
        Drive drive = drives.get(name);
        drive.deleteAll();
        drives.remove(name);
        lgr.info("folder deletion successful");
    }

    private void deleteFolder(String name, Map<String, Folder> current) throws PathNotFoundException {
        if (!current.containsKey(name)) {
            throw new PathNotFoundException("deletion failed, folder does not exist in given path");
        }
        Folder folder = current.get(name);
        folder.deleteAll();
        current.remove(name);
        lgr.info("folder deletion successful");
    }

    private void deleteZip(String name, Map<String, ZipFile> current) throws PathNotFoundException {
        if (!current.containsKey(name)) {
            throw new PathNotFoundException("deletion failed, zip does not exist in given path");
        }
        ZipFile zipFile = current.get(name);
        zipFile.deleteAll();
        current.remove(name);
        lgr.info("zip deletion successful");
    }

    private void deleteTextFile(String name, Map<String, TextFile> current) throws PathNotFoundException {
        if (!current.containsKey(name)) {
            throw new PathNotFoundException("deletion failed, zip does not exist in given path");
        }
        current.remove(name);
        lgr.info("folder deletion successful");
    }

    private void putDrive(String path, String name, Map<String, Drive> drives) throws PathAlreadyExistsException {
        if (drives.containsKey(name)) {
            throw new PathAlreadyExistsException("drive name already exists ");
        }
        Drive drive = new Drive();
        drive.setPath(path);
        drive.setName(name);
        drives.put(name, drive);
        lgr.info("drive created with name " + name);
    }

    private void putFolder(String path, String name, Map<String, Folder> current) throws PathAlreadyExistsException {
        if (current.containsKey(name)) {
            throw new PathAlreadyExistsException("folder already exists in given path");
        }
        Folder folder = new Folder();
        folder.setPath(path);
        folder.setName(name);
        current.put(name, folder);
        lgr.info("folder created with name " + name);
    }


    private void putZipFile(String path, String name, Map<String, ZipFile> current) throws PathAlreadyExistsException {
        if (current.containsKey(name)) {
            throw new PathAlreadyExistsException("zip file already exists in given path");
        }
        ZipFile zipFile = new ZipFile();
        zipFile.setPath(path);
        zipFile.setName(name);
        current.put(name, zipFile);
        lgr.info("zip file created with name " + name);
    }

    private void putTextFile(String path, String name, Map<String, TextFile> current) throws PathAlreadyExistsException {
        if (current.containsKey(name)) {
            throw new PathAlreadyExistsException("text file already exists in given path");
        }
        TextFile textFile = new TextFile(name, path);
        current.put(name, textFile);
        lgr.info("text file created with name " + name);
    }

    /**
     * Gets the current drive from the path
     *
     * @param drives
     * @param path
     * @param pathAsList
     * @return
     * @throws PathNotFoundException
     */
    private Drive getCurrentDrive(Map<String, Drive> drives, String path, List<String> pathAsList) throws PathNotFoundException {
        Drive currentDrive = new Drive();
        if (drives.containsKey(pathAsList.get(0))) {
            currentDrive = drives.get(pathAsList.get(0)); //gets the current drive from the first item off of the path list
        }
        if (currentDrive == null) { //if the current drive is never found then the path is invalid
            throw new PathNotFoundException(path);
        }
        return currentDrive;
    }

    private void nullCheckDrives(Map<String, Drive> drives) throws IllegalFileSystemOperationException {
        if (drives == null) {
            lgr.log(Level.SEVERE, "Drive cannot be null");
            throw new IllegalFileSystemOperationException("Drives cannot be null");
        }
    }


    @Override
    public OsType typeExtractor(Class<FsGen> mft) {

        if (mft.getName().equals(Drive.class.getName())) {
            return OsType.DRIVE;
        } else if (mft.getName().equals(Folder.class.getName())) {
            return OsType.FOLDER;
        } else if (mft.getName().equals(ZipFile.class.getName())) {
            return OsType.ZIPFILE;
        } else if (mft.getName().equals(TextFile.class.getName())) {
            return OsType.TEXTFILE;
        }

        return null;
    }
}
