package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.config.PropertyCache;
import io.github.itzispyder.improperui.render.Element;
import io.github.itzispyder.improperui.render.ImproperUIPanel;
import io.github.itzispyder.improperui.render.elements.*;
import io.github.itzispyder.improperui.script.callbacks.BuiltInCallbacks;
import io.github.itzispyder.improperui.util.ChatUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ScriptParser {

    private static final String ELEMENT = "[a-zA-Z0-9]+(\\s+[#-][a-zA-Z0-9:._-]+)*?\\s*\\{.*\\}";
    private static final String SECTION = "\\s*\\{.*\\}\\s*$";
    public static final Map<String, PropertyCache> CACHE = new HashMap<>();

    public static PropertyCache getCache(String modId) {
        if (!CACHE.containsKey(modId))
            CACHE.put(modId, new PropertyCache(modId));
        return CACHE.get(modId);
    }

    private static final Map<String, Supplier<? extends Element>> tagSuppliers = new HashMap<>() {{
        this.put("checkbox", CheckBox::new);
        this.put("textfield", TextField::new);
        this.put("textarea", TextField::new);
        this.put("div", Element::new);
        this.put("e", Element::new);
        this.put("button", Button::new);
        this.put("link", HyperLink::new);
        this.put("a", HyperLink::new);
        this.put("slider", Slider::new);
        this.put("radio", Radio::new);
        this.put("textbox", TextBox::new);
        this.put("input", TextBox::new);
        this.put("textlabel", Label::new);
        this.put("label", Label::new);
        this.put("positionable", Positionable::new);
        this.put("header1", () -> new Header(1.8F));
        this.put("header2", () -> new Header(1.6F));
        this.put("header3", () -> new Header(1.4F));
        this.put("header4", () -> new Header(1.2F));
        this.put("header5", () -> new Header(1.0F));
        this.put("header6", () -> new Header(0.8F));
        this.put("h1", () -> new Header(1.8F));
        this.put("h2", () -> new Header(1.6F));
        this.put("h3", () -> new Header(1.4F));
        this.put("h4", () -> new Header(1.2F));
        this.put("h5", () -> new Header(1.0F));
        this.put("h6", () -> new Header(0.8F));
    }};

    public static void main(String[] args) {
        parseFile(new File("src/main/resources/assets/improperui/scripts/skibidi.ui"));
    }

    public static void run(File file) {
        var elements = parseFile(file);
        var mc = MinecraftClient.getInstance();

        ImproperUIPanel panel = new ImproperUIPanel();
        panel.registerCallback(new BuiltInCallbacks());
        elements.forEach(panel::addChild);

        mc.execute(() -> mc.setScreen(panel));
    }

    public static void run(String script) {
        var elements = parse(script);
        var mc = MinecraftClient.getInstance();

        ImproperUIPanel panel = new ImproperUIPanel();
        panel.registerCallback(new BuiltInCallbacks());
        elements.forEach(panel::addChild);

        mc.execute(() -> mc.setScreen(panel));
    }

    public static List<Element> parseFile(File file) {
        if (file == null || !file.exists())
            return new ArrayList<>();

        String script = ScriptReader.readFile(file.getPath());
        return parse(script);
    }

    public static List<Element> parse(String script) {
        List<Element> result = new ArrayList<>();
        try {
            script = ScriptReader.condenseLines(script);
            for (String section : ScriptReader.parse(script)) {
                result.add(parseInternal(section));
            }
            for (Element element : result) {
                element.style();
                //element.printAll();
            }
        }
        catch (Exception ex) {
            ChatUtils.sendMessage(StringUtils.color("&cError parsing script: " + ex.getMessage()));
        }
        return result;
    }

    private static Element parseInternal(String excerpt) {
        String[] attr = getAttributes(excerpt);
        return parseInternal0(attr[0], excerpt);
    }

    private static Element parseInternal0(String tag, String excerpt) {
        Element element = tagSuppliers.getOrDefault(tag, Element::new).get();

        if (excerpt.matches(ELEMENT)) {
            callAttributes(element, getAttributes(excerpt));
            excerpt = ScriptReader.firstSection(excerpt, '{', '}');
        }

        for (String line : ScriptReader.parse(excerpt)) {
            if (!line.matches(ELEMENT)) {
                element.queueProperty(line);
                continue;
            }
            String[] attr = getAttributes(line);
            Element child = parseInternal0(attr[0], ScriptReader.firstSection(line, '{', '}'));
            callAttributes(child, attr);
            element.addChild(child);
        }

        return element;
    }

    private static String[] getAttributes(String scriptLine) {
        return scriptLine.replaceFirst(SECTION, "").split("\s+");
    }

    private static void callAttributes(Element element, String[] attrs) {
        for (var attr : attrs)
            element.callAttribute(attr);
    }
}