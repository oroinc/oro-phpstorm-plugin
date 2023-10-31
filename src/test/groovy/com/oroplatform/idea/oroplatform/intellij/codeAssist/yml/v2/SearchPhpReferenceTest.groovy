package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2


class SearchPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.SEARCH
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
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: suggest field names"() {
        suggestions(
            """
            |search:
            |  Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |    fields:
            |      -
            |        name: <caret>
            """.stripMargin(),
            ["name", "code"]
        )
    }
}
