package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public abstract class CompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    abstract def String fileName()

    def completion(String contents, String expected) {
        myFixture.configureByText(fileName(), contents)
        def elements = myFixture.completeBasic()

        if(elements != null) {
            myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        }

        myFixture.checkResult(expected.replace("\r", ""))
    }

    def suggestions(String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions= []) {
        myFixture.configureByText(fileName(), contents)
        myFixture.completeBasic()

        def lookupElements = myFixture.getLookupElementStrings()

        assertNotNull(lookupElements)
        assertContainsElements(lookupElements, expectedSuggestions)
        assertDoesntContain(lookupElements, unexpectedSuggestions)
    }
}
