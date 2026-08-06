package io.github.itzispyder.improperui.config;

public record ConfigKey(String modId, String path, String key) {

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigKey configKey))
            return false;
        return configKey.path.equals(this.path) && configKey.key.equals(this.key);
    }
}
