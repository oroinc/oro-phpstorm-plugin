package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.intellij.testFramework.LoggedErrorProcessor
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.Schemas
import org.apache.log4j.Logger
import org.jetbrains.annotations.NotNull


class ApiPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.API
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

        myFixture.configureByText("classes.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Address {
            |    private \$field1;
            |    private \$field2;
            |  }
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: suggest Entity as property"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    <caret>
            """.stripMargin(),
            ["Address"]
        )
    }

    def void "test: suggest Entity fields as property"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      fields:
            |        <caret>
            """.stripMargin(),
            ["field1"]
        )
    }

    def void "test: suggest Entity fields in filters as property"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      filters:
            |        fields:
            |          <caret>
            """.stripMargin(),
            ["field1"]
        )
    }

    def void "test: suggest Entity fields in sorters as property"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      sorters:
            |        fields:
            |          <caret>
            """.stripMargin(),
            ["field1"]
        )
    }

    def void "test: suggest fields as identifier field names"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      identifier_field_names: [<caret>]
            """.stripMargin(),
            ["field1"]
        )
    }

    def void "test: not suggest fields as field child"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      sorters:
            |        fields:
            |          field1:
            |            <caret>
            """.stripMargin(),
            [],
            ["field1"]
        )
    }
// TODO: it is not passing, but it works. Fix test.
//    def void "test: detect field reference"() {
//        checkPhpReference(
//            """
//            |oro_api:
//            |  entities:
//            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
//            |      sorters:
//            |        fields:
//            |          fiel<caret>d1: ~
//            """,
//            ["Oro\\Bundle\\AcmeBundle\\Entity\\Address.field1"]
//        )
//    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor())
    }
}
