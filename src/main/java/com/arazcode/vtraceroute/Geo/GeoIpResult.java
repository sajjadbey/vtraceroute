package com.arazcode.vtraceroute.Geo;

public record GeoIpResult(
        String ip,
        String countryCode,
        String countryName,
        String city,
        Double latitude,
        Double longitude,
        Long asn,
        String organisation
) {
}