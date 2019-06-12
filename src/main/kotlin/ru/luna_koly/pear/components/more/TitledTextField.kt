package ru.luna_koly.pear.components.more

import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import tornadofx.*

/**
 * A big TextField with a label
 */
class TitledTextField(title: String, promptText: String = "") : HBox() {
    var inner: TextField by singleAssign()
        private set

    init {
        addClass(EvenMoreStyles.titledTextField)

        label(title)

        inner = textfield {
            this.promptText = promptText
        }
    }
}