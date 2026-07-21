package net.md_5.bungee.protocol;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Data;
import lombok.Getter;
import net.md_5.bungee.protocol.packet.*;

import java.util.function.Supplier;

public enum Protocol {

    HANDSHAKE {
        {
            server(Handshake.class, Handshake::new, map(ProtocolConstants.MINECRAFT_1_8, 0x00));
        }
    },
    GAME {
        {
            client(BundleDelimiter.class, BundleDelimiter::new,
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x00));
            client(KeepAlive.class, KeepAlive::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x21),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x20),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x21),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x20),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x21),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x1E),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x20),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x23),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x24),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x26),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x27),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x26),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x2B),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x2C));
            client(Login.class, Login::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x23),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x25),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x26),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x25),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x24),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x26),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x23),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x25),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x24),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x28),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x29),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x2B),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x2C),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x2B),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x30),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x31));
            client(Chat.class, Chat::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_19, -1));
            client(Respawn.class, Respawn::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x07),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x33),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x34),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x35),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x38),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x3B),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x39),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x3D),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x3B),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x3E),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x3D),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x43),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x4C),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x50),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x52));
            client(BossBar.class, BossBar::new,
                    map(ProtocolConstants.MINECRAFT_1_9, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x0D),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x0D),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x09));
            client(PlayerListItem.class, PlayerListItem::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x38),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x2D),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x2E),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x30),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x33),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x34),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x33),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x32),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x36),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x34),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x37),
                    map(ProtocolConstants.MINECRAFT_1_19_3, -1));
            client(TabCompleteResponse.class, TabCompleteResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x0D),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x0F));
            client(ScoreboardObjective.class, ScoreboardObjective::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3B),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x3F),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x4A),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x53),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x56),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x54),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x58),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x5A),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x5C),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x5E),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x64),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x63),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x68),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x6A));
            client(ScoreboardScore.class, ScoreboardScore::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3C),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x44),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x4C),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x4D),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x56),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x59),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x57),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x5B),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x5D),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x5F),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x61),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x68),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x67),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x6C),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x6E));
            client(ScoreboardScoreReset.class, ScoreboardScoreReset::new,
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x44),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x4D),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x4F));
            client(ScoreboardDisplay.class, ScoreboardDisplay::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3D),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x38),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x3B),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x3E),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x43),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x4C),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x4F),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x4D),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x51),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x53),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x55),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x57),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x5C),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x5B),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x60),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x62));
            client(Team.class, Team::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3E),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x43),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x44),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x4C),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x55),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x58),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x56),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x5A),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x5C),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x5E),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x60),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x67),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x66),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x6B),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x6D));
            client(PluginMessage.class, PluginMessage::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x3F),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x17),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x15),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x16),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x15),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x17),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x18));
            client(Kick.class, Kick::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x40),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x17),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x17),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x1D),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x1C),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x20));
            client(Title.class, Title::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x4F),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x50),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x4F),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x59),
                    map(ProtocolConstants.MINECRAFT_1_18, 0x5A),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x5D),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x5B),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x5F),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x61),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x63),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x65),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x6C),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x6B),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x70),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x72));
            client(ClearTitles.class, ClearTitles::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_17, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x0D),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x0E));
            client(Subtitle.class, Subtitle::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_17, 0x57),
                    map(ProtocolConstants.MINECRAFT_1_18, 0x58),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x5B),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x59),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x5D),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x5F),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x61),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x63),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x6A),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x69),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x6E),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x70));
            client(TitleTimes.class, TitleTimes::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_17, 0x5A),
                    map(ProtocolConstants.MINECRAFT_1_18, 0x5B),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x5E),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x5C),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x60),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x62),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x64),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x66),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x6D),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x6C),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x71),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x73));
            client(SystemChat.class, SystemChat::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_19, 0x5F),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x62),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x60),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x64),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x67),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x69),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x6C),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x73),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x72),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x77),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x79));
            client(PlayerListHeaderFooter.class, PlayerListHeaderFooter::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_9_4, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x4A),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x4E),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x53),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x54),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x53),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x5E),
                    map(ProtocolConstants.MINECRAFT_1_18, 0x5F),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x60),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x63),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x61),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x65),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x68),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x6A),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x6D),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x74),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x73),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x78),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x7A));
            client(EntityStatus.class, EntityStatus::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x1C),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x1C),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x1C),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x1D),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x1E),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x22));
            client(Commands.class, Commands::new,
                    map(ProtocolConstants.MINECRAFT_1_13, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x12),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x12),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x10));
            client(GameState.class, GameState::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_15, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x1E),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x1D),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x1E),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x1B),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x1D),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x1C),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x20),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x22),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x23),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x22),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x26));
            client(ViewDistance.class, ViewDistance::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_14, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x4A),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x4C),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x4F),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x51),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x53),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x55),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x59),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x58),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x5D),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x5F));
            client(ServerData.class, ServerData::new,
                    map(ProtocolConstants.MINECRAFT_1_19, 0x3F),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x41),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x47),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x50),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x4F),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x54),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x56));
            client(PlayerListItemRemove.class, PlayerListItemRemove::new,
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x35),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x39),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x3B),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x3D),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x3F),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x3E),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x43),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x45));
            client(PlayerListItemUpdate.class, PlayerListItemUpdate::new,
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x36),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x3C),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x3E),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x40),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x3F),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x44),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x46));
            client(StartConfiguration.class, StartConfiguration::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x65),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x67),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x69),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x70),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x6F),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x74),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x76));
            client(CookieRequest.class, CookieRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x16),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x15));
            client(StoreCookie.class, StoreCookie::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x6B),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x72),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x71),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x76),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x78));
            client(Transfer.class, Transfer::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x73),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x7A),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x7F),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x81));
            client(DisconnectReportDetails.class, DisconnectReportDetails::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21, 0x7A),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x81),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x86),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x88));
            client(ServerLinks.class, ServerLinks::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21, 0x7B),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x82),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x87),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x89));
            client(ClearDialog.class, ClearDialog::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x84),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x89),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x8B));
            client(ShowDialog.class, ShowDialog::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x85),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x8A),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x8C));
            client(ResourcePackSend.class, ResourcePackSend::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x48),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x32),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x37),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x3C),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x3D),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x39),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x38),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x3C),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x3A),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x3D),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x3C),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x40),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x42),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x44),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x46),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x4B),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x4A),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x4F),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x51));
            client(ResourcePackRemove.class, ResourcePackRemove::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x45),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x4A),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x49),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x4E),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x50));

            server(KeepAlive.class, KeepAlive::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x12),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x12),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x14),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x15),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x1A),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x1B),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x1C));
            server(Chat.class, Chat::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x03),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x03),
                    map(ProtocolConstants.MINECRAFT_1_19, -1));
            server(ClientCommand.class, ClientCommand::new,
                    map(ProtocolConstants.MINECRAFT_1_19, 0x03),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x04),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x06),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x07),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x08));
            server(UnsignedClientCommand.class, UnsignedClientCommand::new,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x04),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x06),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x07));
            server(ClientChat.class, ClientChat::new,
                    map(ProtocolConstants.MINECRAFT_1_19, 0x04),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x06),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x07),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x08),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x09));
            server(TabCompleteRequest.class, TabCompleteRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x14),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x06),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x08),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x09),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x08),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x09),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x0D),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x0E),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x0F));
            server(ClientSettings.class, ClientSettings::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x15),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x04),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x04),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x07),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x08),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x07),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x08),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x09),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x0D),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x0E));
            server(PluginMessage.class, PluginMessage::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x17),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x09),
                    map(ProtocolConstants.MINECRAFT_1_12, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x09),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x0A),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x0D),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x0D),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x0F),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x10),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x12),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x14),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x15),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x16));
            server(StartConfiguration.class, StartConfiguration::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x0B),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x0C),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x0E),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x0F),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x10));
            server(CookieResponse.class, CookieResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x11),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x13),
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x14),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x15));
            server(CustomClickAction.class, CustomClickAction::new,
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x41),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x44));
            server(ResourcePackResponse.class, ResourcePackResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_9, 0x16),
                    map(ProtocolConstants.MINECRAFT_1_11, 0x18),
                    map(ProtocolConstants.MINECRAFT_1_12_1, 0x19),
                    map(ProtocolConstants.MINECRAFT_1_13, 0x1C),
                    map(ProtocolConstants.MINECRAFT_1_14, 0x1D),
                    map(ProtocolConstants.MINECRAFT_1_15, 0x1F),
                    map(ProtocolConstants.MINECRAFT_1_16, 0x20),
                    map(ProtocolConstants.MINECRAFT_1_16_2, 0x21),
                    map(ProtocolConstants.MINECRAFT_1_17, 0x22),
                    map(ProtocolConstants.MINECRAFT_1_18, 0x23),
                    map(ProtocolConstants.MINECRAFT_1_19, 0x21),
                    map(ProtocolConstants.MINECRAFT_1_19_1, 0x24),
                    map(ProtocolConstants.MINECRAFT_1_19_3, 0x23),
                    map(ProtocolConstants.MINECRAFT_1_19_4, 0x27),
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x28),
                    map(ProtocolConstants.MINECRAFT_1_20_3, 0x2B),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x2D),
                    map(ProtocolConstants.MINECRAFT_1_21_2, 0x35),
                    map(ProtocolConstants.MINECRAFT_1_21_5, 0x34),
                    map(ProtocolConstants.MINECRAFT_1_21_9, 0x39),
                    map(ProtocolConstants.MINECRAFT_26_1, 0x3B));
        }
    },
    STATUS {
        {
            client(StatusResponse.class, StatusResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00));
            client(PingPacket.class, PingPacket::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01));
            server(StatusRequest.class, StatusRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00));
            server(PingPacket.class, PingPacket::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01));
        }
    },
    LOGIN {
        {
            client(Kick.class, Kick::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00));
            client(EncryptionRequest.class, EncryptionRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01));
            client(LoginSuccess.class, LoginSuccess::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x02));
            client(SetCompression.class, SetCompression::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x03));
            client(LoginPayloadRequest.class, LoginPayloadRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_13, 0x04));
            client(CookieRequest.class, CookieRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x05));
            server(LoginRequest.class, LoginRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x00));
            server(EncryptionResponse.class, EncryptionResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_8, 0x01));
            server(LoginPayloadResponse.class, LoginPayloadResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_13, 0x02));
            server(LoginAcknowledged.class, LoginAcknowledged::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x03));
            server(CookieResponse.class, CookieResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x04));
        }
    },
    CONFIGURATION {
        {
            client(CookieRequest.class, CookieRequest::new,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x00));
            client(PluginMessage.class, PluginMessage::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x00),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x01));
            client(Kick.class, Kick::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x02));
            client(FinishConfiguration.class, FinishConfiguration::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x03));
            client(KeepAlive.class, KeepAlive::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x03),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x04));
            client(StoreCookie.class, StoreCookie::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x0A));
            client(Transfer.class, Transfer::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x0B));
            client(KnownPacks.class, KnownPacks::new,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x0E));
            client(DisconnectReportDetails.class, DisconnectReportDetails::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21, 0x0F));
            client(ServerLinks.class, ServerLinks::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21, 0x10));
            client(ClearDialog.class, ClearDialog::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x11));
            client(ShowDialogDirect.class, ShowDialogDirect::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x12));
            client(ResourcePackSend.class, ResourcePackSend::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x06));
            client(ResourcePackRemove.class, ResourcePackRemove::new, RegisterType.ENCODE,
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x08));
            server(ClientSettings.class, ClientSettings::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x00));
            server(PluginMessage.class, PluginMessage::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x01),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x02));
            server(FinishConfiguration.class, FinishConfiguration::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x02),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x03));
            server(KeepAlive.class, KeepAlive::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x03),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x04));
            server(CustomClickAction.class, CustomClickAction::new,
                    map(ProtocolConstants.MINECRAFT_1_21_6, 0x08));
            server(ResourcePackResponse.class, ResourcePackResponse::new,
                    map(ProtocolConstants.MINECRAFT_1_20_2, 0x05),
                    map(ProtocolConstants.MINECRAFT_1_20_5, 0x06));
        }
    };

    public static final int MAX_PACKET_ID = 0xFF;
    public final DirectionData TO_SERVER = new DirectionData(this, ProtocolConstants.Direction.TO_SERVER);
    public final DirectionData TO_CLIENT = new DirectionData(this, ProtocolConstants.Direction.TO_CLIENT);

    protected final void client(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, ProtocolMapping... mappings) {
        TO_CLIENT.registerPacket(packetClass, constructor, mappings);
    }

    protected final void client(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, RegisterType registerType, ProtocolMapping... mappings) {
        TO_CLIENT.registerPacket(packetClass, constructor, registerType, mappings);
    }

    protected final void server(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, ProtocolMapping... mappings) {
        TO_SERVER.registerPacket(packetClass, constructor, mappings);
    }

    protected final void server(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, RegisterType registerType, ProtocolMapping... mappings) {
        TO_SERVER.registerPacket(packetClass, constructor, registerType, mappings);
    }

    private static ProtocolMapping map(int protocol, int id) {
        return new ProtocolMapping(protocol, id);
    }

    private enum RegisterType {
        ENCODE,
        DECODE,
        BOTH;

        public boolean encode() {
            return this == BOTH || this == ENCODE;
        }

        public boolean decode() {
            return this == BOTH || this == DECODE;
        }
    }

    @Data
    private static class ProtocolData {
        private final int protocolVersion;
        private final Object2IntMap<Class<? extends DefinedPacket>> packetMap = new Object2IntOpenHashMap<>(MAX_PACKET_ID);
        @SuppressWarnings("unchecked")
        private final Supplier<? extends DefinedPacket>[] packetConstructors = new Supplier[MAX_PACKET_ID];
    }

    @Data
    private static class ProtocolMapping {
        private final int protocolVersion;
        private final int packetID;
    }

    public static final class DirectionData {
        private final Int2ObjectMap<ProtocolData> protocols = new Int2ObjectOpenHashMap<>();
        private final Protocol protocolPhase;
        @Getter
        private final ProtocolConstants.Direction direction;

        public DirectionData(Protocol protocolPhase, ProtocolConstants.Direction direction) {
            this.protocolPhase = protocolPhase;
            this.direction = direction;
            for (int protocol : ProtocolConstants.SUPPORTED_VERSION_IDS) {
                protocols.put(protocol, new ProtocolData(protocol));
            }
        }

        private ProtocolData getProtocolData(int version) {
            ProtocolData protocol = protocols.get(version);
            if (protocol == null && protocolPhase != Protocol.GAME) {
                protocol = protocols.values().stream().findFirst().orElse(null);
            }
            return protocol;
        }

        public boolean hasPacket(int i, boolean supportsForge) {
            return supportsForge || i >= 0 && i <= MAX_PACKET_ID;
        }

        public DefinedPacket createPacket(int id, int version) {
            return createPacket(id, version, true);
        }

        public DefinedPacket createPacket(int id, int version, boolean supportsForge) {
            ProtocolData protocolData = getProtocolData(version);
            if (protocolData == null) {
                throw new BadPacketException("Unsupported protocol version " + version);
            }
            if (!hasPacket(id, supportsForge)) {
                throw new BadPacketException("Packet with id " + id + " outside of range");
            }
            Supplier<? extends DefinedPacket> constructor = protocolData.packetConstructors[id];
            return (constructor == null) ? null : constructor.get();
        }

        private void registerPacket(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, ProtocolMapping... mappings) {
            registerPacket(packetClass, constructor, RegisterType.BOTH, mappings);
        }

        private void registerPacket(Class<? extends DefinedPacket> packetClass, Supplier<? extends DefinedPacket> constructor, RegisterType registerType, ProtocolMapping... mappings) {
            int mappingIndex = 0;
            ProtocolMapping mapping = mappings[mappingIndex];
            for (int protocol : ProtocolConstants.SUPPORTED_VERSION_IDS) {
                if (protocol < mapping.protocolVersion) {
                    continue;
                }
                if (mapping.protocolVersion < protocol && mappingIndex + 1 < mappings.length) {
                    ProtocolMapping nextMapping = mappings[mappingIndex + 1];
                    if (nextMapping.protocolVersion == protocol) {
                        Preconditions.checkState(nextMapping.packetID != mapping.packetID,
                                "Duplicate packet mapping (%s, %s)",
                                mapping.protocolVersion, nextMapping.protocolVersion);
                        mapping = nextMapping;
                        mappingIndex++;
                    }
                }
                if (mapping.packetID < 0) {
                    break;
                }
                ProtocolData data = protocols.get(protocol);
                Preconditions.checkState(data.packetConstructors[mapping.packetID] == null,
                        "Duplicate packet mapping (%s) (%s)", mapping.packetID,
                        mapping.getClass().getName());
                Preconditions.checkState(!data.packetMap.containsKey(packetClass),
                        "Duplicate packet mapping (%s) (%s)", mapping.packetID,
                        mapping.getClass().getName());
                if (registerType.encode()) {
                    data.packetMap.put(packetClass, mapping.packetID);
                }
                if (registerType.decode()) {
                    data.packetConstructors[mapping.packetID] = constructor;
                }
            }
        }

        public boolean hasPacket(Class<? extends DefinedPacket> packet, int version) {
            ProtocolData protocolData = getProtocolData(version);
            if (protocolData == null) {
                throw new BadPacketException("Unsupported protocol version");
            }
            return protocolData.packetMap.containsKey(packet);
        }

        int getId(Class<? extends DefinedPacket> packet, int version) {
            ProtocolData protocolData = getProtocolData(version);
            if (protocolData == null) {
                throw new BadPacketException("Unsupported protocol version");
            }
            final int packetId = protocolData.packetMap.get(packet);
            Preconditions.checkArgument(packetId >= 0,
                    "Cannot get ID for packet %s in phase %s with direction %s for protocol version %s",
                    packet, protocolPhase, direction, version);
            return packetId;
        }
    }
}
