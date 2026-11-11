package de.c20za.hitdistance.mixin;

import de.c20za.hitdistance.HitDistanceClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "handleDamageEvent", at = @At("HEAD"))
    private void hitdistance$captureDamage(ClientboundDamageEventPacket packet, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null || packet.entityId() != client.player.getId()) return;

        Entity cause = client.level.getEntity(packet.sourceCauseId());
        if (!(cause instanceof Player attacker) || attacker == client.player) return;

        double distance = attacker.position().distanceTo(client.player.position());
        HitDistanceClient.recordHit(attacker.getName().getString(), distance);
    }
}
