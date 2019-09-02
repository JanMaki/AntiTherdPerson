package site.jnmk.janmaki.Anti_ThirdPerson;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import site.jnmk.janmaki.Anti_ThirdPerson.Cores.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main extends JavaPlugin implements Listener {
    private Core core;

    @Override
    public void onEnable(){
        String version = Bukkit.getServer().getVersion();
        if (version.contains("1.8")){
            core = new Core_1_8();
        }
        else if (version.contains("1.9")){
            core = new Core_1_9();
        }
        else if (version.contains("1.10")){
            core = new Core_1_10();
        }
        else if (version.contains("1.11")){
            core = new Core_1_11();
        }
        else if (version.contains("1.12")){
            core = new Core_1_12();
        }
        else if (version.contains("1.13")) {
            core = new Core_1_13();
        }
        else if (version.contains("1.14")){
            core = new Core_1_14();
        }
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"This plugin is not supported in this server's version");
            return;
        }
        Bukkit.getPluginManager().registerEvents(this,this);
    }

    private Map<Player, Set<Player>> map = new HashMap<>();
    private Map<Player,Map<Way,Double>> locationsMap = new HashMap<>();
    private enum Way{
        X,Y,Z;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        if(locationsMap.equals(player)){
            Map<Way,Double> locationMap = locationsMap.get(player);
            if (locationMap.get(Way.X) == location.getX() && locationMap.get(Way.Y) == location.getY() && locationMap.get(Way.Z) == location.getZ()) {
                return;
            }
        }
        Map<Way,Double> locationMap = new HashMap<>();
        locationMap.put(Way.X,location.getX());
        locationMap.put(Way.Y,location.getY());
        locationMap.put(Way.Z,location.getZ());
        locationsMap.put(player,locationMap);
        function(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        new BukkitRunnable() {
            @EventHandler
            public void run(){
                Player player = event.getPlayer();
                Location location = player.getLocation();
                Map<Way,Double> locationMap = new HashMap<>();
                locationMap.put(Way.X,location.getX());
                locationMap.put(Way.Y,location.getY());
                locationMap.put(Way.Z,location.getZ());
                locationsMap.put(player,locationMap);
                function(player);
            }
        }.runTaskLater(this,1);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        map.remove(player);
        locationsMap.remove(player);
    }

    private void function(Player player){
        for (Player player2:Bukkit.getOnlinePlayers()){
            if (player.equals(player2)){
                continue;
            }
            invUpdate(player,player2);
            invUpdate(player2,player);
        }
    }

    private void invUpdate(Player player1,Player player2) {
        Location location1 = player1.getEyeLocation();
        Location location2 = player2.getLocation();
        if (player1.getGameMode() == GameMode.CREATIVE || player1.getGameMode() == GameMode.SPECTATOR ){
            if (map.containsKey(player1)) {
                for(Player player:map.get(player1)){
                    core.showPlayer(player1,player);
                }
                map.remove(player1);
            }
            return;
        }
        if (player1.getWorld() != player2.getWorld()){
            return;
        }
        boolean mainResult = checkBlock(location1,location2);
        double height = 1.8;
        for (double i = 0.1 ; i <= height ; i += 0.1){
            location2.add(0,0.1,0);
            mainResult = mainResult || checkBlock(location1,location2);
        }
        if (mainResult){
            if (checkMap(player1,player2)){
                if  (!player2.isDead()) {
                    core.showPlayer(player1, player2);
                    map.remove(player1);
                }
            }
        }
        else {
            addMap(player1,player2);
            core.hidePlayer(player1, player2);
        }
    }

    private void addMap(Player player1,Player player2){
        Set<Player> set;
        if (!(map.containsKey(player1))) {
            set = new HashSet<>();
        }
        else if (map.get(player1) == null){
            set = new HashSet<>();
        } else {
            set = map.get(player1);
        }
        set.add(player2);
        map.put(player1,set);
    }

    private boolean checkMap(Player player1, Player player2){
        if (!(map.containsKey(player1))){
            return false;
        }
        Set<Player> set = map.get(player1);
        return set.contains(player2);
    }

    private boolean checkBlock(Location location1,Location location2){
        boolean mainResult = false;
        int acc = 12;

        for (int c = 0; c <= 360 ; c += 360/acc) {
            double i = Math.tan(Math.toRadians(c)) * 0.3 * Math.sqrt(2);
            double n = Math.sin(Math.toRadians(c)) * 0.3 * Math.sqrt(2);
            boolean result = true;
            Location location3 = new Location(location2.getWorld(),location2.getX()+i,location2.getY(),location2.getZ()+n);
            Vector pos1 = location1.toVector();
            Vector pos2 = location3.toVector();
            Vector vector = pos2.clone().subtract(pos1).normalize().multiply(0.1);
            double distance = location1.distance(location3);
            if (distance > 50){
                continue;
            }
            for (double covered = 0; covered < distance; pos1.add(vector)) {
                covered += 0.1;
                Block block = (new Location(location1.getWorld(),pos1.getX(),pos1.getY(), pos1.getZ())).getBlock();
                if (block.isLiquid() || block.getType().isTransparent() || !(block.getType().isOccluding())){
                    continue;
                }
                if (block.getType() != Material.AIR){
                    result = false;
                    break;
                }
            }
            mainResult = mainResult || result;
        }





        /*
        for(double i = -0.3 ; i <= 0.3 ; i+=0.1) {
            for(double n = -0.3 ; n <= 0.3 ; n+=0.1) {
                boolean result = true;
                Location location3 = new Location(location2.getWorld(),location2.getX()+i,location2.getY(),location2.getZ()+n);
                Vector pos1 = location1.toVector();
                Vector pos2 = location3.toVector();
                Vector vector = pos2.clone().subtract(pos1).normalize().multiply(0.1);
                double distance = location1.distance(location3);
                if (distance > 50){
                    continue;
                }
                for (double covered = 0; covered < distance; pos1.add(vector)) {
                    covered += 0.1;
                    Block block = (new Location(location1.getWorld(),pos1.getX(),pos1.getY(), pos1.getZ())).getBlock();
                    if (block.isLiquid() || block.getType().isTransparent() || !(block.getType().isOccluding())){
                        continue;
                    }
                    if (block.getType() != Material.AIR){
                        result = false;
                        break;
                    }
                }
                mainResult = mainResult || result;
            }
        }
         */
        return mainResult;
    }

}