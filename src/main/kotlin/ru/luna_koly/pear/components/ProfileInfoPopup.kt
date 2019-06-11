package ru.luna_koly.pear.components

import javafx.scene.Parent
import ru.luna_koly.pear.components.more.hintbar
import ru.luna_koly.pear.logic.Profile
import tornadofx.*

class ProfileInfoPopup : Fragment() {
    val profile: Profile by params

    override val root = anchorpane {
        prefHeight = 400.0
        prefWidth = 600.0

        label(profile.name) {
            addClass(Decorations.status)

            anchorpaneConstraints {
                topAnchor = 15.0
                leftAnchor = 15.0
                rightAnchor = 15.0
            }
        }

        label(profile.info) {
            addClass(Decorations.status)

            anchorpaneConstraints {
                topAnchor = 60.0
                leftAnchor = 15.0
                rightAnchor = 15.0
            }
        }

        hintbar {
            hint {
                label("ESC") { addClass(Decorations.key) }
                label(" to cancel") { addClass(Decorations.hint) }
            }
        }
    }
}