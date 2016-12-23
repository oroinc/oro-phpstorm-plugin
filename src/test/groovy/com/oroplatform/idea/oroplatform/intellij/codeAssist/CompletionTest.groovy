package com.oroplatform.idea.oroplatform.intellij.codeAssist

import com.intellij.codeInsight.lookup.Lookup

abstract class CompletionTest extends TestCase {

    def completion(String contents, String expected) {
        completion(fileName(), contents, expected)
    }

    def completion(String fileName, String contents, String expected) {
        configureByText(fileName, contents)
        def elements = myFixture.completeBasic()

        if(elements != null) {
            myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        }

        myFixture.checkResult(expected.replace("\r", ""))
    }

    def suggestions(String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions= []) {
        suggestions(fileName(), contents, expectedSuggestions, unexpectedSuggestions)
    }

    def suggestions(String fileName, String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions= []) {
        configureByText(fileName, contents)
        myFixture.completeBasic()

        def lookupElements = myFixture.getLookupElementStrings()

        assertNotNull(lookupElements)
        assertContainsElements(lookupElements, expectedSuggestions.collect { it.toString() })
        assertDoesntContain(lookupElements, unexpectedSuggestions.collect { it.toString() })
    }
}
