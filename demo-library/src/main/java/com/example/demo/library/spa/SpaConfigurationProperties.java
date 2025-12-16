package com.example.demo.library.spa;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spa")
public class SpaConfigurationProperties {

    private Endpoints endpoints = new Endpoints();
    private DevClient devClient = new DevClient();

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public class Endpoints {
        private String authSessionPath = "/api/auth-session";

        public String getAuthSessionPath() {
            return authSessionPath;
        }

        public void seeAuthSessionPath(String path) {
            this.authSessionPath = path;
        }
    }

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
