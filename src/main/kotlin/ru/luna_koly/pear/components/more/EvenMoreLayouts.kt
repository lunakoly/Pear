package ru.luna_koly.pear.components.more

import javafx.event.EventTarget
import javafx.scene.control.ScrollBar
import ru.luna_koly.pear.components.ChatPane
import ru.luna_koly.pear.components.SideBar
import tornadofx.opcr

fun EventTarget.titledtextfield(
    title: String,
    promptText: String = "",
    op: TitledTextField.() -> Unit
): TitledTextField = opcr(this,
    TitledTextField(title, promptText), op)

fun EventTarget.hintbar(
    op: HintBar.() -> Unit
): HintBar = opcr(this, HintBar(), op)

fun EventTarget.overlayedscrollpane(
    op: OverlayedScrollPane.() -> Unit
): OverlayedScrollPane = opcr(this, OverlayedScrollPane(), op)

fun EventTarget.scrollbar(
    op: ScrollBar.() -> Unit
): ScrollBar = opcr(this, ScrollBar(), op)

fun EventTarget.sidebar(
    op: SideBar.() -> Unit
): SideBar = opcr(this, SideBar(), op)

fun EventTarget.chatpane(
    op: ChatPane.() -> Unit
): ChatPane = opcr(this, ChatPane(), op)