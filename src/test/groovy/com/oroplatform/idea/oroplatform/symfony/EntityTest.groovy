package com.oroplatform.idea.oroplatform.symfony

import org.junit.Test
import static org.junit.Assert.*

class EntityTest {
    @Test
    def void "should return null on invalid entity name"() {
        assertEquals(null, Entity.fromFqn("Address"));
        assertEquals(null, Entity.fromFqn("Oro\\AcmeBundle\\Controller\\Address"));
    }

    @Test
    def void "should resolve Doctrine shortcut class names for different types bundle structure"() {
        assertEquals("OroAcmeBundle:Address", Entity.fromFqn("Oro\\Bundle\\AcmeBundle\\Entity\\Address").getShortcutName());
        assertEquals("OroAcmeBundle:Address", Entity.fromFqn("\\Oro\\Bundle\\AcmeBundle\\Entity\\Address").getShortcutName());
        assertEquals("OroAcmeBundle:Address", Entity.fromFqn("Oro\\AcmeBundle\\Entity\\Address").getShortcutName());
        assertEquals("OroAcmeBundle:Translations\\AddressTranslation", Entity.fromFqn("Oro\\AcmeBundle\\Entity\\Translations\\AddressTranslation").getShortcutName());
        assertEquals("AcmeBundle:Address", Entity.fromFqn("AcmeBundle\\Entity\\Address").getShortcutName());
    }
}
