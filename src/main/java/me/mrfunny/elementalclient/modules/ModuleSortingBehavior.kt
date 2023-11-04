package me.mrfunny.elementalclient.modules

import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.SortingBehavior

class ModuleSortingBehavior : SortingBehavior() {
    val first = compareBy<Category> {it.name}
    private val comparator = object : Comparator<Category> {
        override fun compare(o1: Category?, o2: Category?): Int {
            if(o1?.name == "General Settings") return -1;
            if(o2?.name == "General Settings") return 1;
            return first.compare(o1, o2)
        }

    }

    override fun getCategoryComparator(): Comparator<in Category> {
        return comparator
    }
}