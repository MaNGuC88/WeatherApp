package com.example.weatherapp.ui.weather_detail;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.base.BaseFragment;
import com.example.weatherapp.common.Resource;
import com.example.weatherapp.data.models.WeatherResponse;
import com.example.weatherapp.data.remote.WeatherApi;
import com.example.weatherapp.databinding.FragmentWeatherDetailBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WeatherDetailFragment extends BaseFragment<FragmentWeatherDetailBinding> implements LocationListener {

    private WeatherViewModel viewModel;
    private WeatherDetailFragmentArgs args;

    private final String[] perms = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private LocationManager locationManager;

    @Inject
    WeatherApi api;

    public WeatherDetailFragment() {
    }

    @Override
    protected FragmentWeatherDetailBinding bind() {
        return FragmentWeatherDetailBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                viewModel.setCoordinates(location.getLongitude(), location.getLatitude());
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestPermissionLauncher.launch("aaa");
        binding.cityNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCitiesFragment();
            }
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (!isGranted) {
                        ActivityCompat.requestPermissions(requireActivity(), perms, 1);
                    }
                }
            });

    private void openCitiesFragment() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.citiesFragment);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void setupUI() {
        args = WeatherDetailFragmentArgs.fromBundle(getArguments());
        viewModel.setCoordinates(Double.parseDouble(args.getLongitude()), Double.parseDouble(args.getLatitude()));
        viewModel.fetchWeather();
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void setupObservers() {
        setDateAndTime();
        if (isOnline()) {
            viewModel.weatherLiveData.observe(getViewLifecycleOwner(), new Observer<Resource<WeatherResponse>>() {
                @Override
                public void onChanged(Resource<WeatherResponse> response) {
                    switch (response.status) {
                        case SUCCESS:

                            binding.cityNameTv.setText(response.data.getName() + ", " + response.data.getSys().getCountry());

                            String iconUrl = "http://openweathermap.org/img/w/" +
                                    response.data.getResult().get(0).getIcon() + ".png";
                            Glide.with(binding.weatherGeneralIv.getContext())
                                    .load(iconUrl)
                                    .into(binding.weatherGeneralIv);
                            binding.weatherGeneralTv.setText(response.data.getResult().get(0).getDescription());

                            binding.temperatureTv.setText(String.valueOf(Math.round(response.data.getMain().getTemp() * 100) / 100));
                            binding.tempMaxTv.setText(Math.round(response.data.getMain().getTempMax() * 100) / 100 + "°C↑");
                            binding.tempMinTv.setText(Math.round(response.data.getMain().getTempMin() * 100) / 100 + "°C↓");

                            binding.humidityTv.setText(response.data.getMain().getHumidity() + "%");
                            binding.pressureTv.setText(response.data.getMain().getPressure() + "mBar");
                            binding.windTv.setText(response.data.getWind().getSpeed() + "km/h");

                            Date sunRise = new Date(response.data.getSys().getSunrise());
                            SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm a");
                            String sunRiseTime = formatter1.format(sunRise);
                            binding.sunriseTv.setText(sunRiseTime);

                            Date sunSet = new Date(response.data.getSys().getSunset());
                            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm a");
                            String sunSetTime = formatter2.format(sunSet);
                            binding.sunsetTv.setText(sunSetTime);

                            Date dt = new Date(response.data.getDt());
                            SimpleDateFormat formatter3 = new SimpleDateFormat("HH,mm");
                            String dayTime = formatter3.format(dt);
                            binding.daytimeTv.setText(dayTime);

                            break;
                        case ERROR:
                            Toast.makeText(requireActivity(), response.message, Toast.LENGTH_SHORT).show();
                            Log.e("Naima", response.message);
                            break;
                    }
                }
            });
        } else {
            binding.cityNameTv.setText(viewModel.fetchWeatherFromDB().getName() + ", " + viewModel.fetchWeatherFromDB().getSys().getCountry());

            String iconUrl = "http://openweathermap.org/img/w/" +
                    viewModel.fetchWeatherFromDB().getResult().get(0).getIcon() + ".png";
            Glide.with(binding.weatherGeneralIv.getContext())
                    .load(iconUrl)
                    .into(binding.weatherGeneralIv);
            binding.weatherGeneralTv.setText(viewModel.fetchWeatherFromDB().getResult().get(0).getDescription());

            binding.temperatureTv.setText(String.valueOf(Math.round(viewModel.fetchWeatherFromDB().getMain().getTemp() * 100) / 100));
            binding.tempMaxTv.setText(Math.round(viewModel.fetchWeatherFromDB().getMain().getTempMax() * 100) / 100 + "°C↑");
            binding.tempMinTv.setText(Math.round(viewModel.fetchWeatherFromDB().getMain().getTempMin() * 100) / 100 + "°C↓");

            binding.humidityTv.setText(viewModel.fetchWeatherFromDB().getMain().getHumidity() + "%");
            binding.pressureTv.setText(viewModel.fetchWeatherFromDB().getMain().getPressure() + "mBar");
            binding.windTv.setText(viewModel.fetchWeatherFromDB().getWind().getSpeed() + "km/h");

            Date sunRise = new Date(viewModel.fetchWeatherFromDB().getSys().getSunrise());
            SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm a");
            String sunRiseTime = formatter1.format(sunRise);
            binding.sunriseTv.setText(sunRiseTime);

            Date sunSet = new Date(viewModel.fetchWeatherFromDB().getSys().getSunset());
            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm a");
            String sunSetTime = formatter2.format(sunSet);
            binding.sunsetTv.setText(sunSetTime);

            Date dt = new Date(viewModel.fetchWeatherFromDB().getDt());
            SimpleDateFormat formatter3 = new SimpleDateFormat("HH,mm");
            String dayTime = formatter3.format(dt);
            binding.daytimeTv.setText(dayTime);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDateAndTime() {
        long currentDate = System.currentTimeMillis();
        ZonedDateTime dateTime = Instant.ofEpochMilli(currentDate)
                .atZone(ZoneId.of("Asia/Bishkek"));
        String formatted = dateTime.getDayOfWeek().name() + ", " +
                dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy | hh:mm a"));
        binding.tvDate.setText(formatted);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null){
            locationManager.removeUpdates(this);
        }
    }

}
