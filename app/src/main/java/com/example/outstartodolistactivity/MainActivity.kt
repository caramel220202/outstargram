package com.example.outstartodolistactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.outstartodolistactivity.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    val outstarPostFragment = OutstarPostFragment()
    val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tabs = binding.mainTab
        tabs.addTab(tabs.newTab().setIcon(R.drawable.btn_outsta_home))
        tabs.addTab(tabs.newTab().setIcon(R.drawable.btn_outsta_post))
        tabs.addTab(tabs.newTab().setIcon(R.drawable.btn_outsta_my))

        binding.mainPager.adapter = OutstarMainPagerAdapter(3,this@MainActivity,outstarPostFragment)

       binding.mainTab.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
           override fun onTabSelected(tab: TabLayout.Tab?) {
               binding.mainPager.setCurrentItem(tab!!.position)
               if (tab!!.position == 1){
                outstarPostFragment.makePost()
               }
           }

           override fun onTabUnselected(tab: TabLayout.Tab?) {
           }

           override fun onTabReselected(tab: TabLayout.Tab?) {
           }
       })


    }
}
class OutstarMainPagerAdapter(
    val tabCount : Int,
    fragmentActivity: FragmentActivity,
    val outstarPostFragment: OutstarPostFragment
): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return OutstarFeedFragment()
            1 -> return outstarPostFragment
            else -> return OutstarProfileFragment()
        }
    }
}