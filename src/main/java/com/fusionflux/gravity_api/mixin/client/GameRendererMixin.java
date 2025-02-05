package com.fusionflux.gravity_api.mixin.client;

import com.fusionflux.gravity_api.MCXRCompat;
import com.fusionflux.gravity_api.RotationAnimation;
import com.fusionflux.gravity_api.api.GravityChangerAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Camera camera;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    private void inject_renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (this.camera.getFocusedEntity() == null) {
            return;
        }
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && MCXRCompat.isPlayerInVR(player)) {
            return;
        }

        Entity focusedEntity = this.camera.getFocusedEntity();
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(focusedEntity);
        Optional<RotationAnimation> animationOptional = GravityChangerAPI.getGravityAnimation(focusedEntity);
        if (animationOptional.isEmpty()) return;
        RotationAnimation animation = animationOptional.get();
        long timeMs = focusedEntity.world.getTime() * 50 + (long) (tickDelta * 50);
        Quaternion currentGravityRotation = animation.getCurrentGravityRotation(gravityDirection, timeMs);
        matrix.multiply(currentGravityRotation);
    }
}
