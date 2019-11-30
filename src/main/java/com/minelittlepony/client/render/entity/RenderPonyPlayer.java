package com.minelittlepony.client.render.entity;

import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.client.model.ClientPonyModel;
import com.minelittlepony.client.model.ModelWrapper;
import com.minelittlepony.client.model.gear.SaddleBags;
import com.minelittlepony.client.render.DebugBoundingBoxRenderer;
import com.minelittlepony.client.render.IPonyRenderContext;
import com.minelittlepony.client.render.EquineRenderManager.Mode;
import com.minelittlepony.client.render.EquineRenderManager;
import com.minelittlepony.client.render.entity.feature.LayerDJPon3Head;
import com.minelittlepony.client.render.entity.feature.LayerEntityOnPonyShoulder;
import com.minelittlepony.client.render.entity.feature.LayerGear;
import com.minelittlepony.client.render.entity.feature.LayerHeldPonyItemMagical;
import com.minelittlepony.client.render.entity.feature.LayerPonyArmor;
import com.minelittlepony.client.render.entity.feature.LayerPonyCape;
import com.minelittlepony.client.render.entity.feature.LayerPonyCustomHead;
import com.minelittlepony.client.render.entity.feature.LayerPonyElytra;
import com.minelittlepony.mson.api.ModelKey;
import com.minelittlepony.pony.IPony;
import com.minelittlepony.pony.meta.Race;
import com.minelittlepony.pony.meta.Wearable;

import java.util.List;

import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RenderPonyPlayer extends PlayerEntityRenderer implements IPonyRenderContext<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> {

    protected final EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> manager = new EquineRenderManager<>(this);

    public RenderPonyPlayer(EntityRenderDispatcher dispatcher, boolean slim, ModelKey<? extends ClientPonyModel<AbstractClientPlayerEntity>> key) {
        super(dispatcher, slim);

        this.model = manager.setModel(key).getBody();

        addLayers();
    }

    protected void addLayers() {
        features.clear();

        addLayer(new LayerDJPon3Head<>(this));
        addLayer(new LayerPonyArmor<>(this));
        addFeature(new StuckArrowsFeatureRenderer<>(this));
        addLayer(new LayerPonyCustomHead<>(this));
        addLayer(new LayerPonyElytra<>(this));
        addLayer(new LayerHeldPonyItemMagical<>(this));
        addLayer(new LayerPonyCape<>(this));
        addLayer(new LayerEntityOnPonyShoulder<>(this));
        addLayer(new LayerGear<>(this));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected boolean addLayer(FeatureRenderer<AbstractClientPlayerEntity, ? extends ClientPonyModel<AbstractClientPlayerEntity>> feature) {
        return ((List)features).add(feature);
    }

    @Override
    protected void scale(AbstractClientPlayerEntity player, MatrixStack stack, float ticks) {
        manager.preRenderCallback(player, stack, ticks);

        if (player.hasVehicle()) {
            stack.translate(0, player.getHeightOffset(), 0);
        }
    }

    @Override
    public void render(AbstractClientPlayerEntity entity, float entityYaw, float tickDelta, MatrixStack stack, VertexConsumerProvider renderContext, int lightUv) {
        field_4673 = manager.getShadowScale();
        super.render(entity, entityYaw, tickDelta, stack, renderContext, lightUv);

        DebugBoundingBoxRenderer.render(manager.getPony(entity), entity, stack, renderContext);

        // Translate the shadow position after everything is done
        // (shadows are drawn after us)
        // TODO: Get a proper shadow renderer going
        if (!entity.hasVehicle() && !entity.isSleeping()) {
            float yaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
            float l = entity.getWidth() / 2 * manager.getPony(entity).getMetadata().getSize().getScaleFactor();

            stack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(yaw));
            stack.translate(0, 0, -l);
        }

    }

    @Override
    public boolean isVisible(AbstractClientPlayerEntity entity, Frustum camera, double camX, double camY, double camZ) {
        if (entity.isSleeping() && entity == MinecraftClient.getInstance().player) {
            return true;
        }
        return super.isVisible(entity, manager.getFrustrum(entity, camera), camX, camY, camZ);
    }

    @Override
    protected void renderLabelIfPresent(AbstractClientPlayerEntity entity, String name, MatrixStack stack, VertexConsumerProvider renderContext, int maxDistance) {
        if (entity.isSleeping()) {
            if (entity.getSleepingPosition().isPresent() && entity.getEntityWorld().getBlockState(entity.getSleepingPosition().get()).getBlock() instanceof BedBlock) {
                double bedRad = Math.toRadians(entity.getSleepingDirection().asRotation());

                stack.translate(Math.cos(bedRad), 0, -Math.sin(bedRad));
            }
        }
        stack.translate(0, manager.getNamePlateYOffset(entity), 0);
        super.renderLabelIfPresent(entity, name, stack, renderContext, maxDistance);
    }

    @Override
    public final void renderRightArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player) {
        renderArm(stack, renderContext, lightUv, player, Arm.RIGHT);
    }

    @Override
    public final void renderLeftArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player) {
        renderArm(stack, renderContext, lightUv, player, Arm.LEFT);
    }

    protected void renderArm(MatrixStack stack, VertexConsumerProvider renderContext, int lightUv, AbstractClientPlayerEntity player, Arm side) {
        manager.updateModel(player, Mode.FIRST_PERSON);

        stack.push();
        float reflect = side == Arm.LEFT ? 1 : -1;

        stack.translate(reflect * 0.1F, -0.54F, 0);

        if (side == Arm.LEFT) {
            super.renderLeftArm(stack, renderContext, lightUv, player);
        } else {
            super.renderRightArm(stack, renderContext, lightUv, player);
        }

        stack.pop();
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity entity, MatrixStack stack, float ageInTicks, float rotationYaw, float partialTicks) {
        rotationYaw = manager.getRenderYaw(entity, rotationYaw, partialTicks);
        super.setupTransforms(entity, stack, ageInTicks, rotationYaw, partialTicks);

        manager.applyPostureTransform(entity, stack, rotationYaw, partialTicks);
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity player) {
        return manager.getPony(player).getTexture();
    }

    @Override
    public ModelWrapper<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getModelWrapper() {
        return manager.playerModel;
    }

    @Override
    public EquineRenderManager<AbstractClientPlayerEntity, ClientPonyModel<AbstractClientPlayerEntity>> getInternalRenderer() {
        return manager;
    }

    @Override
    public Identifier findTexture(AbstractClientPlayerEntity entity) {
        return getTexture(entity);
    }

    @Override
    public IPony getEntityPony(AbstractClientPlayerEntity entity) {
        return MineLittlePony.getInstance().getManager().getPony(entity);
    }

    @Override
    public Identifier getDefaultTexture(AbstractClientPlayerEntity entity, Wearable wearable) {
        if (wearable == Wearable.SADDLE_BAGS) {
            if (getInternalRenderer().getModel().getMetadata().getRace() == Race.BATPONY) {
                return SaddleBags.TEXTURE;
            }
        }

        return IPonyRenderContext.super.getDefaultTexture(entity, wearable);
    }
}
