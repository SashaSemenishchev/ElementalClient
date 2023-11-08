package me.mrfunny.elementalclient

import gg.essential.universal.UScreen
import me.mrfunny.elementalclient.api.ClientUpdate.gitInfo
import me.mrfunny.elementalclient.event.EventBus
import me.mrfunny.elementalclient.event.EventLink
import me.mrfunny.elementalclient.event.TickEvent
import me.mrfunny.elementalclient.event.WorldBeginLoadEvent
import me.mrfunny.elementalclient.modules.ModuleManager
import me.mrfunny.elementalclient.services.Service
import me.mrfunny.elementalclient.ui.hud.HudEditGui
import me.mrfunny.elementalclient.ui.hud.HudScreen
import me.mrfunny.elementalclient.ui.misc.ElementalMainMenu
import me.mrfunny.elementalclient.ui.moduleoverview.ModuleOverviewGui
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod.EventHandler
import org.lwjgl.input.Keyboard
import java.util.concurrent.Executors
import java.util.function.Consumer
import java.util.logging.Logger

object ElementalClient {

    const val CLIENT_NAME = "ElementalClient"
    const val CLIENT_DATA = "elementalclient/"
    private val clientVersionText = gitInfo["git.build.version"]?.toString() ?: "unknown"
    private val clientCommit = gitInfo["git.commit.id.abbrev"]?.let { "git-${if(it.toString().isBlank()) "HEAD" else it}" } ?: "unknown"
    val IN_DEV
        get() = System.getProperty("dev-mode") != null
    private const val CLIENT_CREATOR = "MrFunny"
    const val MINECRAFT_VERSION = "1.8.9"

    val clientTitle = CLIENT_NAME + " " + clientVersionText + " " + clientCommit + "  | " + MINECRAFT_VERSION + if (IN_DEV) " | DEVELOPMENT BUILD" else ""
    val executor = Executors.newSingleThreadScheduledExecutor()
    val logger = Logger.getLogger("ElementalClient")
    val optionsKeyBinding: KeyBinding = KeyBinding("ElementalClient Master Key", Keyboard.KEY_RSHIFT, "Misc")

    @JvmField
    val eventBus = EventBus()
    @JvmField
    val hudScreen = HudScreen()
    lateinit var mc: Minecraft
    fun startClient() {
        mc = Minecraft.getMinecraft()
        logger.info("Starting $clientTitle by $CLIENT_CREATOR")
        ClientRegistry.registerKeyBinding(optionsKeyBinding)
        eventBus.registerListener(BaseHandler())
        eventBus.registerListener(hudScreen)
        ModuleManager.init()
        for (service in Service.services) {
            eventBus.registerListener(service)
        }
//        for (field in VigilancePalette::class.java.declaredFields) {
//            val modification = ElementalPalette.paletteModifications[field.name] ?: continue
//            if (field.type.simpleName != "BasicState") continue
//            field.isAccessible = true
//            (field.get(VigilancePalette) as BasicState<Color>).set(modification)
//            logger.info("Modified standard Essential palette: of ${field.name} to $modification")
//        }
    }

    class BaseHandler {
        @EventLink
        val ticker = Consumer<TickEvent> {
            if(optionsKeyBinding.isPressed) {
//                UScreen.displayScreen(ModuleOverviewGui())
                UScreen.displayScreen(ElementalMainMenu())
            }
        }

        @EventLink
        val onWorldLoad = Consumer<WorldBeginLoadEvent> {
            hudScreen.init()
        }
    }
}