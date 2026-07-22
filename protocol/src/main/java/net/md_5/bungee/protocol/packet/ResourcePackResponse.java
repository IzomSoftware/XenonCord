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

    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        try {
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_20_5) {
                if (buf.readableBytes() >= 16) {
                    id = readUUID(buf);
                }
            } else {
                buf.markReaderIndex();
                try {
                    hash = readString(buf, 40);
                } catch (IndexOutOfBoundsException e) {
                    buf.resetReaderIndex();
                }
            }
            result = readVarInt(buf);
        } catch (Exception e) {
            result = PackResponse.FAILED_DOWNLOAD.getId();
        }
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

    public enum PackResponse {
        SUCCESSFULLY_LOADED(0),
        DECLINED(1),
        FAILED_DOWNLOAD(2),
        ACCEPTED(3),
        DOWNLOADED(4),
        INVALID_URL(5),
        FAILED_RELOAD(6),
        DISCARDED(7);

        private final int id;

        PackResponse(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }
}
