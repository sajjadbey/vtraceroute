package com.arazcode.vtraceroute.Core;

public enum OperatingSystem {
    WINDOWS,
    LINUX,
    MACOS,
    OTHER;

    public static OperatingSystem detect() {
        String os = System.getProperty("os.name");

        if (os.contains("Windows")) {
            return WINDOWS;
        } else if (os.contains("nux") || os.contains("nix")) {
            return LINUX;
        } else if (os.contains("mac")) {
            return MACOS;
        }
        return OTHER;
    }
}