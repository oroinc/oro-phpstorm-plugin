package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.IOUtil;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;


class PathCollectionExternalizer implements DataExternalizer<Collection<String>> {
    public void save(@NotNull DataOutput out, Collection<String> value) throws IOException {
        for (String str : value) {
            IOUtil.writeUTF(out, str);
        }
    }

    public Collection<String> read(@NotNull DataInput in) throws IOException {
//        final Set<String> result = new THashSet<String>(FileUtil.PATH_HASHING_STRATEGY);
        final Collection<String> result = new LinkedList<String>();
        final DataInputStream stream = (DataInputStream)in;
        while (stream.available() > 0) {
            final String str = IOUtil.readUTF(stream);
            result.add(str);
        }
        return result;
    }
}