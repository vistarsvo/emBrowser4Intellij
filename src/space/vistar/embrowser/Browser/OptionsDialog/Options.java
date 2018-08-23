package space.vistar.embrowser.Browser.OptionsDialog;

import com.intellij.CommonBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import space.vistar.embrowser.Browser.BrowserConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Options extends DialogWrapper {
    private JPanel contentPane;
    private JTextField userAgent;
    private JCheckBox skipCertErrors;

    private String oldUserAgent;
    private boolean oldSkipCertErrors;

    public Options(Project project) {
        super(project, false, false);
        setTitle("Browser options");
        init();

        userAgent.setText(BrowserConfig.userAgent);
        oldUserAgent = userAgent.getText();
        oldSkipCertErrors = skipCertErrors.isSelected();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new OkAction(), new CancelAction()};
    }

    @Nullable
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    protected class OkAction extends DialogWrapper.DialogWrapperAction {
        protected OkAction() {
            super(CommonBundle.getOkButtonText());
        }

        protected void doAction(ActionEvent e) {
            BrowserConfig.userAgent = userAgent.getText();
            close(0);
        }
    }

    protected class CancelAction extends DialogWrapper.DialogWrapperAction {
        protected CancelAction() {
            super(CommonBundle.getCancelButtonText());
        }

        protected void doAction(ActionEvent e) {
            skipCertErrors.setSelected(oldSkipCertErrors);
            userAgent.setText(oldUserAgent);
            close(0);
        }
    }
}
