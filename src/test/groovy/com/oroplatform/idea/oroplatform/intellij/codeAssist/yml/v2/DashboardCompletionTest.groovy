package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2


class DashboardCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.DASHBOARD
    }

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["dashboards"]
        )
    }

    def void "test: suggest dashboards properties"() {
        suggestions(
            """
            |dashboards:
            |  <caret>
            """.stripMargin(),
            ["widgets", "dashboards", "widgets_configuration"]
        )
    }
}
