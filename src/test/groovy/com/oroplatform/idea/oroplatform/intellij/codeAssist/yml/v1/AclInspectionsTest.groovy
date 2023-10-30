package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.SchemaInspection
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class AclInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACL
    }

    def actionRequiredProperties = "label: someLabel"

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
    }

    def void "test: detect not allowed value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: <weak_warning>invalid</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: detect not allowed quoted value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: <weak_warning>"invalid"</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should not see problems with allowed value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: action
            """.stripMargin()
        )
    }

    def void "test: should not see problems with allowed quoted value in choice type"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: "action"
            |""".stripMargin()
        )
    }

    def void "test: should detect missing required properties on top level"() {
        checkInspection(
            """
            |some_id:
            |  <weak_warning descr="The 'label' property is required.">type: "action"</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should detect missing required properties for entity"() {
        checkInspection(
            """
            |some_id:
            |  <weak_warning descr="The 'permission' property is required.">type: "entity"
            |  class: stdClass</weak_warning>
            |""".stripMargin()
        )
    }


    def void "test: should detect unsupported property"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: "action"
            |  <weak_warning>some: value</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should complain about entity property in action"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: "action"
            |  <weak_warning>permission: VIEW</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should detect unsupported property in entity"() {
        checkInspection(
            """
            |some_id:
            |  type: "entity"
            |  permission: VIEW
            |  class: stdClass
            |  <weak_warning>xxx: VIEW</weak_warning>
            |""".stripMargin()
        )
    }


    def void "test: should detect few entity specific properties in action"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: "action"
            |  <weak_warning>permission: VIEW</weak_warning>
            |  <weak_warning>class: stdClass</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should detect invalid property in sequence item"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: "action"
            |  bindings:
            |    - { class: stdClass, method: abc, <weak_warning>some: value</weak_warning> }
            |""".stripMargin()
        )
    }

    def void "test: should detect missing property in sequence item"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: "action"
            |  bindings:
            |    - <weak_warning>{ class: stdClass }</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should detect invalid scalar type"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: <weak_warning>["action"]</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should detect duplicated properties"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  type: "action"
            |  <weak_warning>type: "action"</weak_warning>
            |""".stripMargin()
        )
    }

    def void "test: should detect lack of the value"() {
        checkInspection(
            """
            |some_id:
            |  $actionRequiredProperties
            |  <weak_warning>type:</weak_warning>
            |""".stripMargin()
        )
    }
}
