package xyz.rusin.mvinvimporter;

import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class Utils {
    public static void unpackShulker(String internal) {
        byte[] bytes = Base64.getDecoder().decode(internal);
        NBTDeserializer deserializer = new NBTDeserializer(true, false);
        NamedTag tag;
        try {
            tag = deserializer.fromBytes(bytes);
        } catch (Exception e) {
            System.err.println("WUUUUUT");
            System.err.println(e);
            return;
        }
        ListTag<CompoundTag> list = (ListTag<CompoundTag>) ((CompoundTag)tag.getTag()).getCompoundTag("block-entity-components").getListTag("minecraft:container");
        list.forEach(Utils::consumeSlot);
    }

    private static void consumeSlot(CompoundTag slot) {
        
    };
}
