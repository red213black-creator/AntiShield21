package com.antishield;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding toggleFly;

    public static void register() {
        toggleFly = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.antishield.togglefly",
                        KeyBinding.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "category.antishield"
                )
        );
    }
}
