package peces;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

final class Sonidos {
    private final Clip musica = cargar("oceano.wav");
    private final Clip comer = cargar("comer.wav");
    private final Clip derrota = cargar("derrota.wav");

    Sonidos() {
        if (musica != null) {
            musica.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private Clip cargar(String archivo) {
        URL recurso = Sonidos.class.getResource("/audio/" + archivo);
        Clip clip = null;
        try (AudioInputStream datos = AudioSystem.getAudioInputStream(recurso)) {
            clip = AudioSystem.getClip();
            clip.open(datos);
            return clip;
        } catch (Exception ex) {
            if (clip != null) {
                clip.close();
            }
            System.err.println("Audio no disponible para " + archivo + ": " + ex.getMessage());
            return null;
        }
    }

    void comer() {
        reproducir(comer);
    }

    void derrota() {
        reproducir(derrota);
    }

    private void reproducir(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    void cerrar() {
        cerrar(musica);
        cerrar(comer);
        cerrar(derrota);
    }

    private void cerrar(Clip clip) {
        if (clip != null) {
            clip.close();
        }
    }
}
