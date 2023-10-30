package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript

import com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript.RequireJsConfigParser
import groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist.TestCase
import org.jetbrains.yaml.psi.YAMLFile

class RequireJsConfigParserTest extends TestCase {
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

        assertEquals(Optional.of("some/path/to/somewhere1"), config.getPathForAlias("someAlias1"))
        assertEquals(Optional.of("some/path/to/somewhere2"), config.getPathForAlias("someAlias2"))
        assertEquals(Optional.empty(), config.getPathForAlias("unexisting"))
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

        assertEquals(Optional.of("some/path/to/somewhere1"), config.getPathForAlias("someAlias1"))
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

        assertEquals(Optional.of("alias1"), config.getPackageAliasFor("somePkg", "pkg1"))
        assertEquals(Optional.of("alias2"), config.getPackageAliasFor("somePkg", "pkg2"))
    }

    void "test: aliases for a package for not defined package should be empty"() {
        configureByText(
            """
            |config:
            |  map: ~
            """.stripMargin()
        )

        def config = new RequireJsConfigParser().parse((YAMLFile)myFixture.getFile())

        assertEquals(Optional.empty(), config.getPackageAliasFor("somePkg", "undefined"))
    }
}
