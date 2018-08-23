package space.vistar.embrowser;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.HashMap;

public class Icons {
    public static final String STATE_NORMAL = "normal";
    public static final String STATE_DISABLE = "disabled";
    public static final String STATE_PRESSED = "pressed";

    public static final String BUTTON_BACK = "back";
    public static final String BUTTON_FORWARD = "forw";
    public static final String BUTTON_GO = "go";
    public static final String BUTTON_PLUS = "plus";
    public static final String BUTTON_MINUS = "minus";
    public static final String BUTTON_ZOOM_RESET = "zoomreset";
    public static final String BUTTON_REFRESH = "refr";
    public static final String BUTTON_OPTIONS = "options";
    public static final String BUTTON_SOURCECODE = "sourcecode";

    private String[] buttonsPreload = {
        BUTTON_BACK, BUTTON_FORWARD,
        BUTTON_MINUS, BUTTON_PLUS,
        BUTTON_GO, BUTTON_REFRESH,
        BUTTON_ZOOM_RESET, BUTTON_OPTIONS,
        BUTTON_SOURCECODE,
    };

    private String[] buttonState = {
        STATE_DISABLE, STATE_NORMAL, STATE_PRESSED
    };

    private static HashMap<String, HashMap<String, Icon>> buttonsIcons = new HashMap<>();

    private static Icons instance;

    /**
     * Singleton get Instance
     * @return Icons
     */
    public static synchronized Icons getInstance() {
        if (instance == null) {
            instance = new Icons();
        }
        return instance;
    }

    /**
     * Contractor
     */
    public Icons() {
        for (String button : buttonsPreload) {
            HashMap<String, Icon> buttonIcons = new HashMap<>();
            for (String state : buttonState) {
                buttonIcons.put(state, IconLoader.getIcon("/icons/buttons/" + button + "_" + state + ".png"));
            }
            buttonsIcons.put(button, buttonIcons);
        }
    }

    /**
     * @param button
     * @param state
     * @return null|Icon
     */
    public Icon getButtonIcon(String button, String state) {
        if (!buttonsIcons.containsKey(button)) {
            return null;
        }

        HashMap<String, Icon> buttonIcons = buttonsIcons.get(button);
        if (!buttonIcons.containsKey(state)) {
            return null;
        }

        return buttonIcons.get(state);
    }

    /**
     * @param button Button for setup icon
     * @param iconName icon name
     */
    public static void setButtonIcon(JButton button, String iconName) {
        button.setIcon(Icons.getInstance().getButtonIcon(iconName, Icons.STATE_NORMAL));
        button.setPressedIcon(Icons.getInstance().getButtonIcon(iconName, Icons.STATE_PRESSED));
        button.setDisabledIcon(Icons.getInstance().getButtonIcon(iconName, Icons.STATE_DISABLE));
    }
}
