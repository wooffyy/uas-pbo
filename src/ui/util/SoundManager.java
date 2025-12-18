package ui.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static SoundManager instance;
    private Clip currentClip;
    private String currentTrack;
    private final Map<String, String> trackMap;
    private static final String BGM_PATH = "src/ui/BGM/";

    public SoundManager() {
        trackMap = new HashMap<>();
        // Map logical names to filenames (WAV)
        trackMap.put("MainMenu", "MainMenu.wav");
        trackMap.put("IntroBoss", "IntroBoss.wav");
        trackMap.put("Shop", "Shop.wav");
        trackMap.put("Tactician", "Tactician.wav");
        trackMap.put("Economic", "Economic.wav");
        trackMap.put("FinalBoss", "FinalBoss.wav");
        trackMap.put("Death", "Death.wav");
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void play(String trackName) {
        // If requesting the same track that is already playing, do nothing
        if (trackName.equals(currentTrack) && currentClip != null && currentClip.isRunning()) {
            return;
        }

        stop(); // Stop current music

        String filename = trackMap.get(trackName);
        if (filename == null) {
            System.err.println("SoundManager: Unknown track " + trackName);
            return;
        }

        try {
            // Try explicit path first (since valid in IDE)
            File soundFile = new File(BGM_PATH + filename);
            AudioInputStream audioIn;

            if (soundFile.exists()) {
                audioIn = AudioSystem.getAudioInputStream(soundFile);
            } else {
                // Fallback to classpath resource (if packaged)
                URL url = getClass().getResource("/ui/BGM/" + filename);
                if (url == null) {
                    // Try root classpath
                    url = getClass().getResource("/" + filename);
                }
                if (url == null) {
                    System.err.println("SoundManager: File not found " + filename);
                    return;
                }
                audioIn = AudioSystem.getAudioInputStream(url);
            }

            currentClip = AudioSystem.getClip();
            currentClip.open(audioIn);

            // Loop continuously
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();

            currentTrack = trackName;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            System.err.println("SoundManager: Error playing " + filename);
        }
    }

    public void stop() {
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            currentClip.close();
            currentClip = null;
            currentTrack = null;
        }
    }
}
