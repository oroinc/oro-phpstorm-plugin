package com.oroplatform.idea.oroplatform.intellij.codeAssist

abstract class InspectionTest extends TestCase {
    @Override
    def boolean isWriteActionRequired() {
        return false
    }

    def checkInspection(String s, String filePath = fileName()) {
        configureByText(filePath, s.replace("\r", ""))
        myFixture.checkHighlighting()
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
