package space.vistar.embrowser;


import javafx.application.Platform;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

/**
 * Main panel component
 */
public class BrowserPanel extends JPanel {

    public JTextField urlField;
    private BrowserView browserView;
    public JButton buttonBack;
    public JButton buttonForward;
    public JButton buttonGo;
    public JButton buttonRefresh;
    public JButton buttonMinus;
    public JButton buttonPlus;
    public JButton buttonResetZoom;
    public JLabel zoomLabel;
    public JLabel stateLabel;

    /**
     * @param browserView BrowserView
     */
    public BrowserPanel(BrowserView browserView) {
        this.browserView = browserView;
        initWebView();
    }

    /**
     * @return JPanel
     */
    public JPanel getTopControllers() {
        JPanel topControlPanel = new JPanel();
        GridBagLayout topControlPanelLayout = new GridBagLayout();

        topControlPanel.setLayout(topControlPanelLayout);
        this.urlField = new JTextField();

        buttonBack = new JButton("");
        buttonBack.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonBack, "back");

        buttonForward = new JButton("");
        buttonForward.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonForward, "forw");

        buttonGo = new JButton("");
        buttonGo.setPreferredSize(new Dimension(40, 30));
        setButtonIcon(buttonGo, "go");

        buttonRefresh = new JButton("");
        buttonRefresh.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonRefresh, "refr");

        topControlPanel.add(buttonBack);
        topControlPanel.add(buttonForward);
        topControlPanel.add(urlField);
        topControlPanel.add(buttonGo);
        topControlPanel.add(buttonRefresh);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        topControlPanelLayout.setConstraints(buttonBack, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        topControlPanelLayout.setConstraints(buttonForward, gridBagConstraints);
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        topControlPanelLayout.setConstraints(urlField, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        topControlPanelLayout.setConstraints(buttonGo, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        topControlPanelLayout.setConstraints(buttonRefresh, gridBagConstraints);

        urlField.addActionListener(event -> this.webBrowserLoad());

        urlField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtons();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtons();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtons();
            }

            void updateButtons() {
                updateGoState();
                updateRefreshState();
            }
        });

        buttonBack.addActionListener(event -> browserView.goBack());

        buttonForward.addActionListener(event -> browserView.goForward());

        buttonGo.addActionListener(event -> this.webBrowserLoad());

        buttonRefresh.addActionListener(event -> this.webBrowserLoad());

        return topControlPanel;
    }

    /**
     * @return JPanel
     */
    public JPanel getBottomControllers() {
        JPanel bottomControlPanel = new JPanel();
        GridBagLayout bottomControlPanelLayout = new GridBagLayout();

        bottomControlPanel.setLayout(bottomControlPanelLayout);

        buttonMinus = new JButton("");
        buttonMinus.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonMinus, "minus");

        buttonPlus = new JButton("");
        buttonPlus.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonPlus, "plus");

        buttonResetZoom = new JButton("");
        buttonResetZoom.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonResetZoom, "zoomreset");

        zoomLabel = new JLabel("");
        stateLabel = new JLabel("");
        stateLabel.setSize(100, 30);

        bottomControlPanel.add(stateLabel);
        bottomControlPanel.add(buttonMinus);
        bottomControlPanel.add(buttonResetZoom);
        bottomControlPanel.add(buttonPlus);
        bottomControlPanel.add(zoomLabel);


        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        bottomControlPanelLayout.setConstraints(buttonMinus, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;

        buttonMinus.addActionListener(event -> {
            browserView.setZoom(-0.25);
        });

        buttonResetZoom.addActionListener(event -> {
            browserView.resetZoom();
        });

        buttonPlus.addActionListener(event -> {
            browserView.setZoom(0.25);
        });


        return bottomControlPanel;
    }

    /**
     *
     */
    private void webBrowserLoad()
    {
        String trim = urlField.getText().trim();
        if (!trim.startsWith("http")) {
            trim = "http://" + trim;
        }
        browserView.load(trim);
    }

    /**
     * @param button Button for setup icon
     * @param iconName icon name
     */
    private void setButtonIcon(JButton button, String iconName) {
        button.setIcon(Icons.getInstance().getButtonIcon(iconName, Icons.STATE_NORMAL));
        button.setPressedIcon(Icons.getInstance().getButtonIcon(iconName, Icons.STATE_PRESSED));
        button.setDisabledIcon(Icons.getInstance().getButtonIcon(iconName, Icons.STATE_DISABLE));
    }

    /**
     * Обновляем состояние кнопок истори (туды-сюды)
     */
    private void updateHistoryButtonsState() {
        buttonBack.setEnabled(browserView.hasHistory(-1));
        buttonForward.setEnabled(browserView.hasHistory(1));
    }

    /**
     * Состояние кнопки GO
     */
    private void updateGoState()
    {
        buttonGo.setEnabled(!this.urlField.getText().isEmpty());
    }

    /**
     * Состояние кнопки Refresh
     */
    private void updateRefreshState()
    {
        buttonRefresh.setEnabled(!this.urlField.getText().isEmpty());
    }

    /**
     * Обновление кнопок масштабирования
     */
    public void updateZoomButtons()
    {
        buttonResetZoom.setEnabled(browserView.isScaled());
        buttonPlus.setEnabled(browserView.canZoomIn());
        buttonMinus.setEnabled(browserView.canZoomOut());
    }

    private void initWebView() {
        Platform.setImplicitExit(false);
        SwingUtilities.invokeLater(() -> {
            removeAll();
            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);

            JComponent topControllers = getTopControllers();
            add(topControllers);

            browserView.init();
            browserView.setPanel(this);

            JComponent webPanel = browserView.getNode();
            add(webPanel);

            JComponent bottomControllers = getBottomControllers();
            add(bottomControllers);

            browserView.urlChangeCallback(s -> {
                urlField.setText(s);
                updateHistoryButtonsState();
            });

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;

            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 0;
            layout.setConstraints(topControllers, gridBagConstraints);

            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 10;
            layout.setConstraints(webPanel, gridBagConstraints);

            gridBagConstraints.gridwidth = 0;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 0;
            layout.setConstraints(bottomControllers, gridBagConstraints);

            validate();
            repaint();

            buttonResetZoom.setEnabled(false);
            updateHistoryButtonsState();
            updateGoState();
            updateRefreshState();
        });
    }
}
