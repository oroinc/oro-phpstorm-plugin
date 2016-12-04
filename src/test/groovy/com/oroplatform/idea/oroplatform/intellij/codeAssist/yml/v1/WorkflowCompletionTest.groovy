package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1

public class WorkflowCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.WORKFLOW
    }

    def imported1 = randomIdentifier("imported1")
    def imported2 = randomIdentifier("imported2")
    def imported3 = randomIdentifier("imported3")

    def void "test: suggest properties at top level"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["imports", "workflows"]
        )
    }

    //TODO: refactor tests
    def void "test: support suggestions for imported file"() {
        configureByText(SchemasV1.FilePathPatterns.WORKFLOW,
            """
            |imports:
            |  - { resource: '${imported1}.yml' }
            """.stripMargin()
        )

        suggestions(
            "Resources/config/${imported1}.yml",
            """
            |<caret>
            """.stripMargin(),

            ["imports", "workflows"]
        )
    }

    def void "test: should not support suggestions for not imported file"() {
        configureByText(SchemasV1.FilePathPatterns.WORKFLOW,
            """
            |imports:
            |  - { resource: '${imported1}.yml' }
            """.stripMargin()
        )

        suggestions(
            "Resources/config/notimported.yml",
            """
            |<caret>
            """.stripMargin(),

            [],
            ["imports", "workflows"]
        )
    }

    def void "test: support suggestions for imported files deeper"() {
        configureByText(SchemasV1.FilePathPatterns.WORKFLOW,
            """
            |imports:
            |  - { resource: '${imported2}.yml' }
            """.stripMargin()
        )

        configureByText("Resources/config/${imported2}.yml",
            """
            |imports:
            |  - { resource: '${imported3}.yml' }
            """.stripMargin()
        )

        suggestions(
            "Resources/config/${imported3}.yml",
            """
            |<caret>
            """.stripMargin(),

            ["imports", "workflows"]
        )
    }

    def void "test: suggest properties in 'imports'"() {
        suggestions(
            """
            |imports:
            |  - {<caret>}
            """.stripMargin(),

            ["resource"]
        )
    }

    def void "test: suggest properties in 'workflows'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    <caret>
            """.stripMargin(),

            ["label", "entity", "entity_attribute", "is_system", "start_step", "steps_display_ordered", "attributes", "steps", "transitions", "transition_definitions"]
        )
    }

    def void "test: suggest properties in 'attributes'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      some:
            |        <caret>
            """.stripMargin(),

            ["type", "label", "entity_acl", "property_path", "options"]
        )
    }

    def void "test: suggest properties in 'attributes.entity_acl'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      some:
            |        entity_acl:
            |          <caret>
            """.stripMargin(),

            ["update", "delete"]
        )
    }

    def void "test: suggest properties in 'attributes.options'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      some:
            |        options:
            |          <caret>
            """.stripMargin(),

            ["class", "multiple"]
        )
    }

    def void "test: suggest properties in 'steps'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    steps:
            |      some:
            |        <caret>
            """.stripMargin(),

            ["label", "order", "is_final", "entity_acl", "allowed_transitions"],
            ["name"]
        )
    }

    def void "test: suggest properties in 'steps' in sequence notation"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    steps:
            |      -
            |         <caret>
            """.stripMargin(),

            ["name", "label", "order", "is_final", "entity_acl", "allowed_transitions"]
        )
    }

    def void "test: suggest properties in 'steps.entity_acl'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    steps:
            |      some:
            |        entity_acl:
            |          <caret>
            """.stripMargin(),

            ["update", "delete"]
        )
    }

    def void "test: suggest properties in 'transitions'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transitions:
            |      some:
            |        <caret>
            """.stripMargin(),

            ["step_to", "transition_definition", "is_start", "is_hidden", "is_unavailable_hidden", "acl_resource", "acl_message", "message",
            "display_type", "page_template", "dialog_template", "frontend_options", "form_options"]
        )
    }

    def void "test: suggest properties in 'transitions.frontend_options'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transitions:
            |      some:
            |        frontend_options:
            |          <caret>
            """.stripMargin(),

            ["class", "icon"]
        )
    }

    def void "test: suggest properties in 'transition_definitions'"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some:
            |        <caret>
            """.stripMargin(),

            ["pre_conditions", "conditions", "post_actions", "init_actions"]
        )
    }


    def void "test: suggest attributes in conditions"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      someAttribute:
            |        type: string
            |    transition_definitions:
            |      some:
            |        conditions:
            |          @not_blank: <caret>
            """.stripMargin(),

            ["\$someAttribute"]
        )
    }

    def void "test: suggest attributes in conditions in sequence"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      someAttribute:
            |        type: string
            |    transition_definitions:
            |      some:
            |        conditions:
            |          @not_blank: [<caret>]
            """.stripMargin(),

            ["\$someAttribute"]
        )
    }

    def void "test: suggest attributes in conditions at any level"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      someAttribute:
            |        type: string
            |    transition_definitions:
            |      some:
            |        conditions:
            |          @not_blank:
            |            xxx: <caret>
            """.stripMargin(),

            ["\$someAttribute"]
        )
    }

    def void "test: not suggest attributes in conditions from different workflow"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      some:
            |        conditions:
            |          @not_blank: <caret>
            |  some2:
            |    attributes:
            |      someAttribute:
            |        type: string
            """.stripMargin(),

            [],
            ["\$someAttribute"],
        )
    }

    def void "test: suggest transition definitions in transition"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    transition_definitions:
            |      someTransitionDef: ~
            |    transitions:
            |      xxx:
            |        transition_definition: <caret>
            """.stripMargin(),

            ["someTransitionDef"],
        )
    }

    def void "test: suggest attributes in actions at any level"() {
        suggestions(
            """
            |workflows:
            |  some:
            |    attributes:
            |      someAttribute:
            |        type: string
            |    transition_definitions:
            |      some:
            |        post_actions:
            |          - @assign_value:
            |              xxx: <caret>
            """.stripMargin(),

            ["\$someAttribute"]
        )
    }

    def void "test: complete entity_attribute default value"() {
        completion(
            """
            |workflows:
            |  some:
            |    entity: Some\\MyCustomEntity
            |    entity_attribu<caret>
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    entity: Some\\MyCustomEntity
            |    entity_attribute: my_custom_entity<caret>
            """.stripMargin()
        )
    }

    def void "test: complete entity_attribute default value when entity is not yet defined"() {
        completion(
            """
            |workflows:
            |  some:
            |    entity_attribu<caret>
            """.stripMargin(),
            """
            |workflows:
            |  some:
            |    entity_attribute: <caret>
            """.stripMargin()
        )
    }
}
