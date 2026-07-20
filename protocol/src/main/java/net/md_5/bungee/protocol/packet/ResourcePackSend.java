package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ResourcePackSend extends DefinedPacket {
    private UUID id;
    private String url;
    private String hash;
    private boolean required;
    private BaseComponent prompt;

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_20_5) {
            id = readUUID(buf);
        }
        url = readString(buf, 32767);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_20_5) {
            hash = readString(buf, 32767);
        } else {
            hash = readString(buf, 40);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17) {
            required = buf.readBoolean();
            prompt = readNullable(b -> readBaseComponent(b, protocolVersion), buf);
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_20_5) {
            writeUUID(id != null ? id : new UUID(0L, 0L), buf);
        }
        writeString(url != null ? url : "", buf, 32767);
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_20_5) {
            writeString(hash != null ? hash : "", buf, 32767);
        } else {
            writeString(hash != null ? hash : "", buf, 40);
        }
        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_17) {
            buf.writeBoolean(required);
            writeNullable(prompt, (p, b) -> writeBaseComponent(p, b, protocolVersion), buf);
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
