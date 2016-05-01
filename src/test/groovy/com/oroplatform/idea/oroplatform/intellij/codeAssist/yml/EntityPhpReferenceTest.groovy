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
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Country {
            |    private \$name;
            |  }
            |
            |  class City {}
            |
            |  class CountryManager {}
            |  class CountryRepository implements \\Doctrine\\Common\\Persistence\\ObjectRepository {}
            |}
            |
            |namespace Doctrine\\Common\\Persistence {
            |  interface ObjectRepository {}
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

    def void "test: should suggest entity full name in entity_alias_exclusions"() {
        suggestions(
            """
            |oro_entity:
            |  entity_alias_exclusions:
            |    - <caret>
            """.stripMargin(),
            ["Country"],
            ["OroAcmeBundle:Country"]
        )
    }

    def void "test: should not suggest managers and repositories as entities"() {
        suggestions(
            """
            |oro_entity:
            |  entity_alias_exclusions:
            |    - <caret>
            """.stripMargin(),
            [],
            ["CountryManager", "CountryRepository"]
        )
    }

    def void "test: should suggest entity full name as key in entity_aliases"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    <caret>
            """.stripMargin(),
            ["Country"],
            ["OroAcmeBundle:Country"]
        )
    }

    def void "test: should suggest entity full name as existing key in entity_aliases"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    <caret>: {}
            """.stripMargin(),
            ["Country"],
            ["OroAcmeBundle:Country"]
        )
    }

    def void "test: should not suggest entity in entity_aliases item"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |      <caret>
            """.stripMargin(),
            [],
            ["Country"]
        )
    }

    def void "test: should resolve entity reference in entity_aliases"() {
        checkPhpReference(
            """
            |oro_entity:
            |  entity_aliases:
            |    OroAcmeBundle:Co<caret>untry: {}
            """.stripMargin(),
            ["Country"]
        )
    }

    def void "test: should resolve full entity reference in entity_aliases"() {
        checkPhpReference(
            """
            |oro_entity:
            |  entity_aliases:
            |    Oro\\Bundle\\AcmeBundle\\Ent<caret>ity\\Country: {}
            """.stripMargin(),
            ["Country"]
        )
    }

    def void "test: should complete entity name in entity_aliases"() {
        completion(
            """
            |oro_entity:
            |  entity_aliases:
            |    Cou<caret>
            """.stripMargin(),
            """
            |oro_entity:
            |  entity_aliases:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Country: <caret>
            """.stripMargin(),
        )
    }

    def void "test: should suggest entity full name for second item in entity_aliases"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Country: {}
            |    <caret>
            """.stripMargin(),
            ["City"],
            []
        )
    }

    def void "test: should suggest entity full name for existing key of second item in entity_aliases"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Country: {}
            |    <caret>: {}
            """.stripMargin(),
            ["City"],
            []
        )
    }

    def void "test: should not suggest already defined entity in entity_aliases"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Country: {}
            |    <caret>: {}
            """.stripMargin(),
            [],
            ["Country"]
        )
    }

    def void "test: should not suggest already defined entity in key in progress of entity_aliases"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Country: {}
            |    <caret>
            """.stripMargin(),
            [],
            ["Country"]
        )
    }


    def void "test: should not suggest entity key in value place"() {
        suggestions(
            """
            |oro_entity:
            |  entity_aliases: <caret>
            """.stripMargin(),
            [],
            ["Country"]
        )
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LoggedErrorProcessor.setNewInstance(new LoggedErrorProcessor())
    }
}
