package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript

import com.oroplatform.idea.oroplatform.intellij.codeAssist.FileReferenceTest
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings

class RequireJsReferenceTest extends FileReferenceTest {
    @Override
    String fileName() {
        return "some.js"
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        OroPlatformSettings.getInstance(myFixture.project).setAppDir("app")

        configureByText("vendor/Oro/Bundle/UIBundle/OroUIBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\UIBundle;
            |
            |class OroUIBundle {}
          """.stripMargin()
        )

        configureByText("vendor/Oro/Bundle/UIBundle/Resources/public/js/layout.js", "")
        configureByText("vendor/Oro/Bundle/UIBundle/Resources/public/js/layout/layout2.js", "")
        configureByText("vendor/Oro/Bundle/UIBundle/Resources/public/js/layout/layout3.js", "")
        configureByText("vendor/Oro/Bundle/UIBundle/Resources/public/js/app.js", "")
        configureByText("vendor/Oro/Bundle/UIBundle/Resources/public/js/some/func.js", "")
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

}
