package com.example.premiumapp.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.premiumapp.App

class ImportMediaWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val uriStrings = inputData.getStringArray(KEY_URIS) ?: return Result.failure()
        val app = applicationContext as? App ?: return Result.failure()
        val useCase = app.container.importMediaUseCase

        var successCount = 0
        var failCount = 0

        for ((index, uriStr) in uriStrings.withIndex()) {
            setProgress(
                workDataOf(
                    PROGRESS_CURRENT to index + 1,
                    PROGRESS_TOTAL to uriStrings.size
                )
            )

            try {
                val uri = Uri.parse(uriStr)
                val result = useCase(uri)
                if (result.isSuccess) {
                    successCount++
                } else {
                    failCount++
                }
            } catch (_: Exception) {
                failCount++
            }
        }

        return Result.success(
            workDataOf(
                RESULT_SUCCESS_COUNT to successCount,
                RESULT_FAIL_COUNT to failCount
            )
        )
    }

    companion object {
        const val KEY_URIS = "key_uris"
        const val PROGRESS_CURRENT = "progress_current"
        const val PROGRESS_TOTAL = "progress_total"
        const val RESULT_SUCCESS_COUNT = "result_success_count"
        const val RESULT_FAIL_COUNT = "result_fail_count"
    }
}

class ExportMediaWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val mediaIds = inputData.getLongArray(KEY_MEDIA_IDS) ?: return Result.failure()
        val app = applicationContext as? App ?: return Result.failure()
        val mediaRepo = app.container.mediaRepository

        var successCount = 0
        var failCount = 0

        val items = mediaRepo.getMediaByIds(mediaIds.toList())

        for ((index, item) in items.withIndex()) {
            setProgress(
                workDataOf(
                    PROGRESS_CURRENT to index + 1,
                    PROGRESS_TOTAL to items.size
                )
            )

            val exportResult = mediaRepo.exportToGallery(item)
            if (exportResult.isSuccess) {
                successCount++
            } else {
                failCount++
            }
        }

        return Result.success(
            workDataOf(
                RESULT_SUCCESS_COUNT to successCount,
                RESULT_FAIL_COUNT to failCount
            )
        )
    }

    companion object {
        const val KEY_MEDIA_IDS = "key_media_ids"
        const val PROGRESS_CURRENT = "progress_current"
        const val PROGRESS_TOTAL = "progress_total"
        const val RESULT_SUCCESS_COUNT = "result_success_count"
        const val RESULT_FAIL_COUNT = "result_fail_count"
    }
}
