package me.mrfunny.elementalclient.modules

import gg.essential.vigilance.data.PropertyData
import gg.essential.vigilance.data.toPropertyPath
import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.event.ModuleStateChangeEvent
import me.mrfunny.elementalclient.util.MinecraftInstance
import scala.Enumeration.Val
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.javaType

abstract class Module @JvmOverloads constructor(

    val name: String,
    val description: String="",
    private val canBeEnabled: Boolean = true,
    val spacedName: String = name.split("(?<=[a-z])(?=[A-Z])".toRegex()).joinToString(separator = " ")

) : MinecraftInstance() {
    lateinit var backingProperty: PropertyData
    @Volatile
    var state: Boolean = false
        set(value) {
            if (field == value)
                return
            val event = ModuleStateChangeEvent(this, value)
            ElementalClient.eventBus.callEvent(event)
            if(event.isCancelled) {
                return
            }
            try {
                onToggle(value)
                field = false
            } catch (e: Exception) {
                e.printStackTrace()
//                addNotification(Notification("Failed to start ${getName()} due an error (onToggle())"))
            }

            // Call on enabled or disabled
            if (value) {
                try {
                    onEnable()
                    ElementalClient.eventBus.registerListener(this)
                    field = false
                } catch (e: Exception) {
                    e.printStackTrace()
//                    addNotification(Notification("Failed to start ${getName()} due an error (onEnable())"))
                }


                if (canBeEnabled)
                    field = true
            } else {
                onDisable()
                ElementalClient.eventBus.unregisterListener(this)
                field = false
            }
            if(this::backingProperty.isInitialized) {
                backingProperty.setValue(value)
            }
            if(!ModuleManager.disableWrites) ModuleManager.writeData()
        }
    open fun onEnable() {}
    open fun onDisable() {}
    open fun onToggle(value: Boolean) {}

    open val values
        get() = javaClass.declaredFields
            .filter { !it.isAnnotationPresent(InternalField::class.java) }
            .map { valueField ->
                valueField.isAccessible = true
                valueField[this]
            }.filterIsInstance<Value<*>>().distinctBy { it.name }

    val internalValues = arrayListOf<Value<*>>()
    val valueLookupTable = hashMapOf<String, Value<*>>()
    fun init() {
        internalValues.addAll(getInternalValues(javaClass))
        postInit()
        for (value in values) {
            valueLookupTable[value.name.lowercase()] = value
        }
        for (value in internalValues) {
            valueLookupTable[value.name.lowercase()] = value
        }
    }

    fun <T> lookupValue(property: KMutableProperty0<T>): Value<T>? {
        return valueLookupTable[property.name.lowercase()] as Value<T>
    }
    fun <T: Value<T>> lookupValue(name: String, clazz: Class<out T>): T? {
        val lookup = valueLookupTable[name.lowercase()] ?: return null
        return clazz.cast(lookup)
    }

    open fun postInit() {}

    @JvmName("buildInternalValues")
    protected fun getInternalValues(target: Class<*>) = target.declaredFields
        .filter { it.isAnnotationPresent(InternalField::class.java) }
        .map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()
        .onEach {
            it.propertyPath = "${spacedName.toPropertyPath()}.internal.${it.name.toPropertyPath()}"
        }
        .distinctBy { it.name } as ArrayList<Value<*>>
}