package ir.elmando.beLands.commands;

import com.griefprevention.visualization.BoundaryVisualization;
import com.griefprevention.visualization.VisualizationType;
import ir.elmando.beLands.ConfigManager;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.CreateClaimResult;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.util.BoundingBox;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class LandManager {
    ConfigManager config;
    Economy economy;
    GriefPrevention griefPrevention;

    public LandManager(ConfigManager config, Economy economy, GriefPrevention griefPrevention) {
        this.config = config;
        this.economy = economy;
        this.griefPrevention = griefPrevention;
    }

    public void showClaim(Claim claim, Player player, VisualizationType type) {
        BoundaryVisualization.visualizeClaim(player, claim, type);
    }

    void showClaim(int claim_id, Player player, VisualizationType type) {
        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        BoundaryVisualization.visualizeClaim(player, claim, type);
    }

    public void showArea(Location pos1, Location pos2, Player player, VisualizationType type) {
        BoundaryVisualization.visualizeArea(player, new BoundingBox(pos1, pos2), type);
    }

    public int getLandBuyPrice(Location pos1, Location pos2) {
        if (!pos1.getWorld().equals(pos2.getWorld())) return Integer.MAX_VALUE;

        int deltaX = Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1;
        int deltaZ = Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1;

        return deltaX * deltaZ * config.getInt("economy.buy_price");
    }

    public int getLandSellPrice(Location pos1, Location pos2) {
        int deltaX = Math.abs(pos1.getBlockX() - pos2.getBlockX());
        int deltaZ = Math.abs(pos1.getBlockZ() - pos2.getBlockZ());

        return deltaX * deltaZ * config.getInt("economy.buy_price");
    }

    public int getLandSellPrice(Claim claim) {
        int deltaX = Math.abs(claim.getLesserBoundaryCorner().getBlockX() - claim.getGreaterBoundaryCorner().getBlockX());
        int deltaZ = Math.abs(claim.getLesserBoundaryCorner().getBlockZ() - claim.getGreaterBoundaryCorner().getBlockZ());

        return deltaX * deltaZ * config.getInt("economy.buy_price");
    }

    public CreateClaimResult createClaim(Location pos1, Location pos2, Player player) {
        int claim_id = 0;
        while (claim_id == 0) {
            claim_id = 1 + new Random().nextInt(100000);
            if (griefPrevention.dataStore.getClaim(claim_id) == null) break;
        }
        CreateClaimResult claim = griefPrevention.dataStore.createClaim(pos1.getWorld(), pos1.getBlockX(), pos2.getBlockX(), pos1.getBlockY(), pos2.getBlockY(), pos1.getBlockZ(), pos2.getBlockZ(), player.getUniqueId(), null, (long) claim_id, player);
        return claim;
    }

    public boolean transferClaim(int claim_id, Player owner, String target_username) {
        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        OfflinePlayer target = Bukkit.getOfflinePlayer(target_username);
        if (claim == null || !target.isConnected() || !claim.getOwnerID().equals(owner.getUniqueId())) return false;
        claim.ownerID = target.getUniqueId();
        griefPrevention.dataStore.saveClaim(claim);
        return true;
    }

    public void removeClaim(Claim claim) {
        griefPrevention.dataStore.deleteClaim(claim);
    }

    public boolean trustUser(int claim_id, Player owner, String target_username) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(target_username);
        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        if (claim == null || !claim.getOwnerID().equals(owner.getUniqueId())) return false;
        claim.managers.add(target.getUniqueId().toString());

        claim.setPermission(target.getUniqueId().toString(), ClaimPermission.Build);
        griefPrevention.dataStore.saveClaim(claim);
        return true;
    }

    public Collection<Claim> getAllClaims() {
        return griefPrevention.dataStore.getClaims();
    }

    public boolean isTrusted(int claim_id, Player owner, String target_username) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(target_username);
        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        if (claim == null || !claim.getOwnerID().equals(owner.getUniqueId())) return false;

        return claim.getPermission(target.getUniqueId().toString()) != null;
    }

    public boolean untrustUser(int claim_id, Player owner, String target_username) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(target_username);
        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        if (claim == null || !claim.getOwnerID().equals(owner.getUniqueId())) return false;
        claim.managers.remove(target.getUniqueId().toString());

        claim.setPermission(target.getUniqueId().toString(), null);
        griefPrevention.dataStore.saveClaim(claim);
        return true;
    }

    public Vector<Claim> getClaims(Player player) {
        return griefPrevention.dataStore.getPlayerData(player.getUniqueId()).getClaims();
    }

