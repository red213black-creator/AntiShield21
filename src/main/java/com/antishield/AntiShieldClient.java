package com.antishield;

import net.fabricmc.api.ClientModInitializer;

public class AntiShieldClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AntiShield.init();
        ElytraFly.init();
        TntCart.init();
        AutoCharge.init();
        Keybinds.register();
    }

}
