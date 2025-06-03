package dev.opencollab.benchkit.geyser;

import lombok.Getter;

@Getter
public class BenchkitConfiguration {
    private String address;
    private int port;
    private String key;
    private int authenticationTimeout;
}
