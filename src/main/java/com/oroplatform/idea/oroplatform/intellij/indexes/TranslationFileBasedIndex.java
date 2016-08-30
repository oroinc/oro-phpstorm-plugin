package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.*;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TranslationFileBasedIndex extends ScalarIndexExtension<String> {
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();
    public static final ID<String, Void> KEY = ID.create("com.oroplatform.idea.oroplatform.translations");

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getPath().contains("/cache/dev/translations/catalogue.en");
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return new DataIndexer<String, Void, FileContent>() {
            @NotNull
            @Override
            public Map<String, Void> map(@NotNull FileContent inputData) {
                final Map<String, Void> index = new THashMap<String, Void>();

                if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
                    return index;
                }

                final PhpFile file = (PhpFile) inputData.getPsiFile();

                for (PsiElement element : file.getChildren()) {
                    if(!(element instanceof GroupStatement)) continue;

                    final GroupStatement groupStatement = (GroupStatement) element;

                    indexGroupStatement(index, groupStatement);
                }

                return index;
            }

            private void indexGroupStatement(Map<String, Void> index, GroupStatement groupStatement) {
                for (PsiElement potentialStatement : groupStatement.getStatements()) {
                    if(!(potentialStatement instanceof Statement)) continue;

                    final Statement statement = (Statement) potentialStatement;

                    indexStatement(index, statement);
                }
            }

            private void indexStatement(Map<String, Void> index, Statement statement) {
                for (PsiElement potentialAssignment : statement.getChildren()) {
                    if(!(potentialAssignment instanceof AssignmentExpression)) continue;

                    final AssignmentExpression assignment = (AssignmentExpression) potentialAssignment;

                    if(!(assignment.getValue() instanceof NewExpression)) continue;

                    final NewExpression newExpression = (NewExpression) assignment.getValue();
                    indexNewExpression(index, newExpression);

                }
            }

            private void indexNewExpression(Map<String, Void> index, NewExpression newExpression) {
                for (PsiElement parameter : newExpression.getParameters()) {
                    if(!(parameter instanceof ArrayCreationExpression)) continue;

                    final ArrayCreationExpression catalogue = (ArrayCreationExpression) parameter;

                    indexCatalogue(index, catalogue);
                }
            }

            private void indexCatalogue(Map<String, Void> index, ArrayCreationExpression catalogue) {
                for (ArrayHashElement domainHash : catalogue.getHashElements()) {
                    if(!(domainHash.getValue() instanceof ArrayCreationExpression)) continue;

                    final ArrayCreationExpression translations = (ArrayCreationExpression) domainHash.getValue();

                    indexTranslations(index, translations);
                }
            }

            private void indexTranslations(Map<String, Void> index, ArrayCreationExpression translations) {
                for (ArrayHashElement translationHash : translations.getHashElements()) {
                    if(translationHash.getKey() == null) continue;
                    final String translation = translationHash.getKey().getText();
                    index.put(StringUtil.stripQuotesAroundValue(translation), null);
                }
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
