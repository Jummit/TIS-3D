package li.cil.tis3d.client.init;

import li.cil.tis3d.client.render.block.entity.CasingBlockEntityRenderer;
import li.cil.tis3d.client.render.block.entity.ControllerBlockEntityRenderer;
import li.cil.tis3d.client.render.entity.InvisibleEntityRenderer;
import li.cil.tis3d.common.block.CasingBlock;
import li.cil.tis3d.common.block.entity.CasingBlockEntity;
import li.cil.tis3d.common.block.entity.ControllerBlockEntity;
import li.cil.tis3d.common.entity.InfraredPacketEntity;
import li.cil.tis3d.common.init.Entities;
import li.cil.tis3d.common.module.DisplayModule;
import li.cil.tis3d.common.network.Network;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class BootstrapClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register network handler.
        Network.INSTANCE.initClient();

        // Register event handlers.
        ClientTickCallback.EVENT.register(client -> DisplayModule.LeakDetector.tick());
        ClientTickCallback.EVENT.register(client -> Network.INSTANCE.clientTick());
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((spriteAtlasTexture, registry) -> Textures.registerSprites(registry));
        ClientPickBlockGatherCallback.EVENT.register(BootstrapClient::handlePickBlock);

        // Register entity renderers
        EntityRendererRegistry.INSTANCE.register(Entities.INFRARED_PACKET, (dispatcher, context) -> new InvisibleEntityRenderer<InfraredPacketEntity>(dispatcher));

        // Set up block entity renderer for dynamic module content.
        BlockEntityRendererRegistry.INSTANCE.register(CasingBlockEntity.TYPE, CasingBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ControllerBlockEntity.TYPE, ControllerBlockEntityRenderer::new);

    }

    private static ItemStack handlePickBlock(final PlayerEntity player, final HitResult hitResult) {
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return ItemStack.EMPTY;
        }

        assert hitResult instanceof BlockHitResult;
        final BlockHitResult blockHitResult = (BlockHitResult)hitResult;
        final BlockPos blockPos = blockHitResult.getBlockPos();
        final BlockState blockState = player.getEntityWorld().getBlockState(blockPos);
        final Block block = blockState.getBlock();
        if (block instanceof CasingBlock) {
            return (((CasingBlock)block).getPickStack(player.getEntityWorld(), blockPos, blockHitResult.getSide(), blockState));
        }

        return ItemStack.EMPTY;
    }
}
