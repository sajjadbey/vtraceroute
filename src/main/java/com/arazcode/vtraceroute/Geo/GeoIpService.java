package com.arazcode.vtraceroute.Geo;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

public class GeoIpService implements AutoCloseable {

    private final DatabaseReader cityReader;
    private final DatabaseReader asnReader;

    public GeoIpService(
            InputStream cityDatabase,
            InputStream asnDatabase
    ) throws IOException {

        this.cityReader = new DatabaseReader.Builder(
                cityDatabase
        ).build();

        this.asnReader = new DatabaseReader.Builder(
                asnDatabase
        ).build();
    }

    public GeoIpResult lookup(String ip)
            throws GeoIp2Exception, IOException {

        InetAddress address =
                InetAddress.getByName(ip);

        CityResponse cityResponse =
                cityReader.city(address);

        AsnResponse asnResponse =
                asnReader.asn(address);

        Country country =
                cityResponse.country();

        String countryCode =
                country == null
                        ? null
                        : country.isoCode();

        String countryName =
                country == null
                        ? null
                        : country.name();

        City city =
                cityResponse.city();

        String cityName =
                city == null
                        ? null
                        : city.name();

        Location location =
                cityResponse.location();

        Double latitude =
                location == null
                        ? null
                        : location.latitude();

        Double longitude =
                location == null
                        ? null
                        : location.longitude();

        Long asn =
                asnResponse.autonomousSystemNumber();

        String organisation =
                asnResponse.autonomousSystemOrganization();

        return new GeoIpResult(
                ip,
                countryCode,
                countryName,
                cityName,
                latitude,
                longitude,
                asn,
                organisation
        );
    }

    @Override
    public void close() throws IOException {
        cityReader.close();
        asnReader.close();
    }
}