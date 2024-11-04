package xyz.rusin.mvinvimporter;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import xyz.rusin.mvinvimporter.Inventory.Meta;
import xyz.rusin.mvinvimporter.Inventory.Slot;
import xyz.rusin.mvinvimporter.Inventory.Stats;

public class Commands implements CommandExecutor, TabCompleter {
    public static final String COMMAND = "MvInvImporter";

    private static final List<String> MODES = List.of("set");
    private static final Gson gson = new Gson();

    private final File dataFolder;

    private List<String> availableFiles = new ArrayList<>();
    private List<String> availablePlayers = new ArrayList<>();

    public Commands(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    /**
     * Structure: MvInvImporter set [filename.json] [playername]?
     */
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        refresh();
        if (args.length < 2 || args.length > 3 || !MODES.contains(args[0]) || !availableFiles.contains(args[1])) {
            return false;
        }
        if (args.length == 3 && !availablePlayers.contains(args[2])) {
            return false;
        }
        File jsonFile = new File(dataFolder, args[1]);
        Inventory inventory;
        try {
            inventory = gson.fromJson(Files.lines(jsonFile.toPath()).collect(Collectors.joining(" ")), Inventory.class);
        } catch (IOException | JsonSyntaxException e) {
            MvInvImporter.LOGGER.warning("Cannot read " + args[1]);
            return false;
        }
        String playerName = args.length == 2 ? args[1].replace(".json", "") : args[2];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            MvInvImporter.LOGGER.warning(String.format("Player %s is not online!", playerName));
            return false;
        }

        setPlayerInventory(player, inventory.survival.inventoryContents, inventory.survival.offHandItem);
        setPlayerEnderchestContent(player, inventory.survival.enderChestContents);
        setPlayerStats(player, inventory.survival.stats);

        System.out.println(String.format("Imported content of %s to player %s successfully!", args[1], playerName));
        return true;
    }

    private void setPlayerInventory(Player player, Map<String, Slot> inventory, Slot offHandItem) {
        for (var entry : inventory.entrySet()) {
            ItemStack stack = fromSlot(entry.getValue());
            player.getInventory().setItem(Integer.parseInt(entry.getKey()), stack);
        }
        player.getInventory().setItemInOffHand(fromSlot(offHandItem));
    }

    private void setPlayerEnderchestContent(Player player, Map<String, Slot> inventory) {
        for (var entry : inventory.entrySet()) {
            ItemStack stack = fromSlot(entry.getValue());
            player.getEnderChest().setItem(Integer.parseInt(entry.getKey()), stack);
        }
    }

    private ItemStack fromSlot(Slot slot) {
        if (slot.type.contains("SHULKER")) {
            Utils.unpackShulker(slot.meta.internal);
        }
        ItemStack stack = new ItemStack(Material.valueOf(slot.type));
        stack.setAmount(slot.amount);
        if (slot.meta != null) {
            setDamage(stack, slot.meta);
            setEnchants(stack, slot.meta);
            setName(stack, slot.meta);
        }
        return stack;
    }

    private void setDamage(ItemStack itemStack, Meta meta) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (meta.damage != null && itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(meta.damage);
        }
        if (meta.repairCost != null && itemMeta instanceof Repairable) {
            ((Repairable) itemMeta).setRepairCost(meta.repairCost);
        }
        itemStack.setItemMeta(itemMeta);
    }

    private void setEnchants(ItemStack itemStack, Meta meta) {
        if (meta.enchants == null) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();

        for (var entry : meta.enchants.entrySet()) {
            Enchantment enchantment = Enchantment.getByName(entry.getKey());
            itemMeta.addEnchant(enchantment, entry.getValue(), true);
        }
        itemStack.setItemMeta(itemMeta);
    }


    private void setName(ItemStack itemStack, Meta meta) {
        if (meta.displayName == null) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(meta.displayName);
        itemStack.setItemMeta(itemMeta);
    }

    private void setPlayerStats(Player player, Stats stats) {
        player.setTotalExperience(stats.totalExperience);
        player.setExp(stats.experience);
        player.setLevel(stats.level);

        player.setHealth(stats.health);
        player.setSaturation(stats.satuaration);

        player.setMaximumAir(stats.maxAir);
        player.setRemainingAir(stats.remainingAir);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length == 0 || (args.length == 1 && containsStartsWith(MODES, args[0]))) {
            return MODES;
        }
        refresh();
        if (MODES.contains(args[0])) {
            if (args.length == 1 || (args.length == 2 && containsStartsWith(availableFiles, args[1]))) {
                return availableFiles;
            }
            if (availableFiles.contains(args[1])) {
                if (args.length == 2 || (args.length == 3 && containsStartsWith(availablePlayers, args[2]))) {
                    return availablePlayers;
                }
            }
        }
        return Collections.emptyList();
    }

    private void refresh() {
        availableFiles = Stream.of(dataFolder.list()).filter(name -> name.endsWith(".json")).collect(toList());
        availablePlayers = Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(toList());
    }

    private boolean containsStartsWith(List<String> list, String startsWith) {
        return list.stream().anyMatch(item -> item.toLowerCase().startsWith(startsWith.toLowerCase()));
    }
    
}
