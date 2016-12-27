package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript

import com.oroplatform.idea.oroplatform.intellij.codeAssist.FileReferenceTest

class RequireJsReferenceTest extends FileReferenceTest {
    private String oroUIPath = "vendor/Oro/Bundle/UIBundle/Resources/public/js/"
    private String oroDashboardPath = "vendor/Oro/Bundle/DashboardBundle/Resources/public/js/"

    @Override
    String fileName() {
        return "vendor/Oro/Bundle/SomeBundle/Resources/public/js/some.js"
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("vendor/Oro/Bundle/UIBundle/OroUIBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\UIBundle;
            |
            |class OroUIBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
          """.stripMargin()
        )

        configureByText("vendor/Oro/Bundle/DashboardBundle/OroDashboardBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\DashboardBundle;
            |
            |class OroDashboardBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
          """.stripMargin()
        )

        configureByText("vendor/Oro/Bundle/SomeBundle/OroSomeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\SomeBundle;
            |
            |class OroSomeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
          """.stripMargin()
        )

        configureByText(oroUIPath + "layout.js", "")
        configureByText(oroUIPath + "layout/layout2.js", "")
        configureByText(oroUIPath + "layout/layout3.js", "")
        configureByText(oroUIPath + "app.js", "")
        configureByText(oroUIPath + "some/func.js", "")

        configureByText(oroDashboardPath + "dashboard1.js", "")
        configureByText(oroDashboardPath + "some/dashboard2.js", "")
        configureByText(oroDashboardPath + "some/dashboard3.js", "")
        configureByText(oroDashboardPath + "some/aliased.js", "")
        configureByText(oroDashboardPath + "some/index.html", "")
        configureByText(oroDashboardPath + "some/mapped.js", "")
        configureByText(oroDashboardPath + "some/mappedForAllFiles.js", "")

        configureByText(
            "vendor/Oro/Bundle/DashboardBundle/Resources/config/requirejs.yml",
            """
            |config:
            |  paths:
            |    someAlias: bundles/orodashboard/js/some/aliased.js
            |  map:
            |    orosome/js/some:
            |       mapped: orodashboard/js/some/mapped
            |    '*':
            |       mappedForAllFiles: orodashboard/js/some/mappedForAllFiles
            """.stripMargin()
        )
    }

    def void "test: detect oro js file as reference"() {
        checkFileReferences(
            """
            |require('oroui/js/l<caret>ayout')
            """.stripMargin(),
            ["layout.js"]
        )
    }

    def void "test: not detect irrelevant files"() {
        checkFileReferences(
            """
            |require('oroui/js/l<caret>ayout')
            """.stripMargin(),
            [],
            ["app.js", "func.js"]
        )
    }

    def void "test: detect files in subdirectory"() {
        checkFileReferences(
            """
            |require('oroui/js/some/fu<caret>nc')
            """.stripMargin(),
            ["func.js"]
        )
    }

    def void "test: suggest oroui js files"() {
        suggestions(
            """
            |require('<caret>')
            """.stripMargin(),
            ["oroui/js/layout"]
        )
    }

    def void "test: suggest oroui js files even if given reference exists"() {
        suggestions(
            """
            |require('oroui/js/layout<caret>')
            """.stripMargin(),
            ["oroui/js/layout/layout2"]
        )
    }

    def void "test: detect oro js file as reference in define function"() {
        checkFileReferences(
            """
            |define(['oroui/js/l<caret>ayout'])
            """.stripMargin(),
            ["layout.js"]
        )
    }

    def void "test: detect oro js file as reference in file where reference files are defined"() {
        checkFileReferences(
            oroUIPath + "test.js",
            """
            |require('oroui/js/l<caret>ayout')
            """.stripMargin(),
            ["layout.js"]
        )
    }

    def void "test: detect oro js file as reference in file in subdirectory where reference files are defined"() {
        checkFileReferences(
            oroUIPath + "some/test.js",
            """
            |require('oroui/js/l<caret>ayout')
            """.stripMargin(),
            ["layout.js"]
        )
    }

    def void "test: detect oro js file as reference in file in completely different directory"() {
        checkFileReferences(
            "some/test.js",
            """
            |require('oroui/js/l<caret>ayout')
            """.stripMargin(),
            ["layout.js"]
        )
    }

    def void "test: detect oro dashboard js file as reference"() {
        checkFileReferences(
            """
            |require('orodashboard/js/dash<caret>board1')
            """.stripMargin(),
            ["dashboard1.js"]
        )
    }

    def void "test: detect reference for path alias"() {
        checkFileReferences(
            """
            |require('some<caret>Alias')
            """.stripMargin(),
            ["aliased.js"]
        )
    }

    def void "test: suggest aliases for files"() {
        suggestions(
            """
            |require('<caret>')
            """.stripMargin(),
            ["someAlias"]
        )
    }

    def void "test: suggest mapped packages"() {
        suggestions(
            """
            |require('<caret>')
            """.stripMargin(),
            ["mapped"],
            ["orodashboard/js/some/mapped"]
        )
    }

    def void "test: not suggest mapped packages for different file"() {
        suggestions(
            "vendor/Oro/Bundle/SomeBundle/Resources/public/js/some2.js",
            """
            |require('<caret>')
            """.stripMargin(),
            ["orodashboard/js/some/mapped"],
            ["mapped"]
        )
    }

    def void "test: suggest mapped packages for all files"() {
        suggestions(
            """
            |require('<caret>')
            """.stripMargin(),
            ["mappedForAllFiles"],
            ["orodashboard/js/some/mappedForAllFiles"]
        )
    }

    def void "test: detect mapped packages as reference"() {
        checkFileReferences(
            """
            |require('map<caret>ped')
            """.stripMargin(),
            ["mapped.js"]
        )
    }

    def void "test: not suggest html file"() {
        suggestions(
            """
            |require('<caret>')
            """.stripMargin(),
            [],
            ["orodashboard/js/some/index.html"]
        )
    }
}
