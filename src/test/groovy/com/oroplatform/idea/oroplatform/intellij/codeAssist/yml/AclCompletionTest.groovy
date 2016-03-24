package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public class AclCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    def void testSuggestTypeProperty() {
        completion(
            """
            |some_id:
            |  t<caret>
            """.stripMargin(),
            """
            |some_id:
            |  type<caret>
            """.stripMargin()
        )
    }

    def void testSuggestTypeProperty_withValue() {
        completion(
            """
            |some_id:
            |  t<caret>: entity
            """.stripMargin(),
            """
            |some_id:
            |  type: entity
            """.stripMargin()
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

}