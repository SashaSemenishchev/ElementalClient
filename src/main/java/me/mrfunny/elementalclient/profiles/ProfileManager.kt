package me.mrfunny.elementalclient.profiles

import me.mrfunny.elementalclient.ElementalClient
import me.mrfunny.elementalclient.modules.ModuleManager
import java.io.File
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

object ProfileManager {

    var selectedClientThemeIndex: Int = 0
    val data = File("${ElementalClient.CLIENT_DATA}.profile")
    val profiles = File("${ElementalClient.CLIENT_DATA}profiles/")
    val selectedProfile: String
        get() = data.readText()
    init {
        if(!profiles.mkdirsIfNotExists() || !data.createIfNotExists()) {
            ElementalClient.logger.info("Creating default profile")
            createProfile("default")
        }
    }

    fun File.mkdirsIfNotExists(): Boolean {
        val flag = this.exists()
        if(!flag) {
            this.mkdirs()
        }
        return flag
    }

    fun File.createIfNotExists(): Boolean {
        val flag = this.exists()
        if(!flag) {
            this.createNewFile()
        }
        return flag
    }

    fun initSettings() {
        ModuleManager.category("General Settings") {
            subcategory("Appearance") {
                selector(
                    ::selectedClientThemeIndex,
                    "Color Scheme",
                    "Selects the theme appearance of the client",
                    ColorScheme.values,
                    true
                ) {
                    selectedClientThemeIndex = it
                }
            }
        }
    }

    fun selectDefaultProfile() {
        val selected = data.readText()
        val file = File("${profiles.absolutePath}/$selected.toml")
        val profile = if(!file.exists()) {
            "default"
        } else {
            selected
        }
        selectProfile(profile)
    }

    fun createProfile(name: String?=null) {
        val file = name ?: LocalDateTime.now().let {
                "profile-${it.year}-${it.month}-${it.dayOfMonth}--${it.hour}-${it.minute}-${it.second}"
            }
        File("${profiles.absolutePath}/$file.toml").createNewFile()
        selectProfile(file)
    }

    fun selectProfile(name: String) {
        val profile = File("${profiles.absolutePath}/$name.toml")
        ElementalClient.logger.info("Loading profile from ${profile.absolutePath}")
        if(!profile.exists() && name != "default") {
            throw IllegalArgumentException("Profile $name does not exist")
        }
        ModuleManager.disableWrites = true
        if(ModuleManager.file.absolutePath.equals(profile.absoluteFile)) {
            ElementalClient.hudScreen.init()
            return
        }

        if(ModuleManager.loaded) {
            ModuleManager.reloadFrom(profile)
            ElementalClient.hudScreen.init()
            ModuleManager.disableWrites = false
        }
        data.writeText(name)
    }

    fun String.niceName() = this.replace(".toml", "")

    val availableProfiles: List<String>
        get() {
            val listFiles = profiles.listFiles() ?: return emptyList()
            return listFiles.reversed().map { it.name }
        }
}