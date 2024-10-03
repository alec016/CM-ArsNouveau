package es.degrassi.custommachineryars.client.screen.creation.component.builder;

import es.degrassi.custommachineryars.Registration;
import es.degrassi.custommachineryars.components.item.SourceItemMachineComponent;
import fr.frinn.custommachinery.api.component.MachineComponentType;
import fr.frinn.custommachinery.api.utils.Filter;
import fr.frinn.custommachinery.client.screen.BaseScreen;
import fr.frinn.custommachinery.client.screen.creation.MachineEditScreen;
import fr.frinn.custommachinery.client.screen.creation.component.builder.ItemComponentBuilder;
import fr.frinn.custommachinery.client.screen.popup.PopupScreen;
import fr.frinn.custommachinery.common.component.item.ItemMachineComponent;
import fr.frinn.custommachinery.common.component.item.ItemMachineComponent.Template;
import java.util.Optional;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class ItemSourceComponentBuilder extends ItemComponentBuilder {

    @Override
    public MachineComponentType<ItemMachineComponent> type() {
        return Registration.ITEM_SOURCE_MACHINE_COMPONENT.get();
    }

    @Override
    public PopupScreen makePopup(MachineEditScreen parent, @Nullable ItemMachineComponent.Template template, Consumer<Template> onFinish) {
        return new ItemSourceComponentBuilderPopup(parent, template, onFinish);
    }

    public static class ItemSourceComponentBuilderPopup extends ItemComponentBuilderPopup {

        public ItemSourceComponentBuilderPopup(BaseScreen parent, @Nullable Template template, Consumer<Template> onFinish) {
            super(parent, template, onFinish);
        }

        @Override
        public Template makeTemplate() {
            return new SourceItemMachineComponent.Template(this.id.getValue(), this.mode.getValue(), this.capacity.intValue(), Optional.of(this.maxInput.intValue()), Optional.of(this.maxOutput.intValue()), this.baseTemplate().map(template -> template.filter).orElse(Filter.empty()), Optional.of(this.mode.getValue().getBaseConfig()), this.locked.selected());
        }
    }
}
