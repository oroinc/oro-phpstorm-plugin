package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class ApiDicCompletionTest extends PhpReferenceTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.API
    }

    def service1 = randomIdentifier("service1")
    def service2 = randomIdentifier("service2")
    def service3 = randomIdentifier("service3")
    def service4 = randomIdentifier("service4")
    def service5 = randomIdentifier("service5")
    def service7 = randomIdentifier("service7")
    def service8 = randomIdentifier("service8")
    def form1 = randomIdentifier("form1")
    def form2 = randomIdentifier("form2")
    def form3 = randomIdentifier("form3")

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("classes.php",
            """
            |<?php
            |namespace Oro\\Forms;
            |class UserFormType {}
            |class ContactFormType {}
            """.stripMargin()
        )

        configureByText("Resources/config/oro/app.yml",
            """
            |oro_api:
            |  form_types:
            |    - form.type.$form1
            """.stripMargin()
        )

        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="$service1"></service>
            |    <service id="$service2"></service>
            |    <service id="$service3"></service>
            |    <service id="form.type.$form1" class="Oro\\Forms\\UserFormType">
            |      <tag name="form.type" alias="$form1"/>
            |    </service>
            |    <service id="$service7">
            |      <tag name="form.type" alias="$form2"/>
            |    </service>
            |    <service id="$service8" class="Oro\\Forms\\ContactFormType">
            |      <tag name="oro.api.form.type" alias="$form3"/>
            |    </service>
            |  </services>
            |</container>
          """.stripMargin()
        )

        configureByText("Resources/config/services.yml",
            """
            |services:
            |  $service4:
            |    class: ~
            |  $service5:
            |    class: ~
            """.stripMargin()
        )
    }

    def void "test: suggest services from xml files as delete_handler"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      delete_handler: <caret>
            """.stripMargin(),
            [service1, service2, service3]
        )
    }

    def void "test: suggest services from yml files as delete_handler"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      delete_handler: <caret>
            """.stripMargin(),
            [service4, service5]
        )
    }

    def void "test: suggest api form types as fields.form_type"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      fields:
            |         field1:
            |           form_type: <caret>
            """.stripMargin(),
            [form3]
        )
    }

    def void "test: suggest standard form types defined in app.yml as fields.form_type"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      fields:
            |         field1:
            |           form_type: <caret>
            """.stripMargin(),
            [form1],
            [form2]
        )
    }

    def void "test: detect standard form types references in fields.form_type"() {
        checkPhpReference(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      fields:
            |         field1:
            |           form_type: ${insertSomewhere(form1, "<caret>")}
            """.stripMargin(),
            ["Oro\\Forms\\UserFormType"]
        )
    }

    def void "test: detect oro form types references in fields.form_type"() {
        checkPhpReference(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      fields:
            |         field1:
            |           form_type: ${insertSomewhere(form3, "<caret>")}
            """.stripMargin(),
            ["Oro\\Forms\\ContactFormType"]
        )
    }
}
