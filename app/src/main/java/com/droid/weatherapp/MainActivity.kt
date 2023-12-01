package com.droid.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.droid.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private val  binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("jaipur")
                searchCity()
    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true

            }

        })
    }

    private fun fetchWeatherData(cityName:String) {

        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response=retrofit.getWeatherData(cityName,"ca62fdf605f2f63d454e5d4e89be8935","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful&& responseBody!=null){


                    val Temprature=responseBody.main.temp.toString()
                    val Humidity=responseBody.main.humidity
                    val WindSpeed=responseBody.wind.speed
                    val Sunrise=responseBody.sys.sunrise.toLong()
                    val Sunset=responseBody.sys.sunset.toLong()
                    val Sealevel=responseBody.main.pressure
                    val Condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val MaxTemp=responseBody.main.temp_max
                    val MinTemp=responseBody.main.temp_min


                    binding.temp.text="$Temprature °C"
                    binding.condition.text=Condition
                    binding.weather.text=Condition
                    binding.humidity.text="$Humidity %"
                    binding.sunrise.text="${time(Sunrise)}"
                    binding.sunset.text="${time(Sunset)}"
                    binding.sea.text="$Sealevel hPa"
                    binding.maxTemp.text="Max Temp: $MaxTemp °C"
                    binding.minTemp.text="Min Temp: $MinTemp °C"
                    binding.windSpeed.text="$WindSpeed m/s"
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text="$cityName"
                    Log.d("temp", "onResponse: $Temprature ")



                    changeAccordingToWeather(Condition)

                }
            }



            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeAccordingToWeather(conditions: String) {
        when(conditions){



                    "Clear Sky", "Sunny", "Clear" -> {
                        binding.root.setBackgroundResource(R.drawable.sunny_background)
                        binding.lottieAnimationView.setAnimation(R.raw.sun)
                    }

                    "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" , "Haze" -> {
                        binding.root.setBackgroundResource(R.drawable.colud_background)
                        binding.lottieAnimationView.setAnimation(R.raw.cloud)
                    }

                    "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                        binding.root.setBackgroundResource(R.drawable.rain_background)
                        binding.lottieAnimationView.setAnimation(R.raw.rain)
                    }

                    "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {


                    binding.root.setBackgroundResource(R.drawable.snow_background)
                            binding . lottieAnimationView . setAnimation (R.raw.snow)
                }
                    else ->{
                        binding.root.setBackgroundResource(R.drawable.sunny_background)
                        binding.lottieAnimationView.setAnimation (R.raw.sun)
                    }


        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun date(): String {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    } private fun time(timestamp :Long): String {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))

    }


    fun dayName(timetamp :Long): String{

        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())

    }
    

}

