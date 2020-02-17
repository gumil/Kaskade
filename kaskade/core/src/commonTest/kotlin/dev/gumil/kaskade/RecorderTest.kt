package dev.gumil.kaskade

import kotlin.test.Test
import kotlin.test.assertEquals

class RecorderTest {

    data class TestRecording(val identifier: String) : Recording

    @Test
    fun `recorder should record all recordings`() {
        val recorder = Recorder()
        val expected = listOf(
            TestRecording("one"),
            TestRecording("two"),
            TestRecording("three")
        )

        recorder.record(TestRecording("one"))
        recorder.record(TestRecording("two"))
        recorder.record(TestRecording("three"))

        assertEquals(expected, recorder.flattenRecording())
    }

    @Test
    fun `recorder with single child`() {
        val recorder = Recorder()
        val childRecorder = Recorder()

        val expected = listOf(
            TestRecording("one"),
            TestRecording("two"),
            TestRecording("three"),
            TestRecording("four"),
            TestRecording("five")
        )

        recorder.record(TestRecording("one"))
        recorder.record(TestRecording("two"))
        recorder.record(TestRecording("three"))

        recorder.addChild(childRecorder)

        childRecorder.record(TestRecording("four"))
        childRecorder.record(TestRecording("five"))

        assertEquals(expected, recorder.flattenRecording())
    }

    @Test
    fun `recorder with nested children`() {
        val recorder = Recorder()
        val childRecorder = Recorder()
        val nestedChildRecorder = Recorder()

        val expected = listOf(
            TestRecording("one"),
            TestRecording("two"),
            TestRecording("three"),
            TestRecording("four"),
            TestRecording("five"),
            TestRecording("six")
        )

        recorder.record(TestRecording("one"))
        recorder.record(TestRecording("two"))
        recorder.record(TestRecording("three"))

        recorder.addChild(childRecorder)

        childRecorder.record(TestRecording("four"))
        childRecorder.record(TestRecording("five"))

        childRecorder.addChild(nestedChildRecorder)

        nestedChildRecorder.record(TestRecording("six"))

        assertEquals(expected, recorder.flattenRecording())
    }
}
