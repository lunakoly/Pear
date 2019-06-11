package ru.luna_koly.pear.components

import javafx.scene.control.Button
import ru.luna_koly.pear.components.more.TitledTextField
import ru.luna_koly.pear.components.more.hintbar
import ru.luna_koly.pear.components.more.titledtextfield
import ru.luna_koly.pear.events.InfoUpdatedEvent
import ru.luna_koly.pear.logic.DataBase
import tornadofx.*

class SettingsPopup : Fragment() {
    private val dataBase: DataBase by inject()

    private var name: TitledTextField by singleAssign()
    private var info: TitledTextField by singleAssign()
    private var applyButton: Button by singleAssign()

    override val root = anchorpane {
        prefHeight = 400.0
        prefWidth = 600.0

        name = titledtextfield("Name: ", "Somebody") {
            inner.text = dataBase.user.name

            anchorpaneConstraints {
                topAnchor = 15.0
                leftAnchor = 15.0
                rightAnchor = 15.0
            }
        }

        info = titledtextfield("Info: ", "...") {
            inner.text = dataBase.user.info

            anchorpaneConstraints {
                topAnchor = 105.0
                leftAnchor = 15.0
                rightAnchor = 15.0
            }
        }

        applyButton = button("Apply") {
            addClass(Decorations.brightButton)

            anchorpaneConstraints {
                topAnchor = 230.0
                leftAnchor = 200.0
                rightAnchor = 200.0
            }

            action {
                dataBase.user.name = name.inner.text
                dataBase.user.info = info.inner.text

                dataBase.getProfileConnectors().forEach {
                    it.sendInfoUpdatedNotification()
                }

                fire(InfoUpdatedEvent(dataBase.user))
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