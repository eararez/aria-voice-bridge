package fr.cofear.ariavoicebridge;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class AriaVoicePlayer {

    public static int playToAll(MinecraftServer server, ServerLevel level, short[] audio) {
        VoicechatServerApi api = AriaVoicechatPlugin.SERVER_API;
        if (api == null) {
            System.out.println("[A.R.I.A Voice Bridge] API Simple Voice Chat non prête.");
            return 0;
        }

        int count = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            VoicechatConnection connection = api.getConnectionOf(player.getUUID());
            if (connection == null) {
                continue;
            }

            StaticAudioChannel channel = api.createStaticAudioChannel(
                    UUID.randomUUID(),
                    api.fromServerLevel(level),
                    connection
            );

            if (channel == null) {
                continue;
            }

            AudioPlayer audioPlayer = api.createAudioPlayer(channel, api.createEncoder(), audio);
            audioPlayer.setOnStopped(channel::close);
            audioPlayer.startPlaying();
            count++;
        }

        return count;
    }
}
