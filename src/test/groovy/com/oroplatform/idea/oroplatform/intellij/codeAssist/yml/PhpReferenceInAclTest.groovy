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
            |namespace Oro\\Bundle\\AcmeBundle\\Controller {
            |  class AdminController {
            |    public function editAction() {}
            |    public function someFunc() {}
            |  }
            |  class RegionController {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Controller\\Api {
            |  class AddressApiController {}
            |  class RegionController {}
            |}
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Address extends AbstractAddress {}
            |  abstract class AbstractAddress {}
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: php class should be inside class key"() {
        checkPhpReference(
            """
            |some:
            |  class: "Oro\\Bundle\\AcmeBundle\\Entity\\Address<caret>"
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: detect php class with escaped namespace separator"() {
        checkPhpReference(
            """
            |some:
            |  class: "Oro\\\\Bundle\\\\AcmeBundle\\\\Entity\\\\Address<caret>"
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: detect php entity in shortcut notation"() {
        checkPhpReference(
            """
            |some:
            |  class: OroAcmeBundle:Addre<caret>ss
            """.stripMargin(),

            ["Address"]
        )
    }

    def void "test: should not suggest entity class by full name"() {
        suggestions(
            """
            |some:
            |  class: <caret>
            """.stripMargin(),

            [],
            ["Address", "AdminController"]
        )
    }

    def void "test: should not suggest abstract classes as entity"() {
        suggestions(
            """
            |some:
            |  class: <caret>
            """.stripMargin(),

            [],
            ["OroAcmeBundle:AbstractAddress"]
        )
    }

    def void "test: should suggest entity shortcut class name"() {
        suggestions(
            """
            |some:
            |  class: <caret>
            """.stripMargin(),

            ["OroAcmeBundle:Address"]
        )
    }

    def void "test: should suggest controllers also in subnamespaces of Controller"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: <caret> }
            """.stripMargin(),
            ["AddressApiController"]
        )
    }

    def void "test: should suggest classes with the same names"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: <caret> }
            """.stripMargin(),
            ["RegionController","RegionController"]
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
            |  class: OroAcmeBundle:Address
            """.stripMargin()
        )
    }

    def void "test: should complete fq class name - eof"() {
        completion(
            """
            |some:
            |  class: Addr<caret>""".stripMargin(),
            """
            |some:
            |  class: OroAcmeBundle:Address""".stripMargin()
        )
    }

    def void "test: should complete entity shortcut class name in quotes"() {
        completion(
            """
            |some:
            |  class: "Addr<caret>"
            """.stripMargin(),
            """
            |some:
            |  class: "OroAcmeBundle:Address"
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

            ["AdminController"],
            ["Address"]
        )
    }

    def void "test: should suggest controller action in bindings"() {
        suggestions(
            """
            |some:
            |  bindings:
            |    - { class: "Oro\\\\Bundle\\\\AcmeBundle\\\\Controller\\\\AdminController", method: <caret> }
            """.stripMargin(),

            ["editAction"],
            ["someFunc"]
        )
    }

    def void "test: detect php method reference"() {
        checkPhpReference(
            """
            |some:
            |  bindings:
            |    - { class: "Oro\\\\Bundle\\\\AcmeBundle\\\\Controller\\\\AdminController", method: editActi<caret>on }
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

    private def suggestions(String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions = []) {
        return super.suggestions("acl.yml", contents, expectedSuggestions, unexpectedSuggestions)
    }

    private def completion(String contents, String expected) {
        return super.completion("acl.yml", contents, expected)
    }
}
