package com.lucab.let_me_drink.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record SizedIngredient(Ingredient ingredient, int count) {
    public static final Codec<SizedIngredient> CODEC = Codec.of(
            SizedIngredient::encode,
            SizedIngredient::decode);

    private static <T> DataResult<T> encode(SizedIngredient input, DynamicOps<T> ops, T prefix) {
        // Encode as flattened: Ingredient fields + count
        DataResult<T> ing = Ingredient.CODEC_NONEMPTY.encode(input.ingredient, ops, prefix);
        if (input.count > 1) {
            return ing.flatMap(map -> ops.mergeToMap(map, ops.createString("count"), ops.createInt(input.count)));
        }
        return ing;
    }

    private static <T> DataResult<Pair<SizedIngredient, T>> decode(DynamicOps<T> ops, T input) {
        // Look for count first (it's simple)
        int count = 1;
        DataResult<MapLike<T>> mapRes = ops.getMap(input);
        if (mapRes.result().isPresent()) {
            T countVal = mapRes.result().get().get("count");
            if (countVal != null) {
                DataResult<Number> num = ops.getNumberValue(countVal);
                if (num.result().isPresent()) {
                    count = num.result().get().intValue();
                }
            }
        }
        final int finalCount = count;

        // Try flattened parse first: Ingredient from input
        DataResult<Pair<Ingredient, T>> ingResult = Ingredient.CODEC_NONEMPTY.decode(ops, input);

        if (ingResult.result().isPresent()) {
            return DataResult.success(Pair.of(new SizedIngredient(ingResult.result().get().getFirst(), finalCount),
                    ingResult.result().get().getSecond()));
        }

        // Fallback: Try nested format {"ingredient": ..., "count": ...}
        return RecordCodecBuilder.<SizedIngredient>create(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(SizedIngredient::count)).apply(instance,
                        SizedIngredient::new))
                .decode(ops, input);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            SizedIngredient::ingredient,
            ByteBufCodecs.INT,
            SizedIngredient::count,
            SizedIngredient::new);

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }
}
