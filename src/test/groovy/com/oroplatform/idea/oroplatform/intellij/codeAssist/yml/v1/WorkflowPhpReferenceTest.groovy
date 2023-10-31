package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class WorkflowPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.WORKFLOW
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp()


        myFixture.configureByText("classes.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class SomeClass {
            |  }
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Address extends AbstractEntity {
            |    private \$name;
            |  }
            |  abstract class AbstractEntity {}
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: php class should be inside class key"() {
        checkPhpReference(
            """
            |workflows:
            |  some:
            |    attributes:
            |      some:
            |        options:
            |          class: Oro\\Bundle\\AcmeBundle\\Some<caret>Class
            """.stripMargin(),

            ["Oro\\Bundle\\AcmeBundle\\SomeClass"]
        )
    }

    def void "test: suggest php classes inside class key"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      some:
            |        options:
            |          class: <caret>
            """.stripMargin(),

            ["SomeClass", "Address"]
        )
    }

    def void "test: suggest entity field in trigger"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transitions:
            |      some:
            |        triggers:
            |          - entity_class: Oro\\Bundle\\AcmeBundle\\Entity\\Address
            |            field: <caret>
            """.stripMargin(),

            ["name"]
        )
    }
}
