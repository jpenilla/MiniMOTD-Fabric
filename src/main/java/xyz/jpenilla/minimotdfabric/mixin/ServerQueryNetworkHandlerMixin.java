package xyz.jpenilla.minimotdfabric.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.network.ServerQueryNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.jpenilla.minimotdfabric.Config;
import xyz.jpenilla.minimotdfabric.MiniMOTDFabric;

@Mixin(ServerQueryNetworkHandler.class)
public class ServerQueryNetworkHandlerMixin {
    @Shadow @Final private MinecraftServer server;
    @Shadow private boolean responseSent;

    @Inject(method = "onRequest", at = @At("HEAD"))
    public void onRequest(QueryRequestC2SPacket packet, CallbackInfo ci) {
        if (!this.responseSent) {
            final Config config = MiniMOTDFabric.INSTANCE.getConfig();

            int onlinePlayers = server.getCurrentPlayerCount();
            if (config.getFakePlayers().isEnabled()) {
                onlinePlayers = config.getFakePlayers().fakePlayers(onlinePlayers);
            }
            int maxPlayers = config.getMaxPlayers().getAdjustedMaxPlayers(onlinePlayers, server.getServerMetadata().getPlayers().getPlayerLimit());

            if (config.getMotd().isEnabled()) {
                server.getServerMetadata().setDescription(config.getMotd().getMotd(maxPlayers, onlinePlayers, MiniMOTDFabric.INSTANCE.getProtocolVersionCache()));
            }
            final GameProfile[] oldSample = server.getServerMetadata().getPlayers().getSample();
            final ServerMetadata.Players newPlayers = new ServerMetadata.Players(maxPlayers, onlinePlayers);
            newPlayers.setSample(oldSample);
            server.getServerMetadata().setPlayers(newPlayers);
        }
    }
}
