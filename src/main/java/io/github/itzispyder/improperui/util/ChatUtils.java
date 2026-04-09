package io.github.itzispyder.improperui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public final class ChatUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    public static void sendMessage(String message) {
        if (message != null && mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(message));
        }
    }

    public static void sendFormatted(String message, Object... args) {
        if (message != null && mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(StringUtils.color(String.format(message, args))));
        }
    }

    public static void sendRawText(Component text) {
        if (mc.player != null && text != null) {
            mc.player.sendSystemMessage(text);
        }
    }

    public static void sendChatCommand(String cmd) {
        if (mc.player != null) {
            mc.player.connection.sendCommand(cmd);
        }
    }

    public static void sendChatMessage(String msg) {
        if (mc.player != null) {
            mc.player.connection.sendChat(msg);
        }
    }

    public static void sendBlank(int lines) {
        for (int i = 0; i < lines; i++) {
            sendMessage("");
        }
    }

    public static void sendBlank() {
        sendBlank(1);
    }

    public static void pingPlayer() {
        SoundManager sm = mc.getSoundManager();
        SoundEvent event = SoundEvents.EXPERIENCE_ORB_PICKUP;
        SoundInstance sound = SimpleSoundInstance.forUI(event, 0.1F, 10.0F);
        sm.play(sound);
    }
}
