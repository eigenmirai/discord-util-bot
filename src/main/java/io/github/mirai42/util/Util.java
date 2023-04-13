package io.github.mirai42.util;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    public static final Color blue =   Color.decode("#5865F2");
    public static final Color green =  Color.decode("#57F287");
    public static final Color yellow = Color.decode("#FEE75C");
    public static final Color red =    Color.decode("#ED4245");
    public static final Color pink =   Color.decode("#EB459E");
    private static final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String byteArray2Hex(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (byte b : bytes) {
            buf.append(hex[(b & 0xF0) >> 4]);
            buf.append(hex[b & 0x0F]);
        }
        return buf.toString();
    }

    public static String readStream(InputStream s) {
        StringBuffer out = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(s))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    public static String time() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return formatter.format(LocalDateTime.now());
    }

    public static String firstLetterCaps(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static String rgbString(Color color) {
        return String.format("%s, %s, %s", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String hslString(Color color) {
        int r, g, b, min, max;
        double h, s, l, d;
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        min = Math.min(Math.min(r, g), b);
        max = Math.max(Math.max(r, g), b);
        d = (max - min)/255f;
        l = (min + max)/510f;
        if (l == 0) {
            s = 0;
        } else {
            s = d/(1 - (2*l - 1));
        }
        double a = Math.toDegrees(Math.acos((r - g/2f- b/2f)/Math.sqrt(r*r + g*g + b*b - r*g - r*b - g*b)));
        if (g >= b) {
            h = a;
        } else {
            h = 360 - a;
        }
        DecimalFormat df = new DecimalFormat("#.###");
        return String.format("%s, %s, %s", df.format(h), df.format(s), df.format(l));
    }
}
