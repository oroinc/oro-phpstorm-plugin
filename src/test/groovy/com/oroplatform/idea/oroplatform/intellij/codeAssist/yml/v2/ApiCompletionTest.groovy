package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.testFramework.LoggedErrorProcessor
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2
import org.apache.log4j.Logger
import org.jetbrains.annotations.NotNull

class ApiCompletionTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.API
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp()

        //turn off falling tests on internal errors because there is bug in php plugin during indexing class with field
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor() {
            @Override
            void processError(String message, Throwable t, String[] details, @NotNull Logger logger) {
            }
        })

        configureByText("src/Oro/Bundle/AcmeBundle/Resources/doc/api/user.md", "test")

        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )

        configureByText("src/Oro/Bundle/AcmeBundle/Entity/Address.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Address {
            |    private \$field1;
            |    private \$field2;
            |  }
            |}
            """.stripMargin()
        )
    }

    def void "test: suggest root property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["api"]
        )
    }

    def void "test: suggest api properties"() {
        suggestions(
            """
            |api:
            |  <caret>
            """.stripMargin(),

            ["entities", "relations", "entity_aliases"]
        )
    }

    def void "test: suggest entity_aliases properties"() {
        suggestions(
            """
            |api:
            |  entity_aliases:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            ["alias", "plural_alias"]
        )
    }

    def void "test: suggest new properties for fields"() {
        suggestions(
            """
            |api:
            |  entities:
            |    stdClass:
            |      fields:
            |        field1:
            |          <caret>
            """.stripMargin(),

            ["depends_on"]
        )
    }

    def void "test: suggest new properties for entities"() {
        suggestions(
            """
            |api:
            |  entities:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            ["documentation_resource"]
        )
    }

    def void "test: suggest fields for depends_on section"() {
        suggestions(
            """
            |api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      fields:
            |        field1:
            |          depends_on: [<caret>]
            """.stripMargin(),

            ["field2"]
        )
    }

    def void "test: suggest documentation resource"() {
        suggestions(
            """
            |api:
            |  entities:
            |    stdClass:
            |      documentation_resource: <caret>
            """.stripMargin(),

            ["@OroAcmeBundle/Resources/doc/api/user.md"]
        )
    }

    def void "test: detect documentation resource reference"() {
        checkReference(
            """
            |api:
            |  entities:
            |    stdClass:
            |      documentation_resource: @OroAcmeBundle/Reso<caret>urces/doc/api/user.md
            """.stripMargin(),

            ["user.md"]
        )
    }

    //TODO: refactor test hierarchy
    def checkReference(String content, List<String> expectedReferences) {
        assertEquals(expectedReferences, getReferences(content))
    }

    def List<String> getReferences(String content) {
        configureByText(content)

        myFixture.getProject().getBaseDir().refresh(false, true)

        def element = myFixture.getFile().findElementAt(myFixture.getCaretOffset())
        def elements = [element, element.getParent(), element.getParent().getParent()]

        elements.collect { it.getReferences() }
            .flatten()
            .findAll { it instanceof PsiPolyVariantReferenceBase }
            .collect {  it as PsiPolyVariantReferenceBase }
            .collect { it.multiResolve(false) }
            .flatten()
            .collect { (it.getElement() as PsiNamedElement).getName() }
            .unique()
            .toList()
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor())
    }
}
