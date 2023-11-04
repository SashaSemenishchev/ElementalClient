package me.mrfunny.elementalclient.event

import me.mrfunny.elementalclient.modules.Module
import net.minecraft.block.Block
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.Entity
import net.minecraft.network.Packet
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraft.util.IChatComponent

/**
 * Called when player attacks other entity
 *
 * @param targetEntity Attacked entity
 */
class AttackEvent(val targetEntity: Entity?) : Event()

/**
 * Called when minecraft get bounding box of block
 *
 * @param blockPos block position of block
 * @param block block itself
 * @param boundingBox vanilla bounding box
 */
class BlockBBEvent(blockPos: BlockPos, val block: Block, var boundingBox: AxisAlignedBB?) : Event() {
    val x = blockPos.x
    val y = blockPos.y
    val z = blockPos.z
}

/**
 * Called when player clicks a block
 */
class ClickBlockEvent(val clickedBlock: BlockPos?, val WEnumFacing: EnumFacing?) : Event()

/**
 * Called when client is shutting down
 */
class ClientShutdownEvent : Event()

/**
 * Called when another entity moves
 */
data class EntityMovementEvent(val movedEntity: Entity) : Event()

/**
 * Called when player jumps
 *
 * @param motion jump motion (y motion)
 */
class JumpEvent(var motion: Float) : CancellableEvent()

/**
 * Called when user press a key once
 *
 * @param key Pressed key
 */
class KeyEvent(val key: Int) : Event()
class KeyStateChangeEvent(val key: Int, val keybind: KeyBinding, val newState: Boolean) : Event()

/**
 * Called in "onUpdateWalkingPlayer"
 *
 * @param eventState PRE or POST
 */
class PreMotionEvent() : Event()
class PostMotionEvent() : Event()

/**
 * Called in "onLivingUpdate" when the player is using a use item.
 *
 * @param strafe the applied strafe slow down
 * @param forward the applied forward slow down
 */
class SlowDownEvent(var strafe: Float, var forward: Float) : Event()

/**
 * Called in "onLivingUpdate" when the player is sneaking.
 *
 * @param strafe the applied strafe slow down
 * @param forward the applied forward slow down
 */
class SneakSlowDownEvent(var strafe: Float, var forward: Float) : Event()

/**
 * Called in "onLivingUpdate" after when the player's sprint states are updated
 */
class PostSprintUpdateEvent : Event()

/**
 * Called in "moveFlying"
 */
class StrafeEvent(val strafe: Float, val forward: Float, val friction: Float) : CancellableEvent()

/**
 * Called when player moves
 *
 * @param x motion
 * @param y motion
 * @param z motion
 */
class MoveEvent(var x: Double, var y: Double, var z: Double) : CancellableEvent() {
    var isSafeWalk = false

    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    fun zeroXZ() {
        x = 0.0
        z = 0.0
    }
}

/**
 * Called when receive or send a packet
 */
class ReceivedPacketEvent(val packet: Packet<*>) : CancellableEvent()
class PacketSendEvent(val packet: Packet<*>) : CancellableEvent()

/**
 * Called when a block tries to push you
 */
class PushOutEvent : CancellableEvent()

/**
 * Called when screen is going to be rendered
 */
class Render2DEvent(val partialTicks: Float) : Event()

class ModuleStateChangeEvent(val module: Module, val newState: Boolean, val profileChanged: Boolean=false) : CancellableEvent()

/**
 * Called when world is going to be rendered
 */
class Render3DEvent(val partialTicks: Float) : Event()

/**
 * Called when the screen changes
 */
class ScreenEvent(val guiScreen: GuiScreen?) : Event()

/**
 * Called when the session changes
 */
class SessionEvent : Event()

/**
 * Called when player is going to step
 */
class StepEvent(var stepHeight: Float) : Event()

/**
 * Called when player step is confirmed
 */
class StepConfirmEvent : Event()

/**
 * tick... tack... tick... tack
 */
class TickEvent : Event()

/**
 * Called when minecraft player will be updated
 */
class UpdateEvent : Event()

/**
 * Called when the world changes
 */

class WorldBeginLoadEvent(val worldClient: WorldClient?) : Event()
class WorldEvent(val worldClient: WorldClient?) : Event()

class ChatEvent(val component: IChatComponent) : CancellableEvent()

/**
 * Called when window clicked
 */
class ClickWindowEvent(val windowId: Int, val slotId: Int, val mouseButtonClicked: Int, val mode: Int) :
    CancellableEvent()

class StartupEvent : Event()