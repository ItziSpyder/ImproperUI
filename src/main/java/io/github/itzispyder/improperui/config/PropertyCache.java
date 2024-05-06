package io.github.itzispyder.improperui.config;

import java.util.HashMap;
import java.util.Map;

public class PropertyCache {

    private final Map<String, Properties> cache;
    private final String modId;

    public PropertyCache(String modId) {
        this.cache = new HashMap<>();
        this.modId = modId;
    }

    public void upload(String path, Properties properties) {
        if (properties == null)
            return;
        cache.put(path, properties);
        properties.read(modId, path);
    }

    public Properties get(String path) {
        if (!cache.containsKey(path)) {
            upload(path, new Properties());
        }
        return cache.get(path);
    }

    public Properties.Value getProperty(ConfigKey key) {
        if (key == null)
            return null;
        return get(key.path).getProperty(key.key);
    }

    public void setProperty(ConfigKey key, Object value) {
        setProperty(key, value, false);
    }

    public void setProperty(ConfigKey key, Object value, boolean save) {
        if (key == null)
            return;
        get(key.path).setProperty(key.key, value.toString());
        if (save)
            save(key);
    }

    public void save(ConfigKey key) {
        if (key != null)
            get(key.path).write(modId, key.path);
    }

    public void clear() {
        for (Map.Entry<String, Properties> entry : cache.entrySet()) {
            entry.getValue().write(modId, entry.getKey());
        }
        cache.clear();
    }
}
