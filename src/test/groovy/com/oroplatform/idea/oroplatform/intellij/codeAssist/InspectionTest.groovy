package com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

abstract class InspectionTest extends LightPlatformCodeInsightFixtureTestCase {
    @Override
    def boolean isWriteActionRequired() {
        return false
    }

    abstract def String fileName()

    def checkInspection(String s, String filePath = fileName()) {
        myFixture.configureByText(filePath, s.replace("\r", ""))
        myFixture.checkHighlighting()
    }

    def runQuickFix(String quickFix, String actual) {

        myFixture.configureByText(fileName, actual.replace("\r", ""))

        def caretOffset = myFixture.getEditor().getCaretModel().getOffset()

        //side effect of getAllQuickFixes - caret is moved to "0" offset
        def quickFixes = myFixture.getAllQuickFixes(fileName).findAll { it.getFamilyName() == quickFix || it.getText() == quickFix }
        def quickFixesCount = quickFixes.size()

        def msg = "Expected 1 '$quickFix' quick fix, $quickFixesCount found"
        assertEquals(msg, 1, quickFixesCount)

        myFixture.getEditor().getCaretModel().moveToOffset(caretOffset)
        quickFixes.take(1).forEach { myFixture.launchAction(it) }
    }
}
