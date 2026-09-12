package com.arazcode.vtraceroute;

import com.arazcode.vtraceroute.Core.Hop;
import com.arazcode.vtraceroute.Core.OperatingSystem;
import com.arazcode.vtraceroute.Core.Probe;
import com.arazcode.vtraceroute.Geo.Flags;
import com.arazcode.vtraceroute.Geo.GeoIpResult;
import com.arazcode.vtraceroute.Geo.GeoIpService;
import com.arazcode.vtraceroute.Geo.IpUtils;
import com.arazcode.vtraceroute.Parser.TracerouteParser;
import com.arazcode.vtraceroute.Parser.TracerouteParserFactory;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {
    public static void main(String[] args)
            throws IOException, GeoIp2Exception, InterruptedException {

        System.out.println("Detecting your OS");

        OperatingSystem os = OperatingSystem.detect();

        System.out.println("Your os is: " + os);

        System.out.println("Running Traceroute for " + args[0]);

        Utils.CommandResult result =
                Utils.runTraceroute(os, args[0]);

        TracerouteParser parser =
                TracerouteParserFactory.create(os);

        List<Hop> hops =
                parser.parse(result.stdout());

        try (InputStream cityDatabase =
                     GeoIpService.class.getResourceAsStream(
                             "/GeoLite2-City.mmdb");

             InputStream asnDatabase =
                     GeoIpService.class.getResourceAsStream(
                             "/GeoLite2-ASN.mmdb"
                     );

             GeoIpService geoIp = new GeoIpService(
                     requireDatabase(cityDatabase, "GeoLite2-City.mmdb"),
                     requireDatabase(asnDatabase, "GeoLite2-ASN.mmdb")
             )) {

            Map<String, GeoIpResult> geo =
                    resolveGeo(hops, geoIp);

            for (Hop hop : hops) {

                System.out.println(
                        "Hop " + hop.number()
                );

                for (Probe probe : hop.probes()) {
                    printProbe(probe, geo);
                }
            }
        }
    }

    private static Map<String, GeoIpResult> resolveGeo(
            List<Hop> hops,
            GeoIpService geoIp
    ) throws GeoIp2Exception, IOException {

        Map<String, GeoIpResult> result =
                new HashMap<>();

        for (Hop hop : hops) {

            for (Probe probe : hop.probes()) {

                String ip = probe.ip();

                if (ip == null
                        || IpUtils.isPrivate(ip)
                        || result.containsKey(ip)) {
                    continue;
                }

                try {
                    result.put(
                            ip,
                            geoIp.lookup(ip)
                    );
                } catch (AddressNotFoundException e) {
                    result.put(ip, null);
                }
            }
        }

        return result;
    }

    private static void printProbe(
            Probe probe,
            Map<String, GeoIpResult> geo
    ) {

        if (probe.timeout()) {
            System.out.println("  * timeout");
            return;
        }

        String latency =
                probe.latencyMs() == null
                        ? "?"
                        : String.format(
                        Locale.ROOT,
                        "%.1f",
                        probe.latencyMs()
                );

        System.out.printf(
                Locale.ROOT,
                "  %-18s %8s ms  %s%n",
                probe.ip(),
                latency,
                formatGeo(geo.get(probe.ip()))
        );
    }

    private static String formatGeo(GeoIpResult geo) {

        if (geo == null) {
            return "Private / Unknown";
        }

        String flag =
                Flags.countryFlag(geo.countryCode());

        String country =
                geo.countryName() == null
                        ? "?"
                        : geo.countryName();

        String city =
                geo.city() == null
                        ? ""
                        : ", " + geo.city();

        StringBuilder suffix = new StringBuilder();

        if (geo.asn() != null) {
            suffix.append(" AS").append(geo.asn());
        }

        if (geo.organisation() != null) {
            suffix.append(' ').append(geo.organisation());
        }

        return flag + " " + country + city + suffix;
    }

    private static InputStream requireDatabase(
            InputStream database,
            String name
    ) {

        if (database == null) {
            throw new IllegalStateException(
                    "Missing database " + name +
                            " inside the jar. Bundling the " +
                            "GeoLite2 .mmdb files is required."
            );
        }

        return database;
    }
}