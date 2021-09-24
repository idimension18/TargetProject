package Target.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class ChargeImage extends JComponent {
    Graphics2D gx;
    Jeux jeux;
    AffineTransform old;

    public ChargeImage(Jeux Jeux) {
        jeux = Jeux;
        setMinimumSize(new Dimension(1000, 500));
        setPreferredSize(new Dimension(1000, 500));
        setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MIN_VALUE));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gx = (Graphics2D) g;
        //afiche le fond
        g.drawImage(jeux.background, 0, 0, jeux.windows.getWidth(), jeux.windows.getHeight(), this);
        gx.scale(jeux.windows.getWidth()/1000.0, jeux.windows.getHeight()/500.0);
        old = gx.getTransform();
        //////////LASER////////////////
        for (Laser laser:jeux.lasers) {
            gx.rotate(Math.toRadians(laser.angle), laser.x + (laser.width/2.0),laser.y + (laser.height/2.0));
            g.drawImage(laser.img, (int) laser.x, (int) laser.y, laser.width, laser.height, this);
            gx.setTransform(old);

            ///debug laser////
            if (jeux.debug) {
                g.setColor(Color.red);
                g.fillOval((int) laser.centerX - 4, (int) laser.centerY - 4, 8, 8);
            }
        }
        /////////////TARGET//////////////////
        for (Target target:jeux.targets) {
            g.drawImage(target.img, (int)target.x, (int)target.y, target.diameter, target.diameter,this);
            if (jeux.debug) {
                g.setColor(Color.ORANGE);
                g.drawOval((int) target.x, (int) target.y, target.width, target.height);
            }
        }
        /////////////Asteroide/////////
        for (Asteroide caillou:jeux.cailloux) {
            gx.rotate(Math.toRadians(caillou.angle),caillou.x + (caillou.width/2.0),caillou.y + (caillou.height/2.0));
            g.drawImage(caillou.img,(int) caillou.x, (int) caillou.y, caillou.width, caillou.height, this);
            gx.setTransform(old);
            if (jeux.debug) {
                g.setColor(Color.ORANGE);
                g.drawOval((int) caillou.x, (int) caillou.y, caillou.width, caillou.height);
            }
        }
        //////////SHIP/////////////////
        //afiche le vaisseaux
        gx.rotate(Math.toRadians(jeux.ship.angle), jeux.ship.centerX, jeux.ship.centerY);
        if (jeux.ship.isVisible) {
            g.drawImage(jeux.ship.img, (int) jeux.ship.x, (int) jeux.ship.y, jeux.ship.width, jeux.ship.height, this);
            //afiche le feu
            if (jeux.ship.fireOK) {
                g.drawImage(jeux.ship.fire, (int) jeux.ship.x, (int) jeux.ship.y, jeux.ship.width, jeux.ship.height, this);
            }
        }
        gx.setTransform(old);
        if (jeux.debug) {
            g.setColor(Color.ORANGE);
            g.drawOval((int) jeux.ship.x, (int) jeux.ship.y, jeux.ship.width, jeux.ship.height);
        }
        ////////////la vie///////////
        for (int i = 0; i < jeux.ship.HP; i++) {
            int y = !jeux.debug ? 0 : 20;
            g.drawImage(jeux.ship.lifeImg, i * 45, y, 45, 45, this);
        }
        //l'ernergi
        g.setColor(Color.WHITE);
        g.drawRect(850, 20, 75, 25);
        g.setColor(Color.GREEN);
        g.fillRect(851, 21, (int) (74*((float) jeux.ship.energie/jeux.ship.maxEnergie)), 24);

        /////////////la pause////////////////////////////
        if (jeux.isPause) {
            Color color = new Color(255, 255, 255, 100);
            g.setColor(color);
            g.fillRoundRect(350, 90, 150, 300, 20, 20);
            g.fillRoundRect(550, 90, 150, 300, 20, 20);
        }
    }
}
