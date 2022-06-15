package codes.wasabi.disappearo.service;

import codes.wasabi.disappearo.Disappearo;
import codes.wasabi.disappearo.api.InvisibleFrames;
import codes.wasabi.disappearo.api.Wands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String subCommand = "help";
        if (args.length > 0) {
            subCommand = args[0];
        }
        switch (subCommand) {
            case "help" -> helpCommand(sender);
            case "info" -> infoCommand(sender);
            case "change" -> changeCommand(sender);
            case "wand" -> wandCommmand(sender);
            default -> sender.sendMessage(Component.text("* Unknown subcommand \"" + subCommand + "\"").color(NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length <= 1) return List.of("wand", "change", "info", "help");
        return null;
    }

    private void helpCommand(@NotNull CommandSender sender) {
        sender.sendMessage(
                Component.empty()
                        .append(Component.text("= Disappearo Help =").color(NamedTextColor.GOLD))
                        .append(Component.newline())
                        .append(Component.text("help").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Displays a list of commands for Disappearo").color(NamedTextColor.AQUA))
                        .append(Component.newline())
                        .append(Component.text("info").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Displays basic info about Disappearo").color(NamedTextColor.AQUA))
                        .append(Component.newline())
                        .append(Component.text("change").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Turn a stack of item frames invisible or visible").color(NamedTextColor.AQUA))
                        .append(Component.newline())
                        .append(Component.text("wand").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(" :: ").color(NamedTextColor.GOLD))
                        .append(Component.text("Gives you a wand").color(NamedTextColor.AQUA))
        );
    }

    private void infoCommand(@NotNull CommandSender sender) {
        sender.sendMessage(
                Component.empty()
                        .append(Component.text("= Disappearo =").color(NamedTextColor.GOLD))
                        .append(Component.newline())
                        .append(Component.text("Version ").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(Disappearo.instance.getDescription().getVersion()).color(NamedTextColor.AQUA))
                        .append(Component.newline())
                        .append(Component.text("Created by ").color(NamedTextColor.DARK_AQUA))
                        .append(Component.text("Wasabi").color(NamedTextColor.GREEN))
        );
    }

    private @Nullable Player playerCheck(CommandSender sender) {
        if (sender instanceof Player ply) {
            return ply;
        } else {
            sender.sendMessage(Component.text("* You must be a player to run this command!").color(NamedTextColor.RED));
            return null;
        }
    }

    private void changeCommand(@NotNull CommandSender sender) {
        if (!(sender.hasPermission("dp.change") || sender.isOp())) {
            sender.sendMessage(Component.text("* You don't have permission to run this command!").color(NamedTextColor.RED));
            return;
        }
        Player ply;
        if ((ply = playerCheck(sender)) != null) {
            PlayerInventory inv = ply.getInventory();
            int slot = inv.getHeldItemSlot();
            ItemStack is = inv.getItem(slot);
            if (is == null) {
                sender.sendMessage(Component.text("* You aren't holding anything!").color(NamedTextColor.RED));
                return;
            }
            Material mat = is.getType();
            if (mat.equals(Material.AIR)) {
                sender.sendMessage(Component.text("* You aren't holding anything!").color(NamedTextColor.RED));
                return;
            }
            if (!mat.name().contains("ITEM_FRAME")) {
                sender.sendMessage(Component.text("* You aren't holding an item frame!").color(NamedTextColor.RED));
                return;
            }
            int count = is.getAmount();
            String pluralSuffix = (count == 1 ? "" : "s");
            boolean invis = InvisibleFrames.isInvisible(is);
            if (invis) {
                inv.setItem(slot, InvisibleFrames.setInvisible(is, false));
                sender.sendMessage(Component.text("* Removed invisibility from " + count + " item frame" + pluralSuffix).color(NamedTextColor.GREEN));
            } else {
                inv.setItem(slot, InvisibleFrames.setInvisible(is, true));
                sender.sendMessage(Component.text("* Made " + count + " item frame" + pluralSuffix + " invisible").color(NamedTextColor.GREEN));
            }
        }
    }

    private void wandCommmand(@NotNull CommandSender sender) {
        if (!(sender.hasPermission("dp.wand") || sender.isOp())) {
            sender.sendMessage(Component.text("* You don't have permission to run this command!").color(NamedTextColor.RED));
            return;
        }
        Player ply;
        if ((ply = playerCheck(sender)) != null) {
            PlayerInventory inv = ply.getInventory();
            int handSlot = inv.getHeldItemSlot();
            boolean handFree = false;
            int firstFree = -1;
            if (Disappearo.config.preventDuplicateWands) {
                ItemStack[] conts = inv.getContents();
                for (int i=0; i < conts.length; i++) {
                    ItemStack is = conts[i];
                    boolean free;
                    if (is == null) {
                        free = true;
                    } else {
                        free = is.getType().equals(Material.AIR);
                    }
                    if (i == handSlot) handFree = free;
                    if (free) {
                        if (firstFree == -1) firstFree = i;
                    } else {
                        if (Wands.isWand(is)) {
                            sender.sendMessage(Component.text("* You already have a wand!").color(NamedTextColor.RED));
                            return;
                        }
                    }
                }
            } else {
                ItemStack handItem = inv.getItem(handSlot);
                if (handItem == null) {
                    handFree = true;
                } else {
                    handFree = handItem.getType().equals(Material.AIR);
                }
                firstFree = inv.firstEmpty();
            }
            if (handFree) {
                inv.setItem(handSlot, Wands.createWand());
            } else {
                if (firstFree > -1) {
                    inv.setItem(firstFree, Wands.createWand());
                } else {
                    sender.sendMessage(Component.text("* No free space found in your inventory!").color(NamedTextColor.RED));
                    return;
                }
            }
            sender.sendMessage(Component.text("* Ta-da!").color(NamedTextColor.GREEN));
        }
    }

}
