package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

public class AclCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    def void "test: suggest key in new line"() {
        suggestions(
            """
            |some_id:
            |  <caret>
            """.stripMargin(),

            ["type", "label"]
        )
    }

    def void "test: suggest key in new line when the value is defined"() {
        suggestions(
            """
            |some_id:
            |  <caret>: entity
            """.stripMargin(),

            ["type", "label"]
        )
    }

    def void "test: suggest key in new line without value after some property"() {
        suggestions(
            """
            |some_id:
            |  label: abc
            |  <caret>
            """.stripMargin(),

            ["type", "permission"]
        )
    }

    def void "test: suggest key in new line after some property when the value is defined"() {
        suggestions(
            """
            |some_id:
            |  label: abc
            |  <caret>: entity
            """.stripMargin(),

            ["type", "permission"]
        )
    }

    def void "test: does not suggest keys as property values"() {
        suggestions(
            """
            |some_id:
            |  label: <caret>
            """.stripMargin(),

            [],
            ["type"]
        )
    }

    //TODO: for more sophisticated cases
    def void "ignored test: does not suggest keys as property values at top level"() {
        suggestions(
            """
            |some_id: <caret>
            """.stripMargin(),

            [],
            ["type"]
        )
    }

    def void "test: suggest key in hash object"() {
        suggestions(
            """
            |some_id: { <caret> }
            """.stripMargin(),

            ["type", "label"]
        )
    }

    def void "test: does not suggest keys as values in hash object"() {
        suggestions(
            """
            |some_id: { label: <caret> }
            """.stripMargin(),

            [],
            ["type"]
        )
    }

    def void "test: suggest key in hash object as the second key"() {
        suggestions(
            """
            |some_id: { label: value, <caret> }
            """.stripMargin(),

            ["type", "permission"]
        )
    }

    def void "test: suggest key in has object when the value is defined"() {
        suggestions(
            """
            |some_id: { <caret>: entity }
            """.stripMargin(),

            ["type", "label"]
        )
    }

    def void "test: suggest choice literal values"() {
        suggestions(
            """
            |some_id:
            |  permission: <caret>
            """.stripMargin(),

            ["VIEW", "EDIT"],
            ["type", "label"]
        )
    }

    def void "test: suggest choice literal values inside hash object"() {
        suggestions(
            """
            |some_id: { type: <caret> }
            """.stripMargin(),

            ["entity", "action"],

        )
    }

    def void "test: suggest key inside array"() {
        suggestions(
            """
            |some_id:
            |  bindings:
            |    - { <caret> }
            """.stripMargin(),

            ["class", "method"],
            ["type", "label"]
        )
    }

    def void "test: suggest key inside array when value is defined"() {
        suggestions(
            """
            |some_id:
            |  bindings:
            |    - { <caret>: someClass }
            """.stripMargin(),

            ["class", "method"],
            ["type", "label"]
        )
    }

    def void "test: does not suggest keys of object one level up for array notation"() {
        suggestions(
            """
            |some_id:
            |  - { <caret> }
            """.stripMargin(),

            [],
            ["type"]
        )
    }

    private def completion(String contents, String expected) {
        myFixture.configureByText("acl.yml", contents)
        def elements = myFixture.completeBasic()

        if(elements != null && elements.length == 1) {
            myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        }

        myFixture.checkResult(expected.replace("\r", ""))
    }

    private def suggestions(String contents, Collection<String> expectedSuggestions, Collection<String> unexpectedSuggestions = []) {
        myFixture.configureByText("acl.yml", contents)
        myFixture.completeBasic()

        def lookupElements = myFixture.getLookupElementStrings()

        assertNotNull(lookupElements)
        assertContainsElements(lookupElements, expectedSuggestions)
        assertDoesntContain(lookupElements, unexpectedSuggestions)
    }

}