package io.gumil.kaskade

internal class IncompleteFlowException(event: Event):
        IllegalStateException("$event has an incomplete flow")