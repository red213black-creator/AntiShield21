package com.antishield;

import net.fabricmc.api.ClientModInitializer;

public class AntiShieldClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AntiShield.init();
        Keybinds.register();
    }

}
