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
            |class OroAcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            """
        )
    }

    def void "test: suggest top level properties"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["config", "build"]
        )
    }

    def void "test: suggest properties for config"() {
        suggestions(
            """
            |config:
            |  <caret>
            """.stripMargin(),
            ["shim", "map", "paths", "build_path", "appmodules"]
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

    def void "test: suggest modules from paths as shim properties"() {
        suggestions(
            """
            |config:
            |  shim:
            |    <caret>
            |  paths:
            |    module1: ~
            |    module2: ~
            """.stripMargin(),
            ["module1", "module2"]
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

    def void "test: not suggest js scripts as path keys"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script1.js", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script2.js", "")

        suggestions(
            """
            |config:
            |  paths:
            |    <caret>: bundles/oroacme/js/script1.js
            """.stripMargin(),
            [],
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

    def void "test: suggest properties for build"() {
        suggestions(
            """
            |build:
            |  <caret>
            """.stripMargin(),
            ["paths"]
        )
    }

    def void "test: suggest requirejs modules"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script1.js", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script2.js", "")

        suggestions(
            """
            |config:
            |  map:
            |    <caret>
            """.stripMargin(),
            ["oroacme/js/script1", "oroacme/js/script2"]
        )
    }

    def void "test: detect requirejs module reference"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script1.js", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script2.js", "")

        checkFileReferences(
            """
            |config:
            |  map:
            |    'oroacme/js/sc<caret>ript1': ~
            """.stripMargin(),
            ["script1.js"]
        )
    }

    def void "test: suggest requirejs modules in map section"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script1.js", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script2.js", "")

        suggestions(
            """
            |config:
            |  map:
            |    oroacme/js/script1:
            |      script2: <caret>
            """.stripMargin(),
            ["oroacme/js/script2"]
        )
    }

    def void "test: suggest requirejs modules in shim deps"() {
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script1.js", "")
        configureByText("src/Oro/AcmeBundle/Resources/public/js/script2.js", "")

        suggestions(
            """
            |config:
            |  shim:
            |    some:
            |      deps: [<caret>]
            """.stripMargin(),
            ["oroacme/js/script1", "oroacme/js/script2"]
        )
    }
}
