package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.script.ScriptParser;

public record ConfigReader(String modId, String configFile){

    public ConfigKey getKey(String property) {
        return new ConfigKey(modId, configFile, property);
    }

    public PropertyCache getPropertyCache() {
        return ScriptParser.getCache(modId);
    }

    public Properties.Value read(String property) {
        return getPropertyCache().getProperty(getKey(property));
    }

    public void write(String property, Object value) {
        ScriptParser.getCache(modId).setProperty(getKey(property), value, true);
    }

    public boolean readBool(String property, boolean def) {
        var o = read(property);
        if (o == null)
            write(property, def);
        return o != null ? o.first().toBool() : def;
    }

    public int readInt(String property, int def) {
        return (int)readDouble(property, def);
    }

    public double readFloat(String property, float def) {
        return (float)readDouble(property, def);
    }

    public double readDouble(String property, double def) {
        var o = read(property);
        if (o == null)
            write(property, def);
        return o != null ? o.first().toDouble() : def;
    }

    public String readStr(String property, String def) {
        var o = read(property);
        if (o == null)
            write(property, def);
        return o != null ? o.first().toString() : def;
    }
}
