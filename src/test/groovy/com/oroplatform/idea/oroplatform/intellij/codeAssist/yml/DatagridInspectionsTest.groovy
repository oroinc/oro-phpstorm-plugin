package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest


class DatagridInspectionsTest extends InspectionTest {
    @Override
    String fileName() {
        return "datagrid.yml"
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

    def void "test: allow boolean for options.export"() {
        checkInspection(
            """
            |datagrid:
            |  some_grid:
            |    options:
            |      export: true
            """.stripMargin()
        )
    }

    def void "test: allow object for options.export"() {
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
