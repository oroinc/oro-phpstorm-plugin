package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.v1

import com.oroplatform.idea.oroplatform.intellij.codeAssist.FileReferenceTest
import com.oroplatform.idea.oroplatform.schema.SchemasV1

public class WorkflowFilePathReferenceTest extends FileReferenceTest {

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
