package com.oroplatform.idea.oroplatform;

import org.junit.Test
import static org.junit.Assert.*

public class PhpClassUtilTest {

    @Test
    def void "should resolve Doctrine shortcut class names for different types bundle structure"() {
        assertEquals(null, PhpClassUtil.getDoctrineShortcutClassName("Address"));
        assertEquals("OroAcmeBundle:Address", PhpClassUtil.getDoctrineShortcutClassName("Oro\\Bundle\\AcmeBundle\\Entity\\Address"));
        assertEquals("OroAcmeBundle:Address", PhpClassUtil.getDoctrineShortcutClassName("\\Oro\\Bundle\\AcmeBundle\\Entity\\Address"));
        assertEquals("OroAcmeBundle:Address", PhpClassUtil.getDoctrineShortcutClassName("Oro\\AcmeBundle\\Entity\\Address"));
        assertEquals("AcmeBundle:Address", PhpClassUtil.getDoctrineShortcutClassName("AcmeBundle\\Entity\\Address"));
        assertEquals(null, PhpClassUtil.getDoctrineShortcutClassName("Oro\\AcmeBundle\\Controller\\Address"));
    }

}