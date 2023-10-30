package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.SchemaInspection
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class ApiInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.API
    }

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
    }

    def void "test: should accept alone root element"() {
        checkInspection(
            """
            |api: ~
            """.stripMargin()
        )
    }
}
