package Target.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static java.lang.Math.*;

public class Laser extends Sprite {
    Image img;
    double speed;
    double angle;
    int width, height;
    Boolean destroyed;

    public Laser(double x, double y, double angle) {
        try {
            img = ImageIO.read(new File("data/images/laser.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        width = 50;
        height = 13;
        rayon = 0;

        this.x = x;
        this.y = y;
        this.angle = angle;

        //calcule de tete
        centerX = (x + width/2.0) + (float) cos(toRadians(angle))*(width/2.0);
        centerY = (y + height/2.0) + (float) sin(toRadians(angle))*(height/2.0);
        speed = 10;

        //valeur lambda
        destroyed = false;

    }
    //lance le laser
    void go() {
        x += cos(toRadians(angle))*speed;
        y += sin(toRadians(angle))*speed;
        centerX = (x + width/2.0) + (float) cos(toRadians(angle))*(width/2.0);
        centerY = (y + height/2.0) + (float) sin(toRadians(angle))*(width/2.0);
    }

    //kill le laser hors de l'ecrans
    void overScreen() {
        if (x > 1064 || x < -64 || y > 564 || y < -64) {
            destroyed = true;
        }
    }
}
