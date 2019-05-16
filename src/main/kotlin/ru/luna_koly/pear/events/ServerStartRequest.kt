package ru.luna_koly.pear.events

import tornadofx.EventBus
import tornadofx.FXEvent

object ServerStartRequest : FXEvent(EventBus.RunOn.BackgroundThread)