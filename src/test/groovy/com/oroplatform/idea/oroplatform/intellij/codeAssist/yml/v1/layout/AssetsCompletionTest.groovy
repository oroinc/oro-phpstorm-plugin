package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1.layout

import com.oroplatform.idea.oroplatform.intellij.codeAssist.FileReferenceTest


class AssetsCompletionTest extends FileReferenceTest {
    @Override
    String fileName() {
        return "src/Oro/AcmeBundle/Resources/views/layouts/base/config/assets.yml"
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
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles1.css", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles2.css", "")

        suggestions(
            """
            |styles:
            |  inputs:
            |    - <caret>
            """.stripMargin(),
            ["bundles/oroacme/some_theme/css/styles1.css", "bundles/oroacme/some_theme/css/styles2.css"]
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
