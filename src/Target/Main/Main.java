package Target.Main;

import Target.Game.Jeux;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask task = new Jeux();
        timer.schedule(task, 0,16);
    }
}
