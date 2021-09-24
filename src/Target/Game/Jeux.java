package Target.Game;

import Sound.Sound;
import Target.Main.Fenetre;
import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputAxis;
import com.github.strikerx3.jxinput.enums.XInputButton;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.listener.XInputDeviceListener;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.StrictMath.*;

public class Jeux extends TimerTask {
    Fenetre windows;
    Ship ship;
    ArrayList<Laser> lasers;
    ArrayList<Target> targets;
    ArrayList<Asteroide> cailloux;
    ArrayList<ScoreInfo> scoresInfos;
    ArrayList<Object> toRemove;
    Image background;
    Boolean isPause, createLaser, createAsteroide, debug, debugReady, reset, isFullScreen;
    int targetSize, debriFrequence, debriTimer, laserTimer, laserRelease, score;
    double axisAngle, axisRatio;
    Sound melee, laserSong, breakSong, pauseSong, releaseSong;
    JLabel scoreText, debugUPText, energyText;
    XInputDevice14 device;
    XInputDeviceListener listener;
    Random random;
    StringBuilder sb;
    Formatter formater;
    Font labelFont;

    Boolean circularColision(Sprite obj1, Sprite obj2) {
        return sqrt(pow(abs(obj1.centerX - obj2.centerX),2) + pow(abs(obj1.centerY - obj2.centerY),2)) < obj1.rayon + obj2.rayon;
    }

