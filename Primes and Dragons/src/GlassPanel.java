import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by RyanNiu on 4/18/2016.
 */
class GlassPanel extends JPanel implements MouseListener, MouseMotionListener{
    public final double MASTER_SCALE = 1.0;
    public boolean isOptions = false;
    public GlassPanel(){
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    public void paintComponent(Graphics g) {
        if (isOptions){
            Image cover = Toolkit.getDefaultToolkit().getImage("resources/board/options_menu/dim.png");
            g.drawImage(cover, 0, 0, (int) (cover.getWidth(this) * MASTER_SCALE), (int) (cover.getHeight(this) * MASTER_SCALE), this);
        }
    }
    public void displayOptions(boolean state){
        isOptions = state;
        repaint();
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}
}
