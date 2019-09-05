package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;

public class SecretTileEntity extends TileEntity {

    private final SecretData data = new SecretData(this);

    public SecretTileEntity() {
        super(SecretTileEntities.SECRET_TILE_ENTITY);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("secret_data", this.data.writeNBT(new CompoundNBT()));
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.data.readNBT(compound.getCompound("secret_data"));
    }

    @Override
    public void remove() {
        super.remove();
        if (this.world.isRemote) {
            this.requestModelDataUpdate();
            ModelDataManager.getModelData(new SetTileEntityWorld((ClientWorld) this.world, this.pos, this), this.pos); //Update it with the
        }
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        if(this.removed) {
            return super.getModelData();
        }
        ModelDataMap.Builder builder = new ModelDataMap.Builder()
                .withInitial(SecretModelData.SRM_BASESTATE, this.getWorld().getBlockState(this.getPos()))
                .withInitial(SecretModelData.SRM_DO_RENDER, () -> !this.removed);
        BlockState state = this.world.getBlockState(this.pos);
        if(state.getBlock() instanceof SecretBaseBlock) {
            builder.withInitial(SecretModelData.SRM_RENDER, ((SecretBaseBlock) state.getBlock()).getProvider(this.world, this.pos, state));
            ((SecretBaseBlock) state.getBlock()).applyExtraModelData(this.world, this.pos, state, builder);
        }
        return builder.build();
    }

    public SecretData getData() {
        return this.data;
    }
}
