package space.vistar.embrowser;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JComponent;
import java.security.GeneralSecurityException;
import java.util.function.Consumer;

/**
 * Браузер
 */
public class JavaFxBrowserView implements BrowserView {

    private static final String userAgent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/61.0";

    private WebView browser;
    private WebEngine webEngine;

    private JFXPanel jfxPanel;

    private BrowserPanel panel;

    static {
        // https://stackoverflow.com/questions/22605701/javafx-webview-not-working-using-a-untrusted-ssl-certificate
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
        }
    }


    public JavaFxBrowserView() {
    }

    public void setPanel(BrowserPanel panel) {
        this.panel = panel;
    }

    @Override
    public void init() {
        reload();
    }

    @Override
    public void load(String url) {
        Platform.runLater(() -> {
            webEngine.load(url);
        });
    }

    @Override
    public void reload() {
        jfxPanel = new JFXPanel();
        Platform.runLater(() -> {
            browser = new WebView();
            webEngine = browser.getEngine();
            webEngine.setUserAgent(userAgent);
            webEngine.setUserStyleSheetLocation(getClass().getResource("/default.css").toExternalForm());
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (Worker.State.RUNNING.equals(newValue)) {
                     panel.stateLabel.setText("Loading...");
                }
                else if (Worker.State.FAILED.equals(newValue)) {
                    panel.stateLabel.setText("Loading error");
                    String message = "Error load url: " + panel.urlField.getText();
                    Notifications.Bus.notify(
                            new Notification(
                                    "emBrowser",
                                    "Loading Error",
                                    message,
                                    NotificationType.ERROR));
                    panel.updateHistoryButtonsState();
                } else if (Worker.State.SUCCEEDED.equals(newValue)) {
                    panel.stateLabel.setText("OK");
                    String location = webEngine.getLocation();
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        panel.urlField.setText(location);
                                    });
                                }
                            },
                            500
                    );
                    panel.updateHistoryButtonsState();
                }
            });
        });
    }

    @Override
    public void urlChangeCallback(Consumer<String> consumer) {
        Platform.runLater(() -> {
            webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
                consumer.accept(webEngine.getLocation());
               // panel.urlField.setText(webEngine.getLocation());
            });
        });
    }

    @Override
    public JComponent getNode() {
        Platform.runLater(() -> {
            BorderPane borderPane = new BorderPane();
            borderPane.setCenter(browser);
            Scene scene = new Scene(borderPane);
            jfxPanel.setScene(scene);
        });

        return jfxPanel;
    }

    private double currentScale = 1;
    @Override
    public void setZoom(double scale)
    {
        Platform.runLater(() -> {
            double currentZoom = browser.getZoom();
            if ((scale < 0 && currentZoom >= 0.50)
                || (scale > 0 && currentZoom <= 3.75)) {
                browser.setZoom(currentZoom + scale);
                currentScale = currentZoom + scale;
                panel.updateZoomButtons();
                if (currentScale == 1d) {
                    panel.zoomLabel.setText("");
                } else {
                    panel.zoomLabel.setText(String.valueOf(Math.round(currentScale * 100)) + '%');
                }
            }
        });
    }
    @Override
    public void resetZoom()
    {
        Platform.runLater(() -> {
            browser.setZoom(1);
            currentScale = 1;
            panel.zoomLabel.setText("");
        });
    }

    @Override
    public boolean canZoomIn()
    {
        return currentScale <= 3.75;
    }

    @Override
    public boolean canZoomOut()
    {
        return currentScale >= 0.5;
    }

    @Override
    public boolean isScaled()
    {
        return currentScale != 1d;
    }

    @Override
    public void goBack()
    {
        Platform.runLater(() -> webEngine.getHistory().go(this.hasHistory(-1) ? -1 : 0));
    }

    @Override
    public void goForward()
    {
        Platform.runLater(() -> webEngine.getHistory().go(this.hasHistory(1) ? 1 : 0));
    }

    @Override
    public boolean hasHistory(int way)
    {
        if (webEngine == null) {
            return false;
        }

        final WebHistory history = webEngine.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();

        if (way == -1 ) {
            return entryList.size() > 1 && currentIndex > 0;
        } else if (way == 1) {
            return entryList.size() > 1 && currentIndex < entryList.size() - 1;
        } else {
            return false;
        }
    }
}