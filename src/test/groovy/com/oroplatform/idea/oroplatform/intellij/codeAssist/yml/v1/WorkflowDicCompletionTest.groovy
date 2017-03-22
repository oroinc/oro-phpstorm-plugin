package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.PhpReferenceTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

class WorkflowDicCompletionTest extends PhpReferenceTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.WORKFLOW
    }

    def condition1 = randomIdentifier("condition1")
    def condition2a = randomIdentifier("condition2a")
    def condition2b = randomIdentifier("condition2b")
    def condition3 = randomIdentifier("condition3")
    def unknown = randomIdentifier("unknown")
    def action1 = randomIdentifier("action1")
    def action2a = randomIdentifier("action2a")
    def action2b = randomIdentifier("action2b")
    def action3 = randomIdentifier("action3")
    def assignUrlAction = randomIdentifier("assign_url")
    def orCondition = randomIdentifier("or")
    def trueCondition = randomIdentifier("true")

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText("std_classes.php",
            """
            |<?php
            |namespace Oro\\Component\\ConfigExpression\\Condition;
            |class AbstractComposite {}
            """.stripMargin()
        )

        configureByText("classes.php",
            """
            |<?php
            |namespace Oro\\AcmeBundle;
            |class AccountScopeProvider {
            |   public function getCriteriaField() {
            |       return 'account';
            |   }
            |}
            |class LocalizationScopeProvider {
            |   const LOCALIZATION_TYPE = 'localization';
            |   public function getCriteriaField() {
            |       return self::LOCALIZATION_TYPE;
            |   }
            |}
            |class CustomerScopeProvider {
            |   const CUSTOMER_TYPE = 'customer';
            |   public function getCriteriaField() {
            |       return self::CUSTOMER_TYPE;
            |   }
            |}
            |class UserScopeProvider {
            |   public function getCriteriaField() {
            |       return 'user';
            |   }
            |}
            |class LanguageScopeProvider {
            |   public function getCriteriaField() {
            |       return 'language';
            |   }
            |}
            |class NotCondition {}
            |class Condition2a {}
            |class OrCondition extends \\Oro\\Component\\ConfigExpression\\Condition\\AbstractComposite {}
            |class TrueCondition {}
            |class AssignUrlAction {
            |   const ATTRIBUTE = 'attribute';
            |   private function someFunction() {
            |       \$this->getOption(\$this->options, 'option_from_getOption');
            |       \$this->options['option_from_\$this_options'];
            |       \$options['options_that_shouldnt_exist'];
            |   }
            |   public function initialize(array \$options) {
            |    if (empty(\$options['route'])) {
            |        throw new InvalidParameterException('Route parameter must be specified');
            |    }
            |
            |    if (!empty(\$options['route_parameters']) && !is_array(\$options['route_parameters'])) {
            |        throw new InvalidParameterException('Route parameters must be an array');
            |    }
            |    if (empty(\$options[self::ATTRIBUTE])) {
            |        throw new InvalidParameterException('Attribiute parameters is required');
            |    }
            |   }
            |}
            """.stripMargin()
        )

        configureByText("Resources/config/services.xml",
            """
            |<container>
            |  <services>
            |    <service id="condition1_id" class="Oro\\AcmeBundle\\NotCondition">
            |      <tag name="oro_action.condition" alias="$condition1"/>
            |    </service>
            |    <service id="condition2_id" class="Oro\\AcmeBundle\\Condition2a">
            |      <tag name="oro_action.condition" alias="$condition2a|$condition2b"/>
            |    </service>
            |    <service id="condition4_id">
            |      <tag name="oro_action.condition" alias="condition4"/>
            |    </service>
            |    <service id="some_service">
            |      <tag name="xxx" alias="$unknown"/>
            |    </service>
            |    <service id="${action1}_id">
            |      <tag name="oro_action.action" alias="$action1"/>
            |    </service>
            |    <service id="${action2a}_id">
            |      <tag name="oro_action.action" alias="$action2a|$action2b"/>
            |    </service>
            |    <service id="scope1_id" class="Oro\\AcmeBundle\\AccountScopeProvider">
            |      <tag name="oro_scope.provider" scopeType="workflow_definition"/>
            |    </service>
            |  </services>
            |</container>
          """.stripMargin()
        )

        configureByText("Resources/config/services.yml",
            """
            |parameters:
            |   language.class: Oro\\AcmeBundle\\LanguageScopeProvider
            |services:
            |  condition3_id:
            |    tags:
            |      - { name: oro_action.condition, alias: $condition3 }
            |  ${action3}_id:
            |    tags:
            |      - { name: oro_action.action, alias: $action3 }
            |  assign_url:
            |    class: Oro\\AcmeBundle\\AssignUrlAction
            |    tags:
            |      - { name: oro_action.action, alias: $assignUrlAction }
            |  or:
            |    class: Oro\\AcmeBundle\\OrCondition
            |    tags:
            |      - { name: oro_action.condition, alias: $orCondition }
            |  true_condition:
            |    class: Oro\\AcmeBundle\\TrueCondition
            |    tags:
            |      - { name: oro_action.condition, alias: $trueCondition }
            |  scope2_id:
            |    class: Oro\\AcmeBundle\\LocalizationScopeProvider
            |    tags:
            |      - { name: oro_scope.provider, scopeType: workflow_definition }
            |  scope3_id:
            |    class: '%language.class%'
            |    tags:
            |      - { name: oro_scope.provider, scopeType: workflow_definition }
            |  no_scope4_id:
            |    class: Oro\\AcmeBundle\\UserScopeProvider
            |    tags:
            |      - { name: oro_scope.provider, scopeType: invalid }
            |  scope5_id:
            |    class: Oro\\AcmeBundle\\CustomerScopeProvider
            |  scope5_id_link:
            |    tags:
            |      - { name: oro_scope.provider, scopeType: workflow_definition }
            |      - { name: oro_service_link, service: scope5_id }
            """.stripMargin()
        )
    }

    def void "test: suggest conditions defined in xml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          <caret>
            """.stripMargin(),
            ["@$condition1", "@$condition2a", "@$condition2b"],
            ["@$unknown"]
        )
    }

    def void "test: suggest conditions defined in yml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          <caret>
            """.stripMargin(),
            ["@$condition3"]
        )
    }

    def void "test: does not suggest conditions as property value"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          @not: <caret>
            """.stripMargin(),
            [],
            ["@$condition1", "@$condition2a", "@$condition2b"]
        )
    }

    def void "test: does not suggest conditions as keys for not composite conditions"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@$trueCondition':
            |               <caret>
            """.stripMargin(),
            [],
            ["@$condition1", "@$condition2a", "@$condition2b"]
        )
    }

    def void "test: suggest conditions as keys for composite conditions"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@$orCondition':
            |               <caret>
            """.stripMargin(),
            ["@$condition1", "@$condition2a", "@$condition2b"]
        )
    }

    def void "test: suggest conditions as keys for composite conditions in parameters key"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@$orCondition':
            |               parameters:
            |                   <caret>
            """.stripMargin(),
            ["@$condition1", "@$condition2a", "@$condition2b"]
        )
    }

    def void "test: detect conditions reference"() {
        checkPhpReference(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@${insertSomewhere(condition1, "<caret>")}': ~
            """.stripMargin(),
            ["Oro\\AcmeBundle\\NotCondition"]
        )
    }

    def void "test: detect reference for conditions having multiple aliases"() {
        checkPhpReference(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@${insertSomewhere(condition2a, "<caret>")}': ~
            """.stripMargin(),
            ["Oro\\AcmeBundle\\Condition2a"]
        )
    }

    def void "test: suggest actions after new line defined in xml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          -
            |            <caret>
            """.stripMargin(),
            ["@$action1", "@$action2a", "@$action2b"],
            ["@$unknown"]
        )
    }

    def void "test: suggest actions in the same line defined in xml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - <caret>
            """.stripMargin(),
            ["@$action1", "@$action2a", "@$action2b"],
            ["@$unknown"]
        )
    }

    def void "test: suggest action options"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - "@$assignUrlAction":
            |               <caret>
            """.stripMargin(),
            ["route", "route_parameters", "attribute"]
        )
    }

    def void "test: suggest action options from getOption and \$this->options"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - "@$assignUrlAction":
            |               <caret>
            """.stripMargin(),
            ["option_from_\$this_options", "option_from_getOption"],
            ["options_that_shouldnt_exist"]
        )
    }

    def void "test: suggest action options in parameters property"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - "@$assignUrlAction":
            |               parameters:
            |                 <caret>
            """.stripMargin(),
            ["route", "route_parameters", "attribute"]
        )
    }

    def void "test: suggest parameters, conditions etc. for actions"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - "@$assignUrlAction":
            |               <caret>
            """.stripMargin(),
            ["parameters", "actions", "conditions", "break_on_failure"]
        )
    }

    def void "test: suggest nested actions"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - "@$assignUrlAction":
            |               actions:
            |                 - <caret>
            """.stripMargin(),
            ["@$action1"]
        )
    }

    def void "test: suggest action conditions"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          - "@$assignUrlAction":
            |               conditions:
            |                 - <caret>
            """.stripMargin(),
            ["@$condition1"]
        )
    }

    def void "test: suggest actions defined in yml file"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        init_actions:
            |          -
            |            <caret>
            """.stripMargin(),
            ["@$action3"]
        )
    }

    def void "test: complete conditions as keys"() {
        completion(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          ondition4<caret>
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@condition4': <caret>
            """.stripMargin(),
        )
    }

    def void "test: complete quoted conditions as keys"() {
        completion(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          'ondition4<caret>'
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        conditions:
            |          '@condition4': <caret>
            """.stripMargin(),
        )
    }

    def void "test: complete quoted at beginning of a key"() {
        completion(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        'conditions<caret>
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some_transition:
            |        'conditions': <caret>
            """.stripMargin(),
        )
    }

    def void "test: suggest scope for simple scope provider"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    scopes:
            |      -
            |         <caret>
            """.stripMargin(),
            ["account"]
        )
    }

    def void "test: suggest scope for scope provider with name defined as const reference"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    scopes:
            |      -
            |         <caret>
            """.stripMargin(),
            ["localization"]
        )
    }

    def void "test: not suggest scope for scope provider that has unsupported scopeType"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    scopes:
            |      -
            |         <caret>
            """.stripMargin(),
            [],
            ["user"]
        )
    }

    def void "test: detect scope scope reference"() {
        checkPhpReference(
            """
            |workflows:
            |  some:
            |    scopes:
            |      -
            |         local<caret>ization: ~
            """.stripMargin(),
            ["Oro\\AcmeBundle\\LocalizationScopeProvider"]
        )
    }

    def void "test: detect scope reference when class name is defined as service parameter"() {
        checkPhpReference(
            """
            |workflows:
            |  some:
            |    scopes:
            |      -
            |         lang<caret>uage: ~
            """.stripMargin(),
            ["Oro\\AcmeBundle\\LanguageScopeProvider"]
        )
    }

    def void "test: suggest scope for scope provider extended by oro link"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    scopes:
            |      -
            |         <caret>
            """.stripMargin(),
            ["customer"]
        )
    }

}
