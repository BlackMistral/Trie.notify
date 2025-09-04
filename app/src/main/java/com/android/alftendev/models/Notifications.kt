package com.android.alfendev.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import org.json.JSONObject
import java.util.Date

// Interfaccia per la serializzazione JSON, se non è già definita in un altro file.
// Se è già definita altrove, puoi rimuovere queste 3 righe.
interface IJsonSerializable {
    fun toJson(): JSONObject
}

@Entity
data class Notifications(
    @Id
    var entityId: Long = 0,
    var title: String = String(),
    var time: Date = Date(),
    var text: String = String(),
    var bigText: String? = null,
    var conversationTitle: String? = null,
    var infoText: String? = null,
    var peopleList: String? = null,
    var titleBig: String? = null,
    var isDeleted: Boolean = false,
    
    // --- CAMPI AGGIUNTI PER HASH CHAIN ---
    var previousNotificationHash: String = "",
    var currentNotificationHash: String = ""
    // ------------------------------------

) : IJsonSerializable {
    lateinit var packageName: ToOne<PackageName>

    override fun toString(): String {
        // La funzione toString originale è stata mantenuta per non alterare eventuali log esistenti.
        return "Notifications(entityId=$entityId, packageName='${packageName.target.pkg}', " +
                "title='$title', time=$time, text='$text', bigText=$bigText, " +
                "conversationTitle=$conversationTitle, infoText=$infoText, " +
                "peopleList=$peopleList, titleBig=$titleBig, isDeleted=$isDeleted)"
    }

    override fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("entityId", entityId)
        json.put("title", title)
        // È buona norma salvare i timestamp come Long (millisecondi) in JSON per evitare problemi di fuso orario
        json.put("time", time.time)
        json.put("text", text)
        json.put("bigText", bigText)
        json.put("conversationTitle", conversationTitle)
        json.put("infoText", infoText)
        json.put("peopleList", peopleList)
        json.put("titleBig", titleBig)
        json.put("isDeleted", isDeleted)
        // Gestisce il caso in cui la relazione non sia stata ancora caricata
        if (this::packageName.isInitialized && packageName.target != null) {
            json.put("packageName", packageName.target.pkg)
        } else {
            json.put("packageName", "N/A")
        }

        // --- CAMPI AGGIUNTI INCLUSI NELL'ESPORTAZIONE JSON ---
        json.put("previousNotificationHash", previousNotificationHash)
        json.put("currentNotificationHash", currentNotificationHash)
        // ----------------------------------------------------

        return json
    }
}
