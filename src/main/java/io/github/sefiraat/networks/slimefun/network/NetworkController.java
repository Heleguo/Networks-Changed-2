package io.github.sefiraat.networks.slimefun.network;

import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NetworkController extends NetworkObject {

    private static final String CRAYON = "crayon";
    private static final Map<Location, NetworkRoot> NETWORKS = new HashMap<>();
    private static final Set<Location> CRAYONS = new HashSet<>();

    protected final Map<Location, Boolean> firstTickMap = new HashMap<>();

    public NetworkController(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.CONTROLLER);

        addItemHandler(
            new BlockTicker() {
                @Override
                public boolean isSynchronized() {
                    return false;
                }

                @Override
                public void tick(Block block, SlimefunItem item, Config data) {

                    if (!firstTickMap.containsKey(block.getLocation())) {
                        onFirstTick(block, data);
                        firstTickMap.put(block.getLocation(), true);
                    }

                    addToRegistry(block);
                    NetworkRoot networkRoot = new NetworkRoot(block.getLocation(), NodeType.CONTROLLER);
                    networkRoot.addAllChildren();

                    boolean crayon = CRAYONS.contains(block.getLocation());
                    if (crayon) {
                        networkRoot.setDisplayParticles(true);
                    }

                    NETWORKS.put(block.getLocation(), networkRoot);
                }
            }
        );
    }

    private void onFirstTick(@Nonnull Block block, @Nonnull Config data) {
        final String crayon = data.getString(CRAYON);
        if (Boolean.parseBoolean(crayon)) {
            CRAYONS.add(block.getLocation());
        }
    }

    public static Map<Location, NetworkRoot> getNetworks() {
        return NETWORKS;
    }

    public static Set<Location> getCrayons() {
        return CRAYONS;
    }

    public static void addCrayon(@Nonnull Location location) {
        BlockStorage.addBlockInfo(location, CRAYON, String.valueOf(true));
        CRAYONS.add(location);
    }

    public static void removeCrayon(@Nonnull Location location) {
        BlockStorage.addBlockInfo(location, CRAYON, null);
        CRAYONS.remove(location);
    }

    public static boolean hasCrayon(@Nonnull Location location) {
        return CRAYONS.contains(location);
    }
}
