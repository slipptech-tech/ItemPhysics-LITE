package slipptech.itemphysics;

import net.fabricmc.api.ClientModInitializer;

public final class ItemPhysicsClient
        implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println(
                "[Item Physics Lite] Physics engine initialized."
        );
    }
}