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
public class SetSlot extends DefinedPacket {

    private int windowId;
    private int stateId;
    private int slot;
    private Item item;

    public SetSlot(int windowId, int slot, Item item) {
        this.windowId = windowId;
        this.slot = slot;
        this.item = item;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        windowId = buf.readUnsignedByte();
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            stateId = readVarInt(buf);
        }
        slot = buf.readShort();
        item = readItem(buf, protocolVersion);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        buf.writeByte(windowId);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17_1) {
            writeVarInt(stateId, buf);
        }
        buf.writeShort(slot);
        writeItem(item, buf, protocolVersion);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }
}