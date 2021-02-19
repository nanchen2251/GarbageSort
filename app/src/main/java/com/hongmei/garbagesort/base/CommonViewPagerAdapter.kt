package com.hongmei.garbagesort.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.*

class CommonViewPagerAdapter(fm: FragmentManager, titles: Array<String>) : MyFragmentPagerAdapter(fm, titles) {
    private val fragments = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}
