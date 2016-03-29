package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest

class AclInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return "acl.yml"
    }

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(AclInspection.class)
    }

    def void "test: detect not allowed value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  type: <error>invalid</error>
            """.stripMargin()
        )
    }
}
