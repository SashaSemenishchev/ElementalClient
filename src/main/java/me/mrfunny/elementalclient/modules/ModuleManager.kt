@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package me.mrfunny.elementalclient.modules

import gg.essential.universal.UScreen
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import gg.essential.vigilance.gui.CategoryLabel
import gg.essential.vigilance.gui.SettingsGui
import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.modules.impl.*
import me.mrfunny.elementalclient.profiles.ProfileManager
import me.mrfunny.elementalclient.ui.moduleoverview.ModuleOverviewGui
import me.mrfunny.elementalclient.util.AlphanumericalComparator
import java.io.File

object ModuleManager :
Vigilant(
    File("./elementalclient/profiles/${ProfileManager.selectedProfile}.toml"),
    "ElementalClient",
    backButtonAction = { ModuleManager.backButton() },
    sortingBehavior = ModuleSortingBehavior()
) {
    var categories = hashMapOf<String, Category>()
    val modules = arrayListOf<Module>()
    var loaded = false
    @Volatile
    var disableWrites = true
    fun init() {
        ProfileManager.selectDefaultProfile()
        ProfileManager.initSettings()

        registerModules(
            AutoGG(),
            Keystrokes(),
            NoHurtCam,
            RenderOwnName,
            DiscordRPC
//            TestModule
        )

        initDone()
        disableWrites = false
    }

    fun backButton() {
        writeData()
        ElementalClient.hudScreen.init()
        UScreen.displayScreen(ModuleOverviewGui())
    }

    fun registerModules(vararg modules: Module) {
        for (module in modules) {
            registerModule(module)
        }
    }

    fun registerModule(module: Module) {
        category(module.spacedName) {
            val enabled = PropertyData(
                PropertyAttributesExt(PropertyType.SWITCH, "Enabled", module.spacedName, triggerActionOnInitialization = false),
                KPropertyBackedPropertyValue(module::state),
                ModuleManager
            )
            properties.add(enabled)
            module.backingProperty = enabled
            subcategory("Settings") {
                registerSettings(this, module)
            }
        }

        setCategoryDescription(module.spacedName, (if(module is InProgress) "§e[IN PROGRESS]§r " else "") + module.description)
        modules += module
        categories.clear()
        for (category in getCategories()) {
            categories[category.name] = category
        }
    }

    val modulesComparator = Comparator.comparing<Module?, String>({ it?.name ?: "" }, AlphanumericalComparator())

    private fun initDone() {
        loadData()
        loaded = true
        modules.sortWith(modulesComparator)
        ElementalClient.hudScreen.init()
    }

    override fun readData() {
        super.readData()
        loadInternalValues()
    }

    fun loadInternalValues() {
        for (module in modules) {
            module.init()
            for (internalValue: Value<Any> in module.internalValues as ArrayList<Value<Any>>) {
                val property: Any = fileConfig.get(internalValue.propertyPath) ?: internalValue.value
                internalValue.value = property
                fileConfig.set<Any>(internalValue.propertyPath, internalValue.value)
            }
        }
    }

    fun writeInternalValues() {
        for (module in modules) {
            for (internalValue in module.internalValues) {
                fileConfig.set<Any>(internalValue.propertyPath, internalValue.value ?: "null")
            }
        }
    }

    override fun writeData() {
        writeInternalValues()
        super.writeData()
    }

    private fun registerSettings(builder: CategoryPropertyBuilder, module: Module) {
        for (value in module.values) {
            val property = when(value) {
                is BoolValue -> builder.checkbox(value::value, value.name, value.description, true)
                is IntegerValue -> if(value.isSlider) {
                        builder.slider(value::value, value.name, value.description, value.minimum, value.maximum, true)
                    } else {
                        builder.number(value::value, value.name, value.description, value.minimum, value.maximum, true)
                    }

                is PercentageValue ->
                    builder.percentSlider(value::value, value.name, value.description,true)

                is FloatValue ->
                    builder.decimalSlider(value::value, value.name, value.description, value.minimum, value.maximum, value.decimalPlaces, true)
                is TextValue ->
                    builder.text(value::value, value.name, value.description, value.placeholder, true, protectedText = value.protectedText)
                is ListValue ->
                    builder.selector(value::value, value.name, value.description, value.values, true)
                is ColorValue ->
                    builder.color(value::value, value.name, value.description, true, true)

                else -> null
            }
            property?.dependencyPredicate = { value.isSupported() }
        }
    }

    fun SettingsGui.guiAtModule(module: Module?) {
        if(module == null) return
        try {
            for (child in this.sidebarScroller.children[0].children) {
                if(child !is CategoryLabel) continue
                val category = child.category
                if(category.get().name != module.spacedName) continue
                child.select()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun toString(): String {
        return "ModuleManager[${ProfileManager.selectedProfile}]${modules.map { "${it.name}:${it.state}" }}"
    }

}