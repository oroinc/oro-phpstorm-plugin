package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.intellij.psi.PsiPolyVariantReferenceBase
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest;

public class PhpReferenceInAclTest extends CompletionTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp()


        myFixture.configureByText("classes.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AddressBundle\\Controller {
            |  class AdminController {
            |    public function editAction() {}
            |  }
            |}
            |
            |namespace Oro\\Bundle\\AddressBundle\\Entity {
            |  class Address {}
            |}
            |
            |
          """.stripMargin()
        )
    }

    def void "test: php class should be inside class key"() {
        checkPhpReference(
            """
            |some:
            |  class: "Oro\\Bundle\\AddressBundle\\Entity\\Address<caret>"
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: detect php class with escaped namespace separator"() {
        checkPhpReference(
            """
            |some:
            |  class: "Oro\\\\Bundle\\\\AddressBundle\\\\Entity\\\\Address<caret>"
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: should suggest class by name"() {
        suggestions(
            """
            |some:
            |  class: <caret>
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: should complete fq class name"() {
        completion(
            """
            |some:
            |  class: Addr<caret>
            """.stripMargin(),
            """
            |some:
            |  class: "Oro\\\\Bundle\\\\AddressBundle\\\\Entity\\\\Address"
            """.stripMargin()
        )
    }

    def void "test: should complete fq class name in quotes"() {
        completion(
            """
            |some:
            |  class: "Addr<caret>"
            """.stripMargin(),
            """
            |some:
            |  class: "Oro\\\\Bundle\\\\AddressBundle\\\\Entity\\\\Address"
            """.stripMargin()
        )
    }

    def void "test: should suggest controller class in bindings"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: <caret> }
            """.stripMargin(),

            ["AdminController"]
        )
    }

    def void "test: should suggest controller action in bindings"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: "Oro\\\\Bundle\\\\AddressBundle\\\\Controller\\\\AdminController", method: <caret> }
            """.stripMargin(),

            ["editAction"]
        )
    }

    def void "test: detect php method reference"() {
        checkPhpReference(
            """
            |some:
            |  bindings:
            |    - { class: "Oro\\\\Bundle\\\\AddressBundle\\\\Controller\\\\AdminController", method: editActi<caret>on }
            """.stripMargin(),

            ["editAction"]
        )
    }

    private def checkPhpReference(String content, List<String> expectedReferences) {
        assertEquals(expectedReferences, getPhpReference(content))
    }

    private def List<String> getPhpReference(String content) {
        myFixture.configureByText("acl.yml", content)

        myFixture.getProject().getBaseDir().refresh(false, true)

        def element = myFixture.getFile().findElementAt(myFixture.getCaretOffset())
        def elements = [element, element.getParent(), element.getParent().getParent()]

        elements.collect { it.getReferences() }
                .flatten()
                .findAll { it instanceof PsiPolyVariantReferenceBase }
                .collect {  it as PsiPolyVariantReferenceBase }
                .collect { it.multiResolve(false) }
                .flatten()
                .collect { (it.getElement() as PhpNamedElement).getName() }
                .unique()
                .toList()
    }
}
