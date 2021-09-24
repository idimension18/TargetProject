package Target.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Target extends Sprite{
    Image img;
    int diameter, value;
    String destroyed;

    public Target(double newY, int newDiameter, double colorID) {
        try {
            if (colorID <= 3) {
                this.img = ImageIO.read(new File("data/images/redTarget.png"));
            } else if (colorID > 3 && colorID <= 6) {
                this.img = ImageIO.read(new File("data/images/greenTarget.png"));
            } else {
                this.img = ImageIO.read(new File("data/images/yellowTarget.png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        x = 1000;
        y = newY;
        diameter = newDiameter;
        rayon = diameter/2.0;
        centerX = x + (diameter/2.0);
        centerY = y + (diameter/2.0);
        width = diameter;
        height = diameter;

        //value
        value = (int) (100*(1-((diameter-50.0)/100.0)));

        this.destroyed = "no";
    }
    void move() {
        if (x < -diameter) {
            destroyed = "lost";
        }
        x-=3;
        centerX = x + (diameter/2.0);
    }
}
