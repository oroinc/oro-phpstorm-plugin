package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class ApiCompletionTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.API
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("src/Oro/Bundle/AcmeBundle/Resources/doc/api/user.md", "test")

        configureByText("src/Oro/Bundle/AcmeBundle/AcmeBundle.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            """.stripMargin()
        )

        configureByText("src/Oro/Bundle/AcmeBundle/Entity/Address.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Address {
            |    private \$field1;
            |    private \$field2;
            |  }
            |}
            """.stripMargin()
        )
    }

    def void "test: suggest root property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["api"]
        )
    }

    def void "test: suggest api properties"() {
        suggestions(
            """
            |api:
            |  <caret>
            """.stripMargin(),

            ["entities", "relations", "entity_aliases"]
        )
    }

    def void "test: suggest entity_aliases properties"() {
        suggestions(
            """
            |api:
            |  entity_aliases:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            ["alias", "plural_alias"]
        )
    }

    def void "test: suggest new properties for fields"() {
        suggestions(
            """
            |api:
            |  entities:
            |    stdClass:
            |      fields:
            |        field1:
            |          <caret>
            """.stripMargin(),

            ["depends_on", "fields"]
        )
    }

    def void "test: suggest new properties for entities"() {
        suggestions(
            """
            |api:
            |  entities:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            ["documentation_resource"]
        )
    }

    def void "test: suggest fields for depends_on section"() {
        suggestions(
            """
            |api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      fields:
            |        field1:
            |          depends_on: [<caret>]
            """.stripMargin(),

            ["field2"]
        )
    }

    def void "test: suggest documentation resource"() {
        suggestions(
            """
            |api:
            |  entities:
            |    stdClass:
            |      documentation_resource: <caret>
            """.stripMargin(),

            ["@OroAcmeBundle/Resources/doc/api/user.md"]
        )
    }

    def void "test: detect documentation resource reference"() {
        checkReference(
            """
            |api:
            |  entities:
            |    stdClass:
            |      documentation_resource: @OroAcmeBundle/Reso<caret>urces/doc/api/user.md
            """.stripMargin(),

            ["user.md"]
        )
    }

    def void "test: suggest fields for fields property"() {
        suggestions(
            """
            |api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      fields:
            |        field1:
            |          fields:
            |            <caret>
            """.stripMargin(),

            ["field2"]
        )
    }

    def void "test: suggest properties for nested fields"() {
        suggestions(
            """
            |api:
            |  entities:
            |    Oro\\Bundle\\AcmeBundle\\Entity\\Address:
            |      fields:
            |        field1:
            |          fields:
            |            field2:
            |              <caret>
            """.stripMargin(),

            ["depends_on", "property_path", "data_type"]
        )
    }

    def void "test: suggest disable_meta_properties property for actions"() {
        suggestions(
            """
            |api:
            |  entities:
            |    stdClass:
            |      actions:
            |        get_list:
            |          <caret>
            """.stripMargin(),

            ["disable_meta_properties"]
        )
    }
}
