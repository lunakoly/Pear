package ru.luna_koly.pear.components

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*

class PopupStyle : Stylesheet() {
    companion object {
        val floatingWindowWrapper by cssclass()
        val floatingWindowContent by cssclass()
        val window by cssclass()
        val top by cssclass()
    }

    init {
        floatingWindowWrapper {
            top {
                visibility = FXVisibility.HIDDEN
            }

            window {
                // I couldn't find info on
                // how to remove the effect
                effect = DropShadow(0.0, Color.BLACK)
            }

            floatingWindowContent {
                backgroundColor += c("#2f3136")
                backgroundRadius += box(10.px)
            }
        }
    }
}