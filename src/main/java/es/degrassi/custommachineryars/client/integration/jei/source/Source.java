package es.degrassi.custommachineryars.client.integration.jei.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Source(int amount, boolean isPerTick) {
  public static final Codec<Source> CODEC = RecordCodecBuilder.create(sourceInstance ->
    sourceInstance.group(
      Codec.INT.fieldOf("amount").forGetter(Source::amount),
      Codec.BOOL.fieldOf("perTick").forGetter(Source::isPerTick)
    ).apply(sourceInstance, Source::new));

  public int getAmount() {
    return this.amount;
  }

  public boolean isPerTick() {
    return this.isPerTick;
  }
}
