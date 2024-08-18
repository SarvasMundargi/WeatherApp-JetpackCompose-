package com.example.weatherapp_jetpackcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp_jetpackcompose.api.WeatherModel
import com.example.weatherapp_jetpackcompose.ui.theme.NetworkResponse

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun WeatherPage(viewModel: WeatherViewModel){
    var city by remember {
        mutableStateOf("")
    }

    val weatherResult=viewModel.weatherResult.observeAsState()
    val keyBoardController=LocalSoftwareKeyboardController.current

    Column(modifier= Modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly){
            OutlinedTextField(
                modifier=Modifier.weight(1f),
                value = city, onValueChange ={
                city=it
            },
                label={
                    Text(text = "Search for any location")
                }
            )

            IconButton(onClick = {
                viewModel.getData(city)
                keyBoardController?.hide()
            }) {
                Icon(imageVector = Icons.Default.Search,
                    contentDescription = "Search for any location")
            }
        }

        when(val result=weatherResult.value){
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            NetworkResponse.Loading ->{
                CircularProgressIndicator()
            }
            is NetworkResponse.Success -> {
                WeatherData(result.data)
            }
            null -> {}
        }
    }
}

@Composable
fun WeatherData(data: WeatherModel){
    val lottieAnimationResId : Int= when (data.current.condition.text.lowercase()) {
        "sunny" -> R.raw.sun
        "rain" -> R.raw.rain
        "cloudy" -> R.raw.cloud
        "Partly cloudy"->R.raw.cloud
        // Add more conditions as needed
        else -> R.raw.sun
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally){

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom){

            Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location Icon"
            , modifier = Modifier.size(40.dp))
            
            Text(text = data.location.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = data.location.country, fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "${data.current.temp_c} ° C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieAnimationResId))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.CenterHorizontally)
        )
        
        Text(text = data.current.condition.text
        , fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.background(Color.Transparent).border(2.dp, Color.Black,
            RectangleShape),
            colors= CardDefaults.cardColors(
                containerColor = Color.Transparent // Makes the Card background fully transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp // Removes any default elevation or shadow
            )){
            Column(modifier=Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                    ExtraInfo("Humidity",data.current.humidity)
                    ExtraInfo("Wind Speed",data.current.wind_kph)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                    ExtraInfo("Gust Speed",data.current.gust_kph)
                    ExtraInfo("Precipitation",data.current.precip_mm)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                    ExtraInfo("Pressure",data.current.pressure_mb)
                    ExtraInfo("Cloud",data.current.cloud)
                }
            }
        }

    }
}

@Composable
fun ExtraInfo(key:String,data: String){
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = key, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = data, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}