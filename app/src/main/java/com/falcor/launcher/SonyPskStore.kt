package com.falcor.launcher

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.AtomicFile
import java.io.File
import java.io.FileNotFoundException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal class SonyPskStore(context: Context) {

    private val file = AtomicFile(
        File(context.noBackupFilesDir, "sony_psk.enc")
    )

    private val alias = "falcor_sony_psk"

    private fun keyStore(): KeyStore =
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

    private fun existingKey(): SecretKey? =
        keyStore().getKey(alias, null) as? SecretKey

    private fun encryptionKey(): SecretKey {
        existingKey()?.let { return it }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        generator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setKeySize(256)
                .build()
        )

        return generator.generateKey()
    }

    fun read(): String? {
        val bytes = try {
            file.readFully()
        } catch (_: FileNotFoundException) {
            return null
        }

        require(bytes.size > 28) {
            "Invalid encrypted credential"
        }

        val iv = bytes.copyOfRange(0, 12)
        val encrypted = bytes.copyOfRange(12, bytes.size)

        val key = existingKey()
            ?: error("Credential encryption key unavailable")

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            GCMParameterSpec(128, iv)
        )

        return cipher.doFinal(encrypted).toString(Charsets.UTF_8)
    }

    fun clear() {
        file.delete()

        check(!file.baseFile.exists()) {
            "Unable to remove saved Sony credential"
        }
    }

    fun save(psk: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey())

        val encrypted = cipher.doFinal(
            psk.toByteArray(Charsets.UTF_8)
        )

        require(cipher.iv.size == 12)

        val output = file.startWrite()

        try {
            output.write(cipher.iv)
            output.write(encrypted)
            file.finishWrite(output)
        } catch (e: Exception) {
            file.failWrite(output)
            throw e
        }
    }
}
