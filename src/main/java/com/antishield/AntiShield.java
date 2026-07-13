package com.antishield;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.MinecraftClient;

import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.item.AxeItem;

import net.minecraft.item.ItemStack;

import net.minecraft.util.Hand;



public class AntiShield {


    private static boolean enabled = false;

    private static int oldSlot = -1;



    public static void init(){


        ClientTickEvents.END_CLIENT_TICK.register(client -> {


            if(client.player == null)
                return;



            if(Keybinds.activate.wasPressed()){


                enabled = !enabled;



                if(enabled){


                    oldSlot = client.player.getInventory().selectedSlot;


                    int axe = findAxe(client);


                    if(axe != -1){

                        client.player.getInventory().selectedSlot = axe;

                    }

                }

            }



            if(enabled){


                PlayerEntity target = getTarget(client);



                if(target != null && target.isBlocking()){


                    client.interactionManager.attackEntity(

                            client.player,

                            target

                    );


                    client.player.swingHand(Hand.MAIN_HAND);



                    client.player.getInventory().selectedSlot = oldSlot;


                    enabled = false;

                }

            }


        });


    }



    private static int findAxe(MinecraftClient client){


        for(int i = 0; i < 9; i++){


            ItemStack item = client.player.getInventory().getStack(i);



            if(item.getItem() instanceof AxeItem)

                return i;

        }


        return -1;

    }



    private static PlayerEntity getTarget(MinecraftClient client){


        if(client.targetedEntity instanceof PlayerEntity player)

            return player;


        return null;

    }


}
