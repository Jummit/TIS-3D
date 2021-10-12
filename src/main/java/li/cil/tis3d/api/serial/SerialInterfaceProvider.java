package li.cil.tis3d.api.serial;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Creates a serial interface instance for a specified block position.
 * <p>
 * Implementation will typically check for the presence of a specific tile
 * entity and create a wrapper for the tile entity.
 */
public interface SerialInterfaceProvider {
    /**
     * Checks whether the provider supports the specified block position.
     *
     * @param world    the world containing the position.
     * @param position the position in question.
     * @param side     the side of the position in question.
     * @return whether a {@link SerialInterface} can be provided for the position.
     */
    boolean worksWith(final World world, final BlockPos position, final Direction side);

    /**
     * Creates a new serial interface instance for the specified position.
     *
     * @param world    the world containing the position.
     * @param position the position in question.
     * @param side     the side of the position in question.
     * @return the interface to use for communicating with the position.
     */
    @Nullable
    SerialInterface interfaceFor(final World world, final BlockPos position, final Direction side);

    /**
     * Tests whether the specified serial interface is still valid for the specified position.
     * <p>
     * This is used by the serial port module to determine whether a new serial
     * interface instance has to be constructed for the specified position, or
     * whether an existing one can be re-used. This avoids resetting the serial
     * interfaces state in case of an adjacent block change.
     * <p>
     * Generally this this should return <tt>false</tt> if the interface is not
     * once provided by this provider, or more generally, if it is the same kind
     * of serial interface that would be created via {@link #interfaceFor(World, BlockPos, Direction)},
     * otherwise this should return <tt>true</tt>.
     *
     * @param world           the world containing the position.
     * @param position        the position in question.
     * @param side            the side of the position in question.
     * @param serialInterface the interface to validate.
     * @return <tt>true</tt> if the interface is still valid, <tt>false</tt> if a new one should be created.
     */
    boolean isValid(final World world, final BlockPos position, final Direction side, final SerialInterface serialInterface);
}
