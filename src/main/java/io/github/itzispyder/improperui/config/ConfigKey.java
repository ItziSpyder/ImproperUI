package io.github.itzispyder.improperui.config;

public class ConfigKey {

    public final String path, key;

    public ConfigKey(String entry) {
        entry = entry.trim()
                .replaceAll("\\s+|_", "-")
                .replaceAll("[^a-zA-Z0-9:.-]", "");

        String[] split = entry.split("\\s*:\\s*");
        this.path = split[0];
        this.key = split[1];
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigKey configKey))
            return false;
        return configKey.path.equals(this.path) && configKey.key.equals(this.key);
    }
}
