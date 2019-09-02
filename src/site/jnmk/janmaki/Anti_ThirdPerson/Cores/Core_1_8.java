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
        PacketPlayOutEntityEquipment head = new PacketPlayOutEntityEquipment(entityHider.getId(), 4, CraftItemStack.asNMSCopy(hider.getInventory().getHelmet()));
        entityPlayer.playerConnection.sendPacket(head);
        PacketPlayOutEntityEquipment chest = new PacketPlayOutEntityEquipment(entityHider.getId(),3,CraftItemStack.asNMSCopy(hider.getInventory().getChestplate()));
        entityPlayer.playerConnection.sendPacket(chest);
        PacketPlayOutEntityEquipment legs = new PacketPlayOutEntityEquipment(entityHider.getId(),2,CraftItemStack.asNMSCopy(hider.getInventory().getLeggings()));
        entityPlayer.playerConnection.sendPacket(legs);
        PacketPlayOutEntityEquipment feet = new PacketPlayOutEntityEquipment(entityHider.getId(),1,CraftItemStack.asNMSCopy(hider.getInventory().getBoots()));
        entityPlayer.playerConnection.sendPacket(feet);
        PacketPlayOutEntityEquipment held = new PacketPlayOutEntityEquipment(entityHider.getId(),0,CraftItemStack.asNMSCopy(hider.getInventory().getItemInHand()));
        entityPlayer.playerConnection.sendPacket(held);


    }
}
