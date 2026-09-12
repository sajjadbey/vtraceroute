package com.arazcode.vtraceroute;

import com.arazcode.vtraceroute.Core.OperatingSystem;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Utils {
    public record CommandResult(
            String stdout,
            String stderr,
            int exitCode
    ) {
        public boolean isSuccess() {
            return exitCode == 0;
        }
    }

    public static CommandResult runTraceroute(
            OperatingSystem os,
            String target
    ) throws IOException, InterruptedException {

        ProcessBuilder processBuilder;

        if (os == OperatingSystem.WINDOWS) {
            processBuilder = new ProcessBuilder(
                    "tracert",
                    "-4",
                    target
            );
        } else if (os == OperatingSystem.LINUX) {
            processBuilder = new ProcessBuilder(
                    "traceroute",
                    "-4",
                    target
            );
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported operating system: " + os
            );
        }

        Process process = processBuilder.start();

        String stdout;
        String stderr;

        try (
                BufferedReader outputReader = new BufferedReader(
                        new InputStreamReader(
                                process.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );

                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(
                                process.getErrorStream(),
                                StandardCharsets.UTF_8
                        )
                )
        ) {
            StringBuilder output = new StringBuilder();
            StringBuilder errors = new StringBuilder();

            String line;

            while ((line = outputReader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }

            while ((line = errorReader.readLine()) != null) {
                errors.append(line).append(System.lineSeparator());
            }

            stdout = output.toString();
            stderr = errors.toString();
        }

        int exitCode = process.waitFor();

        return new CommandResult(
                stdout,
                stderr,
                exitCode
        );
    }
}
