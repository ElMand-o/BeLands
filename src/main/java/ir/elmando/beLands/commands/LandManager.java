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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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

        claim.setPermission(target.getUniqueId().toString(), ClaimPermission.Build);
        griefPrevention.dataStore.saveClaim(claim);
        return true;
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

        claim.setPermission(target.getUniqueId().toString(), null);
        griefPrevention.dataStore.saveClaim(claim);
        return true;
    }

    public Vector<Claim> getClaims(Player player) {
        return griefPrevention.dataStore.getPlayerData(player.getUniqueId()).getClaims();
    }

    public Claim getClaim(int claim_id) {
        return griefPrevention.dataStore.getClaim(claim_id);
    }

    public Claim getClaim(Location at) {
        return griefPrevention.dataStore.getClaimAt(at, true, null);
    }

    public void teleport(int claim_id, Player player) {
        Claim claim = griefPrevention.dataStore.getClaim(claim_id);
        if (claim == null) return;

        double midX = (claim.getGreaterBoundaryCorner().getX() + claim.getLesserBoundaryCorner().getX()) / 2.0;
        double midY = claim.getGreaterBoundaryCorner().getY() + 1;
        double midZ = (claim.getGreaterBoundaryCorner().getZ() + claim.getLesserBoundaryCorner().getZ()) / 2.0;

        Location landLocation = new Location(claim.getGreaterBoundaryCorner().getWorld(), midX, midY, midZ);
        player.teleport(landLocation);
    }
}
