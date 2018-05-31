package space.vistar.embrowser;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Tool Window Factory
 */
public class BrowserWindowFactory implements ToolWindowFactory {

    /**
     *  Constructor
     */
    public BrowserWindowFactory() {

    }

    /**
     * @param project Project
     * @param toolWindow ToolWindow
     */
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        final ToolWindowManager manager = ToolWindowManager.getInstance(project);


        System.out.println("create");
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(new Browser(new JavaFxBrowserView()),"", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getActivation().doWhenDone(() -> System.out.println("doWhenDone"));

        toolWindow.activate(() -> System.out.println("activate"));
        toolWindow.hide(() -> System.out.println("hide"));
        toolWindow.getContentManager().getComponent().addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {

                System.out.println("Moved");
            }

            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("Shown");
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                System.out.println("Hidden");
            }
        });

        final ToolWindowManagerListener listener = new ToolWindowManagerListener() {
            @Override
            public void toolWindowRegistered(@NotNull String id) {
            }

            @Override
            public void stateChanged() {
               System.out.println("Toggle");
            }
        };

        toolWindow.show(new Runnable() {
            @Override
            public void run() {
                ((ToolWindowManagerEx) manager).addToolWindowManagerListener(listener);
            }
        });


    }
}
