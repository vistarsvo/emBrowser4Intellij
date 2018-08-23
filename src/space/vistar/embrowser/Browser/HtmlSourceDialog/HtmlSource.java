package space.vistar.embrowser.Browser.HtmlSourceDialog;

import com.intellij.CommonBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HtmlSource extends DialogWrapper {
    private JPanel contentPane;
    private JTextArea htmlSource;
    private JScrollPane scrollPanel;

    public HtmlSource(Project project, String htmlSource) {
        super(project, false, false);
        setTitle("HTML Source");
        Dimension dimension = new Dimension();
        dimension.setSize(700, 500);
        contentPane.setSize(dimension);
        contentPane.setPreferredSize(dimension);
        contentPane.setMaximumSize(dimension);
        this.htmlSource.setText(htmlSource);
        this.htmlSource.setCaretPosition(0);
        init();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new CloseAction()};
    }

    @Nullable
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    protected class CloseAction extends DialogWrapperAction {
        protected CloseAction() {
            super(CommonBundle.getCloseButtonText());
        }

        protected void doAction(ActionEvent e) {
            close(0);
        }
    }
}
