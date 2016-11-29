package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript

import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionTest
import org.jetbrains.yaml.psi.YAMLFile

//TODO: refactor testing base classes
class RequireJsConfigParserTest extends CompletionTest {
    @Override
    String fileName() {
        return "some/requirejs.yml"
    }

    void "test: parse config paths"() {
        configureByText(
            """
            |config:
            |  paths:
            |    someAlias1: some/path/to/somewhere1
            |    someAlias2: some/path/to/somewhere2
            """.stripMargin()
        )

        def config = new RequireJsConfigParser().parse((YAMLFile)myFixture.getFile())

        assertEquals(Optional.of("some/path/to/somewhere1"), config.getPathAliasFor("someAlias1"))
        assertEquals(Optional.of("some/path/to/somewhere2"), config.getPathAliasFor("someAlias2"))
        assertEquals(Optional.empty(), config.getPathAliasFor("unexisting"))
    }

    void "test: parse config paths in quotes"() {
        configureByText(
            """
            |config:
            |  paths:
            |    "someAlias1": "some/path/to/somewhere1"
            """.stripMargin()
        )

        def config = new RequireJsConfigParser().parse((YAMLFile)myFixture.getFile())

        assertEquals(Optional.of("some/path/to/somewhere1"), config.getPathAliasFor("someAlias1"))
    }

    void "test: parse config map"() {
        configureByText(
            """
            |config:
            |  map:
            |    somePkg:
            |       alias1: pkg1
            |       alias2: pkg2
            """.stripMargin()
        )

        def config = new RequireJsConfigParser().parse((YAMLFile)myFixture.getFile())

        assertEquals([alias1: "pkg1", alias2: "pkg2"], config.getPackageAliasesFor("somePkg"))
    }

    void "test: aliases for a package for not defined package should be empty"() {
        configureByText(
            """
            |config:
            |  map: ~
            """.stripMargin()
        )

        def config = new RequireJsConfigParser().parse((YAMLFile)myFixture.getFile())

        assertEquals(new HashMap<String, String>(), config.getPackageAliasesFor("somePkg"))
    }
}
