package com.oroplatform.idea.oroplatform.symfony

import org.junit.Test
import static org.junit.Assert.*

class BundleTest {
    @Test
    def void "should resolve namespace to correct bundle name"() {
        assertEquals("Address", new Bundle("Address").getName())
        assertEquals("OroAddressBundle", new Bundle("Oro\\AddressBundle").getName())
        assertEquals("OroAddressBundle", new Bundle("\\Oro\\AddressBundle").getName())
        assertEquals("OroAddressBundle", new Bundle("Oro\\AddressBundle\\").getName())
        assertEquals("OroAddressBundle", new Bundle("\\Oro\\AddressBundle\\").getName())
        assertEquals("OroAddressBundle", new Bundle("Oro\\Bundle\\AddressBundle").getName())
        assertEquals("OroAddressBundle", new Bundle("Oro\\Bundle\\AddressBundle\\").getName())
        assertEquals("OroAddressBundle", new Bundle("\\Oro\\Bundle\\AddressBundle\\").getName())
    }

    @Test
    def void "should resolve namespace to correct bundle resource name"() {
        assertEquals("address", new Bundle("Address").getResourceName())
        assertEquals("oroaddress", new Bundle("Oro\\AddressBundle").getResourceName())
        assertEquals("oroaddress", new Bundle("\\Oro\\AddressBundle").getResourceName())
        assertEquals("oroaddress", new Bundle("Oro\\AddressBundle\\").getResourceName())
        assertEquals("oroaddress", new Bundle("Oro\\Bundle\\AddressBundle").getResourceName())
    }

    @Test
    def void "should resolve namespace names"() {
        assertEquals("\\Address", new Bundle("Address").getNamespaceName())
        assertEquals("\\Oro\\AddressBundle", new Bundle("Oro\\AddressBundle").getNamespaceName())
        assertEquals("\\Oro\\AddressBundle", new Bundle("\\Oro\\AddressBundle").getNamespaceName())
    }
}
