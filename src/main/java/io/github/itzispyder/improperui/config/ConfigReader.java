package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.script.ScriptParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public float readFloat(String property, float def) {
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

    public <T> void writeList(String property, List<T> list, Function<T, String> factory) {
        writeList(property, list.stream().map(factory).toList());
    }

    public <T> List<T> readList(String property, List<T> def, Function<String, T> factory) {
        List<String> list = readList(property, new ArrayList<>());
        return list.isEmpty() ? def : new ArrayList<>(list.stream().map(factory).toList());
    }

    public void writeList(String property, List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String item: list)
            builder.append("<&>").append(item).append("</&>");
        write(property, builder.toString());
    }

    public List<String> readList(String property, List<String> def) {
        var o = read(property);
        if (o == null)
            writeList(property, def);

        String listStr = readStr(property, "");
        Pattern pattern = Pattern.compile("<&>(.*?)</&>");
        Matcher matcher = pattern.matcher(listStr);
        List<String> list = new ArrayList<>();

        while (matcher.find())
            list.add(matcher.group().replaceAll(pattern.pattern(), "$1"));
        return list;
    }
}
