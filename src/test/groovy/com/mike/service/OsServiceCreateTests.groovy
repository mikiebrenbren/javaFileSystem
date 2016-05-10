package com.mike.service

import com.mike.enums.OsType
import com.mike.exceptions.PathAlreadyExistsException
import com.mike.file.system.Drive
import com.mike.file.system.Folder
import com.mike.file.system.TextFile
import com.mike.file.system.ZipFile
import spock.lang.Specification

/**
 * Created by michaelbrennan on 5/8/16.
 */
class OsServiceCreateTests extends Specification {

    OperatingSystemServiceImpl operatingSystemService = new OperatingSystemServiceImpl()

    def "recursive function test for text"() {
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

        then:
        folder2.getTextFiles().get(name ) != null
        folder2.getTextFiles().get(name).getName() == textFileName
        folder2.getTextFiles().get(name).getPath() == path

    }

    def "recursive function test for text when one is already in path "() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map

        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        TextFile t = new TextFile()
        t.setName("name")
        folder.addTextFile("name", t)
        drive.getFolders().put("is", folder)

        String path = "/this/is"
        String name = "name"
        operatingSystemService.create(name, OsType.TEXTFILE, path, drives)

        then:
        final PathAlreadyExistsException e = thrown()
        e.message != null

    }

    def "test zip file creation"() {
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

        then:
        folder1.getZipFiles().get(name ) != null
        folder1.getZipFiles().get(name).getName() == zipFileName
        folder1.getZipFiles().get(name).getPath() == path
    }

    def "test zip file creation when zip file already exists"() {
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
        ZipFile zipFile = new ZipFile()
        zipFile.setName("zip_file")
        folder1.getZipFiles().put("zip_file", zipFile)
        folder.getFolders().put("path", folder1)


        String path = "/this/is/path"
        String name = "zip_file"
        operatingSystemService.create(name, OsType.ZIPFILE, path, drives)

        then:
        final PathAlreadyExistsException e = thrown()
        e.message != null
    }

    def "test create folder"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map


        String path = "/this/"
        String name = "folder.name_folder"
        operatingSystemService.create(name, OsType.FOLDER, path, drives)

        then:
        drive.getFolders().get(name ) != null
        drive.getFolders().get(name).getName() == name
        drive.getFolders().get(name).getPath() == path
    }

    def "test create folder if one already exists"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map
        Folder f = new Folder()
        f.setName("folder.name_folder")
        drive.addFolder(f.getName(), f)
        drive.addTextFile("t", new TextFile(10))

        String name = "folder.name_folder"
        String path = "/this/"
        operatingSystemService.create(name, OsType.FOLDER, path, drives)

        then:
        final PathAlreadyExistsException e = thrown()
        e.message != null
    }

    def "test drive"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive

        String path = null
        String name = "drive.name"
        operatingSystemService.create(name, OsType.DRIVE, path, drives)

        then:
        drives.get(name ) != null
        drives.get(name).getName() == name
        drives.get(name).getPath() == path
    }

    def "test create drive when one already exists"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("drive.name")
        drives.put("drive.name", drive)  //put the drive in the map


        String path = null
        String name = "drive.name"
        operatingSystemService.create(name, OsType.DRIVE, path, drives)

        then:
        final PathAlreadyExistsException e = thrown()
        e.message != null
    }


}
