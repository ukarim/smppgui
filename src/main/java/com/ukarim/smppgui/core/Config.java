package com.ukarim.smppgui.core;

import static java.lang.System.Logger.Level.ERROR;

import com.ukarim.smppgui.util.SmppCharsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

public final class Config {

    private static final System.Logger logger = System.getLogger(Config.class.getName());

    private final Set<SavedLoginData> savedLogins;

    private Config(Set<SavedLoginData> savedLogins) {
        this.savedLogins = savedLogins;
    }

    public static Config loadConfig() {
        try {
            File settingsFile = getConfigFile();
            try (var in = new FileInputStream(settingsFile)) {
                var properties = new Properties();
                properties.load(in);
                var savedLogins =new HashSet<SavedLoginData>();
                for (Object o : properties.keySet()) {
                    String k = (String) o;
                    if (k.startsWith("conn")) {
                        var sl = SavedLoginData.deserialize(properties.getProperty(k));
                        savedLogins.add(sl);
                    }
                }
                return new Config(savedLogins);
            }
        } catch (Exception e) {
            logger.log(ERROR, "Cannot read settings file ", e);
            return new Config(Set.of());
        }
    }

    public void saveLoginData(SavedLoginData loginData) {
        try {
            File settingsFile = getConfigFile();
            savedLogins.add(loginData);
            Properties properties = new Properties();
            int c = 1;
            for (SavedLoginData sl : savedLogins) {
                properties.put("conn" + ( c++ ), sl.serialize());
            }
            try (var out = new FileOutputStream(settingsFile)) {
                properties.store(out, "");
            }
        } catch (Exception e) {
            logger.log(ERROR, "Cannot save settings file ", e);
        }
    }

    public Set<SavedLoginData> getSavedLoginData() {
        return Set.copyOf(savedLogins);
    }

    private static File getConfigFile() throws IOException {
        Path settingsFilePath = Paths.get(System.getProperty("user.home"), "smppgui.properties");
        File settingsFile = settingsFilePath.toFile();
        if (!settingsFile.exists()) {
            boolean created = settingsFile.createNewFile();
        }
        return settingsFile;
    }

    public static class SavedLoginData {

        private final String host;
        private final int port;
        private final String systemId;
        private final String sessionType;
        private final String systemType;
        private final Charset defaultCharset;

        public SavedLoginData(String host, int port, String systemId, String sessionType, String systemType, Charset defaultCharset) {
            this.host = host;
            this.port = port;
            this.systemId = systemId;
            this.sessionType = sessionType;
            this.systemType = systemType;
            this.defaultCharset = defaultCharset;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getSystemId() {
            return systemId;
        }

        public String getSessionType() {
            return sessionType;
        }

        public String getSystemType() {
            return systemType;
        }

        public Charset getDefaultCharset() {
            return defaultCharset;
        }

        String serialize() {
            return String.format("%s;%s;%s;%s;%s;%s", host, port, systemId, sessionType, systemType, defaultCharset.name());
        }

        static SavedLoginData deserialize(String s) {
            String[] parts = s.split(";");
            String host = parts[0].trim();
            int port = Integer.parseInt(parts[1].trim());
            String systemId = parts[2].trim();
            String sessionType = parts[3].trim();
            String systemType = parts[4].trim();
            var charset = SmppCharsets.forName(parts[5].trim());
            return new SavedLoginData(host, port, systemId, sessionType, systemType, charset);
        }

        @Override
        public String toString() {
            return String.format("%s@%s:%s", systemId, host, port);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SavedLoginData)) return false;
            SavedLoginData that = (SavedLoginData) o;
            return port == that.port
                    && Objects.equals(host, that.host)
                    && Objects.equals(systemId, that.systemId)
                    && Objects.equals(sessionType, that.sessionType)
                    && Objects.equals(systemType, that.systemType)
                    && Objects.equals(defaultCharset, that.defaultCharset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port, systemId, sessionType, systemType, defaultCharset);
        }
    }
}
