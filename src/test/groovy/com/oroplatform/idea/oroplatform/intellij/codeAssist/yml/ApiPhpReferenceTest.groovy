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
            |  class PostSerializeHandler {
            |    public static function someFunc() {}
            |  }
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

    def void "test: detect field reference"() {
        checkPhpReference(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      sorters:
            |        fields:
            |          fie<caret>ld1: ~
            """.stripMargin(),
            ["Oro\\Bundle\\AcmeBundle\\Entity\\Address.\$field1"]
        )
    }

    def void "test: suggest php class in post_serialize"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      post_serialize: [<caret>]
            """.stripMargin(),
            ["PostSerializeHandler"]
        )
    }

    def void "test: suggest php class method in post_serialize"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      post_serialize: [Oro\\Bundle\\AcmeBundle\\PostSerializeHandler, <caret>]
            """.stripMargin(),
            ["someFunc"]
        )
    }

    def void "test: detect php class in post_serialize"() {
        checkPhpReference(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      post_serialize: [Oro\\Bundle\\AcmeB<caret>undle\\PostSerializeHandler]
            """.stripMargin(),
            ["Oro\\Bundle\\AcmeBundle\\PostSerializeHandler"]
        )
    }

    def void "test: detect php class method in post_serialize"() {
        checkPhpReference(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      post_serialize: [Oro\\Bundle\\AcmeBundle\\PostSerializeHandler, someF<caret>unc]
            """.stripMargin(),
            ["Oro\\Bundle\\AcmeBundle\\PostSerializeHandler.someFunc"]
        )
    }

    def void "test: suggest quoted php class in post_serialize"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      post_serialize: ["<caret>"]
            """.stripMargin(),
            ["PostSerializeHandler"]
        )
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor())
    }
}
