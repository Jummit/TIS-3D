package li.cil.tis3d.common.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import li.cil.tis3d.api.SerialAPI;
import li.cil.tis3d.api.serial.SerialInterfaceProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Registry for serial interface providers.
 */
public final class SerialAPIImpl implements SerialAPI {
    public static final SerialAPIImpl INSTANCE = new SerialAPIImpl();

    // --------------------------------------------------------------------- //

    private final List<SerialInterfaceProvider> providers = new ArrayList<>();

    // --------------------------------------------------------------------- //

    @Override
    public void addProvider(final SerialInterfaceProvider provider) {
        if (!providers.contains(provider)) {
            providers.add(provider);
        }
    }

    @Override
    @Nullable
    public SerialInterfaceProvider getProviderFor(final World world, final BlockPos position, final Direction side) {
        for (final SerialInterfaceProvider provider : providers) {
            if (provider.worksWith(world, position, side)) {
                return provider;
            }
        }
        return null;
    }

    // --------------------------------------------------------------------- //

    private SerialAPIImpl() {
    }
}
