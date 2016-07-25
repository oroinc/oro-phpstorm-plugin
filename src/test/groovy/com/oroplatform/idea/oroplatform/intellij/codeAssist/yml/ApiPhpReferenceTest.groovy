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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor())
    }
}
