package ru.luna_koly.pear.components.more

import javafx.geometry.Orientation
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane
import tornadofx.addClass
import tornadofx.anchorpaneConstraints
import tornadofx.scrollpane
import tornadofx.singleAssign

/**
 * ScrollPane that allows it's ScrollBars to
 * overlap the contents and hide when they are
 * not hovered
 */
open class OverlayedScrollPane : AnchorPane() {
    private var scrollPane: ScrollPane by singleAssign()

    init {
        addClass(EvenMoreStyles.overlayedScrollPane)

        scrollPane = scrollpane {
            addClass("edge-to-edge")

            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER

            anchorpaneConstraints {
                bottomAnchor = 0.0
                rightAnchor = 0.0
                leftAnchor = 0.0
                topAnchor = 0.0
            }
        }

        // TODO: hide unnecessary scrollbars & fix their sizes

        scrollbar {
            orientation = Orientation.VERTICAL

            anchorpaneConstraints {
                bottomAnchor = 0.0
                rightAnchor = 0.0
                topAnchor = 0.0
            }

            valueProperty().bindBidirectional(scrollPane.vvalueProperty())
            maxProperty().bind(scrollPane.vmaxProperty())
        }

        scrollbar {
            orientation = Orientation.HORIZONTAL

            anchorpaneConstraints {
                bottomAnchor = 0.0
                rightAnchor = 0.0
                leftAnchor = 0.0
            }

            valueProperty().bindBidirectional(scrollPane.hvalueProperty())
            maxProperty().bind(scrollPane.hmaxProperty())
        }
    }

    fun content(op: ScrollPane.() -> Unit) = scrollPane.apply(op)
}