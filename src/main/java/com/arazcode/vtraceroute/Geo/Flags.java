package com.arazcode.vtraceroute.Geo;

public class Flags {

    public static String countryFlag(String countryCode) {

        if (countryCode == null
                || countryCode.length() != 2) {
            return "🌐";
        }

        return countryCode
                .toUpperCase()
                .chars()
                .mapToObj(c ->
                        String.valueOf(
                                Character.toChars(
                                        0x1F1E6 + c - 'A'
                                )
                        )
                )
                .reduce("", String::concat);
    }
}