package com.example.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//https://api.openweathermap.org/data/2.5/weather?q=London&appid=080028fddcedd226643d22058fd79ff6
//080028fddcedd226643d22058fd79ff6   API key

class MainActivity : AppCompatActivity() {
    private val binding :ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Delhi")
        SearchCity()
    }

    private fun SearchCity() {
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
            .build().create(ApiIterface::class.java)
        val response=retrofit.getWeatherData(cityName,"080028fddcedd226643d22058fd79ff6","metric")
        response.enqueue(object: Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    val temperature= responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min

//                    Log.d("TAG", "onResponse: $temperature")
                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxtemp.text="Max Temp: $maxTemp °C"
                    binding.mintemp.text="Max Temp: $minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.windSpeed.text="$windSpeed m/s"
                    binding.sunRise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityname.text="$cityName"

    changeImageacctoconditon(condition)


                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }


        })
    }

    private fun changeImageacctoconditon(conditions:String) {
        when(conditions){
            "CLear Sky", "Sunny", "CLear"->{
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
                "Partly CLouds", "Clouds", "Overcast", "Mist", "Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding. lottieAnimationView.setAnimation(R.raw.cloud)
                }
                    "Light Rain", "Drizzle","Rain" ,"Moderate Rain", "Showers", "Heavy Rain"->{
                            binding.root.setBackgroundResource(R.drawable.rain_background)
                        binding. lottieAnimationView.setAnimation(R.raw.rain)

                    }
            "Light Snow", "Moderate Snow",  "Blizzard", "Heavy Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding. lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding. lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long):String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}