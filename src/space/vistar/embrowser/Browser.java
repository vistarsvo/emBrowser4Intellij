package space.vistar.embrowser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main panel component
 */
public class Browser extends JPanel {

    private JTextField urlField;

    private BrowserView browserView;

    private BinaryTreeNode<String> history;

    private AtomicBoolean inHistory = new AtomicBoolean(false);

    public Browser(BrowserView browserView) {
        this.browserView = browserView;
        initWebView();
    }

    /**
     * @return
     */
    public JPanel getControllers() {
        JPanel controllers = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        controllers.setLayout(layout);
        urlField = new JTextField();

        JButton buttonPrev = new JButton("");
        buttonPrev.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonPrev, "back");

        JButton buttonNext = new JButton("");
        buttonNext.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonNext, "forw");

        JButton buttonGo = new JButton("");
        buttonGo.setPreferredSize(new Dimension(40, 30));
        setButtonIcon(buttonGo, "go");

        JButton buttonReload = new JButton("");
        buttonReload.setPreferredSize(new Dimension(30, 30));
        setButtonIcon(buttonReload, "refr");

        controllers.add(buttonPrev);
        controllers.add(buttonNext);
        controllers.add(urlField);
        controllers.add(buttonGo);
        controllers.add(buttonReload);
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(buttonPrev, s);
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(buttonNext, s);
        s.gridwidth = 5;
        s.weightx = 1;
        s.weighty = 0;
        layout.setConstraints(urlField, s);
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(buttonGo, s);
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(buttonReload, s);

        urlField.addActionListener(event -> {
            String trim = urlField.getText().trim();
            if (!trim.startsWith("http")) {
                trim = "http://" + trim;
            }
            browserView.load(trim);
        });
        buttonPrev.addActionListener(event -> {
            if (history != null && history.getLeftChild() != null) {
                history = history.getLeftChild();
                if (history.getData() != null) {
                    browserView.load(history.getData().trim());
                    inHistory.set(true);
                }
            }
        });

        buttonNext.addActionListener(event -> {
            if (history != null && history.getRightChild() != null) {
                history = history.getRightChild();
                if (history.getData() != null) {
                    browserView.load(history.getData().trim());
                    inHistory.set(true);
                }
            }
        });

        buttonReload.addActionListener(event -> {
            initWebView();
        });
        return controllers;
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
        SwingUtilities.invokeLater(() -> {
            removeAll();
            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);
            JComponent controllers = getControllers();
            add(controllers);
            browserView.init();
            JComponent webPanel = browserView.getNode();
            add(webPanel);
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
            GridBagConstraints s = new GridBagConstraints();
            s.fill = GridBagConstraints.BOTH;
            s.gridwidth = 0;
            s.weightx = 1;
            s.weighty = 0;
            layout.setConstraints(controllers, s);
            s.gridwidth = 0;
            s.weightx = 1;
            s.weighty = 1;
            layout.setConstraints(webPanel, s);
            validate();
            repaint();
        });
    }
}
