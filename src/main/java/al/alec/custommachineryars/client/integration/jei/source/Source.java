package al.alec.custommachineryars.client.integration.jei.source;

public class Source {
  private final int amount;
  private final boolean isPerTick;

  public Source(int amount, boolean isPerTick) {
    this.amount = amount;
    this.isPerTick = isPerTick;
  }

  public int getAmount() {
    return this.amount;
  }

  public boolean isPerTick() {
    return this.isPerTick;
  }
}
