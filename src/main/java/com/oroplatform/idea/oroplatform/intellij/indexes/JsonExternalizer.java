package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class JsonExternalizer<T> implements KeyDescriptor<T> {
    private final DataExternalizer<String> stringExternalizer = new EnumeratorStringDescriptor();
    private final Class<T> type;
    private final Gson gson = new Gson();

    JsonExternalizer(Class<T> type) {
        this.type = type;
    }

    @Override
    public void save(@NotNull DataOutput out, T value) throws IOException {
        stringExternalizer.save(out, gson.toJson(value, type));
    }

    @Override
    public T read(@NotNull DataInput in) throws IOException {
        try {
            return gson.fromJson(stringExternalizer.read(in), type);

        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    @Override
    public int getHashCode(T value) {
        return value.hashCode();
    }

    @Override
    public boolean isEqual(T val1, T val2) {
        return val1.equals(val2);
    }
}
