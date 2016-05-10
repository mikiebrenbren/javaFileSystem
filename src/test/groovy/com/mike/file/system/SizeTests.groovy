package com.mike.file.system

import com.mike.util.SumUtil
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Created by michaelbrennan on 5/4/16.
 */
@Stepwise
class SizeTests extends Specification {

    Map<String, TextFile> textFiles
    Map<String, ZipFile> zipFiles
    Map<String, Folder> folders

    void setup() {
        textFiles = new HashMap<>()
        zipFiles = new HashMap<>()
        folders = new HashMap<>()
        //add two text files to textfile map with size 3 and 4
        textFiles.put("tf", new TextFile("ttt"))
        textFiles.put("tf1", new TextFile("tttt"))

        //create new zipfile and folder
        def zipFile = new ZipFile()
        Folder folder1 = new Folder()
        folder1.getTextFiles().put("t", new TextFile("ttt"))
        assert folder1.getSize() == 3
        zipFile.getFolders().put("f", folder1) //divide by 2 == 1.5
        assert zipFile.getSize() == 1.5
        zipFiles.put("z", zipFile)
        folders.put("f", new Folder())
        folders.get("f").getSize() == 0

        ZipFile zipFile1 = new ZipFile();
        zipFile1.getTextFiles().put("t1", new TextFile("ttt"))
        zipFile1.getTextFiles().put("t2", new TextFile("ttt"))
        zipFile1.getTextFiles().put("t3", new TextFile("tt"))

        assert zipFile1.getSize() == 4  //divide by 2 == 4
        zipFiles.put("z3", zipFile1)

        //total size of textFiles is 7
        //total is 12.5

    }

    def "test textfile"() {
        when:
        TextFile textFile = new TextFile("tt")
        TextFile textFile2 = new TextFile("")

        then:
        textFile.getSize() == 2
        textFile2.getSize() == 0
    }

    def "test sum util"() {
        when:
        //add two text files to setup,
        TextFile textFile = new TextFile("t")
        TextFile textFile2 = new TextFile("t")
        Folder f = new Folder()
        f.getTextFiles().put("t", textFile)
        f.getTextFiles().put("t2", textFile2)
        Folder fparent = new Folder()
        fparent.getFolders().put("f", f)

        folders.put("fparent", fparent)
        assert fparent.getSize() == 2

        then:
        SumUtil.sum(textFiles, zipFiles, folders) == 12.5 + 2
    }

    def "test zipFile"() {
        when:
        ZipFile zipFile = new ZipFile()
        assert zipFile.getSize() == 0
        zipFile.setFolders(folders)
        zipFile.setZipFiles(zipFiles)
        zipFile.setTextFiles(textFiles)

        assert zipFile.getSize() == 6.25  // total from setup / 2
        zipFile.getTextFiles().put("textFile", new TextFile("tt"))
        zipFile.getSize() == (12.5 + 2) / 2

        then:
        zipFile.getSize() == 7.25

    }

    def "test folder"() {

        when:
        Folder folder = new Folder()
        Folder childFolder = new Folder()
        Folder childFolder2 = new Folder()
        childFolder.getTextFiles().put("cT", new TextFile("ttttt"))
        childFolder.getTextFiles().put("cT1", new TextFile("ttttt"))
        childFolder2.getTextFiles().put("cT", new TextFile("ttttt"))
        childFolder2.getTextFiles().put("cT1", new TextFile("ttttt"))
        folder.setFolders(folders)
        folder.setTextFiles(textFiles)
        folder.setZipFiles(zipFiles)
        folder.getFolders().put("cf", childFolder)
        folder.getFolders().put("cf2", childFolder2)

        then:
        folder.getSize() == 32.5

    }

    def "test drive"() {
        when:
        Drive drive = new Drive()
        drive.setFolders(folders)
        drive.setZipFiles(zipFiles)
        drive.setTextFiles(textFiles)

        then:
        drive.getSize() == 12.5

    }

    def "this is a test"() {
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }


}
