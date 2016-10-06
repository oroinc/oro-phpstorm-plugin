package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.InspectionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.SchemaInspection
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class DashboardInspectionTest extends InspectionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.DASHBOARD
    }

    @Override
    def void setUp() {
        super.setUp()
        myFixture.enableInspections(SchemaInspection.class)
    }

    def void "test: detect invalid integer value"() {
        checkInspection(
            """
            |oro_dashboard_config:
            |  widgets:
            |    some_widget:
            |      items:
            |        some_item:
            |          position: <weak_warning>abc</weak_warning>
            """.stripMargin()
        )
    }

    def void "test: allow positive integer value"() {
        checkInspection(
            """
            |oro_dashboard_config:
            |  widgets:
            |    some_widget:
            |      items:
            |        some_item:
            |          position: 123
            """.stripMargin()
        )
    }

    def void "test: allow negative integer value"() {
        checkInspection(
            """
            |oro_dashboard_config:
            |  widgets:
            |    some_widget:
            |      items:
            |        some_item:
            |          position: -123
            """.stripMargin()
        )
    }
}
