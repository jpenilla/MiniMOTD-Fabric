package xyz.jpenilla.minimotdfabric.mixin;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.jpenilla.minimotdfabric.MiniMOTDFabric;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onHandshake", at = @At("HEAD"))
    public void onRequest(HandshakeC2SPacket packet, CallbackInfo ci) {
        if (packet.getIntendedState() == NetworkState.STATUS && this.server.acceptsStatusQuery()) {
            MiniMOTDFabric.INSTANCE.setProtocolVersionCache(packet.getProtocolVersion());
        }
    }
}
