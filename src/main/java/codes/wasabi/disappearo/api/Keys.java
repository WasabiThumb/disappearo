package codes.wasabi.disappearo.api;

import codes.wasabi.disappearo.Disappearo;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public final class Keys {

    public static NamespacedKey ITEM_FLAG;
    public static NamespacedKey ITEM_DN_FLAG;
    public static NamespacedKey WAND_FLAG;

    public static void initialize(@NotNull Disappearo plugin) {
        ITEM_FLAG = new NamespacedKey(plugin, "dp_invis");
        ITEM_DN_FLAG = new NamespacedKey(plugin, "dp_dn");
        WAND_FLAG = new NamespacedKey(plugin, "dp_wand");
    }

}
