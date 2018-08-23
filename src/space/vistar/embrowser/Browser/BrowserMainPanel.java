package space.vistar.embrowser.Browser;

import com.intellij.openapi.project.Project;
import javafx.application.Platform;
import space.vistar.embrowser.Browser.HtmlSourceDialog.HtmlSource;
import space.vistar.embrowser.Browser.OptionsDialog.Options;
import space.vistar.embrowser.Icons;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BrowserMainPanel extends JPanel {
    private JPanel BrowserPanel;
    private JPanel NavigateControlPanel;
    private JButton backButton;
    private JButton forwardButton;
    private JButton reloadButton;
    //public JTextField url;

    public JComboBox urlComboBox;

    private JButton navigateButton;
    private JButton optionsButton;
    private JButton zoomOutButton;
    private JButton resetZoomButton;
    private JButton zoomInButton;
    public JLabel zoomState;
    private JPanel zoomPanel;
    private JPanel statePanel;
    public JLabel stateLabel;
    private JPanel optionsPanel;
    private JPanel navigatePanel;
    private JPanel urlNavigatePanel;
    private JPanel bottomPanel;
    private JPanel webBrowserViewPanel;
    private JButton htmlSourceButton;
    public JLabel titleLable;
    private JComponent webPanel;

    private Project project;
    private space.vistar.embrowser.BrowserView browserView;
    private String lastUrl;

    /**
     * @param browserView BrowserMainPanel
     */
    public BrowserMainPanel(space.vistar.embrowser.BrowserView browserView, Project project) {
        this.browserView = browserView;
        this.project = project;
        initWebView();
    }

    /**
     * Main Init
     */
    private void initWebView() {
        Platform.setImplicitExit(false);
        SwingUtilities.invokeLater(() -> {
            removeAll();
            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);

            JComponent browserPanel = BrowserPanel;
            add(browserPanel);

            browserView.init();
            browserView.setBrowserPanel(this);

            webPanel = browserView.getNode();
            webBrowserViewPanel.setLayout(new GridLayout(1, 1, 1, 1));
            webPanel.setAlignmentX(1f);
            webPanel.setAlignmentY(1f);
            webBrowserViewPanel.add(webPanel);

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;

            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 1;
            layout.setConstraints(browserPanel, gridBagConstraints);

            this.initIcons();
            this.setListeners();

            validate();
            repaint();

            this.updateHistoryButtonsState();
            this.updateGoState();
            this.updateRefreshState();
        });
    }

    /**
     * Set Icons for buttons
     */
    private void initIcons() {
        Icons.setButtonIcon(backButton, "back");
        Icons.setButtonIcon(forwardButton, "forw");
        Icons.setButtonIcon(navigateButton, "go");
        Icons.setButtonIcon(reloadButton, "refr");
        Icons.setButtonIcon(zoomOutButton, "minus");
        Icons.setButtonIcon(zoomInButton, "plus");
        Icons.setButtonIcon(resetZoomButton, "zoomreset");
        Icons.setButtonIcon(optionsButton, "options");
        Icons.setButtonIcon(htmlSourceButton, "sourcecode");
    }

    /**
     * Set buttons and fields listeners
     */
    private void setListeners() {
        urlComboBox.addActionListener(event -> {
            if (event.getActionCommand().equals("comboBoxChanged")) {
                updateGoState();
                updateRefreshState();
            }
            if (event.getActionCommand().equals("comboBoxEdited")) {
                if (urlComboBox.getItemCount() == 0) {
                    return;
                }
                if (Objects.requireNonNull(urlComboBox.getSelectedItem()).toString().equals(lastUrl)) {
                    return;
                }

                this.webBrowserLoad();
            }
        });

        backButton.addActionListener(event -> browserView.goBack());
        forwardButton.addActionListener(event -> browserView.goForward());
        navigateButton.addActionListener(event -> this.webBrowserLoad());
        reloadButton.addActionListener(event -> this.webBrowserLoad());
        zoomOutButton.addActionListener(event -> browserView.setZoom(-0.25));
        resetZoomButton.addActionListener(event -> browserView.resetZoom());
        zoomInButton.addActionListener(event -> browserView.setZoom(0.25));

        optionsButton.addActionListener(e -> {
            Options options = new Options(project);
            options.show();
        });

        this.updateSourceCodeState();
        htmlSourceButton.addActionListener(e -> {
            HtmlSource htmlSource = new HtmlSource(project, browserView.getHtmlSource());
            htmlSource.show();
        });
    }

    /**
     * Обновляем состояние кнопок истори (туды-сюды)
     */
    public void updateHistoryButtonsState() {
        backButton.setEnabled(browserView.hasHistory(-1));
        forwardButton.setEnabled(browserView.hasHistory(1));
    }

    /**
     * Состояние кнопки GO
     */
    private void updateGoState()
    {
        Object value = urlComboBox.getSelectedItem();
        if (value == null) {
            navigateButton.setEnabled(false);
            return;
        }
        navigateButton.setEnabled(!value.toString().isEmpty());
    }

    /**
     * Состояние кнопки Refresh
     */
    private void updateRefreshState()
    {
        Object value = urlComboBox.getSelectedItem();
        if (value == null) {
            reloadButton.setEnabled(false);
            return;
        }
        reloadButton.setEnabled(!value.toString().isEmpty());
        //reloadButton.setEnabled(!this.url.getText().isEmpty());
    }

    /**
     * Состояние кнопки Source Code
     */
    public void updateSourceCodeState()
    {
        htmlSourceButton.setEnabled(browserView.hasHtmlSource());
    }

    /**
     * Обновление кнопок масштабирования
     */
    public void updateZoomButtons()
    {
        resetZoomButton.setEnabled(browserView.isScaled());
        zoomInButton.setEnabled(browserView.canZoomIn());
        zoomOutButton.setEnabled(browserView.canZoomOut());
    }

    /**
     *
     */
    private void webBrowserLoad()
    {
     //   String trim = url.getText().trim();
        if (urlComboBox.getItemCount() == 0) {
            return;
        }

        String trim = Objects.requireNonNull(urlComboBox.getSelectedItem()).toString();
        if (trim.isEmpty()) {
            return;
        }
        if (!trim.startsWith("http")) {
            trim = "http://" + trim;
        }
        lastUrl = trim;
        browserView.load(trim);
    }
}
