package dev.gumil.kaskade

interface Recording

/**
 * Record [Recording]s and [Recorder]s sequentially.
 */
class Recorder {

    private val recordings = mutableListOf<Recording>()

    private val childRecorders = mutableListOf<Recorder>()

    /**
     * Adds a child recorder
     */
    fun addChild(recorder: Recorder) {
        childRecorders.add(recorder)
    }

    /**
     * Records a recording
     */
    fun record(recording: Recording) {
        recordings.add(recording)
    }

    /**
     * @return list of recordings. Add recordings from the current recorder and then
     * adds recordings from child recorders sequentially
     */
    fun flattenRecording(): List<Recording> {
        return recordings + childRecorders.flatMap { it.flattenRecording() }
    }
}
