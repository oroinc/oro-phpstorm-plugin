package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.RandomIdentifiers
import com.oroplatform.idea.oroplatform.schema.SchemasV1


class ActionsTranslationsCompletionTest extends CompletionTest implements RandomIdentifiers {
    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.ACTIONS
    }

    def trans1 = randomIdentifier("trans1")
    def trans2 = randomIdentifier("trans2")

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        configureByText(
            "some/Resources/translations/messages.en.yml",
            """
            |oro:
            |  user:
            |    $trans1: Action
            |    value.$trans2: Value
            """.stripMargin()
        )
    }

    def void "test: suggest translation messages"() {
        suggestions(
            """
            |operations:
            |  op1:
            |    frontend_options:
            |      confirmation: <caret>
            """.stripMargin(),
            ["oro.user.$trans1", "oro.user.value.$trans2"]
        )
    }

    def void "test: detect translation reference"() {
        checkReference(
            """
            |operations:
            |  op1:
            |    frontend_options:
            |      confirmation: oro.us<caret>er.$trans1
            """.stripMargin(),
            ["Action"]
        )
    }

    def void "test: detect nested translation reference"() {
        checkReference(
            """
            |operations:
            |  op1:
            |    frontend_options:
            |      confirmation: oro.us<caret>er.value.$trans2
            """.stripMargin(),
            ["Value"]
        )
    }
}
