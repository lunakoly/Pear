package ru.luna_koly.pear.components

import tornadofx.*

/**
 * Styles for SideBar
 */
class SideBarStyle : Stylesheet() {
    companion object {
        val sideBar by cssclass()
        val content by cssclass()
        val connectButton by cssclass()
        val searchConnectorsField by cssclass()
        val connectors by cssclass()
    }

    init {
        sideBar {
            backgroundColor += c("#2f3136")
            prefWidth = 300.px
        }

        content {
            spacing = 15.px

            padding = box(
                top = 15.px,
                right = 0.px,
                bottom = 0.px,
                left = 0.px
            )
        }

        connectButton {
            prefWidth = 270.px
        }

        searchConnectorsField {
            prefWidth = 270.px
        }

        connectors {
            prefWidth = 270.px
            spacing = 10.px
        }
    }
}