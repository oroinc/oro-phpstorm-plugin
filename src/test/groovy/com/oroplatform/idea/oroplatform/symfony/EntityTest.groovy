package com.oroplatform.idea.oroplatform.symfony

import org.junit.Test
import static org.junit.Assert.*

class EntityTest {
    @Test
    def void "should return None on invalid entity name"() {
        assertEquals(Optional.empty(), Entity.fromFqn("Address"));
        assertEquals(Optional.empty(), Entity.fromFqn("Oro\\AcmeBundle\\Controller\\Address"));
    }

    @Test
    def void "should resolve Doctrine shortcut class names for different types bundle structure"() {
        assertEquals(Optional.of("OroAcmeBundle:Address"), Entity.fromFqn("Oro\\Bundle\\AcmeBundle\\Entity\\Address").map { it.getShortcutName() });
        assertEquals(Optional.of("OroAcmeBundle:Address"), Entity.fromFqn("\\Oro\\Bundle\\AcmeBundle\\Entity\\Address").map { it.getShortcutName() });
        assertEquals(Optional.of("OroAcmeBundle:Address"), Entity.fromFqn("Oro\\AcmeBundle\\Entity\\Address").map { it.getShortcutName() });
        assertEquals(Optional.of("OroAcmeBundle:Translations\\AddressTranslation"), Entity.fromFqn("Oro\\AcmeBundle\\Entity\\Translations\\AddressTranslation").map { it.getShortcutName() });
        assertEquals(Optional.of("AcmeBundle:Address"), Entity.fromFqn("AcmeBundle\\Entity\\Address").map { it.getShortcutName() });
    }
}
