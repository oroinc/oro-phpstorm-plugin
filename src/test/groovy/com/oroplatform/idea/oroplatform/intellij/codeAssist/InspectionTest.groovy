package com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings

abstract class InspectionTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    def boolean isWriteActionRequired() {
        return false
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()

        OroPlatformSettings.getInstance(myFixture.project).setPluginEnabled(true)
    }

    abstract def String fileName()

    def checkInspection(String s, String filePath = fileName()) {
        configureByText(filePath, s.replace("\r", ""))
        myFixture.checkHighlighting()
    }

    private def configureByText(String filePath, String contents) {
        def file = myFixture.addFileToProject(filePath, contents)
        myFixture.configureFromExistingVirtualFile(file.getVirtualFile())
    }

    def runQuickFix(String quickFix, String actual) {

        configureByText(fileName(), actual.replace("\r", ""))

        def caretOffset = myFixture.getEditor().getCaretModel().getOffset()

        //side effect of getAllQuickFixes - caret is moved to "0" offset
        def quickFixes = myFixture.getAllQuickFixes(fileName()).findAll { it.getFamilyName() == quickFix || it.getText() == quickFix }
        def quickFixesCount = quickFixes.size()

        def msg = "Expected 1 '$quickFix' quick fix, $quickFixesCount found"
        assertEquals(msg, 1, quickFixesCount)

        myFixture.getEditor().getCaretModel().moveToOffset(caretOffset)
        quickFixes.take(1).forEach { myFixture.launchAction(it) }
    }
}
