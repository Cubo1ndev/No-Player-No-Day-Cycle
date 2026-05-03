package es.cubo1ndev.neoforge;

import net.neoforged.fml.common.Mod;

import es.cubo1ndev.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
