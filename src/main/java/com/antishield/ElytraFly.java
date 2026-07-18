package com.antishield;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

public class ElytraFly {

    private static KeyBinding toggleFly;
    private static boolean flightMode = false;
    private static boolean wasGliding = false;

    public static void init() {
        toggleFly = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.antishield.togglefly",
                        KeyBinding.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "category.antishield"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (toggleFly.wasPressed()) {
                flightMode = !flightMode;
                if (flightMode) {
                    equipElytra(client);
                    startFlying(client);
                } else {
                    equipChestplate(client);
                }
            }

            if (flightMode) {
                boolean gliding = client.player.isGliding();
                if (gliding && !wasGliding) {
                    useFirework(client);
                }
                wasGliding = gliding;
            }
        });
    }

    private static void equipElytra(MinecraftClient client) {
        int slot = findSlot(client, stack -> stack.isOf(Items.ELYTRA));
        if (slot != -1) swapSlots(client, slot, 6);
    }

    private static void equipChestplate(MinecraftClient client) {
        int slot = findSlot(client, stack -> {
            EquippableComponent eq = stack.get(DataComponentTypes.EQUIPPABLE);
            return eq != null && eq.slot() == EquipmentSlot.CHEST;
        });
        if (slot != -1) swapSlots(client, slot, 6);
    }

    private static void startFlying(MinecraftClient client) {
        client.player.networkHandler.sendPacket(
                new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING)
        );
    }

    private static void useFirework(MinecraftClient client) {
        int slot = findSlot(client, stack -> stack.getItem() instanceof FireworkRocketItem);
        if (slot == -1) return;
        swapSlots(client, slot, 45);
        client.interactionManager.interactItem(client.player, Hand.OFF_HAND);
        swapSlots(client, 45, slot);
    }

    private static int findSlot(MinecraftClient client, Predicate<ItemStack> check) {
        PlayerInventory inv = client.player.getInventory();
        for (int i = 0; i < inv.main.size(); i++) {
            ItemStack stack = inv.main.get(i);
            if (check.test(stack)) {
                return i < 9 ? 36 + i : i;
            }
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
