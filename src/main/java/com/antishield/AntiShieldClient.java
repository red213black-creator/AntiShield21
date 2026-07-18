package com.antishield;

import net.fabricmc.api.ClientModInitializer;

public class AntiShieldClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ElytraFly.init();
        Keybinds.register();
    }
}
