package com.example.a5eddspellbook

import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.NavigationView)
        val menuButton = findViewById<ImageButton>(R.id.SideMenuButton)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navHome -> replaceFragment(Home())
                R.id.navSearch -> replaceFragment(Search())
                R.id.navSettings -> replaceFragment(Settings())
            }
            drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }


        if (savedInstanceState == null) {
            replaceFragment(Home())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.FragmentContainer, fragment)
        }
    }
}
