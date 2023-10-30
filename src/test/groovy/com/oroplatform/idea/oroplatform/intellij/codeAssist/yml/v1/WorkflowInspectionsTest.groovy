package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.SchemaInspection
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class WorkflowInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.WORKFLOW
    }

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
    }

    def void "test: detect non scalar value for attribute label"() {
        checkInspection(
            """
            |workflows:
            |  workflow1:
            |    attributes:
            |      attr1:
            |        label: <weak_warning>[some]</weak_warning>
            """.stripMargin()
        )
    }

    def void "test: detect multiple non scalar values for attribute properties"() {
        checkInspection(
            """
            |workflows:
            |  workflow1:
            |    attributes:
            |      attr1:
            |        label: <weak_warning>[some]</weak_warning>
            |        type: <weak_warning>[some]</weak_warning>
            """.stripMargin()
        )
    }

    def void "test: detect non sequence or object value for allowed_transitions"() {
        checkInspection(
            """
            |workflows:
            |  workflow1:
            |    steps:
            |      step1:
            |        allowed_transitions: <weak_warning>some</weak_warning>
            """.stripMargin()
        )
    }

    def void "test: detect non sequence or object value for allowed_transitions in steps sequence notation"() {
        checkInspection(
            """
            |workflows:
            |  workflow1:
            |    steps:
            |      - name: name
            |        allowed_transitions: <weak_warning>some</weak_warning>
            """.stripMargin()
        )
    }
}
