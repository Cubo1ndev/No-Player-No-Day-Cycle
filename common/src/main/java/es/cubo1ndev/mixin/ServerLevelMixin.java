package es.cubo1ndev.mixin;

import es.cubo1ndev.ExampleMod;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Redirect(
        method = "tickTime",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V")
    )
    private void redirectSetDayTime(ServerLevel level, long time) {
        if (!level.players().isEmpty() && !ExampleMod.isSimulating()) {
            level.setDayTime(time);
        }
    }
}
