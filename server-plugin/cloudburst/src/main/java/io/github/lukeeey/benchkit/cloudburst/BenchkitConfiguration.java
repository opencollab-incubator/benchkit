package io.github.lukeeey.benchkit.cloudburst;

import lombok.Getter;

@Getter
public class BenchkitConfiguration {
    private String address;
    private int port;
    private String key;
    private int authenticationTimeout;
}
