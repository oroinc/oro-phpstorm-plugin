package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.referenceProvider

import com.intellij.testFramework.DumbModeTestUtils
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest

class PhpFieldReferenceProviderTest extends CompletionTest {

    @Override
    String fileName() {
        return "test.yml"
    }

    void testReferencesInDumbModeShouldNotThrowException() {
        configureByText(fileName(), """
services:
    some_service:
        class: <caret>Acme\\DemoBundle\\Entity\\User
""")

        DumbModeTestUtils.runInDumbModeSynchronously(myFixture.project) {
            // This should not throw IndexNotReadyException
            myFixture.file.findReferenceAt(myFixture.caretOffset)
        }
    }
}
