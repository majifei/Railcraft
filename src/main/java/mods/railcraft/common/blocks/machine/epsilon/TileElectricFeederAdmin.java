package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.api.electricity.IElectricGridObject;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileElectricFeederAdmin extends TileMachineBase implements IElectricGridObject {

    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 0.0);
    private boolean powered;

    @Override
    public void onNeighborBlockChange(Block block) {
        super.onNeighborBlockChange(block);
        checkRedstone();
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        checkRedstone();
    }

    private void checkRedstone() {
        if (Game.isNotHost(getWorld()))
            return;
        boolean p = PowerPlugin.isBlockBeingPowered(worldObj, getPos());
        if (powered != p) {
            powered = p;
            sendUpdateToClient();
        }
    }

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(getWorld()))
            return;

        if (powered) {
            double capacity = chargeHandler.getCapacity();
            try {
                chargeHandler.setCharge(capacity);
            } catch (Throwable err) {
                chargeHandler.addCharge(capacity - chargeHandler.getCharge());
                Game.logErrorAPI("Railcraft", err, IElectricGridObject.class);
            }
        }
        chargeHandler.tick();
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineEpsilon.ELECTRIC_FEEDER_ADMIN;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
        powered = data.getBoolean("powered");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
        data.setBoolean("powered", powered);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (powered != p) {
            powered = p;
            markBlockForUpdate();
        }
    }
}
