package com.example.demo.library.spa;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spa")
public class SpaConfigurationProperties {

    private DevClient devClient = new DevClient();

    public DevClient getDevClient() {
        return devClient;
    }

    public class DevClient {
        private String origin;

        DevClient() {
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }
    }
}
