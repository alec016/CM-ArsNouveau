package al.alec.custommachineryars.mixin.client;

import java.util.Set;
import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelBakery.class)
public interface ModelBakeryAccessor {
  @Accessor("UNREFERENCED_TEXTURES")
  static Set<Material> getMaterials() {
    throw new IllegalStateException();
  }
}
