package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1.layout

import com.oroplatform.idea.oroplatform.intellij.codeAssist.FileReferenceTest


class AssetsCompletionTest extends FileReferenceTest {
    @Override
    String fileName() {
        return "Resources/views/layouts/base/config/assets.yml"
    }

    def void "test: suggest properties at top level"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["styles"]
        )
    }

    def void "test: suggest styles properties"() {
        suggestions(
            """
            |styles:
            |  <caret>
            """.stripMargin(),
            ["inputs", "filters", "output"]
        )
    }

    def void "test: suggest inputs"() {
        configureByText("app/some-file.yml", "")
        configureByText("web/bundles/css/some.css", "")

        suggestions(
            """
            |styles:
            |  inputs:
            |    - <caret>
            """.stripMargin(),
            ["bundles"]
        )
    }

    def void "test: suggest output"() {
        configureByText("app/some-file.yml", "")
        configureByText("web/bundles/css/some.css", "")

        suggestions(
            """
            |styles:
            |  output: <caret>
            """.stripMargin(),
            ["bundles"]
        )
    }

    def void "test: suggest filters"() {
        suggestions(
            """
            |styles:
            |  filters: [<caret>]
            """.stripMargin(),
            ["cssrewrite", "lessphp", "cssmin"]
        )
    }
}
