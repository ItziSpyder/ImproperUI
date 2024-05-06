package io.github.itzispyder.improperui.config;

import java.io.File;

public class Paths {

    public static final String FOLDER = ".improper-ui/";
    public static final String SCRIPTS = FOLDER + "scripts/";
    public static final String CONFIGS = FOLDER + "configs/";
    public static final String ASSETS = FOLDER + "assets/";

    public static void init() {
        makeDirIfAbsent(FOLDER);
        makeDirIfAbsent(SCRIPTS);
        makeDirIfAbsent(CONFIGS);
        makeDirIfAbsent(ASSETS);
    }

    private static void makeDirIfAbsent(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
    }

    public static String getScripts(String modId) {
        return SCRIPTS + modId + "/";
    }

    public static String getConfigs(String modId) {
        return CONFIGS + modId + "/";
    }
}
