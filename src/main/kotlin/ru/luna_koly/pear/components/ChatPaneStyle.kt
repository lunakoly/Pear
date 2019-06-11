package ru.luna_koly.pear.components

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.paint.Color
import tornadofx.*

class ChatPaneStyle : Stylesheet() {
    companion object {
        val chatPane by cssclass()
        val topBar by cssclass()
        val topBarButton by cssclass()
        val messageField by cssclass()
        val protector by cssclass()
    }

    init {
        chatPane {
            backgroundColor += c("#36393f")
        }

        topBar {
            spacing = 1.em
            borderColor += box(c("#2f3136"))

            borderWidth += box(
                top = 0.em,
                left = 0.em,
                right = 0.em,
                bottom = 2.px
            )

            padding = box(
                top = 0.5.em,
                left = 1.2.em,
                right = 1.2.em,
                bottom = 0.5.em
            )
        }

        topBarButton {
            +Decorations.TEXT_NORMAL
            backgroundRadius += box(0.3.em)

            textFill = c("#eeeeee")
            fontSize = 1.3.em

            padding = box(
                top = 0.5.em,
                left = 0.5.em,
                right = 0.5.em,
                bottom = 0.5.em
            )

            and(hover, pressed) {
                cursor = Cursor.HAND
            }

            and(hover) {
                backgroundColor += c("#41444c")
            }

            and(pressed) {
                backgroundColor += c("#484c54")
            }
        }

        messageField {
            +Decorations.TEXT_NORMAL

            backgroundColor += c("#484c52")
            backgroundRadius += box(0.3.em)
            focusColor = Color.TRANSPARENT

            backgroundInsets += box(
                top = 15.px,
                left = 15.px,
                right = 15.px,
                bottom = 15.px
            )

            padding = box(
                top = 2.em,
                left = 2.1.em,
                right = 2.1.em,
                bottom = 2.em
            )

            fontSize = 1.2.em
            textFill = c("#dddddd")
            promptTextFill = c("#999999")
        }

        protector {
            +Decorations.TEXT_NORMAL
            fontSize = 1.3.em

            alignment = Pos.CENTER

            backgroundColor += c("#36393f")
            textFill = c("#dddddd")
        }
    }
}