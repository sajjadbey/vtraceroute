package com.arazcode.vtraceroute.Parser;

import com.arazcode.vtraceroute.Core.Hop;
import com.arazcode.vtraceroute.Core.Probe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindowsTracerouteParser implements TracerouteParser {

    private static final Pattern HOP_PATTERN =
            Pattern.compile("^\\s*(\\d+)\\s+(.*)$");

    private static final Pattern LATENCY_PATTERN =
            Pattern.compile("(<\\d+|\\d+)\\s*ms");

    private static final Pattern IP_PATTERN =
            Pattern.compile("\\[([0-9a-fA-F:.]+)]");

    private static final Pattern PLAIN_IP_PATTERN =
            Pattern.compile("\\b((?:\\d{1,3}\\.){3}\\d{1,3})\\s*$");

    @Override
    public List<Hop> parse(String output) {

        List<Hop> hops = new ArrayList<>();

        for (String line : output.split("\\R")) {

            Matcher hopMatcher = HOP_PATTERN.matcher(line);

            if (!hopMatcher.matches()) {
                continue;
            }

            int hopNumber = Integer.parseInt(hopMatcher.group(1));
            String content = hopMatcher.group(2);

            List<Probe> probes = new ArrayList<>();

            if (content.contains("Request timed out.")) {
                for (int i = 0; i < 3; i++) {
                    probes.add(new Probe(
                            null,
                            null,
                            null,
                            true
                    ));
                }

                hops.add(new Hop(hopNumber, probes));
                continue;
            }

            Matcher latencyMatcher =
                    LATENCY_PATTERN.matcher(content);

            List<Double> latencies = new ArrayList<>();

            while (latencyMatcher.find()) {
                String value = latencyMatcher.group(1);

                if (value.startsWith("<")) {
                    value = value.substring(1);
                }

                latencies.add(Double.parseDouble(value));
            }

            String ip = null;

            Matcher bracketIpMatcher =
                    IP_PATTERN.matcher(content);

            if (bracketIpMatcher.find()) {
                ip = bracketIpMatcher.group(1);
            } else {
                Matcher plainIpMatcher =
                        PLAIN_IP_PATTERN.matcher(content);

                if (plainIpMatcher.find()) {
                    ip = plainIpMatcher.group(1);
                }
            }

            String hostname = null;

            if (ip != null
                    && content.contains("[" + ip + "]")) {

                String beforeIp = content.substring(
                        0,
                        content.indexOf("[" + ip + "]")
                );

                beforeIp = beforeIp.trim();

                int lastSpace = beforeIp.lastIndexOf(' ');

                if (lastSpace >= 0) {
                    beforeIp = beforeIp.substring(lastSpace + 1);
                }

                if (!beforeIp.isEmpty()) {
                    hostname = beforeIp;
                }
            }

            for (int i = 0; i < 3; i++) {

                Double latency =
                        i < latencies.size()
                                ? latencies.get(i)
                                : null;

                probes.add(new Probe(
                        ip,
                        hostname,
                        latency,
                        latency == null
                ));
            }

            hops.add(new Hop(
                    hopNumber,
                    probes
            ));
        }

        return hops;
    }
}