package com.example.weatherapp.ui.cities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherapp.R;
import com.example.weatherapp.base.BaseFragment;
import com.example.weatherapp.databinding.FragmentCitiesBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CitiesFragment extends BaseFragment<FragmentCitiesBinding> {

    public CitiesFragment() {
    }

    @Override
    protected FragmentCitiesBinding bind() {
        return FragmentCitiesBinding.inflate(getLayoutInflater());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            binging.citiesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cityName = binging.citiesEt.getText().toString().trim();
                    returnToWeatherFragment(cityName);
                }
            });

    }

    private void returnToWeatherFragment(String cityName) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate((NavDirections) CitiesFragmentDirections.actionCitiesFragmentToWeatherDetailFragment(cityName));
    }

    @Override
    protected void setupUI() {

    }

    @Override
    protected void setupObservers() {

    }
}