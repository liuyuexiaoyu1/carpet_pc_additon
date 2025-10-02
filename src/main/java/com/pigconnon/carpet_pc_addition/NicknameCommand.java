package com.pigconnon.carpet_pc_addition;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.*;

public class NicknameCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("nickname")
                .requires(source -> {
                    String permissionRule = CPCASettings.Commandnickname;
                    return hasPermission(source, permissionRule);
                })
                .then(literal("prefix")
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("prefix", StringArgumentType.greedyString())
                                        .executes(context -> setPrefix(context,
                                                EntityArgumentType.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "prefix"))))))

                .then(literal("suffix")
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("suffix", StringArgumentType.greedyString())
                                        .executes(context -> setSuffix(context,
                                                EntityArgumentType.getPlayer(context, "player"),
                                                StringArgumentType.getString(context, "suffix"))))))

                .then(literal("set")
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("prefix", StringArgumentType.string())
                                        .then(argument("suffix", StringArgumentType.string())
                                                .executes(context -> setNickname(context,
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        StringArgumentType.getString(context, "prefix"),
                                                        StringArgumentType.getString(context, "suffix")))))))

                .then(literal("remove")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(context -> removeNickname(context,
                                        EntityArgumentType.getPlayer(context, "player")))))

                .then(literal("view")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(context -> viewNickname(context,
                                        EntityArgumentType.getPlayer(context, "player")))))

                .then(literal("reload")
                        .executes(context -> reloadNicknames(context)))
                .then(literal("save")
                        .executes(context -> save(context)))
        );

        // 单独的前缀指令
        dispatcher.register(literal("prefix")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("player", EntityArgumentType.player())
                        .then(argument("prefix", StringArgumentType.greedyString())
                                .executes(context -> setPrefix(context,
                                        EntityArgumentType.getPlayer(context, "player"),
                                        StringArgumentType.getString(context, "prefix")))))
        );

        // 单独的后缀指令
        dispatcher.register(literal("suffix")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("player", EntityArgumentType.player())
                        .then(argument("suffix", StringArgumentType.greedyString())
                                .executes(context -> setSuffix(context,
                                        EntityArgumentType.getPlayer(context, "player"),
                                        StringArgumentType.getString(context, "suffix")))))
        );

    }

    private static int setPrefix(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, String prefix) {
        UUID playerUUID = player.getUuid();
        PlayerNickname nickname = NicknameManager.getInstance().getNickname(playerUUID);
        save(context);
        if (nickname == null) {
            nickname = new PlayerNickname();
        }

        prefix = prefix.replace('&', '§');

        nickname.setPrefix(prefix);
        NicknameManager.getInstance().setNickname(playerUUID, nickname);

        String playerName = player.getGameProfile().getName();
        context.getSource().sendMessage(Text.literal("已设置 " + playerName + " 的前缀为: " + prefix)
                .formatted(Formatting.GREEN));

        if (!context.getSource().getName().equals(playerName)) {
            player.sendMessage(Text.literal("你的前缀已被设置为: " + prefix).formatted(Formatting.YELLOW));
        }

        return 1;
    }
    private static int save(CommandContext<ServerCommandSource> context){
        NicknameManager.getInstance().saveData();
        return 1;
    };

    private static int setSuffix(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, String suffix) {
        UUID playerUUID = player.getUuid();
        PlayerNickname nickname = NicknameManager.getInstance().getNickname(playerUUID);
        save(context);
        if (nickname == null) {
            nickname = new PlayerNickname();
        }

        suffix = suffix.replace('&', '§');

        nickname.setSuffix(suffix);
        NicknameManager.getInstance().setNickname(playerUUID, nickname);

        String playerName = player.getGameProfile().getName();
        context.getSource().sendMessage(Text.literal("已设置 " + playerName + " 的后缀为: " + suffix)
                .formatted(Formatting.GREEN));

        if (!context.getSource().getName().equals(playerName)) {
            player.sendMessage(Text.literal("你的后缀已被设置为: " + suffix).formatted(Formatting.YELLOW));
        }

        return 1;
    }

    private static int setNickname(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, String prefix, String suffix) {
        UUID playerUUID = player.getUuid();
        save(context);
        prefix = prefix.replace('&', '§');
        suffix = suffix.replace('&', '§');

        PlayerNickname nickname = new PlayerNickname(prefix, suffix);
        NicknameManager.getInstance().setNickname(playerUUID, nickname);

        String playerName = player.getGameProfile().getName();
        context.getSource().sendMessage(Text.literal("已设置 " + playerName + " 的前缀为: " + prefix + " 后缀为: " + suffix)
                .formatted(Formatting.GREEN));

        if (!context.getSource().getName().equals(playerName)) {
            player.sendMessage(Text.literal("你的前后缀已被设置").formatted(Formatting.YELLOW));
        }

        return 1;
    }

    private static int removeNickname(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        NicknameManager.getInstance().removeNickname(playerUUID);
        save(context);
        String playerName = player.getGameProfile().getName();
        context.getSource().sendMessage(Text.literal("已移除 " + playerName + " 的所有前后缀")
                .formatted(Formatting.GREEN));

        if (!context.getSource().getName().equals(playerName)) {
            player.sendMessage(Text.literal("你的前后缀已被移除").formatted(Formatting.YELLOW));
        }

        return 1;
    }

    private static int viewNickname(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        PlayerNickname nickname = NicknameManager.getInstance().getNickname(playerUUID);
        String playerName = player.getGameProfile().getName();

        if (nickname != null && (nickname.hasPrefix() || nickname.hasSuffix())) {
            context.getSource().sendMessage(Text.literal(playerName + " 的昵称设置:").formatted(Formatting.GOLD));

            if (nickname.hasPrefix()) {
                context.getSource().sendMessage(Text.literal("前缀: " + nickname.getPrefix())
                        .formatted(Formatting.AQUA));
            } else {
                context.getSource().sendMessage(Text.literal("前缀: 未设置").formatted(Formatting.GRAY));
            }

            if (nickname.hasSuffix()) {
                context.getSource().sendMessage(Text.literal("后缀: " + nickname.getSuffix())
                        .formatted(Formatting.AQUA));
            } else {
                context.getSource().sendMessage(Text.literal("后缀: 未设置").formatted(Formatting.GRAY));
            }

            Text preview = NicknameManager.getInstance().getFormattedChatName(playerUUID, playerName);
            if (preview != null) {
                context.getSource().sendMessage(Text.literal("预览: ").append(preview).formatted(Formatting.GREEN));
            }
        } else {
            context.getSource().sendMessage(Text.literal(playerName + " 没有设置前后缀")
                    .formatted(Formatting.YELLOW));
        }
        return 1;
    }

    private static int reloadNicknames(CommandContext<ServerCommandSource> context) {
        NicknameManager.getInstance().loadData();
        context.getSource().sendMessage(Text.literal("昵称数据已重新加载")
                .formatted(Formatting.GREEN));
        return 1;
    }
    private static boolean hasPermission(ServerCommandSource source, String permissionRule) {
        switch (permissionRule) {
            case "true":
            case "false":
            case "ops":
                return source.hasPermissionLevel(2);
            default:
                try {
                    int requiredLevel = Integer.parseInt(permissionRule);
                    return source.hasPermissionLevel(requiredLevel);
                } catch (NumberFormatException e) {
                    return false;
                }
        }
    }

}