package me.mrfunny.elementalclient.modules.impl

import me.mrfunny.elementalclient.modules.InProgress
import me.mrfunny.elementalclient.modules.Module
import me.mrfunny.elementalclient.modules.TextValue

class AutoGG : Module("AutoGG", "Automatically sends 'Good Game' message when game ends"), InProgress {
    val text1 by TextValue("First Message", "gg", placeholder = "gg")
    val text2 by TextValue("Second Message", "", placeholder = "Sniped by HypixelSS")


}