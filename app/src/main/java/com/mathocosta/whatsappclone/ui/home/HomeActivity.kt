package com.mathocosta.whatsappclone.ui.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mathocosta.whatsappclone.R
import com.mathocosta.whatsappclone.auth.AuthGateway
import com.mathocosta.whatsappclone.databinding.ActivityHomeBinding
import com.mathocosta.whatsappclone.ui.home.chats.ChatsFragment
import com.mathocosta.whatsappclone.ui.home.contacts.ContactsFragment
import com.mathocosta.whatsappclone.ui.settings.SettingsActivity

class HomeActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val viewPagerAdapter by lazy {
        ViewPagerFragmentAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()

        binding.chatsViewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.chatsTabLayout, binding.chatsViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.chats_tab_title)
                1 -> tab.text = getString(R.string.contacts_tab_title)
            }
        }.attach()
    }

    private fun setupActionBar() {
        val toolbar = binding.toolbarLayout.mainToolbar
        toolbar.title = "WhatsApp"
        setSupportActionBar(toolbar)
    }

    private fun getCurrentFragment(): Fragment? = with(binding.chatsViewPager) {
        supportFragmentManager.findFragmentByTag("f$currentItem")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        menu?.findItem(R.id.app_bar_search)?.let {
            val searchView = it.actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Int.MAX_VALUE
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val currentFragment = getCurrentFragment()
                    return if (currentFragment != null && currentFragment is FilterableContent) {
                        currentFragment.getFilter().filter(newText)
                        true
                    } else {
                        false
                    }
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_logout -> {
                AuthGateway.signOut()
                finish()
            }
            R.id.app_bar_settings -> startActivity(
                Intent(this, SettingsActivity::class.java)
            )
        }

        return super.onOptionsItemSelected(item)
    }

    private class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> ChatsFragment()
                1 -> ContactsFragment()
                else -> ChatsFragment()
            }
    }
}