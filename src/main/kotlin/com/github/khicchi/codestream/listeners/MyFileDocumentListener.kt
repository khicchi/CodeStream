package com.github.khicchi.codestream.listeners

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class MyFileDocumentListener : FileDocumentManagerListener {
    override fun beforeDocumentSaving(document: Document) {
        val project = ProjectManager.getInstance().openProjects[0]
        val virtualFile: VirtualFile? = FileDocumentManager.getInstance().getFile(document)
        if (virtualFile != null && !virtualFile.isDirectory) {
            val fileContent = document.text
            sendCodeToWebsite(fileContent)
        }
    }

    private fun sendCodeToWebsite(code: String) {
        val client = OkHttpClient()
        val mediaType = "text/plain".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, code)
        val request = Request.Builder()
            .url("http://localhost:8080/")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }
                println(response.body?.string())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
