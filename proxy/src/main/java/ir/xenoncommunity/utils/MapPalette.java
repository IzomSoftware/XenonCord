package ir.xenoncommunity.utils;

import java.awt.Color;

public class MapPalette {
    private static final Color[] COLORS = {
        new Color(0, 0, 0, 0), new Color(127, 178, 56), new Color(247, 233, 163), new Color(199, 199, 199),
        new Color(255, 0, 0), new Color(160, 160, 255), new Color(167, 167, 167), new Color(0, 124, 0),
        new Color(255, 255, 255), new Color(164, 168, 184), new Color(151, 109, 77), new Color(112, 112, 112),
        new Color(64, 64, 255), new Color(143, 119, 72), new Color(255, 252, 245), new Color(216, 127, 51),
        new Color(178, 76, 216), new Color(102, 153, 216), new Color(229, 229, 51), new Color(127, 204, 25),
        new Color(242, 127, 165), new Color(76, 76, 76), new Color(153, 153, 153), new Color(76, 127, 153),
        new Color(127, 63, 178), new Color(51, 76, 178), new Color(102, 76, 51), new Color(102, 127, 51),
        new Color(153, 51, 51), new Color(25, 25, 25), new Color(250, 238, 77), new Color(92, 219, 213),
        new Color(74, 128, 255), new Color(0, 217, 58), new Color(129, 86, 49), new Color(112, 2, 0),
        new Color(209, 177, 161), new Color(159, 82, 36), new Color(149, 87, 108), new Color(112, 108, 138),
        new Color(186, 157, 25), new Color(103, 117, 52), new Color(160, 77, 78), new Color(57, 41, 35),
        new Color(135, 107, 98), new Color(87, 92, 192), new Color(122, 73, 88), new Color(76, 62, 92),
        new Color(76, 50, 35), new Color(76, 82, 42), new Color(142, 60, 46), new Color(37, 22, 16)
    };
    public static byte getColor(Color color) {
        if (color.getAlpha() < 128) return 0;
        int bestIndex = 0;
        double bestDistance = Double.MAX_VALUE;
        for (int i = 1; i < COLORS.length; i++) {
            Color c = COLORS[i];
            for (int shade = 0; shade < 4; shade++) {
                int r = c.getRed() * getShade(shade) / 255;
                int g = c.getGreen() * getShade(shade) / 255;
                int b = c.getBlue() * getShade(shade) / 255;
                double distance = Math.pow(r - color.getRed(), 2) +
                                  Math.pow(g - color.getGreen(), 2) +
                                  Math.pow(b - color.getBlue(), 2);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestIndex = i * 4 + shade;
                }
            }
        }
        return (byte) bestIndex;
    }
    private static int getShade(int shade) {
        switch (shade) {
            case 0: return 180;
            case 1: return 220;
            case 2: return 255;
            case 3: return 135;
            default: return 255;
        }
    }
}