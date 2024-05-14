package io.github.itzispyder.improperui;

import io.github.itzispyder.improperui.config.Paths;
import io.github.itzispyder.improperui.util.FileValidationUtils;
import net.fabricmc.api.ModInitializer;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class InitContext {

    private final String modId;
    private final Class<? extends ModInitializer> initializer;
    private String[] scriptPaths;

    private boolean initialized = false;
    private final Set<String> paths = new HashSet<>();

    public InitContext(String modId, Class<? extends ModInitializer> initializer, String... scriptPaths) {
        this.modId = modId;
        this.initializer = initializer;
        this.scriptPaths = scriptPaths;
    }

    public void init() {
        if (initialized)
            return;
        initialized = true;
        paths.clear();
        Paths.init();
        ImproperUIAPI.LOGGER.info("Initializing mod '{}' in '{}.class' with {} scripts:", modId, getName(), scriptPaths.length);

        var loader = initializer.getClassLoader();
        for (String path : scriptPaths)
            while (copyResource(loader, path) == -1);
    }

    public void reload() {
        reInit(scriptPaths);
    }

    public void reInit(String... scriptPaths) {
        this.initialized = false;
        this.scriptPaths = scriptPaths;
        this.paths.clear();
        init();
    }

    private int copyResource(ClassLoader loader, String path) {
        try {
            String name = path.trim().replaceAll(".*/", "");
            if (paths.contains(name))
                throw new IllegalArgumentException("path '%s' already exists".formatted(path));

            InputStream is = loader.getResourceAsStream(path);

            if (is == null)
                throw new IllegalArgumentException("resource not found!");

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String read = String.join("\n", br.lines().toList());
            br.close();
            isr.close();
            is.close();

            File file = new File(Paths.getScripts(modId) + name);
            FileValidationUtils.validate(file);

            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(read);
            bw.close();
            fw.close();

            boolean success = file.exists();
            String filePath = file.getPath();

            if (success) {
                paths.add(name);
                ImproperUIAPI.LOGGER.info("-> Successfully cloned path '{}' to '{}'", path, filePath);
            }
            else {
                ImproperUIAPI.LOGGER.error("<- Path '{}' read, but was unable to be copied to '{}'", path, filePath);
            }
            return success ? 1 : -1;
        }
        catch (Exception ex) {
            ImproperUIAPI.LOGGER.error("<- Error copying resource '{}': {}\n", path, ex.getMessage());
            return 0;
        }
    }

    public String getId() {
        return modId;
    }

    public Class<? extends ModInitializer> getInitializer() {
        return initializer;
    }

    public String getName() {
        return initializer.getSimpleName();
    }

    public String[] getPaths() {
        return scriptPaths;
    }
}
