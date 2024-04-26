package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.render.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

    public static void main(String[] args) {
        parseFile(new File("src/main/resources/skibidi.ui"));
    }

    public static List<Element> parseFile(File file) {
        List<Element> result = new ArrayList<>();

        if (file == null || !file.exists())
            return result;

        String script = ScriptReader.readFile(file.getPath());
        for (String section : ScriptReader.getAllSections(script, '{', '}')) {
            result.add(parseInternal(section));
        }

        return result;
    }

    private static Element parseInternal(String excerpt) {
        Element element = new Element();

        for (String line : ScriptReader.parse(excerpt)) {
            element.callProperty(line);
        }
        for (String section : ScriptReader.getAllSections(excerpt, '{', '}')) {
            element.addChild(parseInternal(section));
        }

        System.out.println(element);
        return element;
    }
}
