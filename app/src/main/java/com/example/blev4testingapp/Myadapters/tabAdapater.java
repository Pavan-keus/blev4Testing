package com.example.blev4testingapp.Myadapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.blev4testingapp.ConnectionLessPair;
import com.example.blev4testingapp.ConnectionPair;
import com.example.blev4testingapp.OfflineCommissioning;

public class tabAdapater extends FragmentStateAdapter {
    ConnectionPair a;
    ConnectionLessPair b;
    OfflineCommissioning c;
    public tabAdapater(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ConnectionPair a,ConnectionLessPair b,OfflineCommissioning c) {
        super(fragmentManager, lifecycle);
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return a;
            case 1:
                return b;
            case 2:
                return c;
            default:
                return a;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
