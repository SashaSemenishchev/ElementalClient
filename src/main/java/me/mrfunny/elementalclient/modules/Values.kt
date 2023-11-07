package me.mrfunny.elementalclient.modules

/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
import gg.essential.elementa.state.State
import gg.essential.vigilance.data.PropertyType
import java.awt.Color
import kotlin.math.pow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

abstract class Value<T>(val name: String, open var value: T, val isSupportedPredicate: (() -> Boolean)?, open var description: String="")
    : ReadWriteProperty<Any?, T> {
    open var propertyPath: String? = null
    open var onChangeListener: (old: T, new: T) -> Unit = {_, _ -> }
    fun set(newValue: T): Boolean {
        if (newValue == value)
            return false

        val oldValue = value

        try {
            val handledValue = onChange(oldValue, newValue)
            if (handledValue == oldValue) return false
            onChangeListener(oldValue, newValue)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    open var propertyType: PropertyType = PropertyType.SLIDER

    fun get() = value

    protected open fun onChange(oldValue: T, newValue: T): T {
        return newValue
    }
    open fun isSupported() = isSupportedPredicate?.invoke() ?: true

    // Support for delegating values using the `by` keyword.
    override operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        set(value)
    }
}

/**
 * Bool value represents a value with a boolean
 */
open class BoolValue(name: String, value: Boolean, isSupported: (() -> Boolean)?=null, description: String="") : Value<Boolean>(name, value, isSupported, description) {

    // TODO: Remove when all modules are ported to Kotlin
    constructor(name: String, value: Boolean) : this(name, value, null)

    fun toggle() = set(!value)

    fun isActive() = value && isSupported()
}

/**
 * Integer value represents a value with a integer
 */
open class IntegerValue(name: String, value: Int=0, val range: IntRange = 0..Int.MAX_VALUE, isSupported: (() -> Boolean)?=null, description: String="", open var isSlider: Boolean=false)
    : Value<Int>(name, value, isSupported, description) {

    // TODO: Remove when all modules are ported to Kotlin
//    constructor(name: String, value: Int, minimum: Int, maximum: Int) : this(name, value, minimum..maximum, isSupported, description, isSlider)

    fun set(newValue: Number) = set(newValue.toInt())

    fun isMinimal() = value <= minimum
    fun isMaximal() = value >= maximum

    val minimum = range.first
    val maximum = range.last
}

/**
 * Float value represents a value with a float
 */
open class FloatValue(name: String, value: Float, val range: ClosedFloatingPointRange<Float> = 0f..Float.MAX_VALUE,
                      isSupported: (() -> Boolean)?=null, description: String=""):
    Value<Float>(name, value, isSupported, description) {

    fun set(newValue: Number) = set(newValue.toFloat())

    fun isMinimal() = value <= minimum
    fun isMaximal() = value >= maximum

    val minimum = range.start
    val maximum = range.endInclusive
    open var decimalPlaces = calculateOptimalDecimalPlaces(minimum, maximum)

    private fun calculateOptimalDecimalPlaces(minValue: Float, maxValue: Float): Int {
        // Calculate the range width
        val rangeWidth = maxValue - minValue

        // Initialize the minimum decimal places as 0
        var minDecimalPlaces = 0

        // Define the minimum acceptable precision
        val minPrecision = 1e-3  // Adjust this threshold as needed

        // Keep increasing decimal places until the range can be represented with sufficient precision
        while (rangeWidth < 10.0.pow(-minDecimalPlaces.toDouble()) * minPrecision) {
            minDecimalPlaces++
        }

        return minDecimalPlaces
    }
}

open class PercentageValue(name: String, value: Float, isSupported: (() -> Boolean)?=null, description: String=""):
    FloatValue(name, if(value > 1) value / 100 else value, 0f..1f, isSupported, description)

/**
 * Text value represents a value with a string
 */
open class TextValue(name: String, value: String="", isSupported: (() -> Boolean)?=null, open var placeholder: String="", description: String="", open var protectedText: Boolean=false) : Value<String>(name, value, isSupported, description)
/**
 * List value represents a selectable list of values
 */
open class ListValue(name: String, val values: List<String>, override var value: Int, isSupported: (() -> Boolean)?=null, description: String="")
    : Value<Int>(name, value, isSupported, description) {

    operator fun contains(string: String?) = values.any { it.equals(string, true) }
}

open class ColorValue(name: String, value: Color=Color.WHITE, isSupported: (() -> Boolean)?=null, description: String="")
    : Value<Color>(name, value, isSupported, description)

class BooleanDefinedState<T>(private val property: KMutableProperty0<Boolean>, private val boolTrue: T, private val boolFalse: T) : State<T>() {
    override fun get(): T = if(property.get()) boolTrue else boolFalse
}