package com.ciyato.launcher.data

/**
 * TFLiteCategorizerHelper — Suggestion #28
 * On-device ML categorization using TensorFlow Lite.
 * Falls back to rule-based AppCategorizer when TFLite model not bundled.
 *
 * To enable full TFLite:
 *  1. Add `implementation("org.tensorflow:tensorflow-lite:2.14.0")` to build.gradle.
 *  2. Place `app_categorizer.tflite` in assets/ml/
 *  3. Uncomment the Interpreter block below.
 *
 * The model input: float32[1, 64] (token embedding of app label + package name).
 * The model output: float32[1, N_CATEGORIES] (softmax probabilities).
 */
object TFLiteCategorizerHelper {

    private val CATEGORY_LABELS = AppCategory.entries.map { it.name }

    /** Feature-extract: simple character n-gram hash → fixed-size vector. */
    private fun textToEmbedding(text: String, size: Int = 64): FloatArray {
        val result = FloatArray(size)
        val normalized = text.lowercase().replace(Regex("[^a-z0-9]"), "")
        for (i in normalized.indices) {
            val idx = (normalized[i].code * 31 + i) % size
            result[idx] = (result[idx] + 1f).coerceAtMost(1f)
        }
        return result
    }

    /**
     * Classify an app using the TFLite model if available,
     * otherwise delegate to rule-based AppCategorizer.
     */
    fun classify(label: String, packageName: String): AppCategory {
        // TFLite path (uncomment when model is bundled):
        // val embedding = textToEmbedding("$label $packageName")
        // val interpreter = Interpreter(loadModelFile(context, "ml/app_categorizer.tflite"))
        // val output = Array(1) { FloatArray(CATEGORY_LABELS.size) }
        // interpreter.run(arrayOf(embedding), output)
        // val bestIdx = output[0].indices.maxByOrNull { output[0][it] } ?: 0
        // return AppCategory.valueOf(CATEGORY_LABELS[bestIdx])

        // Rule-based fallback (current):
        return AppCategorizer.categorize(label, packageName)
    }

    /**
     * Batch classify a list of installed apps, returning a map of
     * package name → predicted category.
     */
    fun classifyAll(apps: List<InstalledApp>): Map<String, AppCategory> {
        return apps.associate { app ->
            app.packageName to classify(app.label, app.packageName)
        }
    }

    /**
     * Confidence score for the current (rule-based) classifier.
     * Returns 1.0 for exact keyword matches, 0.5 for partial matches, 0.1 for default.
     */
    fun confidence(label: String, packageName: String): Float {
        val category = classify(label, packageName)
        val combined = "$label $packageName".lowercase()
        val keywordsForCategory = AppCategorizer.categoryKeywords(category)
        return when {
            keywordsForCategory.any { combined.contains(it) } -> 1.0f
            keywordsForCategory.any { combined.contains(it.take(4)) } -> 0.5f
            else -> 0.1f
        }
    }
}
