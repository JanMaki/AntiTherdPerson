package site.jnmk.janmaki.Anti_ThirdPerson;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import site.jnmk.janmaki.Anti_ThirdPerson.Cores.*;

import java.util.*;

public class Main extends JavaPlugin implements Listener {
    private Core core;
    private FileConfiguration config;

    // Overrides!!
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
        else if (version.contains("1.15")){
            core = new Core_1_15();
        }
        else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"This plugin is not supported in this server's version.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this,this);
        saveDefaultConfig();
        config = getConfig();
        if (!config.contains("corners")){
            config.set("corners",12);
        }
        if (!config.contains("run_async")){
            config.set("run_async",true);
        }
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN+"Usage: /atp reload");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")){
            reloadConfig();
            sender.sendMessage(ChatColor.GREEN+"Reload config!");
            return true;
        }
        sender.sendMessage(ChatColor.GREEN+"Usage: /atp reload");
        return true;
    }

    // Functions!!
    private final Map<Player, Set<Player>> map = new HashMap<>();
    private final Map<Player,Map<Way,Double>> locationsMap = new HashMap<>();
    private enum Way{
        X,Y,Z
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

    private final Map<UUID[], BukkitTask> tasks = new HashMap<>();
    private void invUpdate(Player player1,Player player2) {
        UUID[] uuidArray = new UUID[2];
        uuidArray[0] = player1.getUniqueId();
        uuidArray[1] = player2.getUniqueId();
        for (UUID[] key: tasks.keySet()){
            if (key[0].equals(uuidArray[0]) && key[1].equals(uuidArray[1])){
                BukkitTask bukkitTask = tasks.get(key);
                bukkitTask.cancel();
                tasks.remove(key);
                break;
            }
        }
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
        BukkitTask task = runTask(() -> {
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
        },config.getBoolean("run_async", true));
        tasks.put(uuidArray, task);
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
        int acc = config.getInt("corners",12);
        for (int c = 365; c > 0 ; c -= 365/acc) {
            double i = 0.3 * Math.cos(Math.toRadians(c));
            double n = 0.3 * Math.sin(Math.toRadians(c));
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
        return mainResult;
    }

    private BukkitTask runTask(Runnable runnable, boolean isAsync){
        if (isAsync) {
            return Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
        }else {
            return Bukkit.getScheduler().runTask(this, runnable);
        }
    }
}