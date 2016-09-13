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
            |  use Oro\\Bundle;
            |  use Oro\\Bundle as SomeAlias;
            |  class Country {
            |    private \$name;
            |    private \$code;
            |    /**
            |     * @ORM\\ManyToMany(
            |     *      targetEntity="Oro\\Bundle\\AcmeBundle\\Entity\\City",
            |     *      cascade={"ALL"},
            |     *      orphanRemoval=true
            |     * )
            |     */
            |    private \$cities;
            |    /**
            |     * @ORM\\ManyToMany(
            |     *      targetEntity="City",
            |     *      cascade={"ALL"},
            |     *      orphanRemoval=true
            |     * )
            |     */
            |    private \$citiesBySimpleName;
            |    /**
            |     * @ORM\\ManyToMany(
            |     *      targetEntity="Bundle\\AcmeBundle\\Entity\\City",
            |     *      cascade={"ALL"},
            |     *      orphanRemoval=true
            |     * )
            |     */
            |    private \$citiesByImportedNamespace;
            |    /**
            |     * @ORM\\ManyToMany(
            |     *      targetEntity="SomeAlias\\AcmeBundle\\Entity\\City",
            |     *      cascade={"ALL"},
            |     *      orphanRemoval=true
            |     * )
            |     */
            |    private \$citiesByAliasedNamespace;
            |    /**
            |     * @ORM\\ManyToMany(targetEntity="Oro\\Bundle\\AcmeBundle\\Entity\\City", cascade={"ALL"}, orphanRemoval=true)
            |     */
            |    private \$citiesInSingleLine;
            |  }
            |
            |  class City {
            |    private \$string;
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

    def void "test: not detect entity reference for 'name' key"() {
        checkPhpReference(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  fields:
            |    -
            |      na<caret>me: code
            """.stripMargin(),
            []
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

    def void "test: suggest relation field names"() {
        suggestions(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  fields:
            |    -
            |      name: cities
            |      relation_fields:
            |        -
            |          name: <caret>
            """.stripMargin(),
            ["string"]
        )
    }

    def void "test: suggest relation field names by simple name"() {
        suggestions(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  fields:
            |    -
            |      name: citiesBySimpleName
            |      relation_fields:
            |        -
            |          name: <caret>
            """.stripMargin(),
            ["string"]
        )
    }

    def void "test: suggest relation field names by imported namespace"() {
        suggestions(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  fields:
            |    -
            |      name: citiesByImportedNamespace
            |      relation_fields:
            |        -
            |          name: <caret>
            """.stripMargin(),
            ["string"]
        )
    }

    def void "test: suggest relation field names by aliased namespace"() {
        suggestions(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  fields:
            |    -
            |      name: citiesByAliasedNamespace
            |      relation_fields:
            |        -
            |          name: <caret>
            """.stripMargin(),
            ["string"]
        )
    }

    def void "test: suggest relation field names with the PhpDoc in single line"() {
        suggestions(
            """
            |Oro\\Bundle\\AcmeBundle\\Entity\\Country:
            |  fields:
            |    -
            |      name: citiesInSingleLine
            |      relation_fields:
            |        -
            |          name: <caret>
            """.stripMargin(),
            ["string"]
        )
    }
}
