package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.ReferenceTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class ActionsTwigReferenceTest extends ReferenceTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.ACTIONS
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            |
            |namespace Symfony\\Component\\HttpKernel\\Bundle {
            |  class Bundle {}
            |}
            """.stripMargin("|")
        )
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/some1.html.twig", "abc")
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/Foo/some2.html.twig", "abc")
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/Foo/bar/some3.html.twig", "abc")
    }

    def void "test: suggest twig templates"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    button_options:
            |      template: <caret>
            """.stripMargin("|"),
            ["OroAcmeBundle::some1.html.twig", "OroAcmeBundle:Foo:some2.html.twig", "OroAcmeBundle:Foo:bar/some3.html.twig"]
        )
    }

    def void "test: detect twig template reference"() {
        checkReference(
            """
            |operations:
            |  some_op:
            |    button_options:
            |      template: OroAcmeBundle:Fo<caret>o:some2.html.twig
            """.stripMargin("|"),
            ["some2.html.twig"]
        )
    }
}
