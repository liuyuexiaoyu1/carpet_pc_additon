package com.pigconnon.carpet_pc_addition.mixin.nickname;

import com.pigconnon.carpet_pc_addition.NicknameManager;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.UUID;

@Mixin(PlayerListS2CPacket.class)
public class PlayerListS2CPacketMixin {


    @Redirect(
            method = "<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"
            )
    )
    private List<PlayerListS2CPacket.Entry> modifyEntries(java.util.stream.Stream<PlayerListS2CPacket.Entry> stream) {
        return stream.map(entry -> {
            if (entry.profile() != null) {
                UUID playerUUID = entry.profile().getId();
                String originalName = entry.profile().getName();

                Text formattedDisplayName = NicknameManager.getInstance().getFormattedTabName(playerUUID, originalName);
                if (formattedDisplayName != null) {
                    return new PlayerListS2CPacket.Entry(
                            entry.profileId(),
                            entry.profile(),
                            entry.listed(),
                            entry.latency(),
                            entry.gameMode(),
                            formattedDisplayName,
                            entry.chatSession()
                    );
                }
            }
            return entry;
        }).toList();
    }
}
