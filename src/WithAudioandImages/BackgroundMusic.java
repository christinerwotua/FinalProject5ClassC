package WithAudioandImages;

import javax.sound.sampled.*;

public class BackgroundMusic {
    private static Clip clip;

    public static void playLoop(String path) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    BackgroundMusic.class.getResource(path)
            );
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // putar terus
            clip.start();
        } catch (Exception e) {
            System.err.println("Gagal memutar musik: " + e.getMessage());
        }
    }

    public static void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}


