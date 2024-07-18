package me.sebastian420.PandaAC.util;

public class MathUtil {
    public static double getDistanceSquared(double x1, double z1, double x2, double z2) {
        double deltaX = x2 - x1;
        double deltaZ = z2 - z1;
        return Math.round((deltaX * deltaX) + (deltaZ * deltaZ));
    }

    public static double getDistanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double deltaZ = z2 - z1;
        return Math.round((deltaX * deltaX) + (deltaY * deltaY)  + (deltaZ * deltaZ));
    }

    public static double getDistance(double x1, double x2) {
        double dx = x2 - x1;
        return Math.abs(dx);
    }

    public static double getDistance(double x1, double z1, double x2, double z2) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}