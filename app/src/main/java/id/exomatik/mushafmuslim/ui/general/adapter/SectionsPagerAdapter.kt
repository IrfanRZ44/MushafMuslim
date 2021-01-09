package id.exomatik.mushafmuslim.ui.general.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class SectionsPagerAdapter(fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
    }

    override fun getCount(): Int = mFragmentList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun addFragment(
        fragment: Fragment,
        title: String
    ) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }
}