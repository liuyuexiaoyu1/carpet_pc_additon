/*
 * This file is part of the Carpet REMS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025 A Minecraft Server and contributors
 *
 * Carpet REMS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet REMS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet REMS Addition. If not, see <https://www.gnu.org/licenses/>.
 */

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
import rems.carpet.REMSSettings;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {


    @Shadow @Nullable public abstract Entity teleportTo(TeleportTarget teleportTarget);

    @Shadow public abstract boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, boolean resetCamera);

    @Inject(method = "getPassengerTeleportTarget",at = @At("RETURN"), cancellable = true)
    private void getPassengerTeleportTarget(TeleportTarget teleportTarget, Entity passenger,
                                            CallbackInfoReturnable<TeleportTarget> cir){
        if(REMSSettings.MomentumFix) {
            cir.setReturnValue(new TeleportTarget(teleportTarget.world(), teleportTarget.position(), teleportTarget.velocity(),
                    0, 0, teleportTarget.postTeleportTransition()));
        }
    }

//    @Inject(method = "teleportTo",at = @At("HEAD"))
//    private void m(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> cir){
//        LOGGER.info("{} {}",this,teleportTarget);
//    }
}
