package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class EntityInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatternss.ENTITY
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
