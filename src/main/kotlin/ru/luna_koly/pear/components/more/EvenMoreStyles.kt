package ru.luna_koly.pear.components.more

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.paint.Color
import ru.luna_koly.pear.components.Decorations
import tornadofx.*
import java.net.URI

class EvenMoreStyles : Stylesheet() {
    companion object {
        val titledTextField by cssclass()
        val hintBar by cssclass()

        val overlayedScrollPane by cssclass()
        val profileConnectorPane by cssclass()
        val selectedProfileConnectorPane by cssclass()

        val messagePane by cssclass()
        val messageAuthor by cssclass()
        val message by cssclass()
    }

    init {
        titledTextField {
            backgroundRadius += box(0.4.em)
            backgroundColor += c("#72767d")

            padding = box(
                top = 1.em,
                left = 2.em,
                right = 2.em,
                bottom = 1.em
            )

            label {
                +Decorations.TEXT_LIGHT
                textFill = c("#eeeeee")
                fontSize = 2.em

                padding = box(0.em)
            }

            textField {
                focusColor = Color.TRANSPARENT
                backgroundInsets += box(0.em)
                padding = box(0.em)

                +Decorations.TEXT_LIGHT
                fontSize = 2.em

                textFill = c("#eeeeee")
                promptTextFill = c("#aaaaaa")
                backgroundColor += Color.TRANSPARENT
            }
        }

        hintBar {
            alignment = Pos.CENTER_RIGHT

            borderWidth += box(0.2.px)
            borderColor += box(
                top = c("#72767d"),
                left = Color.TRANSPARENT,
                right = Color.TRANSPARENT,
                bottom = Color.TRANSPARENT
            )

            padding = box(
                top = 1.em,
                left = 0.em,
                right = 0.em,
                bottom = 0.em
            )

            separator {
                line {
                    borderColor += box(c("#72767d"))
                    borderWidth += box(0.2.px)
                }

                padding = box(
                    top = 0.em,
                    left = 1.em,
                    right = 1.em,
                    bottom = 0.em
                )
            }
        }

        overlayedScrollPane {
            scrollPane {
                backgroundColor += Color.TRANSPARENT

                viewport {
                    backgroundColor += Color.TRANSPARENT
                }
            }

            // make scrollbars prettier
            scrollBar {
                // vertical thickness
                and(vertical) {
                    select(incrementButton, decrementButton) {
                        padding = box(
                            top = 0.px,
                            right = (-3).px,
                            bottom = 0.px,
                            left = 0.px
                        )

                        scaleX = 0.0
                    }
                }

                // vertical thickness
                and(horizontal) {
                    select(incrementButton, decrementButton) {
                        padding = box(
                            top = 0.px,
                            right = 0.px,
                            bottom = (-3).px,
                            left = 0.px
                        )

                        scaleY = 0.0
                    }
                }

                backgroundColor += Color.TRANSPARENT
                padding = box(5.px)

                track {
                    backgroundColor += c("#11111100")
                    backgroundRadius += box(40.px)
                }

                thumb {
                    backgroundColor += c("#ffffff00")
                    backgroundRadius += box(40.px)
                    backgroundInsets += box(0.px)
                    cursor = Cursor.HAND
                }

                and(hover, pressed) {
                    track {
                        backgroundColor += c("#11111150")
                    }

                    thumb {
                        backgroundColor += c("#ffffffaa")
                    }
                }
            }
        }

        val profileConnectorPaneCommon = mixin {
            fitToWidth = true
            backgroundRadius += box(0.3.em)

            padding = box(
                top = 0.2.em,
                left = 0.2.em,
                right = 0.2.em,
                bottom = 0.2.em
            )

            label {
                +Decorations.TEXT_NORMAL
                fontSize = 1.2.em

                padding = box(
                    top = 0.4.em,
                    left = 1.em,
                    right = 0.em,
                    bottom = 0.4.em
                )
            }
        }

        profileConnectorPane {
            +profileConnectorPaneCommon
            backgroundColor += Color.TRANSPARENT

            label {
                textFill = c("#808080")
            }

            select(hover, pressed) {
                cursor = Cursor.HAND

                label {
                    textFill = c("#eeeeee")
                }
            }

            and(hover) {
                backgroundColor += c("#292b2f")
            }

            and(pressed) {
                backgroundColor += c("#1b1d20")
            }
        }

        selectedProfileConnectorPane {
            +profileConnectorPaneCommon
            backgroundColor += c("#42464d")

            label {
                textFill = c("#eeeeee")
            }
        }

        messagePane {
            padding = box(
                top = 17.px,
                left = 15.px,
                right = 15.px,
                bottom = 0.px
            )

            label {
                padding = box(
                    top = 0.em,
                    left = 1.5.em,
                    right = 1.5.em,
                    bottom = 0.em
                )
            }

            messageAuthor {
                +Decorations.TEXT_NORMAL
                textFill = c("#dddddd")
                fontSize = 1.3.em
            }

            message {
                +Decorations.TEXT_LIGHT
                textFill = c("#dddddd")
                fontSize = 1.2.em
            }

            separator {
                line {
                    borderColor += box(c("#535860"))
                    borderWidth += box(0.2.px)
                }

                padding = box(
                    top = 20.px,
                    left = 0.px,
                    right = 0.px,
                    bottom = 0.px
                )
            }
        }
    }
}