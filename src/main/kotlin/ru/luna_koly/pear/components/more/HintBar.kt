package ru.luna_koly.pear.components.more

import javafx.geometry.Orientation
import javafx.scene.layout.HBox
import tornadofx.*

class HintBar : HBox() {
    init {
        addClass(EvenMoreStyles.hintBar)

        anchorpaneConstraints {
            leftAnchor = 15.0
            rightAnchor = 15.0
            bottomAnchor = 15.0
        }
    }

    fun hint(op: HintBar.() -> Unit) {
        if (children.isNotEmpty())
            this += separator(Orientation.VERTICAL)
        this.apply(op)
    }
}