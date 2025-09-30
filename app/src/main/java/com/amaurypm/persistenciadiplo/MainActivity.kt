package com.amaurypm.persistenciadiplo


import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.amaurypm.persistenciadiplo.Extensions.dataStore
import com.amaurypm.persistenciadiplo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var navHostFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as Fragment

        lifecycleScope.launch(Dispatchers.IO) {
            readColorDS().collect { bgColor ->
                withContext(Dispatchers.Main){
                    navHostFragment.view?.setBackgroundColor(getColor(bgColor))
                }
            }
        }

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

    private fun changeColor(@ColorRes color: Int) {
        navHostFragment.view?.setBackgroundColor(getColor(color))

        lifecycleScope.launch(Dispatchers.IO) {
            saveColorDS(color)
        }
    }

    private suspend fun saveColorDS(@ColorRes color: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(name = Constants.BG_COLOR)] = color
        }
    }

    private fun readColorDS(): Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[intPreferencesKey(Constants.BG_COLOR)] ?: R.color.white
        }
}