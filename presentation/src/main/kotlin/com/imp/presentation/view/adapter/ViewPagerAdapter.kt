package com.imp.presentation.view.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.imp.presentation.base.BaseFragment

/**
 * ViewPager Adapter
 */
class ViewPagerAdapter(activity: AppCompatActivity, private val fragments: ArrayList<BaseFragment<*>>) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun clear(activity: FragmentActivity?) {

        try{

            activity?.let {

                for (fragment in fragments) {
                    it.supportFragmentManager.beginTransaction().remove(fragment).commit()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}