package com.android.alfendev.utils

import com.android.alfendev.models.Notifications

/**
 * Gestisce la logica per la creazione e l'applicazione di una catena di hash
 * alle entità di tipo Notifica.
 */
object HashChainManager {

    // L'hash SHA-256 di una stringa vuota. Usato come punto di partenza se non ci sono altre notifiche.
    private const val GENESIS_HASH = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

    /**
     * Applica la logica della hash chain a un oggetto Notifica prima che venga salvato.
     * Modifica l'oggetto in-place, impostando le sue proprietà 'previousNotificationHash' e 'currentNotificationHash'.
     *
     * @param notification L'oggetto Notifica da modificare.
     */
    fun applyHashChain(notification: Notifications) {
        // Step 1: Recupera l'hash dell'ultima notifica salvata nel database.
        // La funzione getAbsoluteLastNotification() sarà aggiunta a DBUtils nel prossimo task.
        val lastNotification = DBUtils.getAbsoluteLastNotification()
        val previousHash = lastNotification?.currentNotificationHash ?: GENESIS_HASH

        // Step 2: Crea una stringa di dati deterministica e univoca per la notifica corrente.
        // È cruciale che l'ordine e il contenuto siano sempre gli stessi per gli stessi dati.
        val notificationData = StringBuilder().apply {
            append(notification.time.time)
            if (notification::packageName.isInitialized && notification.packageName.target != null) {
                append(notification.packageName.target.pkg)
            }
            append(notification.title)
            append(notification.text)
            append(notification.peopleList ?: "")
        }.toString()

        // Step 3: Calcola il nuovo hash concatenando i dati attuali con l'hash precedente.
        val currentHash = HashingUtils.calculateSha256(notificationData + previousHash)

        // Step 4: Imposta i valori calcolati sull'oggetto notifica.
        notification.previousNotificationHash = previousHash
        notification.currentNotificationHash = currentHash
    }
}
