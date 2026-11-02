package net.anubis.client;

import net.anubis.client.module.impl.render.WeaponInspectModule;
import net.anubis.client.module.impl.render.character.CharacterModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class Anubis3DClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        CharacterModule.initialize();
        WeaponInspectModule.initialize();
        ClientTickEvents.END_CLIENT_TICK.register(client -> CharacterModule.tick());
    }
}
