package com.android.alfendev.utils // <-- MODIFICATO PER CORRISPONDERE ALLA NUOVA POSIZIONE

import java.security.MessageDigest

/**
 * Un oggetto di utilità (Singleton) per le operazioni di hashing.
 * Contiene funzioni pure per calcolare hash crittografici.
 */
object HashingUtils {

    /**
     * Calcola l'hash SHA-256 di una stringa di input.
     *
     * @param input La stringa da cui calcolare l'hash.
     * @return L'hash SHA-256 rappresentato come una stringa esadecimale di 64 caratteri.
     */
    fun calculateSha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-266")
            val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
            // Converte l'array di byte in una stringa esadecimale
            hashBytes.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            // In caso di un errore improbabile (es. SHA-256 non supportato),
            // restituisce un hash della stringa vuota per evitare crash.
            // Loggare l'errore in un'app di produzione sarebbe ideale.
            e.printStackTrace()
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" // SHA-256 di ""
        }
    }
}
