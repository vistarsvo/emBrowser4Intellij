package space.vistar.embrowser;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Интерфейс браузера
 */
public interface BrowserView {
    public void init();
    public void load(String url);
    public void reload();
    public void urlChangeCallback(Consumer<String> consumer);
    public JComponent getNode();
    public void setZoom(double scale);
    public void resetZoom();
    public void goBack();
    public void goForward();
    public boolean hasHistory(int way);
    public boolean isScaled();
    public boolean canZoomIn();
    public boolean canZoomOut();
    public void setPanel(BrowserPanel panel);
}
