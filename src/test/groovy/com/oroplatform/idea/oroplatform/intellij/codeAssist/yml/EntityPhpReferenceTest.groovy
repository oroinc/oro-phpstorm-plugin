package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.intellij.testFramework.LoggedErrorProcessor
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import org.apache.log4j.Logger
import org.jetbrains.annotations.NotNull


class EntityPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return "entity.yml"
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
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Country {
            |    private \$name;
            |  }
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: detect php field reference"() {
        checkPhpReference(
            """
            |oro_entity:
            |  exclusions:
            |    - { entity: Oro\\Bundle\\AcmeBundle\\Entity\\Country, field: na<caret>me }
            """.stripMargin(),

            ["name"]
        )
    }


    def void "test: detect php field reference for shortcut entity class name"() {
        checkPhpReference(
            """
            |oro_entity:
            |  exclusions:
            |    - { entity: OroAcmeBundle:Country, field: na<caret>me }
            """.stripMargin(),

            ["name"]
        )
    }


    def void "test: should handle gracefully missing class name in entity shortcut"() {
        checkPhpReference(
            """
            |oro_entity:
            |  exclusions:
            |    - { entity: OroAcmeBundle:, field: na<caret>me }
            """.stripMargin(),

            []
        )
    }

    def void "test: should suggest entity fields"() {
        suggestions(
            """
            |oro_entity:
            |  exclusions:
            |    - { entity: Oro\\Bundle\\AcmeBundle\\Entity\\Country, field: <caret> }
            """.stripMargin(),
            ["name"]
        )
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor())
    }
}
