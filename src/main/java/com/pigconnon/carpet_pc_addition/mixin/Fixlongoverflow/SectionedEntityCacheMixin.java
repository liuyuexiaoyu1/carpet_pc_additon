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

package com.pigconnon.carpet_pc_addition.mixin.Fixlongoverflow;

import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.world.entity.SectionedEntityCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.pigconnon.carpet_pc_addition.CPCASettings;

@Mixin(SectionedEntityCache.class)
public class SectionedEntityCacheMixin
{
    @Redirect
            (
                    method = "forEachInBox",
                    at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/longs/LongSortedSet;subSet(JJ)Lit/unimi/dsi/fastutil/longs/LongSortedSet;", remap = false)
            )
    private LongSortedSet forEachInBoxMixin(LongSortedSet instance, long l, long r)
    {
        if (CPCASettings.Fixlongoverflow)
            if(l > r && (r - 1L) == Long.MAX_VALUE)
            {
                return instance.tailSet(l);
            }

        return instance.subSet(l, r);
    }


}