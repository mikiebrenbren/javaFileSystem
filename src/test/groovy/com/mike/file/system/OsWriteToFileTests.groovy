package com.mike.file.system

import spock.lang.Specification

/**
 * Created by michaelbrennan on 5/9/16.
 */
class OsWriteToFileTests extends Specification {

    OperatingSystem os = new OperatingSystem()
    def path
    def content

    def "test writing content to file"() {
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
        folder2.addTextFile("path.name", new TextFile())

        os.setDrives(drives)

        String path = "/this/is/a/path/"
        String name = "path.name"
        String textFileName = name + ".txt"
        String fullPathToBeWrittenTo = path + textFileName
        os.writeTofile(fullPathToBeWrittenTo, "ttttt") //write content to file

        then:
        folder2.getSize() == 5
        folder2.getTextFiles().get(name).content == "ttttt"

    }

    def "test writing content to drive"() {
        when:

        Map<String, Drive> drives = [:]  //create map of drive
        Drive drive = new Drive() //create drive named this
        drive.setName("this")
        drives.put("this", drive)  //put the drive in the map

        drives.get("this").addTextFile("path.name", new TextFile())

        os.setDrives(drives)

        String path = "/this/"
        String name = "path.name"
        String textFileName = name + ".txt"
        String fullPathToBeWrittenTo = path + textFileName
        os.writeTofile(fullPathToBeWrittenTo, "ttttt") //write content to file

        then:
        drive.getSize() == 5
        drives.get("this").getTextFiles().get(name).content == "ttttt"

    }
}
