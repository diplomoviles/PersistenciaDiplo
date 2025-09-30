package com.amaurypm.persistenciadiplo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.amaurypm.persistenciadiplo.databinding.ActivityMainBinding
import com.amaurypm.persistenciadiplo.encryption.EncryptedPrefs
import com.amaurypm.persistenciadiplo.encryption.TinkAeadProvider
import com.google.crypto.tink.Aead

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragment: Fragment

    private lateinit var sp: SharedPreferences

    private lateinit var aead: Aead
    private lateinit var encryptedPrefs: EncryptedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as Fragment

        sp = getSharedPreferences(Constants.SP_FILE, Context.MODE_PRIVATE)

        aead = TinkAeadProvider.aead(this)
        encryptedPrefs = EncryptedPrefs(sp, aead)

        val bgColor = encryptedPrefs.getInt(Constants.BG_COLOR, R.color.white)
        changeColor(bgColor)

        val textoACifrar = "Diplomado de Desarrollo de Aplicaciones para Dispositivos Móviles Emisión 14"
        val textoCifrado = aead.encrypt(textoACifrar.toByteArray(Charsets.UTF_8), "dgtic:unam".toByteArray())
        val textoDescifrado = aead.decrypt(textoCifrado, "dgtic:unam".toByteArray())

        Log.d("APPLOGS", "Texto cifrado: ${textoCifrado.toString(Charsets.UTF_8)}")
        Log.d("APPLOGS", "Texto descifrado: ${textoDescifrado.toString(Charsets.UTF_8)}")


        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_red -> {
                changeColor(R.color.my_red)
                true
            }
            R.id.action_green -> {
                changeColor(R.color.my_green)
                true
            }
            R.id.action_blue -> {
                changeColor(R.color.my_blue)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun changeColor(@ColorRes color: Int){
        navHostFragment.view?.setBackgroundColor(getColor(color))
        saveColor(color)
    }

    private fun saveColor(@ColorRes color: Int){
        encryptedPrefs.putInt(Constants.BG_COLOR, color)
    }
}