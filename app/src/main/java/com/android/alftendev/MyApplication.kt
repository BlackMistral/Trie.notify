package com.android.alfendev

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.android.alfendev.models.MyObjectBox
import com.android.alfendev.models.Notifications
import com.android.alfendev.models.PackageName
import com.android.alfendev.utils.MyActivityLifecycleCallbacks
import io.objectbox.Box
import io.objectbox.BoxStore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class MyApplication : Application() {

    // Il companion object ora conterrà solo le istanze Singleton,
    // inizializzate in modo sicuro e "pigro" (lazy).
    companion object {
        // L'istanza dell'applicazione. Verrà inizializzata una sola volta in onCreate.
        lateinit var instance: MyApplication
            private set // Impedisce la modifica dall'esterno

        // L'executor è un thread pool. Lazy è perfetto per crearlo solo quando serve.
        val executor: ExecutorService by lazy { Executors.newCachedThreadPool() }

        // Il database BoxStore. È il cuore di ObjectBox.
        // Viene inizializzato solo quando qualcuno chiede di accedervi per la prima volta.
        val database: BoxStore by lazy {
            // MyObjectBox è la classe generata da ObjectBox. È il modo corretto di costruire il database.
            MyObjectBox.builder()
                .androidContext(instance.applicationContext)
                .build()
        }

        // Le "scatole" (tabelle) di ObjectBox. Anche queste vengono aperte solo quando servono.
        val notifications: Box<Notifications> by lazy { database.boxFor(Notifications::class.java) }
        val packageNames: Box<PackageName> by lazy { database.boxFor(PackageName::class.java) }

        // Valori e preferenze condivise, anch'essi inizializzati in modo lazy.
        val sharedPrefName: String = "NotInfo"
        val sharedPref: SharedPreferences by lazy {
            instance.getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        }

        // Il valore di default, letto dalle risorse solo quando necessario.
        val defaultSwValue: String by lazy {
            instance.getString(R.string.defaultSwitchValue)
        }
        
        // Il PackageManager del sistema.
        val pm: PackageManager by lazy {
            instance.applicationContext.packageManager
        }

        // Lo stato di autenticazione.
        val authSuccess = AtomicBoolean(false)
    }

    override fun onCreate() {
        super.onCreate()
        
        // Inizializziamo l'istanza statica con il contesto corretto fornito da Android.
        // Questa è l'unica cosa che deve essere "eager" (immediata).
        instance = this

        // Registriamo i callback del ciclo di vita delle activity.
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }
}
