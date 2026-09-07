package com.example.sync

import android.content.Context
import android.content.Intent
import com.example.data.repository.FinanceRepository
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class SyncResult {
    data class Success(val message: String, val timestamp: Long = System.currentTimeMillis()) : SyncResult()
    data class Error(val message: String) : SyncResult()
}

class DriveSyncManager(
    private val context: Context,
    private val repository: FinanceRepository
) {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val driveScope = "https://www.googleapis.com/auth/drive.appdata"
    private val backupFileName = "rushu_fin_backup.json"

    val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(driveScope), Scope("https://www.googleapis.com/auth/drive.file"))
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun signOut(onComplete: () -> Unit) {
        googleSignInClient.signOut().addOnCompleteListener { onComplete() }
    }

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    fun getCurrentAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        val account = getCurrentAccount()?.account ?: return@withContext null
        try {
            GoogleAuthUtil.getToken(context, account, "oauth2:$driveScope")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun backupToGoogleDrive(): SyncResult = withContext(Dispatchers.IO) {
        val account = getCurrentAccount()
        if (account == null) {
            return@withContext SyncResult.Error("Google Account not linked. Please sign in to enable free Drive backup.")
        }

        val token = getAccessToken()
        if (token.isNullOrEmpty()) {
            return@withContext SyncResult.Error("Unable to acquire Google Drive authorization. Please sign in again.")
        }

        try {
            val jsonPayload = repository.exportBackupJson()
            val existingFileId = findBackupFileId(token)

            val success = if (existingFileId != null) {
                updateDriveFile(token, existingFileId, jsonPayload)
            } else {
                createDriveFile(token, jsonPayload)
            }

            if (success) {
                val now = System.currentTimeMillis()
                repository.updateSyncInfo(account.email, now)
                SyncResult.Success("Cloud backup updated successfully on Google Drive", now)
            } else {
                SyncResult.Error("Google Drive API failed to write snapshot.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult.Error("Backup failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun restoreFromGoogleDrive(): SyncResult = withContext(Dispatchers.IO) {
        val account = getCurrentAccount()
        if (account == null) {
            return@withContext SyncResult.Error("No Google Account linked. Please sign in first.")
        }

        val token = getAccessToken()
        if (token.isNullOrEmpty()) {
            return@withContext SyncResult.Error("Google authorization missing. Please sign in again.")
        }

        try {
            val fileId = findBackupFileId(token)
                ?: return@withContext SyncResult.Error("No existing backup file found in your Google Drive AppData.")

            val request = Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files/$fileId?alt=media")
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext SyncResult.Error("Failed to fetch backup: HTTP ${response.code}")
                }
                val content = response.body?.string()
                if (content.isNullOrEmpty()) {
                    return@withContext SyncResult.Error("Backup file is empty.")
                }

                val restored = repository.restoreBackupJson(content)
                if (restored) {
                    val now = System.currentTimeMillis()
                    repository.updateSyncInfo(account.email, now)
                    SyncResult.Success("Data restored from Google Drive successfully!", now)
                } else {
                    SyncResult.Error("Failed to parse backup content.")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult.Error("Restore failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun clearCloudBackup(): SyncResult = withContext(Dispatchers.IO) {
        val account = getCurrentAccount()
        if (account == null) {
            return@withContext SyncResult.Error("No Google Account linked. Please sign in first.")
        }

        val token = getAccessToken()
        if (token.isNullOrEmpty()) {
            return@withContext SyncResult.Error("Google authorization missing. Please sign in again.")
        }

        try {
            val fileId = findBackupFileId(token)
                ?: return@withContext SyncResult.Success("No cloud backup found — already clear.")

            val request = Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files/$fileId")
                .header("Authorization", "Bearer $token")
                .delete()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 404) {
                    repository.updateSyncInfo(account.email, 0L)
                    SyncResult.Success("Cloud backup cleared. Starting fresh.")
                } else {
                    SyncResult.Error("Failed to clear backup: HTTP ${response.code}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult.Error("Clear backup failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private fun findBackupFileId(token: String): String? {
        val url = "https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&q=name='$backupFileName' and trashed=false&fields=files(id,name,modifiedTime)"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val json = JSONObject(body)
            val files = json.optJSONArray("files") ?: return null
            if (files.length() > 0) {
                return files.getJSONObject(0).optString("id")
            }
        }
        return null
    }

    private fun createDriveFile(token: String, content: String): Boolean {
        val metadataJson = JSONObject()
            .put("name", backupFileName)
            .put("parents", org.json.JSONArray().put("appDataFolder"))
            .toString()

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(
                metadataJson.toRequestBody("application/json; charset=UTF-8".toMediaType())
            )
            .addPart(
                content.toRequestBody("application/json; charset=UTF-8".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
            .header("Authorization", "Bearer $token")
            .post(multipartBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    private fun updateDriveFile(token: String, fileId: String, content: String): Boolean {
        val body = content.toRequestBody("application/json; charset=UTF-8".toMediaType())
        val request = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files/$fileId?uploadType=media")
            .header("Authorization", "Bearer $token")
            .patch(body)
            .build()

        httpClient.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }
}
