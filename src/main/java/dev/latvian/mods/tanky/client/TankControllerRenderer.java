package dev.latvian.mods.tanky.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import dev.latvian.mods.tanky.block.entity.TankControllerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class TankControllerRenderer implements BlockEntityRenderer<TankControllerBlockEntity> {

	@Override
	public void render(TankControllerBlockEntity entity, float delta, PoseStack matrices, MultiBufferSource source, int light0, int overlay) {
		if (entity.tank.isEmpty()) {
			return;
		}

		int cap = Math.max(entity.tank.getCapacity(), entity.tank.getFluidAmount());

		if (cap <= 0) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		Matrix4f m = matrices.last().pose();
		int light = LevelRenderer.getLightColor(entity.getLevel(), entity.tank.getFluid().getFluid().defaultFluidState().createLegacyBlock(), entity.getBlockPos().above());

		FluidStack fluid = entity.tank.getFluid();
		var renderProperties = ((IClientFluidTypeExtensions)fluid.getFluid().getFluidType().getRenderPropertiesInternal());

		VertexConsumer builder = source.getBuffer(RenderType.translucent());

		mc.getTextureManager().bindForSetup(TextureAtlas.LOCATION_BLOCKS);
		TextureAtlasSprite sprite = mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(renderProperties.getStillTexture(fluid));

		Matrix3f n = matrices.last().normal();
		int color = renderProperties.getTintColor(fluid);
		float r = ((color >> 16) & 255) / 255F;
		float g = ((color >> 8) & 255) / 255F;
		float b = ((color >> 0) & 255) / 255F;
		float a = 1F;

		float y = Math.min(entity.tank.getFluidAmount() / (float) cap * (entity.height - 1F) + 0.01F, entity.height - 1F);

		float u0 = sprite.getU0();
		float v0 = sprite.getV0();
		float u1 = sprite.getU1();
		float vty = y % 1F;
		float vt = sprite.getV(vty == 0F ? 16D : (vty * 16D));
		float v1 = sprite.getV1();

		int tr = entity.radius - 1;

		for (int x = -tr; x <= tr; x++) {
			for (int z = -tr; z <= tr; z++) {
				builder.vertex(m, x + 0F, y + 1F, z + 0F).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, x + 0F, y + 1F, z + 1F).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, x + 1F, y + 1F, z + 1F).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, x + 1F, y + 1F, z + 0F).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			}
		}

		int ay = 0;

		while (y >= 1F) {
			y -= 1F;

			for (int i = -tr; i <= tr; i++) {
				builder.vertex(m, 0F + i, ay + 2F, 1F + tr).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 0F + i, ay + 1F, 1F + tr).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 1F + i, ay + 1F, 1F + tr).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 1F + i, ay + 2F, 1F + tr).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();

				builder.vertex(m, 0F + i, ay + 2F, 0F - tr).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 1F + i, ay + 2F, 0F - tr).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 1F + i, ay + 1F, 0F - tr).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 0F + i, ay + 1F, 0F - tr).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();

				builder.vertex(m, 0F - tr, ay + 2F, 0F + i).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 0F - tr, ay + 1F, 0F + i).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 0F - tr, ay + 1F, 1F + i).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 0F - tr, ay + 2F, 1F + i).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();

				builder.vertex(m, 1F + tr, ay + 2F, 0F + i).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 1F + tr, ay + 2F, 1F + i).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 1F + tr, ay + 1F, 1F + i).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
				builder.vertex(m, 1F + tr, ay + 1F, 0F + i).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			}

			ay++;
		}

		if (y <= 0F) {
			return;
		}

		float yo = 1F + y;

		for (int i = -tr; i <= tr; i++) {
			builder.vertex(m, 0F + i, ay + yo, 1F + tr).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 0F + i, ay + 1F, 1F + tr).color(r, g, b, a).uv(u0, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 1F + i, ay + 1F, 1F + tr).color(r, g, b, a).uv(u1, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 1F + i, ay + yo, 1F + tr).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();

			builder.vertex(m, 0F + i, ay + yo, 0F - tr).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 1F + i, ay + yo, 0F - tr).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 1F + i, ay + 1F, 0F - tr).color(r, g, b, a).uv(u1, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 0F + i, ay + 1F, 0F - tr).color(r, g, b, a).uv(u0, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();

			builder.vertex(m, 0F - tr, ay + yo, 0F + i).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 0F - tr, ay + 1F, 0F + i).color(r, g, b, a).uv(u0, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 0F - tr, ay + 1F, 1F + i).color(r, g, b, a).uv(u1, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 0F - tr, ay + yo, 1F + i).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();

			builder.vertex(m, 1F + tr, ay + yo, 0F + i).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 1F + tr, ay + yo, 1F + i).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 1F + tr, ay + 1F, 1F + i).color(r, g, b, a).uv(u1, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
			builder.vertex(m, 1F + tr, ay + 1F, 0F + i).color(r, g, b, a).uv(u0, vt).overlayCoords(overlay).uv2(light).normal(n, 0F, 1F, 0F).endVertex();
		}
	}
}
