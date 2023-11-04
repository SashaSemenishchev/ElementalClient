@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package me.mrfunny.elementalclient.ui.moduleoverview

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.state.BasicState
import gg.essential.universal.GuiScale
import gg.essential.universal.UKeyboard
import gg.essential.universal.UScreen
import gg.essential.universal.USound
import gg.essential.vigilance.gui.SettingsGui
import gg.essential.vigilance.gui.VigilancePalette
import gg.essential.vigilance.gui.common.IconButton
import gg.essential.vigilance.utils.ImageFactory
import gg.essential.vigilance.utils.onLeftClick
import gg.essential.vigilance.utils.scrollGradient
import me.mrfunny.elementalclient.modules.Module
import me.mrfunny.elementalclient.modules.ModuleManager
import me.mrfunny.elementalclient.profiles.ProfileManager
import me.mrfunny.elementalclient.profiles.ProfileManager.niceName
import me.mrfunny.elementalclient.ui.NoBackground
import me.mrfunny.elementalclient.ui.dialog.ProfileNameDialog

class ModuleOverviewGui : WindowScreen(
    version = ElementaVersion.V2,
    newGuiScale = GuiScale.scaleForScreenSize().ordinal,
    restoreCurrentGuiOnClose = true
), NoBackground {

    private val container by UIContainer().constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 85.percent
        height = 75.percent
//        this.color = ConstantColorConstraint(
    } childOf window

    private val titleBar by OverviewTitleBar(this) childOf container

    private val bottomContainer by UIContainer().constrain {
        y = SiblingConstraint()
        width = 100.percent
        height = FillConstraint()
//        background.setColor(VigilancePalette.getMainBackground())
    } childOf container

    private val leftDivider by UIBlock(VigilancePalette.getDividerDark()).constrain {
        width = 3f.pixels
        height = 100.percent
    } childOf bottomContainer

    private val sidebar by UIBlock(AlphaAspectColorConstraint(VigilancePalette.getMainBackground(), 0.7f)).constrain {
        x = SiblingConstraint()
        width = 25.percent
        height = 100.percent
    } effect ScissorEffect() childOf bottomContainer

    private val middleDivider by UIBlock(VigilancePalette.dividerDark).constrain {
        x = SiblingConstraint()
        width = 3f.pixels
        height = 100.percent
    } childOf bottomContainer

    val sidebarScroller by ScrollComponent(
        "No matching profiles found :(",
        innerPadding = 10f,
        pixelsPerScroll = 25f,
    ).constrain {
        width = 100.percent
        height = 100.percent - SettingsGui.dividerWidth.pixels
    } childOf sidebar scrollGradient 20.pixels

    private val sidebarVerticalScrollbar by UIBlock(VigilancePalette.scrollbar).constrain {
        width = 100.percent
    } childOf middleDivider

    private val rightDivider by UIBlock(VigilancePalette.getDividerDark()).constrain {
        x = 0.pixels(alignOpposite = true)
        width = 3f.pixels
        height = 100.percent
    } childOf bottomContainer

    private val content by UIBlock(VigilancePalette.getMainBackground()).constrain {
        x = SiblingConstraint() boundTo middleDivider
        width = FillConstraint()
        height = 100.percent
    } effect(ScissorEffect()) childOf bottomContainer

    private val bottomDivider by UIBlock(VigilancePalette.getDividerDark()).constrain {
        y = SiblingConstraint()
        width = 100.percent
        height = 3f.pixels
    } childOf container
    val emptyString = "No matching modules found :("
    private val scroller by ScrollComponent(
        emptyString,
        innerPadding = 10f,
        pixelsPerScroll = 25f,
    ).constrain {
        width = 100.percent - (10 + 3).pixels
        height = 100.percent
    } childOf content scrollGradient 20.pixels

    private val scrollBar by UIBlock(VigilancePalette.getScrollBar()).constrain {
        x = 0.pixels(alignOpposite = true)
        width = 3.pixels
    } childOf content

    private val sidebarHorizontalScrollbarContainer by UIContainer().constrain {
        x = 0.pixels boundTo sidebar
        width = 100.percent boundTo sidebar
        height = 3f.pixels
    } childOf bottomDivider

    private val sidebarHorizontalScrollbar by UIBlock(VigilancePalette.getScrollbar()).constrain {
        height = 100.percent
    } childOf sidebarHorizontalScrollbarContainer

    private val backButton by IconButton(VigilancePalette.ARROW_LEFT_4X7).constrain {
        x = SiblingConstraint(18f, alignOpposite = true) boundTo titleBar
        y = CenterConstraint() boundTo titleBar
        width = 17.pixels
        height = AspectConstraint()
    } childOf window

    private val moduleToMiniatures = linkedMapOf<Module, ModuleMiniature>()
    private var selectedProfile: ProfileLabel? = null
    init {
        UIContainer().constrain {
            x = SiblingConstraint()
            height = 100.percent
            width = 7f.pixels
        } childOf scroller
        for (module in ModuleManager.modules) {
            val miniature = ModuleMiniature(module).constrain {
                y = CramSiblingConstraint(3f)
                x = CramSiblingConstraint(3f)
            } childOf scroller
            moduleToMiniatures[module] = miniature
        }

        val nowSelected = ProfileManager.selectedProfile
//        var label: ProfileLabel? = null
        for (availableProfile in ProfileManager.availableProfiles) {
            val formatted = availableProfile.niceName()
            val flag = formatted == nowSelected
            val label = (ProfileLabel(formatted, flag)
                .onLeftClick {
                    if(this !is ProfileLabel) {
                        println("This is weird")
                        return@onLeftClick
                    }
                    if(selectedProfile == this) return@onLeftClick
                    selectedProfile?.deselect()
                    if (!this.isSelected) {
                        USound.playButtonPress()
                        selectedProfile = this
                        this.select()
                        update()
                    }
            } childOf sidebarScroller) as ProfileLabel
            if(flag) {
                selectedProfile = label
                label.isSelected = true
            }
        }

        val scrollerColor = BasicState(VigilancePalette.getButton())
        val plusButton = UIBlock(AlphaAspectColorConstraint(scrollerColor.get(), 0.7f)).constrain {
            y = SiblingConstraint()
            x = CenterConstraint()
            width = ChildBasedSizeConstraint() + 25.pixels
            height = ChildBasedSizeConstraint()
        }.addChild(
            PLUS_ICON.create().constrain { y = CenterConstraint(); x = CenterConstraint()}
        ).onMouseEnter {
            animate {
                setColorAnimation(Animations.OUT_EXP, .25f, AlphaAspectColorConstraint(scrollerColor.get().brighter(), 0.7f))
            }
        }.onMouseLeave {
            animate {
                setColorAnimation(Animations.OUT_EXP, .25f, AlphaAspectColorConstraint(scrollerColor.get(), 0.7f))
            }
        }.onLeftClick {
            USound.playButtonPress()
            ProfileNameDialog {
                val error = handleProfileCreation(it) ?: return@ProfileNameDialog false
                this.refocus()
                if(error.isBlank()) {
                    return@ProfileNameDialog true
                }
                this.error(error)
                return@ProfileNameDialog true
            }.constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                width = 20.percent
            } childOf window

        } childOf sidebarScroller

        scroller.setVerticalScrollBarComponent(scrollBar, true)

        window.onKeyType { _, keyCode ->
            if (UKeyboard.isKeyDown(UKeyboard.KEY_MINUS)) {
                Inspector(window) childOf window
                return@onKeyType
            }
        }
    }

    fun update() {
        for (value in moduleToMiniatures.values) {
            value.update()
        }
    }

    fun handleProfileCreation(name: String): String? {
        if(name.trim() == "") {
            return ""
        }
        try {
            ProfileManager.createProfile(name)
            UScreen.displayScreen(ModuleOverviewGui())
        } catch (e: Throwable) {
            return e.message
        }
        return null
    }

    fun filterModules(query: String) {
        val lower = query.lowercase()
        var hidden = 0
        val miniatures = moduleToMiniatures
//        val shown = ArrayList<ModuleMiniature>(miniatures.size)
//        scroller.allChildren
        for (entry in miniatures.entries) {
            val module = entry.key
            val gui = entry.value
            if(lower in module.name.lowercase() || lower in module.spacedName.lowercase()) {
                gui.unhide(true)
                continue
            }
            gui.hide(true)
            hidden++
        }
        if(hidden == miniatures.size) {
            if("shit" in lower) {
                scroller.emptyText.setText("Mad?")
            } else {
                scroller.emptyText.setText(emptyString)
            }
            scroller.emptyText.unhide(false)

            return
        }

        scroller.emptyText.hide()
        scroller.sortChildren(Comparator.comparing({ (it as? ModuleMiniature)?.module }, ModuleManager.modulesComparator))
//        shown.sortWith(Comparator.comparing({ it.module }, ModuleManager.modulesComparator))
//        for (moduleMiniature in shown) {
//            moduleMiniature.unhide()
//        }
    }

    companion object {
        val PLUS_ICON = ImageFactory {UIImage.ofResourceCached("/vigilance/plus.png")}
    }
}

