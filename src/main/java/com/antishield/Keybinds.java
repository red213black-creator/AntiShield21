package com.antishield;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public static KeyBinding activate;
    public static KeyBinding toggleFly;

    public static void register() {

        activate = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.antishield.activate",
                        GLFW.GLFW_KEY_K,
                        "category.antishield"
                )
        );

        toggleFly = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.antishield.togglefly",
                        GLFW.GLFW_KEY_V,
                        "category.antishield"
                )
        );

    }

}
