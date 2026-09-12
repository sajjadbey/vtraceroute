# vtraceroute

A cross-platform traceroute CLI written in Java that enriches every hop with
geographic and autonomous system (AS) information using MaxMind GeoLite2
databases.

Given a target host, `vtraceroute` runs the native traceroute tool on your
system, parses the output, and prints each hop with its IP, latency, country
flag, country, city, ASN, and organisation.

## Features

- Runs on Windows (`tracert`) and Linux (`traceroute`)
- Visual indicators with country flags per hop
- Geolocation (city level) and ASN/organisation lookup for public IPs
- Private/unknown IPs are detected and reported as such
- Bundled GeoLite2 `City` and `ASN` databases, resolved entirely offline

## Requirements

- Java 17 or later
- Windows (`tracert`) or Linux (`traceroute` installed)

## Usage

Build an executable fat jar:

```bash
./gradlew shadowJar
```

On Windows:

```
.\gradlew.bat shadowJar
```

Run it, passing the target host or IP as an argument:

```bash
java -jar build/libs/vtraceroute.jar example.com
```

Example output:

```
Detecting your OS
Your os is: WINDOWS
Running Traceroute for example.com
Hop 1
  192.168.1.1               1.2 ms  Private / Unknown
Hop 2
  192.0.2.1                12.3 ms  🇺🇸 United States, Ashburn AS15169 Google LLC
Hop 3
  * timeout
```

## How it works

1. The operating system is detected and the native traceroute command
   (`tracert` on Windows, `traceroute` on Linux) is executed for the target.
2. The output is parsed by an OS-specific parser into structured hops and
   probes.
3. Each public IP is looked up in the bundled GeoLite2 databases for
   country, city, ASN, and organisation.

## Build

```bash
./gradlew build
```

Run the test suite:

```bash
./gradlew test
```

## Project structure

```
src/main/java/com/arazcode/vtraceroute/
├── Core/       Hop, Probe, OperatingSystem
├── Geo/        GeoIpService, GeoIpResult, Flags, IpUtils
├── Parser/     TracerouteParser, Windows/Linux parsers + factory
├── Main.java   CLI entry point
└── Utils.java  Process execution helpers
databases/      GeoLite2 City and ASN databases (bundled into the jar)
```

## License

Licensed under the [MIT License](LICENSE).

GeoLite2 databases are provided by MaxMind and are subject to the
[GeoLite2 EULA](https://www.maxmind.com/en/geolite2/eula).