package Target.Game;

import Target.Main.Fenetre;

import javax.swing.*;
import java.awt.*;

public class ScoreInfo {
    double x, y, velocityY;
    int timer;
    JLabel texte;
    boolean ended;

    public ScoreInfo(double x, double y, int value, Fenetre windows) {
        this.x = x;
        this.y = y;
        timer = 0;
        velocityY = 10;

        ended = false;

        texte = new JLabel();
        texte.setText("+"+value);
        texte.setForeground(new Color(255, 255, 255, 200));
        Font labelFont = texte.getFont();
        texte.setFont(new Font(labelFont.getName(), Font.BOLD, 18));
        texte.setBounds((int) x, (int) y, 100, 15);
        windows.add(texte);

    }
    void update(Fenetre windows) {
        timer +=1;
        if (timer < 120) {
            y -= velocityY;
            texte.setBounds((int) x, (int) y, 100, 15);
            velocityY /= 1.5;
        } else {
            windows.remove(texte);
            ended = true;
        }
    }
}
