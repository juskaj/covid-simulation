import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

public class SimulationPanel extends JPanel {
    int width;
    int height;
    Image imgToDraw = null;

    public SimulationPanel(){

    }

    public void updateParameters(int width, int height, Image imgToDraw){
        this.width = width;
        this.height = height;
        this.imgToDraw = imgToDraw;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(imgToDraw != null){
            g.drawImage(imgToDraw, 0, 0, new ImageObserver() {
                @Override
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    return true;
                }
            });
        }
    }
}
