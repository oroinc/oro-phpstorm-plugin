package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class ActionsPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        myFixture.configureByText("classes.php",
            """
            |<?php
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Country {}
            |
            |  class City {}
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: detect entity reference in entities property"() {
        checkPhpReference(
            """
            |operations:
            |  some_op:
            |    entities:
            |      - Oro\\Bundle\\AcmeBundl<caret>e\\Entity\\Country
            """.stripMargin(),

            ["Oro\\Bundle\\AcmeBundle\\Entity\\Country"]
        )
    }
}
