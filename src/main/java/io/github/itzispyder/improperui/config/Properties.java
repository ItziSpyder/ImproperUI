package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.script.ScriptArgs;
import io.github.itzispyder.improperui.util.FileValidationUtils;
import io.github.itzispyder.improperui.util.misc.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Properties {

    private final Map<String, Value> properties;

    public Properties() {
        this.properties = new HashMap<>();
    }

    public Value getProperty(Key key) {
        return properties.get(key.name());
    }

    public Value getProperty(String key) {
        return getProperty(new Key(key));
    }

    public void setProperty(Key key, Value value) {
        if (key != null && value != null)
            properties.put(key.name(), value);
    }

    public void setProperty(String key, String value) {
        setProperty(new Key(key), new Value(value));
    }

    public boolean hasProperty(Key key) {
        return properties.containsKey(key);
    }

    public boolean hasProperty(String key) {
        return hasProperty(new Key(key));
    }

    public void read(InputStream in) {
        try (in; var isr = new InputStreamReader(in); var br = new BufferedReader(isr)) {
            properties.clear();

            String line;
            while ((line = br.readLine()) != null) {
                var pair = callProperty(line);
                if (pair != null)
                    setProperty(pair.left, pair.right);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(OutputStream out) {
        try (out; var osw = new OutputStreamWriter(out); var bw = new BufferedWriter(osw)) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Value> entry : properties.entrySet()) {
                String line = "%s = %s".formatted(entry.getKey(), entry.getValue().getName());
                sb.append(line).append('\n');
            }
            bw.write(sb.toString());
            bw.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void read(String path) {
        path = Paths.getConfigs() + path;
        FileValidationUtils.validate(new File(path));

        try (FileInputStream fis = new FileInputStream(path)) {
            read(fis);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(String path) {
        path = Paths.getConfigs() + path;
        File file = new File(path);
        FileValidationUtils.validate(file);

        try (FileOutputStream fos = new FileOutputStream(path)) {
            write(fos);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Pair<Key, Value> callProperty(String line) {
        String regex = "\\s*((=>)|(->)|:|=)\\s*";
        String[] split = line.trim().split(regex);

        if (split.length < 2)
            return null;

        Key key = new Key(split[0]);
        Value val = new Value(line.substring(split[0].length()).replaceFirst(regex, ""));
        return Pair.of(key, val);
    }

    public record Key(String name) {
        public Key(String name) {
            this.name = name.trim()
                    .replaceAll("\\s+|_", "-")
                    .replaceAll("[^a-zA-Z0-9.-]", "");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key key))
                return false;
            return key.name().equals(this.name());
        }
    }

    public static class Value extends ScriptArgs {
        public Value(String name) {
            super(name.trim().split("\\s+"));
        }

        public String getName() {
            return getAll().toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Value val))
                return false;
            return val.getName().equals(this.getName());
        }
    }
}
