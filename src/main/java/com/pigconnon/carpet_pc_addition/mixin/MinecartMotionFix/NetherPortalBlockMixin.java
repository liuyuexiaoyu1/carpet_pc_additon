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

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.pigconnon.carpet_pc_addition.CPCASettings;

import java.util.Set;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    @Inject(method = "getExitPortalTarget(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/BlockLocating$Rectangle;Lnet/minecraft/util/math/Direction$Axis;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;Lnet/minecraft/world/TeleportTarget$PostDimensionTransition;)Lnet/minecraft/world/TeleportTarget;",at = @At("RETURN"),cancellable = true)
    private static void getExitPortalTarget(ServerWorld world, BlockLocating.Rectangle exitPortalRectangle,
                                            Direction.Axis axis, Vec3d positionInPortal, Entity entity,
                                            TeleportTarget.PostDimensionTransition postDimensionTransition,
                                            CallbackInfoReturnable<TeleportTarget> cir) {
        if (CPCASettings.MinecartMotionFix) {
            BlockPos blockPos = exitPortalRectangle.lowerLeft;
            BlockState blockState = world.getBlockState(blockPos);
            Direction.Axis axis2 = (Direction.Axis) blockState.getOrEmpty(Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
            double d = (double) exitPortalRectangle.width;
            double e = (double) exitPortalRectangle.height;
            EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
            int i = axis == axis2 ? 0 : 90;
            Vec3d vec3d3 = axis == axis2 ? entity.getVelocity() : new Vec3d(entity.getVelocity().z, entity.getVelocity().y,
                    -entity.getVelocity().x);
            double f = (double) entityDimensions.width() / 2.0 + (d - (double) entityDimensions.width()) * positionInPortal.getX();
            double g = (e - (double) entityDimensions.height()) * positionInPortal.getY();
            double h = 0.5 + positionInPortal.getZ();
            boolean bl = axis2 == Direction.Axis.X;
            Vec3d vec3d = new Vec3d((double) blockPos.getX() + (bl ? f : h), (double) blockPos.getY() + g, (double) blockPos.getZ() + (bl ? h : f));
            Vec3d vec3d2 = NetherPortal.findOpenPosition(vec3d, world, entity, entityDimensions);
            cir.setReturnValue(new TeleportTarget(world, vec3d2, vec3d3, 0, 0.0F,
                    PositionFlag.combine(new Set[]{PositionFlag.DELTA, PositionFlag.ROT}), postDimensionTransition));
        }

    }
}