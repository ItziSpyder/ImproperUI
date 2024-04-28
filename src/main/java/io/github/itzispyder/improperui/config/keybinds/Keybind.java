package io.github.itzispyder.improperui.config.keybinds;

import io.github.itzispyder.improperui.client.ImproperUIClient;
import io.github.itzispyder.improperui.util.StringUtils;
import io.github.itzispyder.improperui.util.misc.ManualMap;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class Keybind {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final ImproperUIClient system = ImproperUIClient.getInstance();

    public static final int NONE = -1;
    public static final Map<Integer, String> EXTRAS = ManualMap.fromItems(
            GLFW.GLFW_KEY_LEFT_SHIFT, "L Shift",
            GLFW.GLFW_KEY_RIGHT_SHIFT, "R Shift",
            GLFW.GLFW_KEY_LEFT_ALT, "L Alt",
            GLFW.GLFW_KEY_RIGHT_ALT, "R Alt",
            GLFW.GLFW_KEY_LEFT_CONTROL, "L Control",
            GLFW.GLFW_KEY_RIGHT_CONTROL, "R Control",
            GLFW.GLFW_KEY_PAGE_DOWN, "Page Down",
            GLFW.GLFW_KEY_PAGE_UP, "Page Up",
            GLFW.GLFW_KEY_UP, "Up",
            GLFW.GLFW_KEY_DOWN, "Down",
            GLFW.GLFW_KEY_LEFT, "Left",
            GLFW.GLFW_KEY_RIGHT, "Right",
            GLFW.GLFW_KEY_ESCAPE, "Escape",
            GLFW.GLFW_KEY_BACKSPACE, "Backspace",
            GLFW.GLFW_KEY_INSERT, "Insert",
            GLFW.GLFW_KEY_DELETE, "Delete",
            GLFW.GLFW_KEY_HOME, "Home",
            GLFW.GLFW_KEY_END, "End",
            GLFW.GLFW_KEY_TAB, "Tab",
            GLFW.GLFW_KEY_CAPS_LOCK, "Caps",
            GLFW.GLFW_KEY_SPACE, "Space",
            GLFW.GLFW_KEY_ENTER, "Enter",
            69420 + GLFW.GLFW_MOUSE_BUTTON_1, "Mouse L",
            69420 + GLFW.GLFW_MOUSE_BUTTON_2, "Mouse R",
            69420 + GLFW.GLFW_MOUSE_BUTTON_3, "Mouse M",
            69420 + GLFW.GLFW_MOUSE_BUTTON_4, "Mouse 4",
            69420 + GLFW.GLFW_MOUSE_BUTTON_5, "Mouse 5",
            69420 + GLFW.GLFW_MOUSE_BUTTON_6, "Mouse 6",
            69420 + GLFW.GLFW_MOUSE_BUTTON_7, "Mouse 7",
            69420 + GLFW.GLFW_MOUSE_BUTTON_8, "Mouse 8"
    );
    public static final Map<Integer, String> EXTENDED_NAMES = ManualMap.fromItems(
            GLFW.GLFW_KEY_LEFT_SHIFT, "left_shift",
            GLFW.GLFW_KEY_RIGHT_SHIFT, "right_shift",
            GLFW.GLFW_KEY_LEFT_ALT, "left_alt",
            GLFW.GLFW_KEY_RIGHT_ALT, "right_alt",
            GLFW.GLFW_KEY_LEFT_CONTROL, "left_control",
            GLFW.GLFW_KEY_RIGHT_CONTROL, "right_control",
            GLFW.GLFW_KEY_PAGE_DOWN, "page_down",
            GLFW.GLFW_KEY_PAGE_UP, "page_up",
            GLFW.GLFW_KEY_UP, "up_arrow",
            GLFW.GLFW_KEY_DOWN, "down_arrow",
            GLFW.GLFW_KEY_LEFT, "left_arrow",
            GLFW.GLFW_KEY_RIGHT, "right_arrow",
            GLFW.GLFW_KEY_ESCAPE, "escape",
            GLFW.GLFW_KEY_BACKSPACE, "backspace",
            GLFW.GLFW_KEY_INSERT, "insert",
            GLFW.GLFW_KEY_DELETE, "delete",
            GLFW.GLFW_KEY_HOME, "home",
            GLFW.GLFW_KEY_END, "end",
            GLFW.GLFW_KEY_TAB, "tab",
            GLFW.GLFW_KEY_CAPS_LOCK, "capslock",
            GLFW.GLFW_KEY_SPACE, "space"
    );
    private final String name, id;
    private int key, defaultKey;
    private KeyAction keyAction, changeAction, releaseAction;
    private BindCondition bindCondition;

    public Keybind(String id, int defaultKey, int key, KeyAction keyAction, KeyAction changeAction, KeyAction releaseAction, BindCondition bindCondition) {
        this.id = id;
        this.name = StringUtils.capitalizeWords(id);
        this.key = key;
        this.defaultKey = defaultKey;
        this.keyAction = keyAction;
        this.changeAction = changeAction;
        this.releaseAction = releaseAction;
        this.bindCondition = bindCondition;
        system.addKeybind(this);
    }

    public void onPress() {
        if (bindCondition.meets(this, mc.currentScreen) && keyAction != null) {
            keyAction.onKey(this);
        }
    }

    public void onRelease() {
        if (bindCondition.meets(this, mc.currentScreen) && releaseAction != null) {
            releaseAction.onKey(this);
        }
    }

    public boolean canPress(int keyCode) {
        if (keyCode == NONE)
            return false;
        boolean notNull = !getKeyName().equals("NONE");
        boolean isExtra = EXTRAS.containsKey(key);
        boolean isKey = keyCode == key;
        return (notNull || isExtra) && isKey;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getKey() {
        return key;
    }

    public String getKeyName() {
        String name = GLFW.glfwGetKeyName(key, 32);
        if (name == null) {
            name = EXTRAS.getOrDefault(key, "NONE");
        }
        return name;
    }

    public static String getExtendedKeyName(int key, int scancode) {
        String name = GLFW.glfwGetKeyName(key, scancode);
        if (name == null) {
            name = EXTENDED_NAMES.get(key);
        }
        return name;
    }

    public void setKey(int key) {
        this.key = key;
        this.changeAction.onKey(this);
    }

    public int getDefaultKey() {
        return defaultKey;
    }

    public void setDefaultKey(int defaultKey) {
        this.defaultKey = defaultKey;
    }

    public KeyAction getKeyAction() {
        return keyAction;
    }

    public void setKeyAction(KeyAction keyAction) {
        this.keyAction = keyAction;
    }

    public BindCondition getBindCondition() {
        return bindCondition;
    }

    public void setBindCondition(BindCondition bindCondition) {
        this.bindCondition = bindCondition;
    }

    public KeyAction getChangeAction() {
        return changeAction;
    }

    public void setChangeAction(KeyAction changeAction) {
        this.changeAction = changeAction;
    }

    public KeyAction getReleaseAction() {
        return releaseAction;
    }

    public void setReleaseAction(KeyAction releaseAction) {
        this.releaseAction = releaseAction;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private int key, defaultKey;
        private KeyAction keyAction, changeAction, releaseAction;
        private BindCondition bindCondition;

        public Builder() {
            id = "unregistered-keybind";
            key = defaultKey = NONE;
            keyAction = changeAction = releaseAction = bind -> {};
            bindCondition = (bind, screen) -> true;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder key(int key) {
            this.key = key;
            return this;
        }

        public Builder defaultKey(int defaultKey) {
            this.defaultKey = defaultKey;
            return this;
        }

        public Builder onPress(KeyAction keyAction) {
            this.keyAction = keyAction;
            return this;
        }

        public Builder onRelease(KeyAction releaseAction) {
            this.releaseAction = releaseAction;
            return this;
        }

        public Builder onChange(KeyAction changeAction) {
            this.changeAction = changeAction;
            return this;
        }

        public Builder condition(BindCondition bindCondition) {
            this.bindCondition = bindCondition;
            return this;
        }

        public Keybind build() {
            key = key == NONE ? defaultKey : key;
            return new Keybind(id, defaultKey, key, keyAction, changeAction, releaseAction, bindCondition);
        }
    }
}
