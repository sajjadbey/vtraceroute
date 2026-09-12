package com.arazcode.vtraceroute.Parser;

import com.arazcode.vtraceroute.Core.Hop;
import com.arazcode.vtraceroute.Core.Probe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WindowsTracerouteParserTest {

    private final TracerouteParser parser =
            new WindowsTracerouteParser();

    @Test
    void parsesSingleIpHop() {

        String output = """
                  1    <1 ms    <1 ms    <1 ms  10.10.10.10
                """;

        List<Hop> hops = parser.parse(output);

        assertEquals(1, hops.size());

        Hop hop = hops.get(0);

        assertEquals(1, hop.number());
        assertEquals(3, hop.probes().size());

        for (Probe probe : hop.probes()) {
            assertEquals("10.10.10.10", probe.ip());
            assertEquals(1.0, probe.latencyMs());
            assertFalse(probe.timeout());
            assertNull(probe.hostname());
        }
    }

    @Test
    void parsesHostnameHop() {

        String output = """
                  4     6 ms     5 ms     5 ms  ns1.hamarasystem.net [85.185.3.3]
                """;

        List<Hop> hops = parser.parse(output);

        Probe probe = hops.get(0).probes().get(0);

        assertEquals("85.185.3.3", probe.ip());
        assertEquals("ns1.hamarasystem.net", probe.hostname());
        assertEquals(6.0, probe.latencyMs());
        assertFalse(probe.timeout());
    }

    @Test
    void parsesPartialTimeouts() {

        String output = """
                  7    15 ms     *        *     172.19.17.197
                """;

        List<Hop> hops = parser.parse(output);

        List<Probe> probes = hops.get(0).probes();

        assertEquals("172.19.17.197", probes.get(0).ip());
        assertEquals(15.0, probes.get(0).latencyMs());
        assertFalse(probes.get(0).timeout());

        assertEquals("172.19.17.197", probes.get(1).ip());
        assertNull(probes.get(1).latencyMs());
        assertTrue(probes.get(1).timeout());

        assertEquals("172.19.17.197", probes.get(2).ip());
        assertNull(probes.get(2).latencyMs());
        assertTrue(probes.get(2).timeout());
    }

    @Test
    void parsesRequestTimedOut() {

        String output = """
                 10     *        *        *     Request timed out.
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
    void parsesLessThanThreeLatencies() {

        String output = """
                 15   145 ms     *     136 ms  216.239.38.120
                """;

        List<Hop> hops = parser.parse(output);

        List<Probe> probes = hops.get(0).probes();

        assertEquals(145.0, probes.get(0).latencyMs());
        assertEquals(136.0, probes.get(1).latencyMs());
        assertNull(probes.get(2).latencyMs());
        assertTrue(probes.get(2).timeout());
    }
}