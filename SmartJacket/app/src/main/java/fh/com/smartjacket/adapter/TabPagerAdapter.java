package fh.com.smartjacket.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * PagerAdapter which manages the fragments that are displayed in a TabLayout.
 * Created by nils on 11.12.17.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {
	private final List<Fragment> tabFragments = new ArrayList<>();
	private final List<String> tabTitles = new ArrayList<>();
	private boolean showOnlyIcons = false;

	/**
	 * Constructor.
	 * @param fragmentManager FragmentManager
	 */
	public TabPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	/**
	 * Adds a fragment (tab).
	 * @param fragment Fragment that should be added.
	 * @param title Title of the tab.
	 */
	public void addFragment(Fragment fragment, String title) {
		if (title == null) {
			title = "";
		}

		this.tabFragments.add(fragment);
		this.tabTitles.add(title);
	}

	public void setShowOnlyIcons(boolean showOnlyIcons) {
		this.showOnlyIcons = showOnlyIcons;
	}

	public boolean isShowOnlyIcons() {
		return this.showOnlyIcons;
	}

	/**
	 * Returns the title of the tab at a specified position.
	 * @param position Position of the tab.
	 * @return Title of the tab.
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		return this.tabTitles.get(position);
	}

	/**
	 * Return the Fragment associated with a specified position.
	 *
	 * @param position Position of the requested fragment.
	 */
	@Override
	public Fragment getItem(int position) {
		return this.tabFragments.get(position);
	}

	/**
	 * Return the number of views available.
	 */
	@Override
	public int getCount() {
		return this.tabFragments.size();
	}
}
