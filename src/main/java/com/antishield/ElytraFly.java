package com.antishield;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.function.Predicate;

public class ElytraFly {

    private static boolean flightMode = false;
    private static int fireworkCooldown = 0;

    public static void init() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null)
                return;

            if (Keybinds.toggleFly.wasPressed()) {

                flightMode = !flightMode;

                if (flightMode) {
                    equipElytra(client);
                    startFlying(client);
                } else {
                    equipChestplate(client);
                }
            }

            if (flightMode && client.player.isFallFlying()) {

                if (fireworkCooldown > 0) {
                    fireworkCooldown--;
                } else {
                    useFirework(client);
                    fireworkCooldown = 10;
                }
            }

        });
    }

    private static void equipElytra(MinecraftClient client) {
    int slot = findSlot(client, stack -> stack.isOf(Items.ELYTRA));
        if (slot != -1)
            swapSlots(client, slot, 6);
    }

    private static void equipChestplate(MinecraftClient client) {
        int slot = findSlot(client, stack ->
                stack.getItem() instanceof ArmorItem armor
                        && armor.getSlotType() == EquipmentSlot.CHEST
        );
        if (slot != -1)
            swapSlots(client, slot, 6);
    }

    private static void startFlying(MinecraftClient client) {
        client.player.networkHandler.sendPacket(
                new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING)
        );
    }

    private static void useFirework(MinecraftClient client) {
        int slot = findSlot(client, stack -> stack.getItem() instanceof FireworkRocketItem);
        if (slot == -1)
            return;

        swapSlots(client, slot, 45);
        client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
        swapSlots(client, 45, slot);
    }

    private static int findSlot(MinecraftClient client, Predicate<ItemStack> check) {
        PlayerInventory inv = client.player.getInventory();

        for (int i = 0; i < inv.main.size(); i++) {
            ItemStack stack = inv.main.get(i);

            if (check.test(stack))
                return i < 9 ? 36 + i : i;
        }

        return -1;
    }

    private static void swapSlots(MinecraftClient client, int slotA, int slotB) {
        int syncId = client.player.currentScreenHandler.syncId;

        client.interactionManager.clickSlot(syncId, slotA, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, slotB, 0, SlotActionType.PICKUP, client.player);
        client.interactionManager.clickSlot(syncId, slotA, 0, SlotActionType.PICKUP, client.player);
    }

}
