package com.aslanovaslan.firemessage

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aslanovaslan.firemessage.ui.myAccount.MyAccountFragment
import com.aslanovaslan.firemessage.ui.people.PeopleFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(PeopleFragment())

        val navigation: BottomNavigationView = findViewById(R.id.nav_view)

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_people -> {
                    replaceFragment(PeopleFragment())
                    true
                }
                R.id.navigation_my_account -> {
                    replaceFragment(MyAccountFragment())
                    true
                }
                R.id.navigation_notifications -> {
                    true
                }
                else -> false

            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().
            replace(R.id.fragment_layout,fragment)
            .commit()

    }
}