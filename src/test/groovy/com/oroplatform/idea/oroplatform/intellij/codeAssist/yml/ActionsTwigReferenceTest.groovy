package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.TwigReferenceTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class ActionsTwigReferenceTest extends TwigReferenceTest {
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
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/some.html.twig", "abc")
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/Foo/some.html.twig", "abc")
        configureByText("src/Oro/Bundle/AcmeBundle/Resources/views/Foo/bar/some.html.twig", "abc")
    }

    def void "test: suggest twig templates"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    button_options:
            |      template: <caret>
            """.stripMargin("|"),
            ["OroAcmeBundle::some.html.twig", "OroAcmeBundle:Foo:some.html.twig", "OroAcmeBundle:Foo:bar/some.html.twig"]
        )
    }
}
