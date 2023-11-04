package me.mrfunny.elementalclient.modules.impl

import me.mrfunny.elementalclient.modules.*

object TestModule: Module("TestModule", "this is fr a test") {
    val bool by BoolValue("Test switch", false)
    val int by IntegerValue("Test number")
    val intSlider by IntegerValue("Test slider", 0, 0..50, isSlider = true)
    val float by FloatValue("Test float slider", 1f, 0f..5f)
    val percentage by PercentageValue("Test percentage", 50f)
    val text by TextValue("Test text", "")
    val list by ListValue("Test selection", listOf("Funny is bad", "oClio is bad"), 1)
    val color by ColorValue("Test color")
}