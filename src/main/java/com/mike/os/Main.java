package com.mike.os;

import com.mike.enums.OsType;
import com.mike.exceptions.IllegalFileSystemOperationException;
import com.mike.exceptions.PathAlreadyExistsException;
import com.mike.exceptions.PathNotFoundException;
import com.mike.exceptions.TextFileException;
import com.mike.file.system.OperatingSystem;
import com.oracle.tools.packager.Log;

import java.util.logging.Logger;

/**
 * Created by michaelbrennan on 5/7/16.
 *
 * The operating system is the system that all commands are called from.
 *
 * There are four entity types in the operating system, the DRIVE, FOLDERS, ZIPFILES , and TEXTFILES
 *
 * There are four commands that you can call in the operating system.  The create command can create any type of entity, you must have a least
 * one drive created before any other drive is created.
 *
 * the create command can create any of the four entities
 * the delete command can delete any of the four entities
 * the move command can move any of the four entities
 * the writetofile command can write content to a text file
 *
 * below is an example of how one could use the operating system
 *
 *
 */
public class Main {

    private static Logger lgr = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws PathAlreadyExistsException, TextFileException, IllegalFileSystemOperationException, PathNotFoundException {

        OperatingSystem os = new OperatingSystem();
        String root = "root";
        os.create(root, OsType.DRIVE, null);
        os.create("root2", OsType.DRIVE, null);//this will be a sibling drive of the root drive

        os.create("textFile", OsType.TEXTFILE, root);
        os.create("folder", OsType.FOLDER, root); //root/folder
        os.create("folder1", OsType.FOLDER, root + "/folder"); //nested folder with path root/folder/folder1
        os.create("folder2", OsType.FOLDER, root); // root/folder2
        os.create("zipfile", OsType.ZIPFILE, root + "/folder/"); //  root/folder/zipfile

        os.writeTofile("root/textFile.txt", "this is content");

        lgr.info("Size of drive is " + String.valueOf(os.getDrives().get("root").getSize())); //measures total size of a given entity

        String rootFolder2 = "root/folder2";
        os.create("otherTextFile", OsType.TEXTFILE, rootFolder2);

        os.move(rootFolder2, "destination/");
    }

    public void method(){

    }
}
