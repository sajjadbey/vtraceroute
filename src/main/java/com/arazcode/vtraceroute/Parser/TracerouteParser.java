package com.arazcode.vtraceroute.Parser;

import com.arazcode.vtraceroute.Core.Hop;

import java.util.List;

public interface TracerouteParser {

    List<Hop> parse(String output);
}