package space.vistar.embrowser;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main panel component
 */
public class BrowserPanel extends JPanel {

    private JTextField urlField;
    private BrowserView browserView;
    private BinaryTreeNode<String> history;
    private AtomicBoolean inHistory = new AtomicBoolean(false);

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

        JButton buttonBack = new JButton("");
        buttonBack.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonBack, "back");

        JButton buttonForward = new JButton("");
        buttonForward.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonForward, "forw");

        JButton buttonGo = new JButton("");
        buttonGo.setPreferredSize(new Dimension(40, 30));
        setButtonIcon(buttonGo, "go");

        JButton buttonRefresh = new JButton("");
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
        gridBagConstraints.gridwidth = 5;
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

        buttonBack.addActionListener(event -> {
            if (history != null && history.getLeftChild() != null) {
                history = history.getLeftChild();
                if (history.getData() != null) {
                    browserView.load(history.getData().trim());
                    inHistory.set(true);
                }
            }
        });

        buttonForward.addActionListener(event -> {
            if (history != null && history.getRightChild() != null) {
                history = history.getRightChild();
                if (history.getData() != null) {
                    browserView.load(history.getData().trim());
                    inHistory.set(true);
                }
            }
        });

        buttonGo.addActionListener(event -> this.webBrowserLoad());

        buttonRefresh.addActionListener(event -> {
            //initWebView();
            this.webBrowserLoad();
        });

        return topControlPanel;
    }

    /**
     * @return JPanel
     */
    public JPanel getBottomControllers() {
        JPanel bottomControlPanel = new JPanel();
        GridBagLayout topControlPanelLayout = new GridBagLayout();

        bottomControlPanel.setLayout(topControlPanelLayout);

        JButton buttonMinus = new JButton("");
        buttonMinus.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonMinus, "minus");

        JButton buttonPlus = new JButton("");
        buttonPlus.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonPlus, "plus");


        bottomControlPanel.add(buttonMinus);
        bottomControlPanel.add(buttonPlus);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        topControlPanelLayout.setConstraints(buttonMinus, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;

        buttonMinus.addActionListener(event -> {
            browserView.setZoom(-0.25);
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
        String path = "/icons/buttons/";
        try {
            Image normalIcon = ImageIO.read(getClass().getResource(path + iconName + "_normal.png"));
            Image pressedIcon = ImageIO.read(getClass().getResource(path + iconName + "_pressed.png"));
            Image disablesIcon = ImageIO.read(getClass().getResource(path + iconName + "_disabled.png"));
            button.setIcon(new ImageIcon(normalIcon));
            button.setPressedIcon(new ImageIcon(pressedIcon));
            button.setDisabledIcon(new ImageIcon(disablesIcon));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            JComponent webPanel = browserView.getNode();
            add(webPanel);

            JComponent bottomControllers = getBottomControllers();
            add(bottomControllers);


            browserView.urlChangeCallback(s -> {
                urlField.setText(s);
                if (!inHistory.get())
                    if (history == null) {
                        history = new BinaryTreeNode<>(s);
                    } else {
                        BinaryTreeNode<String> current = new BinaryTreeNode<>(s);
                        history.setRightChild(current);
                        current.setLeftChild(history);
                        history = history.getRightChild();
                    }
                inHistory.set(false);
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
        });
    }
}
