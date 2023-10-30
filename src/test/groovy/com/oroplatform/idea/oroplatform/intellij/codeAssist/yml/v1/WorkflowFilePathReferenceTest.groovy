package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1

public class WorkflowFilePathReferenceTest extends CompletionTest {

    @Override
    String fileName() {
        return SchemasV1.FilePathPatterns.WORKFLOW
    }

    def void "test: yaml files in imports should be a reference"() {
        myFixture.addFileToProject("Resources/config/oro/workflow/sample.yml", "")

        checkFileReferences(
            """
            |imports:
            |  - { resource: "oro/workflow/sample.<caret>yml" }
            """.stripMargin(),
            ["sample"]
        )
    }

}
