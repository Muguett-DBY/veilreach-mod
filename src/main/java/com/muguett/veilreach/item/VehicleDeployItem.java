package com.muguett.veilreach.item;

import com.muguett.veilreach.entity.vehicle.AbstractVehicleEntity;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class VehicleDeployItem extends Item {
    @FunctionalInterface
    public interface VehicleFactory {
        AbstractVehicleEntity create(ServerWorld world);
    }

    private final VehicleFactory vehicleFactory;
    private final double verticalOffset;
    private final List<String> tooltipKeys;

    public VehicleDeployItem(Settings settings, VehicleFactory vehicleFactory, double verticalOffset, String... tooltipKeys) {
        super(settings.maxCount(1));
        this.vehicleFactory = vehicleFactory;
        this.verticalOffset = verticalOffset;
        this.tooltipKeys = List.of(tooltipKeys);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Vec3d spawnPos = context.getBlockPos().toCenterPos().add(0.0D, verticalOffset, 0.0D);
        if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }
        return spawnVehicle(context.getStack(), serverWorld, spawnPos, context.getPlayerYaw(), context.getPlayer());
    }

    public ActionResult spawnVehicle(ItemStack stack, ServerWorld world, Vec3d spawnPos, float yaw, PlayerEntity player) {
        AbstractVehicleEntity vehicle = vehicleFactory.create(world);
        vehicle.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, yaw, 0.0F);

        if (!world.getOtherEntities(vehicle, vehicle.getBoundingBox().expand(-0.1D)).isEmpty()) {
            return ActionResult.FAIL;
        }

        world.spawnEntity(vehicle);
        if (player != null && !player.isCreative()) {
            stack.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        for (String tooltipKey : tooltipKeys) {
            textConsumer.accept(Text.translatable(tooltipKey));
        }
    }
}
