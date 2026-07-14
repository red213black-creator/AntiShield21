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
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class TntCart {

    private static KeyBinding activateCart;

    public static void init() {

        activateCart = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.antishield.tntcart",
                        GLFW.GLFW_KEY_G,
                        "category.antishield"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null)
                return;

            if (activateCart.wasPressed()) {
                placeCartAndSwitchToBow(client);
            }

        });
    }

    private static void placeCartAndSwitchToBow(MinecraftClient client) {

        if (!(client.crosshairTarget instanceof BlockHitResult hit))
            return;

        if (hit.getType() != HitResult.Type.BLOCK)
            return;

        Block block = client.world.getBlockState(hit.getBlockPos()).getBlock();

        if (!isRail(block))
            return;

        int cartSlot = findItem(client, Items.TNT_MINECART);

        if (cartSlot == -1)
            return;

        int oldSlot = client.player.getInventory().selectedSlot;

        client.player.getInventory().selectedSlot = cartSlot;

        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
        client.player.swingHand(Hand.MAIN_HAND);

        int bowSlot = findItem(client, Items.BOW);

        if (bowSlot != -1) {
            client.player.getInventory().selectedSlot = bowSlot;
        } else {
            client.player.getInventory().selectedSlot = oldSlot;
        }
    }

    private static boolean isRail(Block block) {
        return block == Blocks.RAIL
                || block == Blocks.POWERED_RAIL
                || block == Blocks.DETECTOR_RAIL
                || block == Blocks.ACTIVATOR_RAIL;
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