//    public List<String> getAllTrustedUsers(int claim_id) {
//        griefPrevention.dataStore.get
//    }

    public Claim getClaim(int claim_id) {
        return griefPrevention.dataStore.getClaim(claim_id);
    }

    public Claim getClaim(Location at) {
        return griefPrevention.dataStore.getClaimAt(at, true, null);
    }

    public void teleport(int claim_id, Player player) {
        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        if (claim == null) return;

        // Calculate center coordinates
        double midX = (claim.getGreaterBoundaryCorner().getX() + claim.getLesserBoundaryCorner().getX()) / 2.0;
        double midZ = (claim.getGreaterBoundaryCorner().getZ() + claim.getLesserBoundaryCorner().getZ()) / 2.0;

        World world = claim.getGreaterBoundaryCorner().getWorld();
        Location safeLocation = findSafeLocation(world, midX, midZ);

        if (safeLocation != null) {
            player.teleport(safeLocation);
        } else {
            player.sendMessage("Could not find a safe teleport location in this claim.");
        }
    }

    private Location findSafeLocation(World world, double x, double z) {
        // Get the highest solid block at this location
        int surfaceY = world.getHighestBlockYAt((int) x, (int) z);

        // Check a few blocks around the surface level to find the best spot
        for (int yOffset = 0; yOffset <= 3; yOffset++) {
            int checkY = surfaceY + yOffset;

            // Make sure we don't go above build height
            if (checkY >= world.getMaxHeight() - 1) break;

            Location checkLoc = new Location(world, x, checkY + 1, z);

            if (isSafeLocation(checkLoc)) {
                return checkLoc;
            }
        }

        // If surface level doesn't work, try a spiral search nearby
        for (int radius = 1; radius <= 5; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;

                    double newX = x + dx;
                    double newZ = z + dz;

                    int newSurfaceY = world.getHighestBlockYAt((int) newX, (int) newZ);
                    Location checkLoc = new Location(world, newX, newSurfaceY + 1, newZ);

                    if (isSafeLocation(checkLoc)) {
                        return checkLoc;
                    }
                }
            }
        }

        return null;
    }

    private boolean isSafeLocation(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        // Check if the block below is solid (to stand on)
        Block blockBelow = world.getBlockAt(x, y - 1, z);
        if (!blockBelow.getType().isSolid()) {
            return false;
        }

        // Check if the blocks at player level and above are passable
        Block blockAtFeet = world.getBlockAt(x, y, z);
        Block blockAtHead = world.getBlockAt(x, y + 1, z);

        if (!isPassable(blockAtFeet) || !isPassable(blockAtHead)) {
            return false;
        }

        // Avoid dangerous blocks
        if (isDangerous(blockBelow) || isDangerous(blockAtFeet) || isDangerous(blockAtHead)) {
            return false;
        }

        return true;
    }

    private boolean isPassable(Block block) {
        Material type = block.getType();
        return type.isAir() ||
                !type.isSolid() ||
                type == Material.WATER ||
                type == Material.SHORT_GRASS ||
                type == Material.TALL_GRASS ||
                type == Material.FERN ||
                type == Material.LARGE_FERN ||
                type == Material.SNOW;
    }

    private boolean isDangerous(Block block) {
        Material type = block.getType();
        return type == Material.LAVA ||
                type == Material.FIRE ||
                type == Material.MAGMA_BLOCK ||
                type == Material.CACTUS;
    }
}
