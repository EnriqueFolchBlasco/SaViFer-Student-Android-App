package es.efb.isvf_studentapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import es.efb.isvf_studentapp.R
import es.efb.isvf_studentapp.databinding.ActivityMainBinding
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.navigation.NavigationView
import es.efb.isvf_studentapp.fragments.AddPostFragment
import es.efb.isvf_studentapp.fragments.AgendaFragment
import es.efb.isvf_studentapp.fragments.RetrofitFragment
import es.efb.isvf_studentapp.fragments.ChatFragment
import es.efb.isvf_studentapp.fragments.SettingsFragment
import es.efb.isvf_studentapp.utils.PREFERENCES_FILENAME
import es.efb.isvf_studentapp.utils.PREFERENCES_REMEMBER_LOGIN


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,RetrofitFragment.OnFragmentInteractionListener {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigationDrawer()

        val prefs = getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(PREFERENCES_REMEMBER_LOGIN, false)


    }

    private fun setUpNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        val menuItem: MenuItem = binding.navigationView.menu.getItem(0)
        onNavigationItemSelected(menuItem)
        menuItem.isChecked = true
    }

    //override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    //    menuInflater.inflate(R.menu.action_bar_menu, menu)
    //    return true
    //}

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed(){
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun openAddPostFragment() {
        supportFragmentManager.commit {
            replace<AddPostFragment>(R.id.fragment_main_container).addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return when(item.itemId){
            R.id.action_retrofitFragment -> {
                supportFragmentManager.commit {
                    replace<RetrofitFragment>(R.id.fragment_main_container).addToBackStack(null)
                    setReorderingAllowed(true)
                }
                true
            }
            R.id.action_chatFragment -> {
                supportFragmentManager.commit {
                    replace<ChatFragment>(R.id.fragment_main_container).addToBackStack(null)
                    setReorderingAllowed(true)
                }
                true
            }
            R.id.action_settingsFragment -> {
                supportFragmentManager.commit {
                    replace<SettingsFragment>(R.id.fragment_main_container).addToBackStack(null)
                    setReorderingAllowed(true)
                }
                true
            }
            R.id.action_freeFragment -> {
                supportFragmentManager.commit {
                    replace<AgendaFragment>(R.id.fragment_main_container).addToBackStack(null)
                    setReorderingAllowed(true)

                }
                true
            }
            R.id.action_settingsLogout -> {
                logout()
                true
            }
            else -> false
        }

    }

    private fun logout() {
        val prefs = getSharedPreferences(PREFERENCES_FILENAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putBoolean(PREFERENCES_REMEMBER_LOGIN, false)
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}