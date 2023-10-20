package com.oroplatform.idea.oroplatform.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import com.oroplatform.idea.oroplatform.OroPlatformBundle;
import com.oroplatform.idea.oroplatform.intellij.indexes.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;

public class OroPlatformForm implements Configurable {

    private final Project project;

    private JButton appDirDefault;
    private TextFieldWithBrowseButton appDir;
    private JPanel component;
    private JCheckBox pluginEnabled;

    public OroPlatformForm(@NotNull Project project) {
        this.project = project;
        appDirDefault.addActionListener(e -> appDir.setText(OroPlatformSettings.DEFAULT_APP_DIRECTORY));
        appDir.addBrowseFolderListener(listener(appDir));
    }

    private TextBrowseFolderListener listener(final TextFieldWithBrowseButton textField) {

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        return new TextBrowseFolderListener(descriptor, project) {

            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile projectDir = ProjectUtil.guessProjectDir(project);
                VirtualFile selectedFile = FileChooser.chooseFile(
                        descriptor,
                        project,
                        VfsUtil.findRelativeFile(appDir.getText(), projectDir)
                );

                if (null == selectedFile) {
                    return;
                }

                String relativePath = projectDir != null ? VfsUtil.getRelativePath(selectedFile, projectDir) : null;

                appDir.setText(relativePath);
            }
        };
    }

    @Nls
    @Override
    public String getDisplayName() {
        return OroPlatformBundle.message("name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return component;
    }

    @Override
    public boolean isModified() {
        return !appDir.getText().equals(getSettings().getAppDir()) || pluginEnabled.isSelected() != getSettings().isPluginEnabled();
    }

    @Override
    public void apply() throws ConfigurationException {
        final boolean hasBeenEnabled = pluginEnabled.isSelected() && !getSettings().isPluginEnabled();

        getSettings().setAppDir(appDir.getText());
        getSettings().setPluginEnabled(pluginEnabled.isSelected());

        if(hasBeenEnabled) {
            rebuildIndexes();
        }
    }

    private void rebuildIndexes() {
        final FileBasedIndex index = FileBasedIndex.getInstance();

        final ID<?, ?>[] indexIds = new ID<?, ?>[] {
            ImportFileBasedIndex.KEY, ImportFileBasedIndex.KEY, DatagridFileBasedIndex.KEY, ServicesFileBasedIndex.KEY,
            AclFileBasedIndex.KEY, OperationFileBasedIndex.KEY, RouteFileBasedIndex.KEY, TranslationFileBasedIndex.KEY
        };

        for (ID<?, ?> indexId : indexIds) {
            index.requestRebuild(indexId);
        }
    }

    @Override
    public void reset() {
        appDir.setText(getSettings().getAppDir());
        pluginEnabled.setSelected(getSettings().isPluginEnabled());
    }

    @Override
    public void disposeUIResources() {

    }

    private OroPlatformSettings getSettings() {
        return OroPlatformSettings.getInstance(project);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        component = new JPanel();
        component.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        component.setPreferredSize(new Dimension(300, 32));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        component.add(panel1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        appDir = new TextFieldWithBrowseButton();
        appDir.setPreferredSize(new Dimension(300, 24));
        panel1.add(appDir, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(700, -1), new Dimension(700, -1), 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("com/oroplatform/idea/oroplatform/messages/OroPlatformBundle").getString("settings.appDir"));
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        appDirDefault = new JButton();
        this.$$$loadButtonText$$$(appDirDefault, ResourceBundle.getBundle("com/oroplatform/idea/oroplatform/messages/OroPlatformBundle").getString("settings.default"));
        panel1.add(appDirDefault, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        component.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return component;
    }
}
