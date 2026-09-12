package com.arazcode.vtraceroute.Parser;

import com.arazcode.vtraceroute.Core.OperatingSystem;

public class TracerouteParserFactory {

    public static TracerouteParser create(
            OperatingSystem os
    ) {

        return switch (os) {

            case WINDOWS ->
                    new WindowsTracerouteParser();

            case LINUX ->
                    new LinuxTracerouteParser();

            default ->
                    throw new UnsupportedOperationException(
                            "Unsupported operating system: " + os
                    );
        };
    }
}