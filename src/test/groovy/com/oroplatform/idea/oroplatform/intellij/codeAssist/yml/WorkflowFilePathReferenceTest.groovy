package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.intellij.psi.PsiFileSystemItem
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import com.oroplatform.idea.oroplatform.schema.Schemas


public class WorkflowFilePathReferenceTest extends CompletionTest {

    @Override
    String fileName() {
        return Schemas.FilePathPatterns.WORKFLOW
    }

    def void "test: yaml files in imports should be a reference"() {
        myFixture.addFileToProject("Resources/config/oro/workflow/sample.yml", "")

        def files = getResolvedFileReferences(
            """
            |imports:
            |  - { resource: "oro/workflow/sample.<caret>yml" }
            """.stripMargin()
        )

        assertEquals(1, files.findAll { it.contains("sample") }.size())
    }

    private def String[] getResolvedFileReferences(String contents) {
        configureByText(contents)

        def element = myFixture.getFile().findElementAt(myFixture.getCaretOffset()).getParent()

        element.getReferences()
            .collect { it.resolve() }
            .flatten()
            .findAll { it instanceof PsiFileSystemItem }
            .collect { (PsiFileSystemItem) it }
            .collect { it.getVirtualFile().getCanonicalPath() }
    }

}
