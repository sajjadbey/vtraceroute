package com.arazcode.vtraceroute.Geo;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GeoIpServiceTest {

    @Test
    void looksUpPublicIp() throws GeoIp2Exception, IOException {

        try (InputStream city =
                     Objects.requireNonNull(
                             getClass().getResourceAsStream(
                                     "/GeoLite2-City.mmdb"
                             )
                     );

             InputStream asn =
                     Objects.requireNonNull(
                             getClass().getResourceAsStream(
                                     "/GeoLite2-ASN.mmdb"
                             )
                     );

             GeoIpService geoIp = new GeoIpService(
                     city,
                     asn
             )) {

            GeoIpResult result =
                    geoIp.lookup("8.8.8.8");

            assertEquals("8.8.8.8", result.ip());
            assertEquals("US", result.countryCode());
            assertEquals("United States", result.countryName());
            assertEquals(15169L, result.asn());
            assertEquals("Google LLC", result.organisation());
        }
    }

    @Test
    void detectsPrivateIps() {

        assertTrue(IpUtils.isPrivate("10.10.10.10"));
        assertTrue(IpUtils.isPrivate("172.19.17.197"));
        assertTrue(IpUtils.isPrivate("192.168.1.1"));
        assertTrue(IpUtils.isPrivate("127.0.0.1"));
        assertFalse(IpUtils.isPrivate("8.8.8.8"));
        assertFalse(IpUtils.isPrivate("85.185.3.3"));
    }

    @Test
    void countryFlagEmoji() {

        assertEquals("🇮🇷", Flags.countryFlag("IR"));
        assertEquals("🇩🇪", Flags.countryFlag("de"));
        assertEquals("🌐", Flags.countryFlag(null));
        assertEquals("🌐", Flags.countryFlag("USA"));
    }
}