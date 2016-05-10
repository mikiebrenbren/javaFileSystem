package com.mike.service

import com.mike.enums.OsType
import com.mike.exceptions.IllegalFileSystemOperationException
import com.mike.exceptions.PathNotFoundException
import com.mike.file.system.Drive
import com.mike.file.system.Folder
import spock.lang.Specification

/**
 * Created by michaelbrennan on 5/8/16.
 */
class OsServiceDeleteTests extends Specification{

    OperatingSystemServiceImpl operatingSystemService = new OperatingSystemServiceImpl()

    def "delete drive test and test with path not null"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("drive.name")
        drives.put("drive.name", drive)  //put the drive in the map


        String path = null
        String name = "drive.name"
        String path2 = "this/is/a/path"
        operatingSystemService.delete(name, OsType.DRIVE, path, drives)
        operatingSystemService.delete(name, OsType.DRIVE, path2, drives) //attempt to delete again to test exception

        then:
        drives.isEmpty()
        final IllegalFileSystemOperationException e = thrown()
        e.message != null
    }

    def "test create folder"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map

        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        drive.getFolders().put("is", folder)

        Folder folder1 = new Folder()  //add another folder and put it in folder
        folder1.setName("a")
        folder.getFolders().put("a", folder1)

        Folder folder2 = new Folder() //add another folder and put it in folder1
        folder2.setName("path")
        folder1.getFolders().put("path", folder2)

        String path = "/this/is/a/path"
        String name = "path.name"
        String textFileName = name + ".txt"
        operatingSystemService.create(name, OsType.TEXTFILE, path, drives)
        assert folder2.getTextFiles().get(name ) != null
        assert folder2.getTextFiles().get(name).getName() == textFileName
        assert folder2.getTextFiles().get(name).getPath() == path
        assert !folder2.getTextFiles().isEmpty()
        operatingSystemService.delete(name, OsType.TEXTFILE, path, drives)
        operatingSystemService.delete(name, OsType.TEXTFILE, path, drives) //attempt to delete again to test exception


        then:
        folder2.getTextFiles().isEmpty()
        final PathNotFoundException e = thrown()
        e.message != null

    }

    def "test zip file deletion"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this_drive")
        drives.put("this", drive)  //put the drive in the map

        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        drive.getFolders().put("is", folder)

        Folder folder1 = new Folder()  //add another folder and put it in folder
        folder1.setName("path")
        folder.getFolders().put("path", folder1)


        String path = "/this/is/path"
        String name = "zip_file"
        String zipFileName = name + ".zip"
        operatingSystemService.create(name, OsType.ZIPFILE, path, drives)
        assert folder1.getZipFiles().get(name ) != null
        assert folder1.getZipFiles().get(name).getName() == zipFileName
        assert folder1.getZipFiles().get(name).getPath() == path
        assert !folder1.getZipFiles().isEmpty()
        operatingSystemService.delete(name, OsType.ZIPFILE, path, drives)
        operatingSystemService.delete(name, OsType.ZIPFILE, path, drives)//attempt to delete again to test exception

        then:
        folder1.getTextFiles().isEmpty()
        final PathNotFoundException e = thrown()
        e.message != null
    }

    def "test delete folder"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map


        String path = "/this/"
        String name = "folder.name_folder"
        operatingSystemService.create(name, OsType.FOLDER, path, drives)
        assert drive.getFolders().get(name ) != null
        assert drive.getFolders().get(name).getName() == name
        assert drive.getFolders().get(name).getPath() == path
        assert !drive.getFolders().isEmpty()
        operatingSystemService.delete(name, OsType.FOLDER, path, drives)
        operatingSystemService.delete(name, OsType.FOLDER, path, drives)//attempt to delete again to test exception

        then:
        drive.getFolders().isEmpty()
        final PathNotFoundException e = thrown()
        e.message != null

    }

}
