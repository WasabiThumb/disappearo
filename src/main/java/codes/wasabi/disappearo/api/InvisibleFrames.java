package codes.wasabi.disappearo.api;

import codes.wasabi.disappearo.Disappearo;
import codes.wasabi.disappearo.util.NBTEditor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InvisibleFrames {

    public static boolean isInvisible(@Nullable ItemStack in) {
        if (in == null) return false;
        ItemMeta meta = in.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return (pdc.getOrDefault(Keys.ITEM_FLAG, PersistentDataType.BYTE, (byte) 0) == ((byte) 1));
    }

    public static @NotNull ItemStack setInvisible(@NotNull ItemStack in, boolean invisible) {
        ItemMeta meta = in.getItemMeta();
        if (meta == null) return in;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        boolean curInvisible = (pdc.getOrDefault(Keys.ITEM_FLAG, PersistentDataType.BYTE, (byte) 0) == ((byte) 1));
        if (curInvisible == invisible) return in;
        if (invisible) {
            pdc.set(Keys.ITEM_FLAG, PersistentDataType.BYTE, (byte) 1);
            Component displayName = meta.displayName();
            if (displayName != null) {
                String dn = GsonComponentSerializer.gson().serialize(displayName);
                pdc.set(Keys.ITEM_DN_FLAG, PersistentDataType.STRING, dn);
            } else {
                pdc.set(Keys.ITEM_DN_FLAG, PersistentDataType.STRING, "NONE");
            }
            if (in.getType().equals(Material.GLOW_ITEM_FRAME)) {
                meta.displayName(Disappearo.config.lang.glowItemFrame);
            } else {
                meta.displayName(Disappearo.config.lang.itemFrame);
            }
        } else {
            pdc.set(Keys.ITEM_FLAG, PersistentDataType.BYTE, (byte) 0);
            String dn = pdc.getOrDefault(Keys.ITEM_DN_FLAG, PersistentDataType.STRING, "NONE");
            if (dn.equals("NONE")) {
                meta.displayName(null);
            } else {
                meta.displayName(GsonComponentSerializer.gson().deserialize(dn));
            }
        }
        in.setItemMeta(meta);
        return in;
    }

    public static boolean isInvisible(@Nullable Entity e) {
        if (e == null) return false;
        return NBTEditor.getBoolean(e, "Invisible");
    }

    public static @NotNull Entity setInvisible(@NotNull Entity e, boolean invisible) {
        NBTEditor.set(e, invisible, "Invisible");
        return e;
    }

}
