/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drowskifree;

/**
 * http://drow.today
 *
 * @author datampq
 */
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

/**
 *
 * @author green
 */
public class FrameDragListener extends MouseAdapter {

    private final JFrame frame;
    private Point mouseDownCompCoords = null;

    public FrameDragListener(JFrame frame) {
        this.frame = frame;
    }

    public void mouseReleased(MouseEvent e) {
        mouseDownCompCoords = null;
    }

    public void mousePressed(MouseEvent e) {
        mouseDownCompCoords = e.getPoint();
    }

    public void mouseDragged(MouseEvent e) {
        Point currCoords = e.getLocationOnScreen();
        try {
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        } catch (Exception ex) {

        }

    }
}
