package io.gumil.kaskade

internal class IncompleteFlowException(loggable: Loggable):
        IllegalStateException("Action ${loggable.className()} has an incomplete flow")