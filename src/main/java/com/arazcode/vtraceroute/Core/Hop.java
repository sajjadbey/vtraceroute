package com.arazcode.vtraceroute.Core;

import java.util.List;

public record Hop(
        int number,
        List<Probe> probes
) {
}