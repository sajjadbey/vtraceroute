package com.arazcode.vtraceroute.Geo;

import java.net.InetAddress;

public class IpUtils {

    public static boolean isPrivate(String ip) {

        try {
            InetAddress address =
                    InetAddress.getByName(ip);

            return address.isSiteLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isLinkLocalAddress();

        } catch (Exception e) {
            return false;
        }
    }
}