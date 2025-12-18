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
    private final Map<String, Clip> clipCache; // Cache for preloaded clips
    private static final String BGM_PATH = "src/ui/BGM/";

    public SoundManager() {
        trackMap = new HashMap<>();
        clipCache = new HashMap<>();

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

    /**
     * Preloads all BGM tracks into memory to reduce latency.
     * Call this during initial loading.
     */
    public void preloadAll() {
        for (String track : trackMap.keySet()) {
            loadClip(track);
        }
    }

    private Clip loadClip(String trackName) {
        if (clipCache.containsKey(trackName)) {
            return clipCache.get(trackName);
        }

        String filename = trackMap.get(trackName);
        if (filename == null)
            return null;

        try {
            AudioInputStream audioIn = null;
            File soundFile = new File(BGM_PATH + filename);

            if (soundFile.exists()) {
                audioIn = AudioSystem.getAudioInputStream(soundFile);
            } else {
                URL url = getClass().getResource("/ui/BGM/" + filename);
                if (url == null)
                    url = getClass().getResource("/" + filename);
                if (url != null) {
                    audioIn = AudioSystem.getAudioInputStream(url);
                }
            }

            if (audioIn != null) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clipCache.put(trackName, clip);
                return clip;
            }
        } catch (Exception e) {
            System.err.println("SoundManager: Failed to preload " + trackName);
            e.printStackTrace();
        }
        return null;
    }

    public void play(String trackName) {
        // If requesting the same track that is already playing, do nothing
        if (trackName.equals(currentTrack) && currentClip != null && currentClip.isRunning()) {
            return;
        }

        stop(); // Stop current music

        Clip clip = clipCache.get(trackName);
        if (clip == null) {
            // Try loading on demand if not cached
            clip = loadClip(trackName);
        }

        if (clip != null) {
            currentClip = clip;
            currentClip.setFramePosition(0); // Rewind
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();
            currentTrack = trackName;
        } else {
            System.err.println("SoundManager: Could not play " + trackName);
        }
    }

    public void stop() {
        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            // Do NOT close the clip, keep it open for reuse
            currentClip = null;
            currentTrack = null;
        }
    }
}
