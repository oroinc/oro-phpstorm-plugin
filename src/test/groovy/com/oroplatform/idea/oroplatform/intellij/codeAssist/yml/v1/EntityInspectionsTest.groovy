package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.SchemaInspection
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class EntityInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ENTITY
    }

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
    }

    def void "test: invalid alias value should be detected"() {
        checkInspection(
            """
            |oro_entity:
            |  entity_aliases:
            |    stdClass:
            |      alias: <weak_warning>Some value</weak_warning>
            """.stripMargin()
        )
    }


    def void "test: valid alias value shouldn't be reported as error"() {
        checkInspection(
            """
            |oro_entity:
            |  entity_aliases:
            |    stdClass:
            |      alias: value123
            """.stripMargin()
        )
    }
}
