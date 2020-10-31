package xyz.rusin.mvinvimporter;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Inventory {

    @SerializedName("SURVIVAL")
    Container survival;
    
    static class Container {
        Stats stats;
    
        Map<String, Slot> enderChestContents;
        Map<String, Slot> inventoryContents;
        Slot offHandItem;
    }

    static class Stats {
        @SerializedName("ex")
        Double exhaustion;
        @SerializedName("ma")
        Integer maxAir;
        @SerializedName("fl")
        Integer floodLevel;
        @SerializedName("el")
        Integer level;
        @SerializedName("hp")
        Double health;
        @SerializedName("xp")
        Float experience;
        @SerializedName("txp")
        Integer totalExperience;
        @SerializedName("ft")
        Integer fireTicks;
        @SerializedName("fd")
        Double fallDistance;
        @SerializedName("sa")
        Float satuaration;
        @SerializedName("ra")
        Integer remainingAir;
    }

    static class Slot {
        String type;
        int amount = 1;
        Meta meta;
    }

    static class Meta {
        Map<String, Integer> enchants;
        @SerializedName("repair-cost")
        Integer repairCost;
        @SerializedName("Damage")
        Integer damage;
        @SerializedName("display-name")
        String displayName;
    }
}
