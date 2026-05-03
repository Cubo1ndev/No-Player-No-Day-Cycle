package es.cubo1ndev;

import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public final class ExampleMod {
    public static final String MOD_ID = "no_player_no_day_cycle";

    private static final Set<ResourceKey<Level>> frozenByMod = new HashSet<>();
    private static boolean simulating = false;

    public static boolean isSimulating() { return simulating; }

    public static void init() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) ->
            dispatcher.register(Commands.literal("npdcinfo")
                .requires(src -> src.hasPermission(2))
                .executes(ExampleMod::executeInfo)
                .then(Commands.literal("simulate")
                    .executes(ExampleMod::executeSimulate))));

        PlayerEvent.PLAYER_JOIN.register(ExampleMod::onPlayerJoin);
        PlayerEvent.PLAYER_QUIT.register(ExampleMod::onPlayerQuit);
    }

    private static void onPlayerJoin(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null || simulating) return;
        server.getAllLevels().forEach(level -> {
            if (frozenByMod.remove(level.dimension())) {
                level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, server);
            }
        });
    }

    private static void onPlayerQuit(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null || simulating) return;
        boolean lastPlayer = server.getPlayerList().getPlayers().stream()
            .filter(p -> p != player).findAny().isEmpty();
        if (lastPlayer) {
            freezeAll(server);
        }
    }

    private static void freezeAll(MinecraftServer server) {
        server.getAllLevels().forEach(level -> {
            if (level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                    && frozenByMod.add(level.dimension())) {
                level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
            }
        });
    }

    private static void unfreezeAll(MinecraftServer server) {
        server.getAllLevels().forEach(level -> {
            if (frozenByMod.remove(level.dimension())) {
                level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, server);
            }
        });
    }

    private static int executeInfo(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        long gameTime = level.getLevelData().getGameTime();
        long dayTime  = level.getLevelData().getDayTime();
        int  players  = level.players().size();
        long day      = dayTime / 24000L;

        ctx.getSource().sendSuccess(() -> Component.literal(
            "[NPNDC] gameTime=" + gameTime +
            "  dayTime=" + dayTime +
            "  day=" + day +
            "  players=" + players +
            "  simulate=" + simulating
        ), false);
        return 1;
    }

    private static int executeSimulate(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        simulating = !simulating;
        if (simulating) {
            freezeAll(server);
        } else {
            unfreezeAll(server);
        }
        ctx.getSource().sendSuccess(() -> Component.literal(
            "[NPNDC] Empty server simulation " + (simulating ? "ON" : "OFF")
        ), true);
        return 1;
    }
}
