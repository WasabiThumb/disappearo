package codes.wasabi.disappearo.service;

import codes.wasabi.disappearo.Disappearo;
import codes.wasabi.disappearo.api.InvisibleFrames;
import codes.wasabi.disappearo.api.Wands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

public class Events implements Listener {

    @EventHandler
    public void onPlace(HangingPlaceEvent event) {
        Hanging h = event.getEntity();
        if (h instanceof ItemFrame) {
            Player ply = event.getPlayer();
            if (ply != null) {
                ItemStack is = ply.getActiveItem();
                boolean replace = (is == null);
                if (!replace) replace = is.getType().equals(Material.AIR);
                if (replace) is = ply.getInventory().getItemInMainHand();
                if (InvisibleFrames.isInvisible(is)) {
                    InvisibleFrames.setInvisible(h, true);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(HangingBreakEvent event) {
        Hanging h = event.getEntity();
        if (h instanceof ItemFrame) {
            if (InvisibleFrames.isInvisible(h)) {
                event.setCancelled(true);
                h.remove();
                ItemStack is = new ItemStack((h instanceof GlowItemFrame ? Material.GLOW_ITEM_FRAME : Material.ITEM_FRAME), 1);
                Location loc = h.getLocation();
                World w = loc.getWorld();
                w.dropItemNaturally(loc, InvisibleFrames.setInvisible(is, true));
                w.playSound(loc, Sound.ENTITY_ITEM_FRAME_BREAK, SoundCategory.NEUTRAL, 1f, 1f);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBreak(HangingBreakByEntityEvent event) {
        Hanging h = event.getEntity();
        if (h instanceof ItemFrame frame) {
            Entity r = event.getRemover();
            if (r != null) {
                if (r instanceof Player ply) {
                    ItemStack is = ply.getActiveItem();
                    boolean replace = (is == null);
                    if (!replace) replace = is.getType().equals(Material.AIR);
                    if (replace) is = ply.getInventory().getItemInMainHand();
                    if (Wands.isWand(is)) {
                        event.setCancelled(true);
                        genericWand(frame, ply);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();
        if (damager instanceof Player ply) {
            if (victim instanceof ItemFrame frame) {
                ItemStack is = ply.getActiveItem();
                boolean replace = (is == null);
                if (!replace) replace = is.getType().equals(Material.AIR);
                if (replace) is = ply.getInventory().getItemInMainHand();
                if (Wands.isWand(is)) {
                    event.setCancelled(true);
                    genericWand(frame, ply);
                }
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!Disappearo.config.denyWandInCrafting) return;
        ItemStack[] items = event.getInventory().getMatrix();
        for (ItemStack is : items) {
            if (Wands.isWand(is)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onMoveItem(InventoryMoveItemEvent event) {
        if (!Disappearo.config.preventDuplicateWands) return;
        ItemStack target = event.getItem();
        if (!Wands.isWand(target)) return;
        for (ItemStack is : event.getDestination().getContents()) {
            if (Wands.isWand(is)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!Disappearo.config.preventDuplicateWands) return;
        Inventory iv = event.getInventory();
        ItemStack cursor = event.getCursor();
        int slot = event.getRawSlot();
        if (!Wands.isWand(cursor)) return;
        ItemStack[] conts = iv.getContents();
        for (int i=0; i < conts.length; i++) {
            if (i == slot) continue;
            ItemStack is = conts[i];
            if (Wands.isWand(is)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!Disappearo.config.preventDuplicateWands) return;
        Inventory iv = event.getInventory();
        Map<Integer, ItemStack> items = event.getNewItems();
        if (items.values().stream().noneMatch(Wands::isWand)) return;
        Set<Integer> slots = items.keySet();
        ItemStack[] conts = iv.getContents();
        for (int i=0; i < conts.length; i++) {
            if (slots.contains(i)) continue;
            ItemStack is = conts[i];
            if (Wands.isWand(is)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPickup(InventoryPickupItemEvent event) {
        if (!Disappearo.config.preventDuplicateWands) return;
        ItemStack target = event.getItem().getItemStack();
        if (!Wands.isWand(target)) return;
        for (ItemStack is : event.getInventory().getContents()) {
            if (Wands.isWand(is)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (!Disappearo.config.preventDuplicateWands) return;
        Entity ent = event.getEntity();
        if (ent instanceof InventoryHolder ih) {
            Item item = event.getItem();
            if (!Wands.isWand(item.getItemStack())) return;
            Inventory inv = ih.getInventory();
            for (ItemStack is : inv.getContents()) {
                if (Wands.isWand(is)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    private void genericWand(ItemFrame frame, Player ply) {
        if (Disappearo.config.enforceWandUsagePerms) {
            if (!(ply.hasPermission("dp.wand") || ply.isOp())) {
                ply.sendMessage(Component.text("* You don't have permissions to use the wand!").color(NamedTextColor.RED));
                return;
            }
        }
        boolean isInvisible = InvisibleFrames.isInvisible(frame);
        InvisibleFrames.setInvisible(frame, !isInvisible);
        Location loc = frame.getLocation();
        Color color = (isInvisible ? Color.GRAY : Color.AQUA);
        Sound sound = (isInvisible ? Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF : Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON);
        Particle.DustOptions opts = new Particle.DustOptions(color, 1);
        ply.spawnParticle(Particle.REDSTONE, loc, 5, 0.2, 0.2, 0.2, 1, opts);
        ply.playSound(ply.getLocation(), sound, SoundCategory.BLOCKS, 1f, 1f);
    }

    @EventHandler
    public void onWand(PlayerInteractEntityEvent event) {
        Player ply = event.getPlayer();
        EquipmentSlot es = event.getHand();
        ItemStack is = ply.getInventory().getItem(es);
        if (!Wands.isWand(is)) return;
        event.setCancelled(true);
        Entity ent = event.getRightClicked();
        if (ent instanceof ItemFrame frame) {
            genericWand(frame, ply);
        }
    }

}
