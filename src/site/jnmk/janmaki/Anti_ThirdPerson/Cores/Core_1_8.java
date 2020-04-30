package site.jnmk.janmaki.Anti_ThirdPerson.Cores;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class Core_1_8 extends Core {

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
        ItemStack[] items = new ItemStack[5];
        items[0] = CraftItemStack.asNMSCopy(hider.getInventory().getItemInHand());
        items[1] = CraftItemStack.asNMSCopy(hider.getInventory().getBoots());
        items[2] = CraftItemStack.asNMSCopy(hider.getInventory().getLeggings());
        items[3] = CraftItemStack.asNMSCopy(hider.getInventory().getChestplate());
        items[4] = CraftItemStack.asNMSCopy(hider.getInventory().getHelmet());
        for(int i = 0; i< 5; i++){
            PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(entityHider.getId(), i, items[i]);
            entityPlayer.playerConnection.sendPacket(equipmentPacket);
        }
        PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityHider);
        entityPlayer.playerConnection.sendPacket(infoPacket);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityHider.getId(), entityHider.getDataWatcher(), true);
        entityPlayer.playerConnection.sendPacket(metadataPacket);
    }
}
