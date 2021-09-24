package Target.Game;

import Sound.Sound;
import Target.Main.Fenetre;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static java.lang.Math.*;
import static java.lang.StrictMath.abs;

public class Ship extends Sprite {
    BufferedImage img, fire;
    Image lifeImg;
    JLabel blowGIF, sparkGIF;
    String Sheet;
    float jetPower, speedMax;
    double angle, velocityX, velocityY;
    int blowTime, blowTimer, rotationSpeed, damagedTime, damagedTimer, blinkTime, blinkTimer, sparkTime, sparkTimer, HP, energie, maxEnergie, chargeTime, chargeTimer;
    boolean fireOK, isBlow, isVisible, isDamaged, isStunt, recharge;
    Sound spark, blow;

    public Ship() {
        Sheet = "data/images/sprites.png";
        try {
            img = ImageIO.read(new File(Sheet)).getSubimage(2048, 0, 256, 256);
            fire = ImageIO.read(new File(Sheet)).getSubimage(2048, 256, 256, 256);
            lifeImg = ImageIO.read(new File("data/images/life.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        blowGIF = new JLabel(new ImageIcon("data/images/blow.gif"));
        sparkGIF = new JLabel(new ImageIcon("data/images/spark.gif"));

        spark = new Sound(new File("data/music/spark.wav"));
        blow = new Sound(new File("data/music/blow.wav"));

        width = 64;
        height = 64;
        angle = 0;

        x = 500;
        y = 250;
        centerX = x + (width/2.0f);
        centerY = y + (height/2.0f);
        rayon = 32;

        velocityX = 0;
        velocityY = 0;

        jetPower = 0.1f;
        rotationSpeed = 7;
        speedMax = 5.0f;

        fireOK = false;
        isVisible = true;

        isDamaged = false;
        damagedTime = 120; //pair number
        damagedTimer = 0;

        blinkTime = 10;
        blinkTimer = 0;

        isBlow = false;
        blowTime = 150;
        blowTimer = 0;

        sparkTime = 60;
        sparkTimer = 0;

        HP = 5;

        maxEnergie = 30;
        energie = 30;
        recharge = true;
        chargeTime = 5;
        chargeTimer = 0;

    }
    void go() {
        if (fireOK) {
            velocityX += cos(toRadians(angle)) * jetPower;
            velocityY += sin(toRadians(angle)) * jetPower;

            if (abs(velocityX) > speedMax) {
                velocityX = velocityX > 0 ? speedMax : -speedMax;
            }
            if (abs(velocityY) > speedMax) {
                velocityY = velocityY > 0 ? speedMax : -speedMax;
            }
        }
    }

    //tourne
    void tourne(double newAngle) {
        double diff = abs(angle - newAngle);
        if (diff >= rotationSpeed) {
            if ((angle < newAngle && diff < 180) || (angle > newAngle && diff > 180)) {
                angle += rotationSpeed;
                if (angle >= 360) {
                    angle -= 360;
                }
            } else {
                angle -= rotationSpeed;
                if (angle < 0) {
                    angle += 360;
                }
            }
        } else {
            angle = newAngle;
        }
    }

    ////phisique du jeux
    void velocity() {
        x += velocityX;
        y += velocityY;
        centerX = x + (width/2.0f);
        centerY = y + (height/2.0f);
    }

    void collide() {
        if (x > 1000 - width || x < 0) {
            velocityX = 0;
            if (x < 0) {
                x = 0;
            }
            if (x > 1000 - width) {
                x = (1000 - width);
            }
        }
        if (y > 500 - height ||y <0) {
            velocityY = 0;
            if (y < 0) {
                y=0;
            }
            if (y > (500 - height)) {
                y = (500 - height);
            }
        }
    }

    Boolean blowUp(Fenetre windows) {
        if (blowTimer < blowTime) {
            isVisible = false;
            blowTimer += 1;
            blowGIF.setBounds((int) (x-(350)/2)+(width/2), (int) (y-(350)/2)+(height/2), 350, 350); // for example, you can use your own values
            if (blowTimer == 1) {
                windows.getContentPane().add(blowGIF);
            }
            x += velocityX/2;
            y += velocityY/2;
            centerX = x + (width/2.0f);
            centerY = y + (height/2.0f);
            return false;
        } else {
            windows.getContentPane().remove(blowGIF);
            isVisible = true;
            isBlow = false;
            blowTimer = 0;
            return true;
        }
    }

    void damaged(Fenetre windows) {
        if (damagedTimer < damagedTime) {
            damagedTimer += 1;
            blinkTimer += 1;
            sparkTimer += 1;

            sparkGIF.setBounds((int) x, (int) y, width, height);
            if (sparkTimer == 1) {
                windows.getContentPane().add(sparkGIF);
            } else if (sparkTimer > sparkTime) {
                recharge = true;
                windows.getContentPane().remove(sparkGIF);
            }
            if (blinkTimer > blinkTime) {
                blinkTimer = 0;
                isVisible = !isVisible;
            }
            if (damagedTimer == damagedTime/4) {
                isStunt = false;
                velocityX = 0;
                velocityY = 0;
            } else if (damagedTimer < damagedTime/4) {
                recharge = false;
                fireOK = false;
                velocityX += (velocityX == 0 && velocityY ==0) ? 3 : 0;
                x -= velocityX/2;
                y -= velocityY/2;
                centerX = x + (width/2.0f);
                centerY = y + (height/2.0f);
            }
        } else {
            isDamaged = false;
            isVisible = true;
            damagedTimer = 0;
            blinkTimer = 0;
            sparkTimer = 0;
            recharge = true;
        }
    }

    void dash(Fenetre windows) {

    }

    void battery() {
        if (recharge && energie < maxEnergie) {
            if (chargeTimer == chargeTime) {
                energie += 1;
                chargeTimer = 0;
            } else {
                chargeTimer += 1;
            }
        }
    }
}
