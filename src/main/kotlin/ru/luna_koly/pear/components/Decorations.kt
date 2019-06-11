package ru.luna_koly.pear.components

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import tornadofx.*

class Decorations : Stylesheet() {
    companion object {
        val ACCENT = c("#7b8ed4")
        val ACCENT_HOVER = c("#7387ce")
        val ACCENT_PRESSED = c("#677bc4")

        val TEXT_NORMAL = mixin {
            fontFamily = "Segoe UI Semibold"
        }

        val TEXT_BOLD = mixin {
            fontFamily = "Segoe UI"
            fontWeight = FontWeight.BOLD
        }

        val TEXT_LIGHT = mixin {
            fontFamily = "Segoe UI"
            fontWeight = FontWeight.LIGHT
        }

        val key by cssclass()
        val hint by cssclass()

        val brightButton by cssclass()
        val accurateTextField by cssclass()

        val status by cssclass()
    }

    init {
        key {
            +TEXT_BOLD

            textFill = Color.WHITE
            backgroundColor += c("#72767d")
            backgroundRadius += box(0.1.em)

            borderWidth += box(
                top = 0.em,
                left = 0.em,
                right = 0.em,
                bottom = 0.2.em
            )

            borderColor += box(
                top = Color.TRANSPARENT,
                left = Color.TRANSPARENT,
                right = Color.TRANSPARENT,
                bottom = c("#505050")
            )

            padding = box(
                top = 0.1.em,
                left = 0.2.em,
                right = 0.2.em,
                bottom = 0.1.em
            )
        }

        hint {
            +TEXT_LIGHT
            textFill = c("#6b6c70")
            fontSize = 1.em

            padding = box(0.em)
        }

        brightButton {
            +TEXT_NORMAL
            fontSize = 1.2.em

            textFill = Color.WHITE
            backgroundColor += ACCENT

            backgroundInsets += box(0.em)
            focusColor = Color.TRANSPARENT

            padding = box(
                top = 0.4.em,
                left = 1.em,
                right = 1.em,
                bottom = 0.4.em
            )

            and(hover) {
                backgroundColor += ACCENT_HOVER
                cursor = Cursor.HAND
            }

            and(pressed) {
                backgroundColor += ACCENT_PRESSED
            }
        }

        accurateTextField {
            +TEXT_BOLD

            backgroundColor += c("#202225")
            backgroundRadius += box(0.3.em)
            focusColor = Color.TRANSPARENT

            padding = box(
                top = 0.5.em,
                left = 0.9.em,
                right = 0.9.em,
                bottom = 0.5.em
            )

            textFill = c("#aaaaaa")
            promptTextFill = c("#505050")
        }

        status {
            +TEXT_NORMAL
            alignment = Pos.CENTER

            fontSize = 1.3.em
            textFill = c("#aaaaaa")
        }
    }
}