package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import gnu.trove.THashSet;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class Cache {
    public static synchronized <K, V> Collection<V> getSet(final Project project, final PsiElement dataHolder, Key<CachedValue<Collection<V>>> cacheKey, final ID<K, Collection<V>> indexId, final K indexKey, final GlobalSearchScope scope) {
        CachedValue<Collection<V>> cachedValue = dataHolder.getUserData(cacheKey);

        if(cachedValue == null) {
            cachedValue = CachedValuesManager.getManager(project).createCachedValue(new CachedValueProvider<Collection<V>>() {
                @Nullable
                @Override
                public Result<Collection<V>> compute() {
                    final FileBasedIndex fileBasedIndex = FileBasedIndex.getInstance();
                    final Collection<V> values = new THashSet<V>();
                    for (Collection<V> ks : fileBasedIndex.getValues(indexId, indexKey, scope)) {
                        values.addAll(ks);
                    }

                    return CachedValueProvider.Result.createSingleDependency(values, dataHolder);
                }
            }, false);

            dataHolder.putUserData(cacheKey, cachedValue);
        }

        return cachedValue.getValue();
    }
}
