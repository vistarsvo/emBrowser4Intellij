package space.vistar.embrowser.Browser;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import space.vistar.embrowser.BrowserView;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;
import java.util.function.Consumer;

/**
 * Браузер
 */
public class JavaFxBrowserView implements BrowserView {
    private WebView browser;
    private WebEngine webEngine;
    private JFXPanel jfxPanel;
    private BrowserMainPanel browserPanel;
    private String lastHmlSource = "";

    static {
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

    /**
     * Construct
     */
    public JavaFxBrowserView() {

    }

    /**
     * @param browserPanel
     */
    public void setBrowserPanel(BrowserMainPanel browserPanel) {
        this.browserPanel = browserPanel;
    }

    @Override
    public String getHtmlSource() {
        return this.lastHmlSource;
    }

    @Override
    public boolean hasHtmlSource() {
        return lastHmlSource.length() > 0;
    }

    @Override
    public void init() {
        reload();
    }

    @Override
    public void load(String url) {
        lastHmlSource = "";
        Platform.runLater(() -> {
            webEngine.setUserAgent(BrowserConfig.userAgent);
            webEngine.load(url);
        });
    }

    @Override
    public void reload() {
        lastHmlSource = "";
        jfxPanel = new JFXPanel();
        Platform.runLater(() -> {
            browser = new WebView();
            webEngine = browser.getEngine();
            webEngine.setUserAgent(BrowserConfig.userAgent);
            webEngine.setUserStyleSheetLocation(getClass().getResource("/default.css").toExternalForm());

            webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
                browserPanel.stateLabel.setText("...");
                if (Worker.State.SCHEDULED.equals(newValue)) {
                    browserPanel.stateLabel.setText("Scheduled loading");
                } else if (Worker.State.CANCELLED.equals(newValue)) {
                    browserPanel.stateLabel.setText("Loading canceled");
                } else if (Worker.State.RUNNING.equals(newValue)) {
                     browserPanel.stateLabel.setText("Loading document and resources...");
                }
                else if (Worker.State.FAILED.equals(newValue)) {
                    browserPanel.stateLabel.setText("Loading error");
                    String message = "Error load url: ";// + browserPanel.url.getText();
                    Notifications.Bus.notify(
                            new Notification(
                                    "emBrowser",
                                    "Loading Error",
                                    message,
                                    NotificationType.ERROR));
                    browserPanel.updateHistoryButtonsState();
                } else if (Worker.State.SUCCEEDED.equals(newValue)) {

                    String location = webEngine.getLocation();
                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                 //   Platform.runLater(() -> browserPanel.url.setText(location));
                                }
                            },
                            500
                    );
                    browserPanel.updateHistoryButtonsState();
                    browserPanel.titleLable.setText(webEngine.getTitle());

                    org.w3c.dom.Document doc = webEngine.getDocument();
                    try {
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                        OutputStream output = new OutputStream()
                        {
                            private StringBuilder string = new StringBuilder();
                            @Override
                            public void write(int b) throws IOException {
                                this.string.append((char) b );
                            }

                            public String toString(){
                                return this.string.toString();
                            }
                        };

                        transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(output, "UTF-8")));
                        this.lastHmlSource = output.toString();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        this.lastHmlSource = ex.getMessage();
                    }
                    String con = this.getHtmlSource();
                    browserPanel.stateLabel.setText(" " + (Math.round(con.length() / 1024) + " kb"));
                    browserPanel.updateSourceCodeState();

                    browserPanel.urlComboBox.removeAllItems();
                    ObservableList<WebHistory.Entry> ent = webEngine.getHistory().getEntries();
                    for (int x = 0; x < ent.size(); x++) {
                        browserPanel.urlComboBox.addItem(ent.get(ent.size() - 1 - x).getUrl());
                        if (x >= 15) {
                            break;
                        }
                    }
                }
            });
        });
    }

    @Override
    public void urlChangeCallback(Consumer<String> consumer) {
        Platform.runLater(() ->
                webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) ->
                        consumer.accept(webEngine.getLocation())));
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
                browserPanel.updateZoomButtons();
                browserPanel.zoomState.setText("zoom " + String.valueOf(Math.round(currentScale * 100)) + '%');
            }
        });
    }

    @Override
    public void resetZoom()
    {
        Platform.runLater(() -> {
            browser.setZoom(1);
            currentScale = 1;
            browserPanel.zoomState.setText("zoom " + String.valueOf(Math.round(currentScale * 100)) + '%');
            browserPanel.updateZoomButtons();
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