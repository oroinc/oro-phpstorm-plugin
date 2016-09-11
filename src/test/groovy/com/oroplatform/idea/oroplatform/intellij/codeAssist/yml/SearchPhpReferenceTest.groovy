package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.Schemas


class SearchPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return Schemas.FilePathPatterns.SEARCH
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
            |  class Country {
            |    private \$name;
            |    private \$code;
            |  }
            |
            |  class City {
            |    private \$name;
            |  }
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: suggest entity name"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),
            ["Country", "City"]
        )
    }

    def void "test: detect entity reference"() {
        checkPhpReference(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Co<caret>untry: ~
            """.stripMargin(),
            ["Oro\\Bundle\\AcmeBundle\\Entity\\Country"]
        )
    }

    def void "test: suggest field names"() {
        suggestions(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  fields:
            |    -
            |      name: <caret>
            """.stripMargin(),
            ["name", "code"]
        )
    }

    def void "test: suggest title fields names"() {
        suggestions(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  title_fields: [<caret>]
            """.stripMargin(),
            ["name", "code"]
        )
    }
}
