package dev.gumil.kaskade

internal class IncompleteFlowException(action: Action) :
    IllegalStateException("$action has an incomplete flow")
