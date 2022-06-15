package codes.wasabi.disappearo.api;

import codes.wasabi.disappearo.Disappearo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Wands {

    public static boolean isWand(@Nullable ItemStack is) {
        if (is == null) return false;
        ItemMeta meta = is.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        byte b = pdc.getOrDefault(Keys.WAND_FLAG, PersistentDataType.BYTE, (byte) 0);
        return b == ((byte) 1);
    }

    public static @NotNull ItemStack createWand() {
        ItemStack is = new ItemStack(Material.STICK, 1);
        is.editMeta((ItemMeta meta) -> {
            meta.displayName(Disappearo.config.lang.wand);
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(Keys.WAND_FLAG, PersistentDataType.BYTE, (byte) 1);
        });
        return is;
    }

}
