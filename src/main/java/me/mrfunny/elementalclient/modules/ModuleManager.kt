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
            TestModule
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
            registerProperty(enabled)
            module.backingProperty = enabled
            subcategory("Settings") {
                registerSettings(this, module)
            }
        }

        setCategoryDescription(module.spacedName, (if(module is InProgress) "§e[IN PROGRESS]§r " else "") + module.description)
        if(loaded) {
            loadData()
        }
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
    }

    override fun readData() {
        super.readData()
        loadInternalValues()
    }

    fun loadInternalValues() {
        for (module in modules) {
            module.init()
            for (internalValue: Value<Any> in module.internalValues as ArrayList<Value<Any>>) {
                val property: Any = fileConfig.get(internalValue.propertyPath) ?: continue
                internalValue.value = property
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
            val hidden = !value.isSupported()
            when(value) {
                is BoolValue -> {
                    builder.checkbox(value::value, value.name, value.description, true, hidden)
                }
                is IntegerValue -> {
                    if(value.isSlider) {
                        builder.slider(value::value, value.name, value.description, value.minimum, value.maximum, true, hidden)
                    } else {
                        builder.number(value::value, value.name, value.description, value.minimum, value.maximum, true, hidden)
                    }
                }
                is PercentageValue -> {
                    builder.percentSlider(value::value, value.name, value.description,true, hidden)
                }
                is FloatValue -> {
                    builder.decimalSlider(value::value, value.name, value.description, value.minimum, value.maximum, value.decimalPlaces, true, hidden)
                }
                is TextValue -> {
                    builder.text(value::value, value.name, value.description, value.placeholder, true, hidden, false)
                }
                is ListValue -> {
                    builder.selector(value::value, value.name, value.description, value.values, true, hidden)
                }
                is ColorValue -> {
                    builder.color(value::value, value.name, value.description, true, true, hidden)
                }
            }
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