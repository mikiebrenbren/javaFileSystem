package com.mike.file.system

import com.mike.enums.OsType
import spock.lang.Specification
import spock.lang.Stepwise

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by michaelbrennan on 5/7/16.
 */
@Stepwise
class NameTests extends Specification {

    private final Logger lgr = Logger.getLogger(this.getClass().getName());

    Map<String, TextFile> textFiles
    Map<String, ZipFile> zipFiles
    Map<String, Folder> folders

    void setup() {
        textFiles = new HashMap<>()
        zipFiles = new HashMap<>()
        folders = new HashMap<>()
    }

    def "text file naming tests"() {
        when:
        TextFile t = new TextFile()
        t.setName("tFile")

        then:
        t.getName() == "tFile.txt"

    }

    def "text file list"() {
        when:
        TextFile t = new TextFile()
        t.setName("tFile")

        then:
        t.getName() == "tFile.txt"

    }

    def " test enums"() {
        setup:
        lgr.log(Level.FINEST, "enum testing")

        expect:
        OsType.DRIVE.name() == Drive.class.getSimpleName().toUpperCase()
        OsType.FOLDER.name() == Folder.class.getSimpleName().toUpperCase()
        OsType.ZIPFILE.name() == ZipFile.class.getSimpleName().toUpperCase()
        OsType.TEXTFILE.name() == TextFile.class.getSimpleName().toUpperCase()
    }

}
