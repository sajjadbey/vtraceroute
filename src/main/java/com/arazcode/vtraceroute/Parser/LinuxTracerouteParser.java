package com.arazcode.vtraceroute.Parser;

import com.arazcode.vtraceroute.Core.Hop;
import com.arazcode.vtraceroute.Core.Probe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxTracerouteParser implements TracerouteParser {

    private static final Pattern HOP_PATTERN =
            Pattern.compile("^\\s*(\\d+)\\s+(.*)$");

    private static final Pattern RESPONSE_PATTERN =
            Pattern.compile(
                    "(?:(\\S+)\\s+)?\\(([0-9a-fA-F:.]+)\\)\\s+([0-9.]+)\\s+ms"
            );

    @Override
    public List<Hop> parse(String output) {

        List<Hop> hops = new ArrayList<>();

        for (String line : output.split("\\R")) {

            Matcher hopMatcher =
                    HOP_PATTERN.matcher(line);

            if (!hopMatcher.matches()) {
                continue;
            }

            int hopNumber =
                    Integer.parseInt(hopMatcher.group(1));

            String content =
                    hopMatcher.group(2);

            List<Probe> probes =
                    new ArrayList<>();

            Matcher responseMatcher =
                    RESPONSE_PATTERN.matcher(content);

            while (responseMatcher.find()) {

                String hostname =
                        responseMatcher.group(1);

                String ip =
                        responseMatcher.group(2);

                double latency =
                        Double.parseDouble(
                                responseMatcher.group(3)
                        );

                probes.add(new Probe(
                        ip,
                        hostname,
                        latency,
                        false
                ));
            }

            while (probes.size() < 3) {

                probes.add(new Probe(
                        null,
                        null,
                        null,
                        true
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