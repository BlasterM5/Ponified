package com.minelittlepony.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;

import com.minelittlepony.client.model.ModelType;
import com.minelittlepony.client.model.entity.ZomponyModel;
import com.minelittlepony.client.render.entity.npc.textures.TextureSupplier;

public class ZomponyRenderer<Zombie extends HostileEntity> extends PonyRenderer<Zombie, ZomponyModel<Zombie>> {

    public static final Identifier ZOMBIE = new Identifier("minelittlepony", "textures/entity/zombie/zombie_pony.png");
    public static final Identifier HUSK = new Identifier("minelittlepony", "textures/entity/zombie/husk_pony.png");
    public static final Identifier DROWNED = new Identifier("minelittlepony", "textures/entity/zombie/drowned_pony.png");

    public ZomponyRenderer(EntityRendererFactory.Context context, Identifier texture, float scale) {
        super(context, ModelType.ZOMBIE, TextureSupplier.of(texture), scale);
    }

    public static ZomponyRenderer<ZombieEntity> zombie(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, ZOMBIE, 1);
    }

    public static ZomponyRenderer<HuskEntity> husk(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, HUSK, 1.0625F);
    }

    public static ZomponyRenderer<DrownedEntity> drowned(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, DROWNED, 1);
    }

    public static ZomponyRenderer<GiantEntity> giant(EntityRendererFactory.Context context) {
        return new ZomponyRenderer<>(context, ZOMBIE, 3);
    }
}
