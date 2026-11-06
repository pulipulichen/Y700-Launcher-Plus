package info.pulipuli.y700launcherplus

import org.junit.Assert.assertEquals
import org.junit.Test

class LauncherLayoutTest {
    private val layout = LauncherLayout(
        pages = listOf(listOf("one"), listOf("two"), listOf("three")),
        dock = listOf("two", "one"), defaultPage = 1, drawerColumns = 5
    )

    @Test fun movingDefaultPageFollowsItsContent() {
        val next = layout.movePage(1, 2)
        assertEquals(listOf(listOf("one"), listOf("three"), listOf("two")), next.pages)
        assertEquals(2, next.defaultPage)
    }

    @Test fun movingAnotherPagePreservesDefaultContent() {
        assertEquals(0, layout.movePage(0, 2).defaultPage)
        assertEquals(2, layout.movePage(2, 0).defaultPage)
    }

    @Test fun deletingPageBeforeDefaultAdjustsIndex() {
        assertEquals(0, layout.removePage(0).defaultPage)
        assertEquals(listOf(listOf("two"), listOf("three")), layout.removePage(0).pages)
    }

    @Test fun deletingDefaultSelectsNeighbor() {
        assertEquals(1, layout.removePage(1).defaultPage)
        assertEquals(1, layout.copy(defaultPage = 2).removePage(2).defaultPage)
    }

    @Test fun deletingPageAfterDefaultPreservesIndexAndDock() {
        val next = layout.removePage(2)
        assertEquals(1, next.defaultPage)
        assertEquals(layout.dock, next.dock)
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotDeleteOnlyPage() {
        LauncherLayout(listOf(emptyList())).removePage(0)
    }

    @Test fun versionTwoRoundTrip() {
        assertEquals(layout, LauncherLayout.fromJson(layout.toJson()))
    }

    @Test fun versionOneDefaultsAndOrderArePreserved() {
        val old = """{"version":1,"pages":[["b","a"]],"dock":["a"],"hidden":[],"columns":4,"showLabels":false}"""
        val restored = LauncherLayout.fromJson(old)
        assertEquals(0, restored.defaultPage)
        assertEquals(0, restored.drawerColumns)
        assertEquals(listOf("b", "a"), restored.pages[0])
        assertEquals(restored, LauncherLayout.fromJson(restored.toJson()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidDefaultPageIsRejected() {
        LauncherLayout.fromJson(layout.toJson().replace("\"defaultPage\":1", "\"defaultPage\":9"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidDrawerColumnsAreRejected() {
        LauncherLayout.fromJson(layout.toJson().replace("\"drawerColumns\":5", "\"drawerColumns\":99"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun futureVersionIsRejected() {
        LauncherLayout.fromJson(layout.toJson().replace("\"version\":2", "\"version\":99"))
    }
}
