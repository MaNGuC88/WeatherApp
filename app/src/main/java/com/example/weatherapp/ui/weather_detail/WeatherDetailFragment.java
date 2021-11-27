package com.example.weatherapp.ui.weather_detail;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.base.BaseFragment;
import com.example.weatherapp.common.Resource;
import com.example.weatherapp.data.models.Weather;
import com.example.weatherapp.data.models.WeatherResponse;
import com.example.weatherapp.data.remote.WeatherApi;
import com.example.weatherapp.databinding.FragmentWeatherDetailBinding;
import com.example.weatherapp.ui.cities.CitiesFragment;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class WeatherDetailFragment extends BaseFragment<FragmentWeatherDetailBinding> {

    private WeatherViewModel viewModel;
    private WeatherDetailFragmentArgs args;

    @Inject
    WeatherApi api;

    public WeatherDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected FragmentWeatherDetailBinding bind() {
        return FragmentWeatherDetailBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binging.cityNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCitiesFragment();
            }
        });
    }

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
        viewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);
        viewModel.setCityName(args.getCityName());
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

                            binging.cityNameTv.setText(response.data.getName() + ", " + response.data.getSys().getCountry());

                            String iconUrl = "http://openweathermap.org/img/w/" +
                                    response.data.getResult().get(0).getIcon() + ".png";
                            Glide.with(binging.weatherGeneralIv.getContext())
                                    .load(iconUrl)
                                    .into(binging.weatherGeneralIv);
                            binging.weatherGeneralTv.setText(response.data.getResult().get(0).getDescription());

                            binging.temperatureTv.setText(String.valueOf(Math.round(response.data.getMain().getTemp() * 100) / 100));
                            binging.tempMaxTv.setText(Math.round(response.data.getMain().getTempMax() * 100) / 100 + "°C↑");
                            binging.tempMinTv.setText(Math.round(response.data.getMain().getTempMin() * 100) / 100 + "°C↓");

                            binging.humidityTv.setText(response.data.getMain().getHumidity() + "%");
                            binging.pressureTv.setText(response.data.getMain().getPressure() + "mBar");
                            binging.windTv.setText(response.data.getWind().getSpeed() + "km/h");

                            Date sunRise = new Date(response.data.getSys().getSunrise());
                            SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm a");
                            String sunRiseTime = formatter1.format(sunRise);
                            binging.sunriseTv.setText(sunRiseTime);

                            Date sunSet = new Date(response.data.getSys().getSunset());
                            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm a");
                            String sunSetTime = formatter2.format(sunSet);
                            binging.sunsetTv.setText(sunSetTime);

                            Date dt = new Date(response.data.getDt());
                            SimpleDateFormat formatter3 = new SimpleDateFormat("HH,mm");
                            String dayTime = formatter3.format(dt);
                            binging.daytimeTv.setText(dayTime);

                            break;
                        case ERROR:
                            Toast.makeText(requireActivity(), response.message, Toast.LENGTH_SHORT).show();
                            Log.e("Naima", response.message);
                            break;
                    }
                }
            });
        } else {
            binging.cityNameTv.setText(viewModel.fetchWeatherFromDatabase().getName() + ", " + viewModel.fetchWeatherFromDatabase().getSys().getCountry());

            String iconUrl = "http://openweathermap.org/img/w/" +
                    viewModel.fetchWeatherFromDatabase().getResult().get(0).getIcon() + ".png";
            Glide.with(binging.weatherGeneralIv.getContext())
                    .load(iconUrl)
                    .into(binging.weatherGeneralIv);
            binging.weatherGeneralTv.setText(viewModel.fetchWeatherFromDatabase().getResult().get(0).getDescription());

            binging.temperatureTv.setText(String.valueOf(Math.round(viewModel.fetchWeatherFromDatabase().getMain().getTemp() * 100) / 100));
            binging.tempMaxTv.setText(Math.round(viewModel.fetchWeatherFromDatabase().getMain().getTempMax() * 100) / 100 + "°C↑");
            binging.tempMinTv.setText(Math.round(viewModel.fetchWeatherFromDatabase().getMain().getTempMin() * 100) / 100 + "°C↓");

            binging.humidityTv.setText(viewModel.fetchWeatherFromDatabase().getMain().getHumidity() + "%");
            binging.pressureTv.setText(viewModel.fetchWeatherFromDatabase().getMain().getPressure() + "mBar");
            binging.windTv.setText(viewModel.fetchWeatherFromDatabase().getWind().getSpeed() + "km/h");

            Date sunRise = new Date(viewModel.fetchWeatherFromDatabase().getSys().getSunrise());
            SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm a");
            String sunRiseTime = formatter1.format(sunRise);
            binging.sunriseTv.setText(sunRiseTime);

            Date sunSet = new Date(viewModel.fetchWeatherFromDatabase().getSys().getSunset());
            SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm a");
            String sunSetTime = formatter2.format(sunSet);
            binging.sunsetTv.setText(sunSetTime);

            Date dt = new Date(viewModel.fetchWeatherFromDatabase().getDt());
            SimpleDateFormat formatter3 = new SimpleDateFormat("HH,mm");
            String dayTime = formatter3.format(dt);
            binging.daytimeTv.setText(dayTime);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDateAndTime() {
        long currentDate = System.currentTimeMillis();
        ZonedDateTime dateTime = Instant.ofEpochMilli(currentDate)
                .atZone(ZoneId.of("Asia/Bishkek"));
        String formatted = dateTime.getDayOfWeek().name() + ", " +
                dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy | hh:mm a"));
        binging.tvDate.setText(formatted);

    }

}