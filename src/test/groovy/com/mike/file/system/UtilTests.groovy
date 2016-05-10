package com.mike.file.system

import com.mike.exceptions.IllegalFileSystemOperationException
import com.mike.util.PathUtil
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Created by michaelbrennan on 5/7/16.
 */
@Stepwise
class UtilTests extends Specification {

    PathUtil pathUtil = new PathUtil()

    def "test path to list class"() {
        when:
        def path = "/this/is/a.pstring/path_path"
        def p = ""

        def p0 = pathUtil.getPathAsList(path).size()
        pathUtil.getPathAsList(p).size()


        then:
        p0 == 4
        final IllegalFileSystemOperationException e = thrown()
        e.message != null

    }

    def "test path to list class exception"() {

        when:
        def path2 = "/this/is/a!/path_path"
        pathUtil.getPathAsList(path2).size()

        then:
        final IllegalFileSystemOperationException e1 = thrown()
        e1.message != null

    }
}
