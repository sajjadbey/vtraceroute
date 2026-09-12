package com.arazcode.vtraceroute.Parser;

import com.arazcode.vtraceroute.Core.Hop;
import com.arazcode.vtraceroute.Core.Probe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinuxTracerouteParserTest {

    private final TracerouteParser parser =
            new LinuxTracerouteParser();

    @Test
    void parsesHostnameHop() {

        String output = """
                 1  ip-87-248-145-1.hosted-by.parsvds.com (87.248.145.1)  0.431 ms  0.371 ms  0.324 ms
                """;

        List<Hop> hops = parser.parse(output);

        assertEquals(1, hops.size());

        Hop hop = hops.get(0);

        assertEquals(1, hop.number());
        assertEquals(3, hop.probes().size());

        Probe probe = hop.probes().get(0);

        assertEquals("87.248.145.1", probe.ip());
        assertEquals("ip-87-248-145-1.hosted-by.parsvds.com", probe.hostname());
        assertEquals(0.431, probe.latencyMs());
        assertFalse(probe.timeout());
    }

    @Test
    void parsesMultipleIpsPerHop() {

        String output = """
                 11  192.178.109.239 (192.178.109.239)  71.458 ms 192.178.109.155 (192.178.109.155) 72.487 ms 209.85.252.21 (209.85.252.21) 71.642 ms
                """;

        List<Hop> hops = parser.parse(output);

        List<Probe> probes = hops.get(0).probes();

        assertEquals(3, probes.size());

        assertEquals("192.178.109.239", probes.get(0).ip());
        assertEquals(71.458, probes.get(0).latencyMs());

        assertEquals("192.178.109.155", probes.get(1).ip());
        assertEquals(72.487, probes.get(1).latencyMs());

        assertEquals("209.85.252.21", probes.get(2).ip());
        assertEquals(71.642, probes.get(2).latencyMs());

        for (Probe probe : probes) {
            assertFalse(probe.timeout());
        }
    }

    @Test
    void fillsFullyTimedOutHop() {

        String output = """
                 2  * * *
                """;

        List<Hop> hops = parser.parse(output);

        List<Probe> probes = hops.get(0).probes();

        assertEquals(3, probes.size());

        for (Probe probe : probes) {
            assertTrue(probe.timeout());
            assertNull(probe.ip());
            assertNull(probe.latencyMs());
        }
    }

    @Test
    void fillsPartialTimeouts() {

        String output = """
                 3  81.52.188.208 (81.52.188.208)  3.124 ms  *  *
                """;

        List<Hop> hops = parser.parse(output);

        List<Probe> probes = hops.get(0).probes();

        assertEquals(3, probes.size());

        assertEquals("81.52.188.208", probes.get(0).ip());
        assertFalse(probes.get(0).timeout());

        assertTrue(probes.get(1).timeout());
        assertTrue(probes.get(2).timeout());
    }

    @Test
    void ignoresNonHopLines() {

        String output = """
                traceroute to example.com (93.184.216.34), 30 hops max, 60 byte packets
                 1  1.2.3.4 (1.2.3.4)  0.5 ms  0.4 ms  0.6 ms
                """;

        List<Hop> hops = parser.parse(output);

        assertEquals(1, hops.size());
        assertEquals(1, hops.get(0).number());
    }
}