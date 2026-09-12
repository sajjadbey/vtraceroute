package com.arazcode.vtraceroute.Core;

public record Probe(
        String ip,
        String hostname,
        Double latencyMs,
        boolean timeout
) {
}