package io.github.itzispyder.improperui.config;

public class ConfigKey {

    public final String modId, path, key;

    public ConfigKey(String entry) {
        entry = entry.trim()
                .replaceAll("\\s+|_", "-")
                .replaceAll("[^a-zA-Z0-9:.-]", "");

        String[] split = entry.split("\\s*:\\s*");


        switch (split.length) {
            case 2 -> {
                this.modId = "improperui";
                this.path = split[0];
                this.key = split[1];
            }
            case 3 -> {
                this.modId = split[0];
                this.path = split[1];
                this.key = split[2];
            }
            default -> throw new IllegalArgumentException("malformed config key: \"%s\"".formatted(entry));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigKey configKey))
            return false;
        return configKey.path.equals(this.path) && configKey.key.equals(this.key);
    }
}
