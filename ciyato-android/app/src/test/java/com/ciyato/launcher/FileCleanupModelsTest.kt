package com.ciyato.launcher

import com.ciyato.launcher.data.CleanupAnalysisResult
import com.ciyato.launcher.data.CleanupFileRef
import com.ciyato.launcher.data.DuplicateCleanupGroup
import org.junit.Assert.assertEquals
import org.junit.Test

class FileCleanupModelsTest {
    @Test
    fun reclaimableBytesCountsOnlyExtraVerifiedCopies() {
        val group = DuplicateCleanupGroup(
            bytesPerFile = 4_096L,
            files = listOf(
                CleanupFileRef("content://tree/one", "one.pdf", 4_096L),
                CleanupFileRef("content://tree/two", "two.pdf", 4_096L),
                CleanupFileRef("content://tree/three", "three.pdf", 4_096L),
            ),
        )
        assertEquals(8_192L, group.reclaimableBytes)

        val result = CleanupAnalysisResult(
            rootUri = "content://tree",
            inspectedEntries = 3,
            hashedFiles = 3,
            wasBounded = false,
            groups = listOf(group),
            completedAt = 1L,
        )
        assertEquals(8_192L, result.reclaimableBytes)
    }
}
