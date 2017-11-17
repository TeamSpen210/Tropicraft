package net.tropicraft.core.common.block.tileentity.message;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.tropicraft.core.common.block.tileentity.TileEntityDrinkMixer;

public class MessageMixerInventory extends MessageTileEntity<TileEntityDrinkMixer> {

	@Nonnull
	private NonNullList<ItemStack> inventory;
	@Nonnull
	private ItemStack result = ItemStack.EMPTY;

	public MessageMixerInventory() {
		super();
	}

	public MessageMixerInventory(TileEntityDrinkMixer mixer) {
		super(mixer);
		this.inventory = mixer.ingredients;
		this.result = mixer.result;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeByte(inventory.size());
		for (ItemStack i : inventory) {
			ByteBufUtils.writeItemStack(buf, i);
		}
		ByteBufUtils.writeItemStack(buf, result);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.inventory = NonNullList.withSize(buf.readByte(), ItemStack.EMPTY);
		for (int i = 0; i < inventory.size(); i++) {
			this.inventory.set(i, ByteBufUtils.readItemStack(buf));
		}
		this.result = ByteBufUtils.readItemStack(buf);
	}

	public static final class Handler implements IMessageHandler<MessageMixerInventory, IMessage> {

		@Override
		public IMessage onMessage(MessageMixerInventory message, MessageContext ctx) {
			TileEntityDrinkMixer mixer = message.getClientTileEntity();
			if (mixer != null) {
				mixer.ingredients = message.inventory;
				mixer.result = message.result;
			}
			return null;
		}
	}
}
