package es.degrassi.custommachineryars.client.integration.jei.source;

public record Source(int amount, boolean isPerTick) {
  public int getAmount() {
    return this.amount;
  }

  public boolean isPerTick() {
    return this.isPerTick;
  }
}
