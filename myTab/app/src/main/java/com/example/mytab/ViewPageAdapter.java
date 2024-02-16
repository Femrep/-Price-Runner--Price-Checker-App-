package com.example.mytab;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {

    private SharedViewModel sharedViewModel;

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity,SharedViewModel viewModel){
        super(fragmentActivity);

        this.sharedViewModel = viewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        switch (position){
            case 0:
                Tab1 tab1 = new Tab1();
                tab1.setSharedViewModel(sharedViewModel);
                return tab1;
            case 1:
                Tab2 tab2 = new Tab2();
                tab2.setSharedViewModel(sharedViewModel);

                return tab2;

            default:
                return new Tab1();
        }
    }

    @Override
    public int getItemCount(){return 2;}
}
