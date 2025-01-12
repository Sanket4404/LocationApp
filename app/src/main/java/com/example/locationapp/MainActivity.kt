package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel : LocationViewModel = viewModel()
            LocationAppTheme {
                MyApp(viewModel)

            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils, viewModel = viewModel , context = context)


}





@Composable
fun LocationDisplay(
    viewModel: LocationViewModel,
    locationUtils: LocationUtils,
    context: Context
){
    val location = viewModel.location.value

    val address = location?.let{

        locationUtils.reverseGeocodeLocation(location)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true){
                //I have access to the location

                locationUtils.requestLocationUpdates(viewModel=viewModel)

            }else{
                // Ask for permission
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)

                if (rationalRequired){
                    Toast.makeText(context,
                        "Location Permission is Required for this feature ", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,
                        "Location Permission is Required Please enable it from Settings ", Toast.LENGTH_LONG).show()
                }
            }

        })




    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        if (location != null){
            Text("Address: ${location.latitude} ${location.longitude}  \n Full Address:\n $address ")
        }else {
            Text("Location Not Available")
        }

        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)){
                //Permission already granted update the location
                locationUtils.requestLocationUpdates(viewModel)

            }else{
                //Request the Permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }

        }) {
            Text("Get Location ")
        }

    }

}


