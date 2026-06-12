package fr.cofear.ariavoicebridge;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod(AriaVoiceBridge.MODID)
public class AriaVoiceBridge {

    public static final String MODID = "ariavoicebridge";

    public AriaVoiceBridge() {
        // Le mod principal ne fait que déclarer la commande.
        // Le plugin Simple Voice Chat est dans AriaVoicechatPlugin.
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Events {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(
                    Commands.literal("aria_voice_url")
                            .requires(source -> source.hasPermission(2))
                            .then(Commands.argument("url", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String url = StringArgumentType.getString(ctx, "url");
                                        var source = ctx.getSource();
                                        var server = source.getServer();

                                        source.sendSuccess(new TextComponent("A.R.I.A récupère l'audio..."), true);

                                        CompletableFuture.runAsync(() -> {
                                            try {
                                                short[] audio = WavLoader.load48kMonoPcmFromUrl(url);
                                                server.execute(() -> {
                                                    int count = AriaVoicePlayer.playToAll(server, source.getLevel(), audio);
                                                    source.sendSuccess(new TextComponent("A.R.I.A envoyée en vocal à " + count + " joueur(s)."), true);
                                                });
                                            } catch (Exception e) {
                                                server.execute(() -> source.sendFailure(new TextComponent("Erreur A.R.I.A voice: " + e.getMessage())));
                                                e.printStackTrace();
                                            }
                                        });

                                        return 1;
                                    }))
            );
        }
    }
}
