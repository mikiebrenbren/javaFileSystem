package com.mike.file.system

import com.mike.enums.OsType
import com.mike.exceptions.IllegalFileSystemOperationException
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Created by michaelbrennan on 5/7/16.
 */
@Stepwise
public class ValidatorTests extends Specification {

    def validator = new OsValidator()

    //todo test file name and path stuff

    def "global validator"() {
        when:
        Map<String, Drive> driveMap = [:]
        Drive drive = new Drive()
        driveMap.put("d", drive)
        validator.globalValidate(driveMap, OsType.DRIVE, "this/is/the/", "name")

        then:
        1 == 1

    }

    def "path validator tests"() {
        setup:
        validator.validateFileName("path")
        validator.validateFileName("path123")
        validator.validateFileName("path_path")
        validator.validateFileName("path_path.path")
    }

    def "file validator test"() {
        when:
        validator.validateFileName("!@#%")

        then:
        final IllegalFileSystemOperationException e = thrown()
        e.message != null
    }

}
