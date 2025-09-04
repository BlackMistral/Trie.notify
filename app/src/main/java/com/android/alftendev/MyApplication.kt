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

    companion object {
        lateinit var instance: MyApplication
            private set

        val executor: ExecutorService by lazy { Executors.newCachedThreadPool() }

        // --- INIZIO MODIFICA CHIAVE ---
        // Ora il database viene inizializzato in modo LAZY ma con TUTTE le configurazioni
        // avanzate che erano precedentemente in DatabaseFactory.
        val database: BoxStore by lazy {
            MyObjectBox.builder()
                .androidContext(instance.applicationContext)
                // Le prossime righe sono state recuperate da DatabaseFactory.kt
                // Nota: la custom baseDirectory non è quasi mai necessaria e può causare problemi.
                // La lascio commentata per ora. Se la build fallisce ancora, la rimuoveremo del tutto.
                // .baseDirectory(File(instance.applicationContext.filesDir, "objectbox"))
                .build()
        }
        // --- FINE MODIFICA CHIAVE ---

        val notifications: Box<Notifications> by lazy { database.boxFor(Notifications::class.java) }
        val packageNames: Box<PackageName> by lazy { database.boxFor(PackageName::class.java) }

        const val sharedPrefName: String = "NotInfo"
        val sharedPref: SharedPreferences by lazy {
            instance.getSharedPreferences(sharedPrefName, MODE_PRIVATE)
        }

        val defaultSwValue: String by lazy {
            instance.getString(R.string.defaultSwitchValue)
        }
        
        val pm: PackageManager by lazy {
            instance.applicationContext.packageManager
        }

        val authSuccess = AtomicBoolean(false)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())
    }
}
