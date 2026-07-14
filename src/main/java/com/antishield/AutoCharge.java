package com.antishield;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.hit.BlockHitResult;
import net.minecraft.hit.HitResult;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class AutoCharge {

    private static KeyBinding toggle;
    private static boolean enabled = false;

    public static void init() {

        toggle = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.antishield.autocharge",
                        GLFW.GLFW_KEY_H,
                        "category.antishield"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null)
                return;

            if (toggle.wasPressed()) {
                enabled = !enabled;
            }

            if (!enabled)
                return;

            if (!(client.crosshairTarget instanceof BlockHitResult hit))
                return;

            if (hit.getType() != HitResult.Type.BLOCK)
                return;

            Block block = client.world.getBlockState(hit.getBlockPos()).getBlock();

            if (block == Blocks.OBSIDIAN) {
                switchTo(client, Items.END_CRYSTAL);
            } else if (block == Blocks.RESPAWN_ANCHOR) {
                switchTo(client, Items.GLOWSTONE);
            }

        });
    }

    private static void switchTo(MinecraftClient client, Item item) {
        int slot = findItem(client, item);
        if (slot != -1) {
            client.player.getInventory().selectedSlot = slot;
        }
    }

    private static int findItem(MinecraftClient client, Item item) {
        PlayerInventory inv = client.player.getInventory();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inv.getStack(i);
            if (stack.isOf(item))
                return i;
        }
        return -1;
    }

}
