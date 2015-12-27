package li.cil.tis3d.common.block;

import li.cil.tis3d.common.init.Items;
import li.cil.tis3d.common.tile.TileEntityController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Block for the controller driving the casings.
 */
public final class BlockController extends Block {
    public BlockController() {
        super(Material.iron);
    }

    // --------------------------------------------------------------------- //
    // Common

    @Override
    public boolean isSideSolid(final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
        // Allow levers to be placed on us (wouldn't work because of isFullCube = false otherwise).
        return true;
    }

    @Override
    public boolean isFullCube() {
        // Prevent fences from visually connecting.
        return false;
    }

    @Override
    public boolean hasTileEntity(final IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(final World world, final IBlockState state) {
        return new TileEntityController();
    }

    @Override
    public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        final ItemStack stack = player.getHeldItem();
        if (stack != null) {
            final Item item = stack.getItem();
            if (item == net.minecraft.init.Items.book) {
                if (!world.isRemote) {
                    if (!player.capabilities.isCreativeMode) {
                        stack.splitStack(1);
                    }
                    final ItemStack bookManual = new ItemStack(Items.bookManual);
                    if (player.inventory.addItemStackToInventory(bookManual)) {
                        player.inventoryContainer.detectAndSendChanges();
                    }
                    if (bookManual.stackSize > 0) {
                        player.dropItem(bookManual, false, false);
                    }
                }
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
    }

    // --------------------------------------------------------------------- //
    // Redstone

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(final World world, final BlockPos pos) {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityController) {
            final TileEntityController controller = (TileEntityController) tileEntity;
            return controller.getState() == TileEntityController.ControllerState.READY ? 15 : 0;
        }
        return 0;
    }

    // --------------------------------------------------------------------- //
    // Networking

    @Override
    public void onNeighborBlockChange(final World world, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityController) {
            final TileEntityController controller = (TileEntityController) tileEntity;
            controller.checkNeighbors();
        }
        super.onNeighborBlockChange(world, pos, state, neighborBlock);
    }
}
