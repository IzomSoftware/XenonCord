package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ResourcePackResponse extends DefinedPacket {
    private UUID id;
    private String hash;
    private int result;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_20_5) {
            id = readUUID(buf);
        } else {
            hash = readString(buf, 40);
        }
        result = readVarInt(buf);
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_20_5) {
            writeUUID(id != null ? id : new UUID(0L, 0L), buf);
        } else {
            writeString(hash != null ? hash : "", buf, 40);
        }
        writeVarInt(result, buf);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        try {
            handler.handle(this);
        } catch (OutOfMemoryError e) {
            System.gc();
        }
    }
}