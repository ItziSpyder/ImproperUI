package io.github.itzispyder.improperui.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public final class ChatUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendMessage(String message) {
        if (message != null && mc.player != null) {
            mc.player.sendMessage(Text.literal(message));
        }
    }

    public static void sendFormatted(String message, Object... args) {
        if (message != null && mc.player != null) {
            mc.player.sendMessage(Text.literal(StringUtils.color(String.format(message, args))));
        }
    }

    public static void sendRawText(Text text) {
        if (mc.player != null && text != null) {
            mc.player.sendMessage(text);
        }
    }

    public static void sendChatCommand(String cmd) {
        if (mc.player != null) {
            mc.player.networkHandler.sendCommand(cmd);
        }
    }

    public static void sendChatMessage(String msg) {
        if (mc.player != null) {
            mc.player.networkHandler.sendChatMessage(msg);
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
        SoundEvent event = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
        SoundInstance sound = PositionedSoundInstance.master(event, 0.1F, 10.0F);
        sm.play(sound);
    }
}
