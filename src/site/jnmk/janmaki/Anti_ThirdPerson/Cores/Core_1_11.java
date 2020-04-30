package site.jnmk.janmaki.Anti_ThirdPerson.Cores;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.craftbukkit.v1_11_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;

public class Core_1_11 extends Core {
    @Override
    public void hidePlayer(Player player, Player hider) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(((CraftPlayer)hider).getHandle().getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void showPlayer(Player player, Player hider) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityPlayer entityHider = ((CraftPlayer) hider).getHandle();
        PacketPlayOutNamedEntitySpawn packet2 = new PacketPlayOutNamedEntitySpawn(entityHider);
        entityPlayer.playerConnection.sendPacket(packet2);
        PacketPlayOutEntity.PacketPlayOutEntityLook packet3 = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityHider.getId(), toByte(hider.getLocation().getYaw()), toByte(hider.getLocation().getPitch()),true);
        entityPlayer.playerConnection.sendPacket(packet3);
        PacketPlayOutEntityHeadRotation packet4 = new PacketPlayOutEntityHeadRotation(entityHider,toByte(hider.getLocation().getYaw()));
        entityPlayer.playerConnection.sendPacket(packet4);
        Map<EquipmentSlot, ItemStack> items = new HashMap<>();
        items.put(EquipmentSlot.HAND, CraftItemStack.asNMSCopy(hider.getInventory().getItemInMainHand()));
        items.put(EquipmentSlot.OFF_HAND, CraftItemStack.asNMSCopy(hider.getInventory().getItemInOffHand()));
        items.put(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(hider.getInventory().getChestplate()));
        items.put(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(hider.getInventory().getHelmet()));
        items.put(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(hider.getInventory().getLeggings()));
        items.put(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(hider.getInventory().getBoots()));
        for (EquipmentSlot equipmentSlot: EquipmentSlot.values()){
            PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityHider.getId(), CraftEquipmentSlot.getNMS(equipmentSlot), items.get(equipmentSlot));
            entityPlayer.playerConnection.sendPacket(equipmentPacket);
        }
        PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityHider);
        entityPlayer.playerConnection.sendPacket(infoPacket);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityHider.getId(), entityHider.getDataWatcher(), true);
        entityPlayer.playerConnection.sendPacket(metadataPacket);
    }
}
