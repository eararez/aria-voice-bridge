package fr.cofear.ariavoicebridge;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;

@ForgeVoicechatPlugin
public class AriaVoicechatPlugin implements VoicechatPlugin {

    public static VoicechatServerApi SERVER_API;

    @Override
    public String getPluginId() {
        return AriaVoiceBridge.MODID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        // Rien à faire ici pour le moment.
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        SERVER_API = event.getVoicechat();
        System.out.println("[A.R.I.A Voice Bridge] Simple Voice Chat API prête.");
    }
}
