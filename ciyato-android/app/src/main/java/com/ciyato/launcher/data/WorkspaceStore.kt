package com.ciyato.launcher.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * Versioned workspace storage. Visual order and creation identity intentionally
 * stay separate so inserting or reordering a workspace never renumbers it.
 */
data class WorkspaceRecord(
    val id: String,
    val creationOrder: Int,
    val name: String? = null,
    val appPackages: List<String> = emptyList(),
    val categoryKeys: List<String> = emptyList(),
    val starterDismissed: Boolean = false,
)

data class WorkspaceLayout(
    val workspaces: List<WorkspaceRecord>,
    val visualOrder: List<String>,
    val defaultWorkspaceId: String,
    val version: Int = CURRENT_VERSION,
) {
    companion object {
        const val CURRENT_VERSION = 1
    }

    fun workspaceAt(index: Int): WorkspaceRecord? = visualOrder
        .getOrNull(index)
        ?.let { id -> workspaces.firstOrNull { it.id == id } }

    fun indexOf(id: String): Int = visualOrder.indexOf(id)
}

object WorkspaceStore {
    private const val MAX_WORKSPACES = 10

    fun parse(raw: String): WorkspaceLayout? = runCatching {
        val root = JSONObject(raw)
        if (root.optInt("version", 0) != WorkspaceLayout.CURRENT_VERSION) return null
        val records = root.optJSONArray("workspaces") ?: return null
        val workspaces = buildList {
            for (index in 0 until records.length()) {
                val item = records.optJSONObject(index) ?: continue
                val id = item.optString("id")
                val creationOrder = item.optInt("creationOrder", 0)
                if (id.isBlank() || creationOrder <= 0) continue
                add(
                    WorkspaceRecord(
                        id = id,
                        creationOrder = creationOrder,
                        name = item.optString("name").takeIf { it.isNotBlank() },
                        appPackages = stringList(item.optJSONArray("appPackages")),
                        categoryKeys = stringList(item.optJSONArray("categoryKeys")),
                        starterDismissed = item.optBoolean("starterDismissed", false),
                    ),
                )
            }
        }
        val visualOrder = stringList(root.optJSONArray("visualOrder"))
        val defaultWorkspaceId = root.optString("defaultWorkspaceId")
        WorkspaceLayout(workspaces, visualOrder, defaultWorkspaceId).takeIf(::isValid)
    }.getOrNull()

    fun serialize(layout: WorkspaceLayout): String = JSONObject().apply {
        put("version", WorkspaceLayout.CURRENT_VERSION)
        put("defaultWorkspaceId", layout.defaultWorkspaceId)
        put("visualOrder", JSONArray(layout.visualOrder))
        put("workspaces", JSONArray().apply {
            layout.workspaces.forEach { workspace ->
                put(
                    JSONObject().apply {
                        put("id", workspace.id)
                        put("creationOrder", workspace.creationOrder)
                        workspace.name?.let { put("name", it) }
                        put("appPackages", JSONArray(workspace.appPackages.distinct()))
                        put("categoryKeys", JSONArray(workspace.categoryKeys.distinct()))
                        put("starterDismissed", workspace.starterDismissed)
                    },
                )
            }
        })
    }.toString()

    fun migrateLegacy(
        count: Int,
        page0Apps: String,
        page2Apps: String,
        workspaceApps: String,
        workspaceCategories: String,
        ciyatoPackage: String,
    ): WorkspaceLayout {
        // Legacy storage counted the central Home page as a workspace page.
        // V2 stores only movable workspaces, leaving Home outside this model.
        val safePagerCount = count.coerceIn(3, MAX_WORKSPACES + 1)
        val safeCount = safePagerCount - 1
        val legacyApps = runCatching { JSONObject(workspaceApps) }.getOrDefault(JSONObject())
        val legacyCategories = runCatching { JSONObject(workspaceCategories) }.getOrDefault(JSONObject())
        val workspaces = (0 until safeCount).map { index ->
            val legacyPageIndex = if (index == 0) 0 else index + 1
            val apps = when (legacyPageIndex) {
                0 -> csv(page0Apps)
                2 -> csv(page2Apps)
                else -> csv(legacyApps.optString(legacyPageIndex.toString()))
            }.toMutableList()
            if (index == 0 && ciyatoPackage.isNotBlank()) {
                // Ciyato is a first-class standalone app on Workspace 1. Keep
                // every legacy shortcut, but make the Ciyato entry deterministic.
                apps.remove(ciyatoPackage)
                apps.add(0, ciyatoPackage)
            }
            WorkspaceRecord(
                id = "workspace-${index + 1}",
                creationOrder = index + 1,
                name = "Workspace ${index + 1}",
                appPackages = apps.distinct(),
                categoryKeys = csv(legacyCategories.optString(legacyPageIndex.toString())),
            )
        }
        return WorkspaceLayout(
            workspaces = workspaces,
            visualOrder = workspaces.map(WorkspaceRecord::id),
            defaultWorkspaceId = workspaces.first().id,
        )
    }

