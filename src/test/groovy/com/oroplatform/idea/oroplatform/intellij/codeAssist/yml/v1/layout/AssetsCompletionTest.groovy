package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1.layout

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest

class AssetsCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return "src/Oro/AcmeBundle/Resources/views/layouts/base/config/assets.yml"
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        configureByText("src/Oro/AcmeBundle/OroAcmeBundle.php",
            """
            |<?php
            |namespace Oro\\AcmeBundle;
            |
            |class OroAcmeBundle {}
            """
        )
    }

    def void "test: suggest properties at top level"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["styles"]
        )
    }

    def void "test: suggest properties at top level in completely empty file"() {
        suggestions(
            """<caret>""".stripMargin(),
            ["styles"]
        )
    }

    def void "test: complete properties at top level in completely empty file"() {
        completion(
            """s<caret>""".stripMargin(),
            """styles: <caret>""".stripMargin(),
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

    def void "test: suggest inputs in quotes"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles1.css", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles2.css", "")

        suggestions(
            """
            |styles:
            |  inputs:
            |    - '<caret>'
            """.stripMargin(),
            ["bundles/oroacme/some_theme/css/styles1.css", "bundles/oroacme/some_theme/css/styles2.css"]
        )
    }

    def void "test: suggest inputs in one quote"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles1.css", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles2.css", "")

        suggestions(
            """
            |styles:
            |  inputs:
            |    - '<caret>
            """.stripMargin(),
            ["bundles/oroacme/some_theme/css/styles1.css", "bundles/oroacme/some_theme/css/styles2.css"]
        )
    }

    def void "test: close missing quote for inputs"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles1.css", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles2.css", "")

        completion(
            """
            |styles:
            |  inputs:
            |    - 'styles1<caret>
            """.stripMargin(),
            """
            |styles:
            |  inputs:
            |    - 'bundles/oroacme/some_theme/css/styles1.css'
            """.stripMargin()
        )
    }

    def void "test: close missing double quote for inputs"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles1.css", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/css/styles2.css", "")

        completion(
            """
            |styles:
            |  inputs:
            |    - "styles1<caret>
            """.stripMargin(),
            """
            |styles:
            |  inputs:
            |    - "bundles/oroacme/some_theme/css/styles1.css"
            """.stripMargin()
        )
    }

    def void "test: not suggest js as inputs"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/some_theme/js/main.js", "")

        suggestions(
            """
            |styles:
            |  inputs:
            |    - '<caret>'
            """.stripMargin(),
            [],
            ["bundles/oroacme/some_theme/js/main.js"]
        )
    }

    def void "test: not suggest inputs from other bundles"() {
        configureByText("src/Oro/AcmeBundle123/Resources/public/some_theme/css/style.css", "")
        configureByText("src/Oro/AcmeBundle123/OroAcmeBundle.php",
            """
            |<?php
            |namespace Oro\\AcmeBundle;
            |
            |class OroAcmeBundle {}
            """
        )

        suggestions(
            """
            |styles:
            |  inputs:
            |    - '<caret>'
            """.stripMargin(),
            [],
            ["bundles/oroacme123/some_theme/css/style.css"]
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
        suggestions(
            """
            |styles:
            |  output: <caret>
            """.stripMargin(),
            ["css/layout/base/styles.css"]
        )
    }

    def void "test: suggest output at the end of the file"() {
        suggestions(
            """
            |styles:
            |  output: <caret>""".stripMargin(),
            ["css/layout/base/styles.css"]
        )
    }

    def void "test: suggest filters"() {
        configureByText("app/config/config.yml",
            """
            |assetic:
            |  filters:
            |    filter1: ~
            |    filter2: ~
            |    filter3: ~
            """.stripMargin()
        )

        suggestions(
            """
            |styles:
            |  filters: [<caret>]
            """.stripMargin(),
            ["filter1", "filter2", "filter3"]
        )
    }
}
