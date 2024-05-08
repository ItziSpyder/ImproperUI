package io.github.itzispyder.improperui.script.callbacks;

import io.github.itzispyder.improperui.ImproperUIAPI;
import io.github.itzispyder.improperui.script.CallbackHandler;
import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.events.KeyEvent;
import io.github.itzispyder.improperui.script.events.MouseEvent;
import io.github.itzispyder.improperui.util.ChatUtils;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

public class BuiltInCallbacks implements CallbackListener {

    @CallbackHandler
    public void openGithub(MouseEvent e) {
        if (e.input.isDown())
            Util.getOperatingSystem().open("https://github.com/itzispyder/improperui");
    }

    @CallbackHandler
    public void openModrinth(MouseEvent e) {
        if (e.input.isDown())
            Util.getOperatingSystem().open("https://modrinth.com/mod/improperui");
    }

    @CallbackHandler
    public void openDiscord(MouseEvent e) {
        if (e.input.isDown())
            Util.getOperatingSystem().open("https://discord.gg/tMaShNzNtP");
    }

    @CallbackHandler
    public void openWiki(MouseEvent e) {
        if (e.input.isDown())
            Util.getOperatingSystem().open("https://github.com/itzispyder/improperui/wiki");
    }

    @CallbackHandler
    public void openExampleScreen(MouseEvent e) {
        if (e.input.isDown())
            ImproperUIAPI.parseAndRunFile("improperui", "example.ui");
    }

    @CallbackHandler
    public void sendHelloWorld(MouseEvent e) {
        if (e.input.isDown())
            ChatUtils.sendMessage("Hello World");
    }

    @CallbackHandler
    public void sendHelloWorld(KeyEvent e) {
        if (e.input.isDown())
            ChatUtils.sendFormatted("Hello World + %s", GLFW.glfwGetKeyName(e.key, e.scan));
    }

    @CallbackHandler
    public void printSelf(MouseEvent e) {
        if (e.input.isDown())
            ChatUtils.sendMessage("target: " + e.target);
    }

    @CallbackHandler
    public void printSelf(KeyEvent e) {
        if (e.input.isDown())
            ChatUtils.sendMessage("target: " + e.target);
    }
}
