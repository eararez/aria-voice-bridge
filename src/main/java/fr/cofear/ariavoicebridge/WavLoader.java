package fr.cofear.ariavoicebridge;

import javax.sound.sampled.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class WavLoader {

    private static final int MAX_BYTES = 2_500_000; // Evite de charger un gros fichier par erreur.

    public static short[] load48kMonoPcmFromUrl(String urlText) throws Exception {
        URL url = new URL(urlText);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("User-Agent", "AriaVoiceBridge/1.0");

        int code = conn.getResponseCode();
        if (code < 200 || code >= 300) {
            throw new IOException("HTTP " + code + " sur " + urlText);
        }

        try (InputStream input = new BufferedInputStream(new LimitedInputStream(conn.getInputStream(), MAX_BYTES))) {
            return readAs48kMonoPcm(input);
        }
    }

    private static short[] readAs48kMonoPcm(InputStream input) throws Exception {
        try (AudioInputStream sourceStream = AudioSystem.getAudioInputStream(input)) {
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    48000F,
                    16,
                    1,
                    2,
                    48000F,
                    false
            );

            try (AudioInputStream pcmStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream)) {
                byte[] bytes = readAllBytes(pcmStream);
                short[] samples = new short[bytes.length / 2];

                for (int i = 0, s = 0; i + 1 < bytes.length; i += 2, s++) {
                    int lo = bytes[i] & 0xFF;
                    int hi = bytes[i + 1];
                    samples[s] = (short) ((hi << 8) | lo);
                }

                return samples;
            }
        }
    }

    private static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        int total = 0;

        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > MAX_BYTES) {
                throw new IOException("Audio trop long");
            }
            out.write(buffer, 0, read);
        }

        return out.toByteArray();
    }

    private static class LimitedInputStream extends FilterInputStream {
        private final long max;
        private long count;

        protected LimitedInputStream(InputStream in, long max) {
            super(in);
            this.max = max;
        }

        @Override
        public int read() throws IOException {
            int value = super.read();
            if (value != -1) {
                checkLimit(1);
            }
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int read = super.read(b, off, len);
            if (read > 0) {
                checkLimit(read);
            }
            return read;
        }

        private void checkLimit(int read) throws IOException {
            count += read;
            if (count > max) {
                throw new IOException("Fichier audio trop lourd");
            }
        }
    }
}
