package com.infiren.crispyapp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.infiren.crispyapp.view.fragment.HelpFragmentFour
import com.infiren.crispyapp.view.fragment.HelpFragmentOne
import com.infiren.crispyapp.view.fragment.HelpFragmentThree
import com.infiren.crispyapp.view.fragment.HelpFragmentTwo

class HelpFragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HelpFragmentOne()
            1 -> HelpFragmentTwo()
            2 -> HelpFragmentThree()
            3 -> HelpFragmentFour()
            else -> HelpFragmentOne()
        }
    }
}