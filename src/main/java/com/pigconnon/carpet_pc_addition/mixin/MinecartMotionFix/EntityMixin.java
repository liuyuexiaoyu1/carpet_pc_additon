

package com.pigconnon.carpet_pc_addition.mixin.MinecartMotionFix;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.pigconnon.carpet_pc_addition.CPCASettings;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {


    @Shadow @Nullable public abstract Entity teleportTo(TeleportTarget teleportTarget);

    @Shadow public abstract boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, boolean resetCamera);

    @Inject(method = "getPassengerTeleportTarget",at = @At("RETURN"), cancellable = true)
    private void getPassengerTeleportTarget(TeleportTarget teleportTarget, Entity passenger,
                                            CallbackInfoReturnable<TeleportTarget> cir){
        if(CPCASettings.MinecartMotionFix) {
            cir.setReturnValue(new TeleportTarget(teleportTarget.world(), teleportTarget.position(), teleportTarget.velocity(),
                    0, 0, teleportTarget.postTeleportTransition()));
        }
    }
}
