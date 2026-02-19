package net.md_5.bungee.protocol.packet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.md_5.bungee.nbt.Tag;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private int id;
    private int count;
    private int data; // Damage for 1.8-1.12
    private Tag tag; // NBT
}