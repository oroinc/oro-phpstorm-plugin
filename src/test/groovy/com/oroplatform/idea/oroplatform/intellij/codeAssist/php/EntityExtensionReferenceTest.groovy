package com.oroplatform.idea.oroplatform.intellij.codeAssist.php

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest

class EntityExtensionReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return "test.php"
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText(
            "classes.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class AcmeBundle extends \\Symfony\\Component\\HttpKernel\\Bundle\\Bundle {}
            |}
            |
            |namespace Oro\\Bundle\\AcmeBundle\\Entity {
            |  class Country {
            |    public function getName(){}
            |  }
            |}
            |
            |namespace Extend\\Entity {
            |  class EX_OroAcmeBundle_Country {
            |    public function getIsoName(){}
            |  }
            |}
            """.stripMargin()
        )
    }

    def void "test: suggest extension method for variable"() {
        suggestions(
            """
            |<?php
            |\$country = new Oro\\Bundle\\AcmeBundle\\Entity\\Country();
            |\$country-><caret>
            """.stripMargin(),
            ["getName", "getIsoName"]
        )
    }

    def void "test: suggest extension method for property with defined value"() {
        suggestions(
            """
            |<?php
            |class SomeClass {
            |  public \$country;
            |  public function __construct() {
            |    \$this->country = new \\Oro\\Bundle\\AcmeBundle\\Entity\\Country();
            |  }
            |}
            |\$obj = new SomeClass();
            |\$obj->country-><caret>
            """.stripMargin(),
            ["getName", "getIsoName"]
        )
    }

    def void "test: suggest extension method for variable with @var phpDoc"() {
        suggestions(
            """
            |<?php
            |/** @var \$country Oro\\Bundle\\AcmeBundle\\Entity\\Country */
            |\$country = null;
            |\$country-><caret>
            """.stripMargin(),
            ["getName", "getIsoName"]
        )
    }

    def void "test: suggest extension method for method return type"() {
        suggestions(
            """
            |<?php
            |class SomeClass {
            |  public static function getCountry() { return new Oro\\Bundle\\AcmeBundle\\Entity\\Country(); }
            |}
            |SomeClass::getCountry()-><caret>
            """.stripMargin(),
            ["getName", "getIsoName"]
        )
    }

    def void "test: suggest extension method for method with @return phpDoc"() {
        suggestions(
            """
            |<?php
            |class SomeClass {
            |  /** @return Oro\\Bundle\\AcmeBundle\\Entity\\Country */
            |  public static function getCountry() { return null; }
            |}
            |SomeClass::getCountry()-><caret>
            """.stripMargin(),
            ["getName", "getIsoName"]
        )
    }

    def void "test: suggest extension method for property with @var phpDoc"() {
        suggestions(
            """
            |<?php
            |class SomeClass {
            |  /** @var Oro\\Bundle\\AcmeBundle\\Entity\\Country */
            |  public static \$country;
            |}
            |SomeClass::\$country-><caret>
            """.stripMargin(),
            ["getName", "getIsoName"]
        )
    }
}
