package com.raytech.gerenciadortimes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessAdapter extends FragmentPagerAdapter {
    private MainActivity activity;

    public TabAccessAdapter(@NonNull FragmentManager fm, MainActivity activity) {
        super(fm);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 :
                First first = new First(this.activity);
                return first;
            case 1 :
                Second second = new Second(this.activity);
                return second;
            case 2 :
                Third third = new Third(this.activity);
                return third;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0 :
                return "Lista";
            case 1 :
                return "Resultado";
            case 2 :
                return "Histórico";
            default:
                return null;
        }
    }
}