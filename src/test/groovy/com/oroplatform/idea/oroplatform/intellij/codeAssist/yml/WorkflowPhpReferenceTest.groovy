package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class WorkflowPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.WORKFLOW
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
            |  class Address extends AbstractEntity {}
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
}
