package com.pigconnon.carpet_pc_addition.mixin.nickname;

import com.pigconnon.carpet_pc_addition.NicknameManager;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Mixin(PlayerListS2CPacket.class)
public class PlayerListS2CPacketMixin {


    @Inject(method = "<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V", at = @At("RETURN"))
    private void onPlayerListPacketConstructed(java.util.EnumSet<PlayerListS2CPacket.Action> actions, java.util.Collection<net.minecraft.server.network.ServerPlayerEntity> players, CallbackInfo ci) {


        boolean shouldProcess = actions.contains(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME) ||
                actions.contains(PlayerListS2CPacket.Action.ADD_PLAYER);



        if (shouldProcess) {
            try {
                // 使用反射获取 entries 字段
                java.lang.reflect.Field entriesField = PlayerListS2CPacket.class.getDeclaredField("entries");
                entriesField.setAccessible(true);
                java.util.List<PlayerListS2CPacket.Entry> entries = (java.util.List<PlayerListS2CPacket.Entry>) entriesField.get(this);



                // 创建新的 entries 列表来替换原来的
                java.util.List<PlayerListS2CPacket.Entry> modifiedEntries = new java.util.ArrayList<>();
                boolean modified = false;

                for (PlayerListS2CPacket.Entry entry : entries) {
                    if (entry.profile() != null) {
                        UUID playerUUID = entry.profile().getId();
                        String originalName = entry.profile().getName();

                        Text formattedDisplayName = NicknameManager.getInstance().getFormattedTabName(playerUUID, originalName);
                        if (formattedDisplayName != null) {


                            // 创建新的 Entry 对象，使用格式化后的显示名称
                            PlayerListS2CPacket.Entry newEntry = new PlayerListS2CPacket.Entry(
                                    entry.profileId(),
                                    entry.profile(),
                                    entry.listed(),
                                    entry.latency(),
                                    entry.gameMode(),
                                    formattedDisplayName,  // 使用新的显示名称
                                    entry.chatSession()
                            );

                            modifiedEntries.add(newEntry);
                            modified = true;
                            continue;
                        }
                    }
                    // 如果没有修改，保持原 Entry
                    modifiedEntries.add(entry);
                }

                if (modified) {
                    // 替换整个 entries 列表
                    entriesField.set(this, modifiedEntries);

                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}
