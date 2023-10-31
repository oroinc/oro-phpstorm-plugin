package com.oroplatform.idea.oroplatform.schema

import com.oroplatform.idea.oroplatform.schema.Container
import com.oroplatform.idea.oroplatform.schema.OneOf
import com.oroplatform.idea.oroplatform.schema.Property
import com.oroplatform.idea.oroplatform.schema.PropertyDescriptionProvider
import com.oroplatform.idea.oroplatform.schema.TypePropertyDescriptionProvider
import org.junit.Test
import static org.junit.Assert.*

class TypePropertyDescriptionProviderTest {

    @Test
    def void "should use property value type as description"() {
        final PropertyDescriptionProvider provider = new TypePropertyDescriptionProvider();
        assertEquals("object", provider.getDescription(Property("some", Container())))
        assertEquals("string", provider.getDescription(Property("some", Scalars.any)))
        assertEquals("integer", provider.getDescription(Property("some", Scalars.integer)))
        assertEquals("boolean", provider.getDescription(Property("some", Scalars.bool)))
        assertEquals("string[]", provider.getDescription(Property("some", new Sequence(Scalars.any))))
        assertEquals("object[]", provider.getDescription(Property("some", new Sequence(Container()))))
        assertEquals("integer or boolean", provider.getDescription(Property("some", OneOf(Scalars.integer, Scalars.bool))))
        assertEquals("integer", provider.getDescription(Property("some", OneOf(Scalars.integer, Scalars.integer))))
        assertEquals("boolean", provider.getDescription(Property("some", Scalars.bool)))
        assertEquals("boolean", provider.getDescription(Property("some", Repeated.atAnyLevel(Scalars.bool))))
    }
}
