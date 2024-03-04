package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.SchemaInspection
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class DatagridInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.DATAGRID
    }

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
    }

    def void "test: allow any string as value for 'type' in 'source'"() {
        checkInspection(
            """
            |datagrid:
            |  some_grid:
            |    source:
            |      type: abc
            """.stripMargin()
        )
    }

    def void "test: allow boolean for options(dot)export"() {
        checkInspection(
            """
            |datagrid:
            |  some_grid:
            |    options:
            |      export: true
            """.stripMargin()
        )
    }

    def void "test: allow object for options(dot)export"() {
        checkInspection(
            """
            |datagrid:
            |  some_grid:
            |    options:
            |      export:
            |        csv:
            |          label: abc
            """.stripMargin()
        )
    }
}
