package com.oroplatform.idea.oroplatform

import com.oroplatform.idea.oroplatform.SimpleSuffixMatcher
import org.junit.Test
import static org.junit.Assert.*

class SimpleSuffixMatcherTest {

    @Test
    def void "should match exactly the same suffix"() {
        def matcher = new SimpleSuffixMatcher("Resources/config/some.yml")
        assertTrue(matcher.matches("Resources/config/some.yml"))
        assertFalse(matcher.matches("Resources/config/some.yml/some"))
    }

    @Test
    def void "should match even if there is extra prefix"() {
        def matcher = new SimpleSuffixMatcher("Resources/config/some.yml")
        assertTrue(matcher.matches("/some/prefix/Resources/config/some.yml"))
    }

    @Test
    def void "should match if there is wildcard"() {
        def matcher = new SimpleSuffixMatcher("Resources/config/*.yml")
        assertTrue(matcher.matches("Resources/config/some.yml"))
        assertFalse(matcher.matches("Resources/config/some.yml/some"))
        assertFalse(matcher.matches("Resources/config/some.yml2"))
    }

    @Test
    def void "wildcard should not match to directory separator"() {
        def matcher = new SimpleSuffixMatcher("Resources/config/*.yml")
        assertFalse(matcher.matches("Resources/config/xxx/some.yml"))
    }

    @Test
    def void "double wildcard should match to directory separator"() {
        def matcher = new SimpleSuffixMatcher("Resources/config/**/*.yml")
        assertTrue(matcher.matches("Resources/config/xxx/yyy/zzz/some.yml"))
    }
}
