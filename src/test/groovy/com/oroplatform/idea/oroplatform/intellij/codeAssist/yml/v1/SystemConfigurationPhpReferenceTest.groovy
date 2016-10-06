package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class SystemConfigurationPhpReferenceTest extends PhpReferenceTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.SYSTEM_CONFIGURATION
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        myFixture.configureByText("classes.php",
            """
            |<?php
            |
            |namespace Oro\\Bundle\\AcmeBundle {
            |  class SettingsFormConfigurator {
            |    public static function buildForm(){}
            |    public static function buildForm2(){}
            |    public function instanceFunction(){}
            |  }
            |}
            |
          """.stripMargin()
        )
    }

    def void "test: resolve php class in configurator"() {
        checkPhpReference(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: Oro\\Bundle\\Acme<caret>Bundle\\SettingsFormConfigurator
            """.stripMargin(),

            ["Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator"]
        )
    }

    def void "test: suggest php class in configurator"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: <caret>
            """.stripMargin(),

            ["SettingsFormConfigurator"]
        )
    }

    def void "test: resolve quoted php class in configurator"() {
        checkPhpReference(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: "Oro\\\\Bundle\\\\Acme<caret>Bundle\\\\SettingsFormConfigurator"
            """.stripMargin(),

            ["Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator"]
        )
    }

    def void "test: resolve php method in configurator"() {
        checkPhpReference(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator::buil<caret>dForm
            """.stripMargin(),

            ["Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator", "Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator.buildForm"]
        )
    }

    def void "test: suggest php method in configurator"() {
        suggestions(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator::<caret>
            """.stripMargin(),

            ["Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator::buildForm"],
            ["Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator::instanceFunction"]
        )
    }

    def void "test: complete colon after php class in configurator"() {
        completion(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: SettingsFormConfigurat<caret>
            """.stripMargin(),

            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: Oro\\Bundle\\AcmeBundle\\SettingsFormConfigurator::<caret>
            """.stripMargin()
        )
    }

    def void "test: complete colon after quoted php class in configurator"() {
        completion(
            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: "SettingsFormConfigurat<caret>"
            """.stripMargin(),

            """
            |oro_system_configuration:
            |  groups:
            |    platform:
            |      configurator: "Oro\\\\Bundle\\\\AcmeBundle\\\\SettingsFormConfigurator::<caret>"
            """.stripMargin()
        )
    }
}
