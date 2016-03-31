package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest

class AclInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return "acl.yml"
    }

    def requiredProperties = """
                             |  label: someLabel
                             """.stripMargin()

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(AclInspection.class)
    }

    def void "test: detect not allowed value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: <error>invalid</error>
            """.stripMargin()
        )
    }

    def void "test: detect not allowed quoted value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: <error>"invalid"</error>
            """.stripMargin()
        )
    }

    def void "test: should not see problems with allowed value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: action
            """.stripMargin()
        )
    }

    def void "test: should not see problems with allowed quoted value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: "action"
            """.stripMargin()
        )
    }

    def void "test: should detect missing required properties on top level"() {
        checkInspection(
            """
            |<error descr="The 'label' property is required.">some_id:</error>
            |  type: "action"
            """.stripMargin()
        )
    }
}
