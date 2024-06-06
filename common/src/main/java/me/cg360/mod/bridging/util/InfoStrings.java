package me.cg360.mod.bridging.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class InfoStrings {

    public static final Component TOGGLE_BRIDGING = Component.translatable("notif.bridgingmod.toggle_bridging").withStyle(ChatFormatting.GOLD).append(": ");

    public static final Component ON = Component.translatable("notif.bridgingmod.action.enabled").withStyle(ChatFormatting.GREEN);
    public static final Component OFF = Component.translatable("notif.bridgingmod.action.disabled").withStyle(ChatFormatting.RED);

}
