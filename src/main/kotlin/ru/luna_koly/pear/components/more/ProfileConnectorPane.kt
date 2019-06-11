package ru.luna_koly.pear.components.more

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import ru.luna_koly.pear.components.SideBar
import ru.luna_koly.pear.logic.Person
import ru.luna_koly.pear.logic.ProfileConnector
import tornadofx.*

class ProfileConnectorPane(
    val profileConnector: ProfileConnector,
    private val sideBar: SideBar
) : HBox() {
    private var name: Label by singleAssign()

    init {
        addClass(EvenMoreStyles.profileConnectorPane)

        name = label(profileConnector.profile.name)

        setOnMouseClicked {
            sideBar.selectConnector(this)
        }
    }

    fun update(person: Person) {
        name.text = person.name
    }
}