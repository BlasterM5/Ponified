package com.minelittlepony.client.model.part;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import com.minelittlepony.api.model.IPart;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.pony.meta.Gender;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.mson.api.ModelContext;
import com.minelittlepony.mson.api.MsonModel;
import com.minelittlepony.mson.api.model.PartBuilder;

public class PonySnout implements IPart, MsonModel {

    private boolean visible = false;

    private ModelPart mare;
    private ModelPart stallion;

    public PonySnout(ModelPart tree) {

    }

    @Override
    public void init(ModelContext context) {
        mare = context.findByName("mare");
        stallion = context.findByName("stallion");

        PartBuilder head = context.getContext();
        head.addChild("mare", mare);
        head.addChild("stallion", stallion);
    }

    public void rotate(float x, float y, float z) {
        mare.setAngles(x, y, z);
        stallion.setAngles(x, y, z);
    }

    @Override
    public void renderPart(MatrixStack stack, VertexConsumer vertices, int overlayUv, int lightUv, float red, float green, float blue, float alpha, ModelAttributes attributes) {
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setGender(Gender gender) {
        boolean show = visible && MineLittlePony.getInstance().getConfig().snuzzles.get();

        mare.visible = (show && gender.isMare());
        stallion.visible = (show && gender.isStallion());
    }
}
