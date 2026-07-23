package com.ciyato.launcher

import com.ciyato.launcher.data.WorkspaceStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WorkspaceStoreTest {

    @Test
    fun `legacy pager data migrates without losing either workspace`() {
        val layout = WorkspaceStore.migrateLegacy(
            count = 3,
            page0Apps = "com.example.left",
            page2Apps = "com.example.right",
            workspaceApps = "{}",
            workspaceCategories = "{\"0\":\"WORK\",\"2\":\"SOCIAL\"}",
            ciyatoPackage = "com.ciyato.launcher",
        )

        assertEquals(2, layout.workspaces.size)
        assertEquals("workspace-1", layout.workspaceAt(0)?.id)
        assertEquals(listOf("com.ciyato.launcher", "com.example.left"), layout.workspaceAt(0)?.appPackages)
        assertEquals(listOf("com.example.right"), layout.workspaceAt(1)?.appPackages)
        assertEquals(listOf("SOCIAL"), layout.workspaceAt(1)?.categoryKeys)
    }

    @Test
    fun `inserting before a workspace preserves creation identity`() {
        val original = WorkspaceStore.migrateLegacy(3, "", "", "{}", "{}", "com.ciyato.launcher")
        val inserted = requireNotNull(WorkspaceStore.insert(original, 0))

        assertEquals(listOf("workspace-3", "workspace-1", "workspace-2"), inserted.visualOrder)
        assertEquals(1, inserted.workspaces.first { it.id == "workspace-1" }.creationOrder)
        assertEquals(3, inserted.workspaces.first { it.id == "workspace-3" }.creationOrder)
    }

    @Test
    fun `removing a workspace can transfer its shortcuts without renumbering`() {
        val initial = WorkspaceStore.migrateLegacy(3, "com.example.left", "com.example.right", "{}", "{}", "com.ciyato.launcher")
        val result = requireNotNull(WorkspaceStore.remove(initial, "workspace-1", "workspace-2"))

        assertEquals(listOf("workspace-2"), result.visualOrder)
        assertEquals(
            listOf("com.example.right", "com.ciyato.launcher", "com.example.left"),
            result.workspaceAt(0)?.appPackages,
        )
        assertNotNull(WorkspaceStore.parse(WorkspaceStore.serialize(result)))
    }

    @Test
    fun `duplicate inserts a new permanent identity directly after its source`() {
        val initial = WorkspaceStore.migrateLegacy(3, "com.example.left", "com.example.right", "{}", "{}", "com.ciyato.launcher")
        val result = requireNotNull(WorkspaceStore.duplicate(initial, "workspace-1"))

        assertEquals(listOf("workspace-1", "workspace-3", "workspace-2"), result.visualOrder)
        assertEquals(listOf("com.ciyato.launcher", "com.example.left"), result.workspaceAt(1)?.appPackages)
        assertEquals(3, result.workspaceAt(1)?.creationOrder)
    }

    @Test
    fun `default workspace is persisted independently from visual order`() {
        val initial = WorkspaceStore.migrateLegacy(3, "", "", "{}", "{}", "com.ciyato.launcher")
        val result = requireNotNull(WorkspaceStore.setDefault(initial, "workspace-2"))

        assertEquals("workspace-2", result.defaultWorkspaceId)
        assertNotNull(WorkspaceStore.parse(WorkspaceStore.serialize(result)))
    }
}
