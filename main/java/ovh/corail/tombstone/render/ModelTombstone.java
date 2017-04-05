package ovh.corail.tombstone.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelTombstone extends ModelBase {
	public ModelRenderer Base01;
	public ModelRenderer Base02;
	public ModelRenderer Body01;
	public ModelRenderer Body01_1;
	public ModelRenderer Top01;
	public ModelRenderer CrossBase;
	public ModelRenderer shape8;
	public ModelRenderer shape8_1;
	public ModelRenderer CrossMiddleUp;
	public ModelRenderer CrossMiddleDown;
	public ModelRenderer shape8_2;
	public ModelRenderer shape13;
	public ModelRenderer shape13_1;

	public ModelTombstone() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.shape8_1 = new ModelRenderer(this, 0, 0);
		this.shape8_1.setRotationPoint(-0.25F, 14.5F, -2.05F);
		this.shape8_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
		this.Top01 = new ModelRenderer(this, 0, 0);
		this.Top01.setRotationPoint(-2.0F, 18.5F, -6.0F);
		this.Top01.addBox(0.0F, 0.0F, 0.0F, 4, 1, 12, 0.0F);
		this.Body01 = new ModelRenderer(this, 0, 0);
		this.Body01.setRotationPoint(-2.0F, 19.5F, -6.0F);
		this.Body01.addBox(0.0F, 0.0F, 0.0F, 4, 4, 12, 0.0F);
		this.Base01 = new ModelRenderer(this, 0, 0);
		this.Base01.setRotationPoint(3.0F, 23.5F, 0.5F);
		this.Base01.addBox(-8.0F, 0.0F, -8.0F, 10, 1, 15, 0.0F);
		this.CrossMiddleDown = new ModelRenderer(this, 0, 0);
		this.CrossMiddleDown.setRotationPoint(-0.25F, 15.0F, -1.0F);
		this.CrossMiddleDown.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
		this.shape13_1 = new ModelRenderer(this, 0, 0);
		this.shape13_1.setRotationPoint(-0.5F, 17.5F, -0.5F);
		this.shape13_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.shape8_2 = new ModelRenderer(this, 0, 0);
		this.shape8_2.setRotationPoint(-0.25F, 13.0F, -0.25F);
		this.shape8_2.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.shape8 = new ModelRenderer(this, 0, 0);
		this.shape8.setRotationPoint(-0.25F, 14.5F, 0.45F);
		this.shape8.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
		this.Base02 = new ModelRenderer(this, 0, 0);
		this.Base02.setRotationPoint(-4.5F, 23.0F, -7.0F);
		this.Base02.addBox(0.0F, 0.0F, 0.0F, 9, 1, 14, 0.0F);
		this.CrossBase = new ModelRenderer(this, 0, 0);
		this.CrossBase.setRotationPoint(-0.25F, 15.5F, -0.25F);
		this.CrossBase.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
		this.Body01_1 = new ModelRenderer(this, 0, 0);
		this.Body01_1.setRotationPoint(-2.5F, 19.0F, -6.5F);
		this.Body01_1.addBox(0.0F, 0.0F, 0.0F, 5, 1, 13, 0.0F);
		this.shape13 = new ModelRenderer(this, 0, 0);
		this.shape13.setRotationPoint(-1.05F, 18.0F, -1.0F);
		this.shape13.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
		this.CrossMiddleUp = new ModelRenderer(this, 0, 0);
		this.CrossMiddleUp.setRotationPoint(-0.25F, 14.0F, -1.0F);
		this.CrossMiddleUp.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.shape8_1.offsetX, this.shape8_1.offsetY, this.shape8_1.offsetZ);
		GlStateManager.translate(this.shape8_1.rotationPointX * f5, this.shape8_1.rotationPointY * f5, this.shape8_1.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.translate(-this.shape8_1.offsetX, -this.shape8_1.offsetY, -this.shape8_1.offsetZ);
		GlStateManager.translate(-this.shape8_1.rotationPointX * f5, -this.shape8_1.rotationPointY * f5, -this.shape8_1.rotationPointZ * f5);
		this.shape8_1.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.Top01.offsetX, this.Top01.offsetY, this.Top01.offsetZ);
		GlStateManager.translate(this.Top01.rotationPointX * f5, this.Top01.rotationPointY * f5, this.Top01.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 0.5D, 1.0D);
		GlStateManager.translate(-this.Top01.offsetX, -this.Top01.offsetY, -this.Top01.offsetZ);
		GlStateManager.translate(-this.Top01.rotationPointX * f5, -this.Top01.rotationPointY * f5, -this.Top01.rotationPointZ * f5);
		this.Top01.render(f5);
		GlStateManager.popMatrix();
		this.Body01.render(f5);
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.Base01.offsetX, this.Base01.offsetY, this.Base01.offsetZ);
		GlStateManager.translate(this.Base01.rotationPointX * f5, this.Base01.rotationPointY * f5, this.Base01.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 0.5D, 1.0D);
		GlStateManager.translate(-this.Base01.offsetX, -this.Base01.offsetY, -this.Base01.offsetZ);
		GlStateManager.translate(-this.Base01.rotationPointX * f5, -this.Base01.rotationPointY * f5, -this.Base01.rotationPointZ * f5);
		this.Base01.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.CrossMiddleDown.offsetX, this.CrossMiddleDown.offsetY, this.CrossMiddleDown.offsetZ);
		GlStateManager.translate(this.CrossMiddleDown.rotationPointX * f5, this.CrossMiddleDown.rotationPointY * f5, this.CrossMiddleDown.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 0.5D, 1.0D);
		GlStateManager.translate(-this.CrossMiddleDown.offsetX, -this.CrossMiddleDown.offsetY, -this.CrossMiddleDown.offsetZ);
		GlStateManager.translate(-this.CrossMiddleDown.rotationPointX * f5, -this.CrossMiddleDown.rotationPointY * f5, -this.CrossMiddleDown.rotationPointZ * f5);
		this.CrossMiddleDown.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.shape13_1.offsetX, this.shape13_1.offsetY, this.shape13_1.offsetZ);
		GlStateManager.translate(this.shape13_1.rotationPointX * f5, this.shape13_1.rotationPointY * f5, this.shape13_1.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 0.5D, 1.0D);
		GlStateManager.translate(-this.shape13_1.offsetX, -this.shape13_1.offsetY, -this.shape13_1.offsetZ);
		GlStateManager.translate(-this.shape13_1.rotationPointX * f5, -this.shape13_1.rotationPointY * f5, -this.shape13_1.rotationPointZ * f5);
		this.shape13_1.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.shape8_2.offsetX, this.shape8_2.offsetY, this.shape8_2.offsetZ);
		GlStateManager.translate(this.shape8_2.rotationPointX * f5, this.shape8_2.rotationPointY * f5, this.shape8_2.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.translate(-this.shape8_2.offsetX, -this.shape8_2.offsetY, -this.shape8_2.offsetZ);
		GlStateManager.translate(-this.shape8_2.rotationPointX * f5, -this.shape8_2.rotationPointY * f5, -this.shape8_2.rotationPointZ * f5);
		this.shape8_2.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.shape8.offsetX, this.shape8.offsetY, this.shape8.offsetZ);
		GlStateManager.translate(this.shape8.rotationPointX * f5, this.shape8.rotationPointY * f5, this.shape8.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 0.5D, 0.5D);
		GlStateManager.translate(-this.shape8.offsetX, -this.shape8.offsetY, -this.shape8.offsetZ);
		GlStateManager.translate(-this.shape8.rotationPointX * f5, -this.shape8.rotationPointY * f5, -this.shape8.rotationPointZ * f5);
		this.shape8.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.Base02.offsetX, this.Base02.offsetY, this.Base02.offsetZ);
		GlStateManager.translate(this.Base02.rotationPointX * f5, this.Base02.rotationPointY * f5, this.Base02.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 0.5D, 1.0D);
		GlStateManager.translate(-this.Base02.offsetX, -this.Base02.offsetY, -this.Base02.offsetZ);
		GlStateManager.translate(-this.Base02.rotationPointX * f5, -this.Base02.rotationPointY * f5, -this.Base02.rotationPointZ * f5);
		this.Base02.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.CrossBase.offsetX, this.CrossBase.offsetY, this.CrossBase.offsetZ);
		GlStateManager.translate(this.CrossBase.rotationPointX * f5, this.CrossBase.rotationPointY * f5, this.CrossBase.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 1.0D, 0.5D);
		GlStateManager.translate(-this.CrossBase.offsetX, -this.CrossBase.offsetY, -this.CrossBase.offsetZ);
		GlStateManager.translate(-this.CrossBase.rotationPointX * f5, -this.CrossBase.rotationPointY * f5, -this.CrossBase.rotationPointZ * f5);
		this.CrossBase.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.Body01_1.offsetX, this.Body01_1.offsetY, this.Body01_1.offsetZ);
		GlStateManager.translate(this.Body01_1.rotationPointX * f5, this.Body01_1.rotationPointY * f5, this.Body01_1.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 0.5D, 1.0D);
		GlStateManager.translate(-this.Body01_1.offsetX, -this.Body01_1.offsetY, -this.Body01_1.offsetZ);
		GlStateManager.translate(-this.Body01_1.rotationPointX * f5, -this.Body01_1.rotationPointY * f5, -this.Body01_1.rotationPointZ * f5);
		this.Body01_1.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.shape13.offsetX, this.shape13.offsetY, this.shape13.offsetZ);
		GlStateManager.translate(this.shape13.rotationPointX * f5, this.shape13.rotationPointY * f5, this.shape13.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 0.5D, 1.0D);
		GlStateManager.translate(-this.shape13.offsetX, -this.shape13.offsetY, -this.shape13.offsetZ);
		GlStateManager.translate(-this.shape13.rotationPointX * f5, -this.shape13.rotationPointY * f5, -this.shape13.rotationPointZ * f5);
		this.shape13.render(f5);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.CrossMiddleUp.offsetX, this.CrossMiddleUp.offsetY, this.CrossMiddleUp.offsetZ);
		GlStateManager.translate(this.CrossMiddleUp.rotationPointX * f5, this.CrossMiddleUp.rotationPointY * f5, this.CrossMiddleUp.rotationPointZ * f5);
		GlStateManager.scale(0.5D, 0.5D, 1.0D);
		GlStateManager.translate(-this.CrossMiddleUp.offsetX, -this.CrossMiddleUp.offsetY, -this.CrossMiddleUp.offsetZ);
		GlStateManager.translate(-this.CrossMiddleUp.rotationPointX * f5, -this.CrossMiddleUp.rotationPointY * f5, -this.CrossMiddleUp.rotationPointZ * f5);
		this.CrossMiddleUp.render(f5);
		GlStateManager.popMatrix();
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
