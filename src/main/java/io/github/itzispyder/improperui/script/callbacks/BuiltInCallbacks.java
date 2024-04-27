package io.github.itzispyder.improperui.script.callbacks;

import io.github.itzispyder.improperui.script.CallbackHandler;
import io.github.itzispyder.improperui.script.CallbackListener;
import io.github.itzispyder.improperui.script.events.KeyEvent;
import io.github.itzispyder.improperui.script.events.MouseEvent;
import io.github.itzispyder.improperui.util.ChatUtils;
import org.lwjgl.glfw.GLFW;

public class BuiltInCallbacks implements CallbackListener {

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
}
