

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