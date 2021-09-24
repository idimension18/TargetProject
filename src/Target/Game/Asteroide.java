package Target.Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Asteroide extends Sprite{
    BufferedImage img;
    File sheet;
    double angle, rotationSpeed;
    Boolean destroyed;

    public Asteroide(double newY, int newSize, int type, double newAngle, int sens) {
        sheet = new File("data/images/asteroide.png");
        try {
            if (type == 0) {
                img = ImageIO.read(sheet).getSubimage(0, 0, 128, 128);
            } else if (type == 1) {
                img = ImageIO.read(sheet).getSubimage(128, 0, 128, 128);
            } else if (type == 2) {
                img = ImageIO.read(sheet).getSubimage(0, 128, 128, 128);
            } else {
                img = ImageIO.read(sheet).getSubimage(128, 128, 128, 128);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        x = 1000.0;
        y = newY;
        centerX = x + (width/2.0f);
        centerY = y + (height/2.0f);

        width = newSize;
        height = newSize;
        rayon = newSize/2.0;
        angle = (float) newAngle;

        destroyed = false;
        int speedRatio = 60;
        rotationSpeed = sens == 0 ? speedRatio/rayon : -speedRatio/rayon;
    }
    void move() {
        destroyed = x < -width;
        angle += rotationSpeed;
        if (angle >= 360) {
            angle -= 360;
        }
        x -= 3;
        centerX = x + (width/2.0f);
        centerY = y + (height/2.0f);
    }
}
