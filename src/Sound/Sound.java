package Sound;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Sound contain all methode to play a song in java
 * and it works !
 */
public class Sound {
    private Clip clip;
    private final File soundFile;
    private int position;
    public Sound(File file) {
        soundFile = file;
        soundCharge();
    }

    private void soundCharge() {
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        clip.setFramePosition(position);
        position = 0;
    }

    public void loop(){
        soundCharge();
        clip.loop(clip.LOOP_CONTINUOUSLY);
    }

    public void play() {
        soundCharge();
        clip.start();
    }
    public void pause () {
        position = clip.getFramePosition();
        clip.stop();
    }
    public void stop(){
        clip.stop();
    }

    public Boolean isPlaying(){
        return clip.isRunning();
    }
    
    public int getFramePosition() { return clip.getFramePosition(); }
}
