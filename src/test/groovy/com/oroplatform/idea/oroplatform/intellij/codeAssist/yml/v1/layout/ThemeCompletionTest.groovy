package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1.layout

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest


class ThemeCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return "Resources/views/layouts/theme1/theme.yml"
    }

    def void "test: suggest properties at top level"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["label", "logo", "screenshot", "directory", "parent", "groups"]
        )
    }

    def void "test: suggest parent"() {
        configureByText("Resources/views/layouts/theme2/theme.yml", "")

        suggestions(
            """
            |parent: <caret>
            """.stripMargin(),
            ["theme2"]
        )
    }
}
