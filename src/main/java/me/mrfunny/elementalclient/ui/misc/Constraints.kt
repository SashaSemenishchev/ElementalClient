package me.mrfunny.elementalclient.ui.misc

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.MappedState
import gg.essential.elementa.state.State
interface Unscalable {
    var value: Float
}
class FinalScaleConstraint(val constraint: SuperConstraint<Float>, value: State<Float>) : MasterConstraint, Unscalable {
    constructor(constraint: SuperConstraint<Float>, value: Float) : this(constraint, BasicState(value))
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private val valueState: MappedState<Float, Float> = value.map { it }
    override var value: Float
        get() = valueState.get()
        set(value) = valueState.set(value)

    fun bindValue(newState: State<Float>) = apply {
        valueState.rebind(newState)
    }

    override fun animationFrame() {
        super.animationFrame()
        constraint.animationFrame()
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        return (constraint as XConstraint).getXPosition(component) * valueState.get()
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        return (constraint as YConstraint).getYPosition(component) * valueState.get()
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return (constraint as WidthConstraint).getWidth(component) * valueState.get()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return (constraint as HeightConstraint).getHeight(component) * valueState.get()
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return (constraint as RadiusConstraint).getRadius(component) * valueState.get()
    }

    override fun to(component: UIComponent): SuperConstraint<Float> {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context, please apply this to the components beforehand.")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        constraint.visit(visitor, type, setNewConstraint = false)
    }
}

class UnscalableConstraint<out T: MasterConstraint>(val parent: T): MasterConstraint, Unscalable {
    override var cachedValue: Float
        get() = parent.cachedValue
        set(value) { parent.cachedValue = value }
    override var constrainTo: UIComponent?
        get() = parent.constrainTo
        set(value) { parent.constrainTo = value}
    override var recalculate: Boolean
        get() = parent.recalculate
        set(value) { parent.recalculate = value }

    override fun getHeightImpl(component: UIComponent) = parent.getHeightImpl(component)

    override fun getRadiusImpl(component: UIComponent) = parent.getRadiusImpl(component)

    override fun getWidthImpl(component: UIComponent) = parent.getWidthImpl(component)

    override fun getXPositionImpl(component: UIComponent) = parent.getXPositionImpl(component)

    override fun getYPositionImpl(component: UIComponent) = parent.getYPositionImpl(component)

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        parent.visitImpl(visitor, type)
    }

    override var value: Float
        get() = 0f
        set(value) {}

}

class ScaledPixelConstraint @JvmOverloads constructor(
    value: State<Float>,
    scale: State<Float>,
    alignOpposite: State<Boolean> = BasicState(false),
    alignOutside: State<Boolean> = BasicState(false)
) : MasterConstraint, Unscalable {
    @JvmOverloads constructor(
        value: Float,
        scale: Float = 1.0f,
        alignOpposite: Boolean = false,
        alignOutside: Boolean = false
    ) : this(BasicState(value), BasicState(scale), BasicState(alignOpposite), BasicState(alignOutside))
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private val valueState: MappedState<Float, Float> = value.map { it }
    private val alignOppositeState: MappedState<Boolean, Boolean> = alignOpposite.map { it }
    private val alignOutsideState: MappedState<Boolean, Boolean> = alignOutside.map { it }
    private val scaleState: MappedState<Float, Float> = scale.map { it }

    override var value: Float
        get() = scaleState.get()
        set(value) { scaleState.set(value) }
    var pixels: Float
        get() = valueState.get()
        set(value) { valueState.set(value) }
    var alignOpposite: Boolean
        get() = alignOppositeState.get()
        set(value) { alignOppositeState.set(value) }
    var alignOutside: Boolean
        get() = alignOutsideState.get()
        set(value) { alignOutsideState.set(value) }

    fun bindValue(newState: State<Float>) = apply {
        valueState.rebind(newState)
    }

    fun bindAlignOpposite(newState: State<Boolean>) = apply {
        alignOppositeState.rebind(newState)
    }

    fun bindAlignOutside(newState: State<Boolean>) = apply {
        alignOutsideState.rebind(newState)
    }

    override fun getXPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)
        val value = this.valueState.get() * this.scaleState.get()

        return if (alignOppositeState.get()) {
            if (alignOutsideState.get()) {
                target.getRight() + value
            } else {
                target.getRight() - value - component.getWidth()
            }
        } else {
            if (alignOutsideState.get()) {
                target.getLeft() - component.getWidth() - value
            } else {
                target.getLeft() + value
            }
        }
    }

    override fun getYPositionImpl(component: UIComponent): Float {
        val target = (constrainTo ?: component.parent)
        val value = this.valueState.get() * this.scaleState.get()

        return if (alignOppositeState.get()) {
            if (alignOutsideState.get()) {
                target.getBottom() + value
            } else {
                target.getBottom() - value - component.getHeight()
            }
        } else {
            if (alignOutsideState.get()) {
                target.getTop() - component.getHeight() - value
            } else {
                target.getTop() + value
            }
        }
    }

    override fun getWidthImpl(component: UIComponent): Float {
        return valueState.get() * this.scaleState.get()
    }

    override fun getHeightImpl(component: UIComponent): Float {
        return valueState.get() * this.scaleState.get()
    }

    override fun getRadiusImpl(component: UIComponent): Float {
        return valueState.get() * this.scaleState.get()
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.X -> {
                if (alignOppositeState.get()) {
                    visitor.visitParent(ConstraintType.X)
                    visitor.visitParent(ConstraintType.WIDTH)
                    if (alignOutsideState.get())
                        visitor.visitSelf(ConstraintType.WIDTH)
                } else {
                    visitor.visitParent(ConstraintType.X)
                    if (alignOutsideState.get())
                        visitor.visitSelf(ConstraintType.WIDTH)
                }
            }
            ConstraintType.Y -> {
                if (alignOppositeState.get()) {
                    visitor.visitParent(ConstraintType.Y)
                    visitor.visitParent(ConstraintType.HEIGHT)
                    if (alignOutsideState.get())
                        visitor.visitSelf(ConstraintType.HEIGHT)
                } else {
                    visitor.visitParent(ConstraintType.Y)
                    if (alignOutsideState.get())
                        visitor.visitSelf(ConstraintType.HEIGHT)
                }
            }
            ConstraintType.WIDTH,
            ConstraintType.HEIGHT,
            ConstraintType.RADIUS,
            ConstraintType.TEXT_SCALE -> {}
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}