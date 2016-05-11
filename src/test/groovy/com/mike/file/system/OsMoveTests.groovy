package com.mike.file.system

import com.mike.exceptions.PathAlreadyExistsException
import com.mike.service.OperatingSystemServiceImpl
import spock.lang.Specification

/**
 * Created by michaelbrennan on 5/11/16.
 */
class OsMoveTests extends Specification {

    OperatingSystemServiceImpl operatingSystemService = new OperatingSystemServiceImpl()
    OperatingSystem os = new OperatingSystem()


    def "move text"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map


        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        drive.getFolders().put("is", folder)

        def parentPath = "this/is/tf.txt"
        def content = "55555"

        def textFileName = "tf"

        def file = new TextFile(textFileName, parentPath)
        file.setContent(content)
        folder.getTextFiles().put(textFileName, file)

        Folder folder1 = new Folder()  //add another folder and put it in folder
        folder1.setName("a")
        folder.getFolders().put("a", folder1)

        Folder folder2 = new Folder() //add another folder and put it in folder1
        folder2.setName("path")
        folder1.getFolders().put("path", folder2)

        String path = "/this/is/a/path"
        os.setDrives(drives)
        os.move(parentPath, path)
        then:
        folder2.getTextFiles().get(textFileName).getContent() == content
        folder.getTextFiles().isEmpty()

    }

    def "move test when already exists"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map


        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        drive.getFolders().put("is", folder)

        def parentPath = "this/is/tf.txt"
        def content = "55555"

        def textFileName = "tf"

        def file = new TextFile(textFileName, parentPath)
        file.setContent(content)
        folder.getTextFiles().put(textFileName, file)

        Folder folder1 = new Folder()  //add another folder and put it in folder
        folder1.setName("a")
        folder.getFolders().put("a", folder1)

        Folder folder2 = new Folder() //add another folder and put it in folder1
        folder2.setName("path")
        folder1.getFolders().put("path", folder2)

        String path = "/this/is/a/path"
        os.setDrives(drives)
        folder2.getTextFiles().put("tf", new TextFile("tf", "this/is/a/path"))
        os.move(parentPath, path)

        then:
        final PathAlreadyExistsException e = thrown()
        e != null

    }

    def "move zip"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map


        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        drive.getFolders().put("is", folder)

        def parentPath = "this/is/zp.zip"
        def zipfilename = "zp"

        def file = new ZipFile()
        folder.getZipFiles().put(zipfilename, file)

        Folder folder1 = new Folder()  //add another folder and put it in folder
        folder1.setName("a")
        folder.getFolders().put("a", folder1)

        Folder folder2 = new Folder() //add another folder and put it in folder1
        folder2.setName("path")
        folder1.getFolders().put("path", folder2)

        String path = "/this/is/a/path"
        os.setDrives(drives)
        os.move(parentPath, path)

        then:
        folder.getZipFiles().isEmpty()
        !folder2.getZipFiles().isEmpty()
        folder2.getZipFiles().get(zipfilename) != null


    }

    def "move zip when on exists "() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map


        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        drive.getFolders().put("is", folder)

        def parentPath = "this/is/zp.zip"
        def zipfilename = "zp"

        def file = new ZipFile()
        folder.getZipFiles().put(zipfilename, file)

        Folder folder1 = new Folder()  //add another folder and put it in folder
        folder1.setName("a")
        folder.getFolders().put("a", folder1)

        Folder folder2 = new Folder() //add another folder and put it in folder1
        folder2.setName("path")
        folder1.getFolders().put("path", folder2)

        folder2.getZipFiles().put(zipfilename, new ZipFile())

        String path = "/this/is/a/path"
        os.setDrives(drives)
        os.move(parentPath, path)

        then:
        final PathAlreadyExistsException e = thrown()
        e.message != null
    }

    def "move folder"() {
        when:
        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map


        Folder folder = new Folder() //create a folder and set name to is and put it in the drive
        folder.setName("is")
        drive.getFolders().put("is", folder)

        def parentPath = "this/is/fldr"
        def folderName = "fldr"

        def foldertomove = new Folder()
        folder.getFolders().put(folderName, foldertomove)

        Folder folder1 = new Folder()  //add another folder and put it in folder
        folder1.setName("a")
        folder.getFolders().put("a", folder1)

        Folder folder2 = new Folder() //add another folder and put it in folder1
        folder2.setName("path")
        folder1.getFolders().put("path", folder2)


        String path = "/this"
        os.setDrives(drives)
        os.move(parentPath, path)

        then:
        drive.getFolders().get(folderName).getName() == foldertomove.getName()
        !folder.getFolders().isEmpty()

    }

    }
