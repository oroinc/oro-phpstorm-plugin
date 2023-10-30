package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v2

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV2

class SystemConfigurationCompletionTest extends CompletionTest {
    @Override
    String fileName() {
        return SchemasV2.FilePathPatterns.SYSTEM_CONFIGURATION
    }

    def void "test: suggest root node"() {
        suggestions(
            """
            |<caret>
            """.stripMargin(),

            ["system_configuration"]
        )
    }

    def void "test: suggest properties under system configuration"() {
        suggestions(
            """
            |system_configuration:
            |  <caret>
            """.stripMargin(),

            ["groups", "fields", "tree"]
        )
    }

    def void "test: suggest groups on first level of tree"() {
        suggestions(
            """
            |system_configuration:
            |  groups:
            |    group1: ~
            |    group2: ~
            |  fields:
            |    field1: ~
            |    field2: ~
            |  tree:
            |    tree1:
            |      <caret>
            """.stripMargin(),

            ["group1", "group2"],
            ["field1", "field2"]
        )
    }
}
