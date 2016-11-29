package com.oroplatform.idea.oroplatform.intellij.codeAssist.javascript

import org.junit.Test
import static org.junit.Assert.*

class RequireJsConfigTest {
    @Test
    void "merged config should contain paths from both configs"() {
        def config1 = new RequireJsConfig([alias1: "path1"], new HashMap<>())
        def config2 = new RequireJsConfig([alias2: "path2"], new HashMap<>())

        def actual = config1.merge(config2)

        assertEquals(Optional.of("path1"), actual.getPathAliasFor("alias1"))
        assertEquals(Optional.of("path2"), actual.getPathAliasFor("alias2"))
    }

    @Test
    void "override path alias by merged config"() {
        def config1 = new RequireJsConfig([alias1: "path1"], new HashMap<>())
        def config2 = new RequireJsConfig([alias1: "path2"], new HashMap<>())

        def actual = config1.merge(config2)

        assertEquals(Optional.of("path2"), actual.getPathAliasFor("alias1"))
    }

    @Test
    void "merge mapping for different libraries"() {
        def config1 = new RequireJsConfig(new HashMap<>(), [pkg1: [alias1: "somePkg1"]])
        def config2 = new RequireJsConfig(new HashMap<>(), [pkg2: [alias2: "somePkg2"]])

        def actual = config1.merge(config2)

        assertEquals([alias1: "somePkg1"], actual.getPackageAliasesFor("pkg1"))
        assertEquals([alias2: "somePkg2"], actual.getPackageAliasesFor("pkg2"))
    }

    @Test
    void "merge mapping for the same library"() {
        def config1 = new RequireJsConfig(new HashMap<>(), [pkg1: [alias1: "somePkg1"]])
        def config2 = new RequireJsConfig(new HashMap<>(), [pkg1: [alias2: "somePkg2"]])

        def actual = config1.merge(config2)

        assertEquals([alias1: "somePkg1", alias2: "somePkg2"], actual.getPackageAliasesFor("pkg1")) }
}
