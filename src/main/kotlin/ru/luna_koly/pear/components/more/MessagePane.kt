package ru.luna_koly.pear.components.more

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import ru.luna_koly.pear.logic.Message
import tornadofx.addClass
import tornadofx.label
import tornadofx.*

class MessagePane(message: Message) : VBox() {
    init {
        addClass(EvenMoreStyles.messagePane)

        label(message.author.name) {
            addClass(EvenMoreStyles.messageAuthor)

            maxWidth = Double.MAX_VALUE

            if (message.isOurs)
                alignment = Pos.CENTER_RIGHT
        }

        label(message.text) {
            addClass(EvenMoreStyles.message)

            maxWidth = Double.MAX_VALUE
        }

        separator(Orientation.HORIZONTAL)
    }
}