package moze_intel.projecte.utils;

import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SetFlyPKT;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.network.packets.SwingItemPKT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;

/**
 * Helper class for player-related methods.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class PlayerHelper
{
	public static void disableFlight(EntityPlayerMP playerMP)
	{
		if (!playerMP.capabilities.isCreativeMode)
		{
			updateClientServerFlight(playerMP, false);
			PlayerChecks.removePlayerFlyChecks(playerMP);
		}
	}

	public static void enableFlight(EntityPlayerMP playerMP)
	{
		if (playerMP.capabilities.isCreativeMode)
		{
			return;
		}

		updateClientServerFlight(playerMP, true);
		PlayerChecks.addPlayerFlyChecks(playerMP);
	}

	public static Coordinates getBlockLookingAt(EntityPlayer player, double maxDistance)
	{
		Tuple vecs = getLookVec(player, maxDistance);
		MovingObjectPosition mop = player.worldObj.rayTraceBlocks(((Vec3) vecs.getFirst()), ((Vec3) vecs.getSecond()));
		if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			return new Coordinates(mop.blockX, mop.blockY, mop.blockZ);
		}
		return null;
	}

	/**
	 * Returns a vec representing where the player is looking, capped at maxDistance away.
	 */
	public static Tuple getLookVec(EntityPlayer player, double maxDistance)
	{
		// Thank you ForgeEssentials
		Vec3 look = player.getLook(1.0F);
		Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY + (player.getEyeHeight() - player.getDefaultEyeHeight()), player.posZ);
		Vec3 src = playerPos.addVector(0, player.getEyeHeight(), 0);
		Vec3 dest = src.addVector(look.xCoord * maxDistance, look.yCoord * maxDistance, look.zCoord * maxDistance);
		return new Tuple(src, dest);
	}

	public static void setPlayerFireImmunity(EntityPlayer player, boolean value)
	{
		ReflectionHelper.setEntityFireImmunity(player, value);
	}

	public static void setPlayerWalkSpeed(EntityPlayer player, float value)
	{
		ReflectionHelper.setPlayerCapabilityWalkspeed(player.capabilities, value);
	}

	public static void swingItem(EntityPlayerMP player)
	{
		PacketHandler.sendTo(new SwingItemPKT(), player);
	}

	public static void updateClientServerFlight(EntityPlayerMP player, boolean state)
	{
		PacketHandler.sendTo(new SetFlyPKT(state), player);
		player.capabilities.allowFlying = state;

		if (!state)
		{
			player.capabilities.isFlying = false;
		}
	}

	public static void updateClientStepHeight(EntityPlayerMP player, float value)
	{
		PacketHandler.sendTo(new StepHeightPKT(value), player);
	}
}
