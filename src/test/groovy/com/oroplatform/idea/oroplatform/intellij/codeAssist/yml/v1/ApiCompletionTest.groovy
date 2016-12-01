package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class ApiCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.API
    }

    def void "test: suggest root property"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["oro_api"]
        )
    }

    def void "test: suggest oro_api properties"() {
        suggestions(
            """
            |oro_api:
            |  <caret>
            """.stripMargin(),

            ["entities", "relations"]
        )
    }

    def void "test: suggest entities properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            ["exclude", "inherit", "exclusion_policy", "max_results", "order_by", "disable_inclusion", "disable_fieldset",
             "hints", "identifier_field_names", "delete_handler", "form_type", "form_options", "fields",
             "filters", "sorters", "actions", "subresources"]
        )
    }

    def void "test: not suggest not supported entities properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            [],
            ["post_serialize"]
        )
    }

    def void "test: suggest fields properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      fields:
            |        field1:
            |          <caret>
            """.stripMargin(),

            ["exclude", "description", "property_path", "data_transformer", "collapse", "form_type", "form_options",
             "data_type", "meta_property", "target_class", "target_type"]
        )
    }

    def void "test: suggest filters properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      filters:
            |        <caret>
            """.stripMargin(),

            ["exclusion_policy", "fields"]
        )
    }

    def void "test: suggest relations properties"() {
        suggestions(
            """
            |oro_api:
            |  relations:
            |    stdClass:
            |      <caret>
            """.stripMargin(),

            ["fields", "filters", "sorters"]
        )
    }

    def void "test: suggest order_by values"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      order_by:
            |        xxx: <caret>
            """.stripMargin(),

            ["ASC", "DESC"]
        )
    }

    def void "test: suggest filters.fields properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      filters:
            |        fields:
            |          xxx:
            |            <caret>
            """.stripMargin(),

            ["exclude", "description", "property_path", "data_type", "allow_array"]
        )
    }

    def void "test: suggest sorters properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      sorters:
            |        <caret>
            """.stripMargin(),

            ["exclusion_policy", "fields"]
        )
    }

    def void "test: suggest sorters.fields properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      sorters:
            |        fields:
            |          xxx:
            |            <caret>
            """.stripMargin(),

            ["exclude", "property_path"]
        )
    }

    def void "test: suggest actions properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      actions:
            |        <caret>
            """.stripMargin(),

            ["get", "get_list", "create", "update", "delete", "delete_list"]
        )
    }

    def void "test: suggest particular action properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      actions:
            |        delete:
            |          <caret>
            """.stripMargin(),

            ["exclude", "description", "documentation", "acl_resource", "max_results", "order_by", "page_size", "disable_sorting",
             "disable_inclusion", "disable_fieldset", "form_type", "form_options", "status_codes", "fields"]
        )
    }

    def void "test: suggest actions.fields properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      actions:
            |        delete:
            |          fields:
            |            xxx:
            |              <caret>
            """.stripMargin(),

            ["exclude", "form_type", "form_options"]
        )
    }

    def void "test: suggest actions.status_codes properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      actions:
            |        delete:
            |          status_codes:
            |            200:
            |              <caret>
            """.stripMargin(),

            ["exclude", "description"]
        )
    }

    def void "test: suggest actions.status_codes values"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      actions:
            |        delete:
            |          status_codes:
            |            <caret>
            """.stripMargin(),

            ["200", "400"]
        )
    }

    def void "test: suggest subresources properties"() {
        suggestions(
            """
            |oro_api:
            |  entities:
            |    stdClass:
            |      subresources:
            |        xxx:
            |          <caret>
            """.stripMargin(),

            ["exclude", "target_class", "target_type", "actions", "filters", "acl_resource"]
        )
    }
}
