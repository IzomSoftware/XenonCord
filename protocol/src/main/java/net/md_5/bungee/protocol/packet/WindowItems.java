package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WindowItems extends DefinedPacket {

    private int windowId;
    private int stateId;
    private Item[] items;
    private Item carriedItem;

    public WindowItems(int windowId, Item[] items) {
        this.windowId = windowId;
        this.items = items;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        windowId = buf.readUnsignedByte();
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            stateId = readVarInt(buf);
        }
        int count;
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            count = readVarInt(buf);
        } else {
            count = buf.readShort();
        }
        items = new Item[count];
        for (int i = 0; i < count; i++) {
            items[i] = readItem(buf, protocolVersion);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            carriedItem = readItem(buf, protocolVersion);
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeByte(windowId);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            writeVarInt(stateId, buf);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            writeVarInt(items.length, buf);
        } else {
            buf.writeShort(items.length);
        }
        for (Item item : items) {
            writeItem(item, buf, protocolVersion);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            writeItem(carriedItem, buf, protocolVersion);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }
}