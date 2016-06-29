package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript

import com.oroplatform.idea.oroplatform.intellij.codeAssist.FileReferenceTest
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings

class RequireJsReferenceTest extends FileReferenceTest {
    private String oroUIPath = "vendor/Oro/Bundle/UIBundle/Resources/public/js/"

    @Override
    String fileName() {
        return "some.js"
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("vendor/Oro/Bundle/UIBundle/OroUIBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\UIBundle;
            |
            |class OroUIBundle {}
          """.stripMargin()
        )

        configureByText(oroUIPath + "layout.js", "")
        configureByText(oroUIPath + "layout/layout2.js", "")
        configureByText(oroUIPath + "layout/layout3.js", "")
        configureByText(oroUIPath + "app.js", "")
        configureByText(oroUIPath + "some/func.js", "")
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
}