    fun insert(layout: WorkspaceLayout, visualIndex: Int): WorkspaceLayout? {
        if (!isValid(layout) || layout.workspaces.size >= MAX_WORKSPACES) return null
        val creationOrder = (layout.workspaces.maxOfOrNull(WorkspaceRecord::creationOrder) ?: 0) + 1
        val workspace = WorkspaceRecord(
            id = "workspace-$creationOrder",
            creationOrder = creationOrder,
            name = "Workspace $creationOrder",
        )
        val order = layout.visualOrder.toMutableList()
        order.add(visualIndex.coerceIn(0, order.size), workspace.id)
        return layout.copy(workspaces = layout.workspaces + workspace, visualOrder = order)
    }

    fun remove(layout: WorkspaceLayout, workspaceId: String, moveContentsTo: String? = null): WorkspaceLayout? {
        if (!isValid(layout) || layout.workspaces.size <= 1 || workspaceId !in layout.visualOrder) return null
        val removed = layout.workspaces.firstOrNull { it.id == workspaceId } ?: return null
        val remaining = layout.workspaces.filterNot { it.id == workspaceId }.toMutableList()
        val destinationId = moveContentsTo?.takeIf { it != workspaceId && it in layout.visualOrder }
        if (destinationId != null) {
            val destinationIndex = remaining.indexOfFirst { it.id == destinationId }
            if (destinationIndex >= 0) {
                val destination = remaining[destinationIndex]
                remaining[destinationIndex] = destination.copy(
                    appPackages = (destination.appPackages + removed.appPackages).distinct(),
                    categoryKeys = (destination.categoryKeys + removed.categoryKeys).distinct(),
                )
            }
        }
        val order = layout.visualOrder.filterNot { it == workspaceId }
        val defaultId = layout.defaultWorkspaceId.takeIf { it != workspaceId } ?: order.first()
        return WorkspaceLayout(remaining, order, defaultId)
    }

    fun reorder(layout: WorkspaceLayout, sourceIndex: Int, destinationIndex: Int): WorkspaceLayout? {
        if (!isValid(layout) || sourceIndex !in layout.visualOrder.indices) return null
        val order = layout.visualOrder.toMutableList()
        val id = order.removeAt(sourceIndex)
        order.add(destinationIndex.coerceIn(0, order.size), id)
        return layout.copy(visualOrder = order)
    }

    fun rename(layout: WorkspaceLayout, workspaceId: String, name: String): WorkspaceLayout? {
        val cleanName = name.trim().take(40)
        if (!isValid(layout) || workspaceId !in layout.visualOrder || cleanName.isBlank()) return null
        return layout.copy(workspaces = layout.workspaces.map { workspace ->
            if (workspace.id == workspaceId) workspace.copy(name = cleanName) else workspace
        })
    }

    fun duplicate(layout: WorkspaceLayout, workspaceId: String): WorkspaceLayout? {
        if (!isValid(layout) || layout.workspaces.size >= MAX_WORKSPACES) return null
        val source = layout.workspaces.firstOrNull { it.id == workspaceId } ?: return null
        val creationOrder = (layout.workspaces.maxOfOrNull(WorkspaceRecord::creationOrder) ?: 0) + 1
        val copy = source.copy(
            id = "workspace-$creationOrder",
            creationOrder = creationOrder,
            name = "Copy of ${source.name ?: "Workspace ${source.creationOrder}"}".take(40),
            starterDismissed = source.starterDismissed ||
                source.appPackages.isNotEmpty() || source.categoryKeys.isNotEmpty(),
        )
        val order = layout.visualOrder.toMutableList()
        order.add(layout.indexOf(workspaceId) + 1, copy.id)
        return layout.copy(workspaces = layout.workspaces + copy, visualOrder = order)
    }

    fun setDefault(layout: WorkspaceLayout, workspaceId: String): WorkspaceLayout? {
        if (!isValid(layout) || workspaceId !in layout.visualOrder) return null
        return layout.copy(defaultWorkspaceId = workspaceId)
    }

    fun withWorkspace(layout: WorkspaceLayout, workspace: WorkspaceRecord): WorkspaceLayout? {
        if (!isValid(layout) || workspace.id !in layout.visualOrder) return null
        return layout.copy(workspaces = layout.workspaces.map { current ->
            if (current.id == workspace.id) workspace.copy(
                appPackages = workspace.appPackages.distinct(),
                categoryKeys = workspace.categoryKeys.distinct(),
            ) else current
        })
    }

    private fun isValid(layout: WorkspaceLayout): Boolean {
        val ids = layout.workspaces.map(WorkspaceRecord::id)
        val sequences = layout.workspaces.map(WorkspaceRecord::creationOrder)
        return layout.version == WorkspaceLayout.CURRENT_VERSION &&
            ids.size in 1..MAX_WORKSPACES &&
            ids.distinct().size == ids.size &&
            sequences.distinct().size == sequences.size &&
            layout.visualOrder.size == ids.size &&
            layout.visualOrder.toSet() == ids.toSet() &&
            layout.defaultWorkspaceId in ids
    }

    private fun stringList(array: JSONArray?): List<String> = buildList {
        if (array == null) return@buildList
        for (index in 0 until array.length()) {
            array.optString(index).trim().takeIf { it.isNotBlank() }?.let(::add)
        }
    }.distinct()

    private fun csv(value: String): List<String> = value
        .split(',')
        .map(String::trim)
        .filter(String::isNotEmpty)
        .distinct()
}
