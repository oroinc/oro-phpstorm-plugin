package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1.layout

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest


class RequireJsCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return "src/Oro/AcmeBundle/Resources/views/layouts/base/config/requirejs.yml"
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

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["config"]
        )
    }

    def void "test: suggest properties for config"() {
        suggestions(
            """
            |config:
            |  <caret>
            """.stripMargin(),
            ["shim", "map", "paths", "build_path"]
        )
    }

    def void "test: suggest properties for shim"() {
        suggestions(
            """
            |config:
            |  shim:
            |    jquery:
            |      <caret>
            """.stripMargin(),
            ["deps", "exports"]
        )
    }

    def void "test: suggest js scripts"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script1.js", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script2.js", "")

        suggestions(
            """
            |config:
            |  paths:
            |    script1: <caret>
            """.stripMargin(),
            ["bundles/oroacme/js/script1.js", "bundles/oroacme/js/script2.js"]
        )
    }

    def void "test: not suggest css as js scripts"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/css/style1.css", "")

        suggestions(
            """
            |config:
            |  paths:
            |    script1: <caret>
            """.stripMargin(),
            [],
            ["bundles/oroacme/css/style1.css"]
        )
    }
}