    public Jeux() {
        /////debug SET///
        debug = false;
        debugReady = true;

        //les objects
        windows = new Fenetre();
        ship = new Ship();
        lasers = new ArrayList<>();
        targets = new ArrayList<>();
        scoresInfos = new ArrayList<>();
        cailloux = new ArrayList<>();
        sb = new StringBuilder();
        formater = new Formatter(sb);

        //active la manette
        try {
            device = XInputDevice14.getDeviceFor(0);
        } catch (XInputNotLoadedException e) {
            e.printStackTrace();
        }
        axisAngle = 0;
        axisRatio = 0;

        //variable autre
        isPause = false;
        isFullScreen = false;
        random = new Random();
        createLaser = false;
        laserRelease = 5;
        laserTimer = laserRelease;
        createAsteroide = false;
        debriFrequence = 30; //diviser par 60
        debriTimer = 0;
        reset = false;
        score = 0;

        //gestion graphique
        windows.setPreferredSize(new Dimension(1000, 500));
        try {
            background = ImageIO.read(new File("data/images/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        windows.setContentPane(new ChargeImage(this));
        windows.pack();
        windows.setLocationRelativeTo(null); //centre la fenetre
        windows.setVisible(true);

        //score
        scoreText = new JLabel();
        scoreText.setForeground(Color.GREEN);
        labelFont = scoreText.getFont();
        scoreText.setFont(new Font(labelFont.getName(), Font.PLAIN, 20));
        scoreText.setBounds(450, 0, 125, 20);
        windows.add(scoreText);

        //energy
        energyText = new JLabel();
        energyText.setForeground(Color.GREEN);
        labelFont = energyText.getFont();
        energyText.setFont(new Font(labelFont.getName(), Font.PLAIN, 14));
        energyText.setBounds(850, 0, 50, 20);
        energyText.setText("energy");
        windows.add(energyText);


        // variable sonor
        melee = new Sound(new File ("data/music/TargetSong.wav"));
        laserSong = new Sound(new File("data/music/lazer.wav"));
        breakSong = new Sound(new File("data/music/break.wav"));
        pauseSong = new Sound(new File("data/music/pause.wav"));
        releaseSong = new Sound(new File("data/music/release.wav"));

        //evenement clavier
        KeyListener keys = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                //active/desactive le debugMode
                if (e.getKeyCode() == KeyEvent.VK_F3) {
                    debug = !debug;
                }
                //fullScrenn mode
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    if (isFullScreen) {
                        //desactive le mode fullScreen
                        isFullScreen = false;
                        windows.dispose(); //rends la fenetre modelable
                        windows.setResizable(true);
                        windows.setUndecorated(false);
                        if (windows.getExtendedState() == 0) {
                            windows.setSize(new Dimension(1000, 500));
                        }
                        windows.setExtendedState(windows.getExtendedState());
                    } else {
                        //active le mode fullScreen
                        isFullScreen = true;
                        windows.dispose(); //rends la fenetre modelable
                        windows.setResizable(false);
                        windows.setUndecorated(true);
                        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //get screen Size
                        windows.setSize(screenSize);
                    }
                    windows.setLocationRelativeTo(null); //centre la fenetre
                    windows.setVisible(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        };
        //evenement manette
        listener = new XInputDeviceListener() {
            @Override
            public void connected() {
                // Resume the game
                System.out.println("controller connected !");
            }

            @Override
            public void disconnected() {
                // Pause the game and display a message
                System.out.println("controller disconnected !");
            }

            @Override
            public void buttonChanged(final XInputButton button, final boolean pressed) {
                // The given button was just pressed (if pressed == true) or released (pressed == false)
                if (pressed) {
                    //tire des laser
                    if (button == XInputButton.A) {
                        createLaser = true;
                        ship.recharge = false;
                    }
                    /*if (button == XInputButton.RIGHT_SHOULDER) {
                        ship.fireOK = true;
                    }*/
                    //mets en pause
                    if (button == XInputButton.START && !ship.isBlow) {
                        if (isPause) {
                            isPause = false;
                            releaseSong.play();
                            melee.loop();
                        } else {
                            isPause = true;
                            pauseSong.play();
                            melee.pause();
                        }
                    }
                }
                if (!pressed) {
                    //arrete de tirer des lasers
                    if (button == XInputButton.A && !ship.isBlow) {
                        createLaser = false;
                        ship.recharge = true;
                    }
                    /*if (button == XInputButton.RIGHT_SHOULDER) {
                        ship.fireOK = false;
                    }*/
                }
            }
        };
        device.addListener(listener);
        windows.addKeyListener(keys);

        //lance la music
        melee.loop();

        ///////////////////debugELEMENTS//////////////////
        debugUPText = new JLabel();
        debugUPText.setForeground(Color.WHITE);
        debugUPText.setBounds(0, 5, 1000, 15);
        windows.add(debugUPText);
    }

    public void run() {
        ///////////////controle de la manette///////////////
        if (device.poll()) {
            //get axis state
            XInputComponents components = device.getComponents();
            XInputAxes axes = components.getAxes();
            double x = axes.get(XInputAxis.LEFT_THUMBSTICK_X);
            double y = axes.get(XInputAxis.LEFT_THUMBSTICK_Y);
            axisRatio = sqrt(pow(x,2) + pow(y, 2));
            if (x != 1.52587890625E-5 || y != 1.52587890625E-5) {
                axisAngle = Math.toDegrees(acos(x / axisRatio));
                if (y>0) {
                    axisAngle += 2*(180-axisAngle);
                }
            }
            //get trigger state
            ship.fireOK = axes.get(XInputAxis.RIGHT_TRIGGER) == 1.0;
        }
        if (!isPause) {
            //////////////////////creation de truc//////////////////////
            debriTimer += 1; //timer
            if (debriTimer > debriFrequence) {
                debriTimer = 0;
                if (Math.random() < 0.3) { //frequence modulable
                    //creation de cible
                    targetSize = random.nextInt(100) + 50;
                    targets.add(new Target(Math.random() * (500-targetSize), targetSize, Math.random() * 9));
                }
                if (Math.random() < 0.7) {
                    //creation de cailloux
                    int newSize = 40 + random.nextInt(100);
                    cailloux.add(new Asteroide(random.nextDouble() * (500 - newSize),
                            newSize,
                            random.nextInt(4),
                            random.nextDouble() * 359,
                            random.nextInt(2)));
                }
            }
            //////////////////////////////////////////SHIP///////////////////////////////
            //application
            if (!ship.isBlow) {
                if (ship.isDamaged) {
                    ship.damaged(windows);
                }
                if (!ship.isStunt) {
                    ship.go();
                    ship.velocity();
                }
                ship.tourne(axisAngle);
                ship.collide();
                ship.battery();
            } else {
                reset = ship.blowUp(windows);
            }

            //////////////reset tout//////////////
            if (reset) {
                reset = false;
                score = 0;
                ship = new Ship();
                lasers = new ArrayList<>();
                targets = new ArrayList<>();
                cailloux = new ArrayList<>();
                melee.loop();
            }

            //////////////////////////////////LASER///////////////////////////////////
            if (createLaser) {
                if (laserTimer >= laserRelease && ship.energie > 0) {
                    laserTimer = 0;
                    ship.energie -= 1;
                    lasers.add(new Laser(ship.x + (ship.width / 2.0) - (50 / 2.0),
                            ship.y + (ship.height / 2.0) - (13 / 2.0),
                            ship.angle));
                    laserSong.play();
                } else {
                    laserTimer += 1; //timer
                }
            } else {
                laserTimer = laserRelease; //reinitialise le timer
            }

            for (Laser laser : lasers) {
                laser.go();
                laser.overScreen();
            }
            /////////////////////////////TARGET///////////////////////////////
            for (Target target : targets) {
                if (target.destroyed.equals("no")) {
                    target.move();
                }
            }
            //cailloux
            toRemove = new ArrayList<>();
            for (Asteroide caillou:cailloux){
                caillou.move();
                if (caillou.destroyed) {
                    toRemove.add(caillou);
                }
            }
            cailloux.removeAll(toRemove.stream()
                    .filter(e -> e instanceof Asteroide)
                    .map(e -> (Asteroide) e)
                    .collect(Collectors.toList()));
            //////////////////////////////COLISSION///////////////////
            //moi et les cailloux
            if (!ship.isDamaged && !ship.isBlow) {
                for (Asteroide caillou : cailloux) {
                    if (circularColision(ship, caillou)) {
                        ship.HP -= 1;
                        if (ship.HP > 0) {
                            ship.isDamaged = true;
                            ship.isStunt = true;
                            ship.spark.play();
                        } else {
                            ship.isBlow = true;
                            createLaser = false;
                            melee.stop();
                            ship.blow.play();
                        }
                        break;
                    }
                }
            }
            //laser et caillou
            for (Asteroide caillou:cailloux) {
                for (Laser laser:lasers) {
                    if (circularColision(caillou, laser) && !laser.destroyed) {
                        laser.destroyed = true;
                    }
                }
            }
            //laser et target
            for (Target target:targets) {
                for (Laser laser:lasers) {
                    if (circularColision(target, laser) && !laser.destroyed) {
                        laser.destroyed = true;
                        target.destroyed = "win";
                        score += target.value;
                        ScoreInfo newScoreInfo = new ScoreInfo(target.centerX, target.centerY, target.value, windows);
                        scoresInfos.add(newScoreInfo);
                    }
                }
            }
            ////////////////////////actualisation de liste//////////////////
            //liste de laser
            toRemove = new ArrayList<>();
            for (Laser laser : lasers) {
                if (laser.destroyed) {
                    toRemove.add(laser);
                }
            }
            lasers.removeAll(toRemove.stream()
                    .filter(e -> e instanceof Laser)
                    .map(e -> (Laser) e)
                    .collect(Collectors.toList()));

            //liste de target
            for (Target target : targets) {
                if (!target.destroyed.equals("no")) {
                    targets.remove(target);
                    if (target.destroyed.equals("win")) {
                        breakSong.play();
                    }
                    break;
                }
            }

            //scoreInfo
            for (ScoreInfo scoreInfo:scoresInfos) {
                if (scoreInfo.ended) {
                    toRemove.add(scoreInfo);
                }
            }
            scoresInfos.removeAll(toRemove.stream()
                    .filter(e -> e instanceof ScoreInfo)
                    .map(e -> (ScoreInfo) e)
                    .collect(Collectors.toList()));
            ////////////////////du texte///////////////
            //le score
            sb.delete(0, String.valueOf(sb).length());
            formater.format("score : %04d", score);
            scoreText.setText(String.valueOf(sb));

            //scoreInfo
            for (ScoreInfo scoreInfo:scoresInfos) {
                scoreInfo.update(windows);
            }

            //up debug texte
            sb.delete(0, String.valueOf(sb).length());
            if (debug) {
                formater.format("ship.x : %s ;  ship.y : %s  ;  ship.energie : %s",
                        (int) ship.x, (int) ship.y, ship.energie);
                debugUPText.setText(String.valueOf(sb));
            }
        }
        ///////////////////Afichage///////////////////////////////
        windows.repaint();
    }
}
