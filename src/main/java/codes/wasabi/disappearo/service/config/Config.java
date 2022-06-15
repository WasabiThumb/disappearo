package codes.wasabi.disappearo.service.config;

import codes.wasabi.disappearo.Disappearo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private final MiniMessage mm = MiniMessage.miniMessage();

    public final ConfigLang lang = new ConfigLang();
    public boolean preventDuplicateWands;
    public boolean enforceWandUsagePerms;
    public boolean denyWandInCrafting;

    private final Disappearo plugin;
    private final FileConfiguration cfg;
    public Config(Disappearo plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        cfg = plugin.getConfig();
        load();
    }

    private Component deserialize(String s) {
        try {
            return mm.deserializeOrNull(s);
        } catch (Exception e) {
            return null;
        }
    }

    public void load() {
        lang.itemFrame = deserialize(cfg.getString("lang.invisible-item-frame", "<color:#38ffdb>Invisible</color> Item Frame"));
        lang.glowItemFrame = deserialize(cfg.getString("lang.invisible-glow-item-frame", "<color:#38ffdb>Invisible</color> Glow Item Frame"));
        lang.wand = deserialize(cfg.getString("lang.wand", "<rainbow>Disappearo Wand</rainbow>"));
        preventDuplicateWands = cfg.getBoolean("prevent-duplicate-wands", true);
        enforceWandUsagePerms = cfg.getBoolean("enforce-wand-usage-perms", true);
        denyWandInCrafting = cfg.getBoolean("deny-wand-in-crafting", true);
    }

    public void save() {
        cfg.set("lang.invisible-item-frame", mm.serializeOrNull(lang.itemFrame));
        cfg.set("lang.invisible-glow-item-frame", mm.serializeOrNull(lang.glowItemFrame));
        cfg.set("lang.wand", mm.serializeOrNull(lang.wand));
        cfg.set("prevent-duplicate-wands", preventDuplicateWands);
        cfg.set("enforce-wand-usage-perms", enforceWandUsagePerms);
        cfg.set("deny-wand-in-crafting", denyWandInCrafting);
        plugin.saveConfig();
    }

}
