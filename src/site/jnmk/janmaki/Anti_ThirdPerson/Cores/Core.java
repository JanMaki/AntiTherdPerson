package site.jnmk.janmaki.Anti_ThirdPerson.Cores;

import org.bukkit.entity.Player;

public abstract class  Core {
    public abstract void hidePlayer(Player player,Player hider);

    public abstract void showPlayer(Player player,Player hider);

    protected byte toByte(float yaw_pitch) {
        return (byte) ((int)(yaw_pitch * 256.0f / 360.0f));
    }
}
