package ru.luna_koly.pear.components.more

import javafx.geometry.Orientation
import javafx.scene.layout.HBox
import tornadofx.*

/**
 * Provides a box with hints at the bottom of
 * a popup
 */
class HintBar : HBox() {
    init {
        addClass(EvenMoreStyles.hintBar)

        anchorpaneConstraints {
            leftAnchor = 15.0
            rightAnchor = 15.0
            bottomAnchor = 15.0
        }
    }

    /**
     * Adds a hint to the list
     */
    fun hint(op: HintBar.() -> Unit) {
        if (children.isNotEmpty())
            this += separator(Orientation.VERTICAL)
        this.apply(op)
    }
}