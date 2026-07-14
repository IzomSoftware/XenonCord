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
public class ResourcePackRemove extends DefinedPacket {
    private UUID id;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (buf.readBoolean()) {
            id = readUUID(buf);
        } else {
            id = null;
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (id != null) {
            buf.writeBoolean(true);
            writeUUID(id, buf);
        } else {
            buf.writeBoolean(false);
        }
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