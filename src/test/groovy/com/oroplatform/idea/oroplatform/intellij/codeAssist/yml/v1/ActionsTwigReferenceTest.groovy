package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class ActionsTwigReferenceTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
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

        configureByText("vendor/acme2-bundle/Acme2Bundle.php",
            """
            |<?php
            |namespace Oro\\Bundle\\Acme2Bundle {
            
            |  class Acme2Bundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin("|")
        )

        configureByText("vendor/acme2-bundle/Resources/views/acme2Some1.html.twig", "abc")
        configureByText("vendor/acme2-bundle/Resources/views/Foo/acme2Some2.html.twig", "abc")
        configureByText("vendor/acme2-bundle/Resources/views/Foo/bar/acme2Some3.html.twig", "abc")
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

    def void "test: suggest twig templates from not standard bundle location"() {
        suggestions(
            """
            |operations:
            |  some_op:
            |    button_options:
            |      template: <caret>
            """.stripMargin("|"),
            ["OroAcme2Bundle::acme2Some1.html.twig", "OroAcme2Bundle:Foo:acme2Some2.html.twig", "OroAcme2Bundle:Foo:bar/acme2Some3.html.twig"]
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

    def void "test: detect twig template reference from not standard bundle location"() {
        checkReference(
            """
            |operations:
            |  some_op:
            |    button_options:
            |      template: OroAcme2Bundle:Fo<caret>o:bar/acme2Some3.html.twig
            """.stripMargin("|"),
            ["acme2Some3.html.twig"]
        )
    }
}
