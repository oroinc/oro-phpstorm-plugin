package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest

public class AclCompletionTest extends CompletionTest {

    @Override
    String fileName() {
        return "acl.yml"
    }

    def void "test: suggest key in new line"() {
        suggestions(
            """
            |some_id:
            |  <caret>
            """.stripMargin(),

            ["type", "label"]
        )
    }

    def void "test: complete property keys"() {
        completion(
            """
            |some_id:
            |  typ<caret>
            """.stripMargin(),
            """
            |some_id:
            |  type: <caret>
            """.stripMargin(),
        )
    }

    def void "test: complete property keys - eof reach"() {
        completion(
            """
            |some_id:
            |  typ<caret>""".stripMargin(),
            """
            |some_id:
            |  type: <caret>""".stripMargin(),
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

    def void "test: complete property keys on update"() {
        completion(
            """
            |some_id:
            |  typ<caret>: entity
            """.stripMargin(),
            """
            |some_id:
            |  type: <caret>entity
            """.stripMargin()
        )
    }


    def void "test: complete property keys on update - fix carret position"() {
        completion(
            """
            |some_id:
            |  typ<caret>  :   entity
            """.stripMargin(),
            """
            |some_id:
            |  type  :   <caret>entity
            """.stripMargin()
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

    def void "test: does not suggest keys as property values at top level"() {
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

    def void "test: suggest choice scalar values"() {
        suggestions(
            """
            |some_id:
            |  permission: <caret>
            """.stripMargin(),

            ["VIEW", "EDIT"],
            ["type", "label"]
        )
    }

    def void "test: suggestions should be case insensitive"() {
        completion(
            """
            |some_id:
            |  permission: vi<caret>
            """.stripMargin(),

            """
            |some_id:
            |  permission: VIEW
            """.stripMargin(),
        )
    }

    def void "test: complete choice scalar values"() {
        completion(
            """
            |some_id:
            |  permission: VIE<caret>
            """.stripMargin(),
            """
            |some_id:
            |  permission: VIEW<caret>
            """.stripMargin(),
        )
    }

    def void "test: suggest choice scalar values inside hash object"() {
        suggestions(
            """
            |some_id: { type: <caret> }
            """.stripMargin(),

            ["entity", "action"],

        )
    }

    def void "test: suggest key inside sequence"() {
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

    def void "test: suggest key inside sequence when value is defined"() {
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

    def void "test: does not suggest keys of object one level up for sequence notation"() {
        suggestions(
            """
            |some_id:
            |  - { <caret> }
            """.stripMargin(),

            [],
            ["type"]
        )
    }

    def void "test: does not suggest already existing keys for key in progress"() {
        suggestions(
            """
            |some_id:
            |  type: action
            |  <caret>
            """.stripMargin(),

            [ "label" ],
            [ "type" ]
        )
    }

    def void "test: does not suggest already existing keys"() {
        suggestions(
            """
            |some_id:
            |  type: action
            |  <caret>:
            """.stripMargin(),

            [ "label" ],
            [ "type" ]
        )
    }

}