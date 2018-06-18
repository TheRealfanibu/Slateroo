package ai.A3C;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TrainRenderListener extends KeyAdapter {
    private boolean rendering = false;

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_R)
            rendering = !rendering;
    }

    public boolean isRendering() {
        return rendering;
    }
}
