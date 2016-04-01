package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest

class AclInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return "acl.yml"
    }

    def requiredProperties = "label: someLabel"

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
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
            |some_id:
            |  <error descr="The 'label' property is required.">type: "action"</error>
            """.stripMargin()
        )
    }

    def void "test: should detect missing required properties for entity"() {
        checkInspection(
            """
            |some_id:
            |  <error descr="The 'permission' property is required.">$requiredProperties
            |  type: "entity"
            |  class: stdClass</error>
            """.stripMargin()
        )
    }


    def void "test: should detect unsupported property"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: "action"
            |  <error>some: value</error>
            """.stripMargin()
        )
    }

    def void "test: should complain about entity property in action"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: "action"
            |  <error>permission: VIEW</error>
            """.stripMargin()
        )
    }

    def void "test: should detect unsupported property in entity"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: "entity"
            |  permission: VIEW
            |  class: stdClass
            |  <error>xxx: VIEW</error>
            """.stripMargin()
        )
    }


    def void "test: should detect few entity specific properties in action"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: "action"
            |  <error>permission: VIEW</error>
            |  <error>class: stdClass</error>
            """.stripMargin()
        )
    }

    def void "test: should detect invalid property in sequence item"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: "action"
            |  bindings:
            |    - { class: stdClass, method: abc, <error>some: value</error> }
            """.stripMargin()
        )
    }

    def void "test: should detect missing property in sequence item"() {
        checkInspection(
            """
            |some_id:
            |  $requiredProperties
            |  type: "action"
            |  bindings:
            |    - <error>{ class: stdClass }</error>
            """.stripMargin()
        )
    }
}
