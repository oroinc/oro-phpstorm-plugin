package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2


class NavigationCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.NAVIGATION
    }

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["navigation"]
        )
    }

    def void "test: suggest navigation properties"() {
        suggestions(
            """
            |navigation:
            |  <caret>
            """.stripMargin(),

            ["menu_config", "titles", "navigation_elements"]
        )
    }

    def void "test: suggest oro_menu_config properties"() {
        suggestions(
            """
            |navigation:
            |  menu_config:
            |    <caret>
            """.stripMargin(),

            ["templates", "items", "tree"]
        )
    }
}
