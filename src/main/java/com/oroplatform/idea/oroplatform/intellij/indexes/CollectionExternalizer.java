package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.util.io.DataExternalizer;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;


class CollectionExternalizer<T> implements DataExternalizer<Collection<T>> {

    private final DataExternalizer<T> dataExternalizer;

    CollectionExternalizer(DataExternalizer<T> dataExternalizer) {
        this.dataExternalizer = dataExternalizer;
    }

    public void save(@NotNull DataOutput out, Collection<T> values) throws IOException {
        for (T value : values) {
            dataExternalizer.save(out, value);
        }
    }

    public Collection<T> read(@NotNull DataInput in) throws IOException {
        final Collection<T> result = new THashSet<T>();
        final DataInputStream stream = (DataInputStream)in;
        while (stream.available() > 0) {
            final T value = dataExternalizer.read(stream);
            if(value != null) {
                result.add(value);
            }
        }
        return result;
    }
}