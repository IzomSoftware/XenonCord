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
public class MapData extends DefinedPacket {

    private int mapId;
    private byte scale;
    private boolean trackingPosition;
    private boolean locked;
    private int iconCount;
    private byte columns;
    private byte rows;
    private byte x;
    private byte z;
    private byte[] data;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        mapId = readVarInt(buf);
        scale = buf.readByte();
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_9) {
            trackingPosition = buf.readBoolean();
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_14) {
            locked = buf.readBoolean();
        }
        iconCount = readVarInt(buf);
        if (iconCount > 0) {
            for (int i = 0; i < iconCount; i++) {
                if (protocolVersion >= ProtocolConstants.MINECRAFT_1_12_2) {
                    readVarInt(buf);
                } else {
                    buf.readByte();
                }
                buf.readByte();
                buf.readByte();
                if (protocolVersion >= ProtocolConstants.MINECRAFT_1_12_2) {
                    buf.readByte();
                    if (buf.readBoolean()) {
                        readBaseComponent(buf, protocolVersion);
                    }
                }
            }
        }

        columns = buf.readByte();
        if (columns > 0) {
            rows = buf.readByte();
            x = buf.readByte();
            z = buf.readByte();
            data = readArray(buf);
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        writeVarInt(mapId, buf);
        buf.writeByte(scale);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_9) {
            buf.writeBoolean(trackingPosition);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_14) {
            buf.writeBoolean(locked);
        }
        writeVarInt(iconCount, buf);

        buf.writeByte(columns);
        if (columns != 0) {
            buf.writeByte(rows);
            buf.writeByte(x);
            buf.writeByte(z);
            writeVarInt(data.length, buf);
            buf.writeBytes(data);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }
}