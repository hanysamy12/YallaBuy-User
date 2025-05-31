package com.example.yallabuy_user.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.SmartCollectionsItem
import org.koin.androidx.compose.koinViewModel

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = koinViewModel()) {
    LaunchedEffect(Unit) {
        homeViewModel.getAllCategories()
        homeViewModel.getAllBrands()
    }
    val uiCategoriesState by homeViewModel.categories.collectAsState()
    val uiBrandState by homeViewModel.brands.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
        // .background(color = Color.Yellow)
    ) {
        when (uiBrandState) {
            is ApiResponse.Success -> {
                val brands = (uiBrandState as ApiResponse.Success).data
                val categories = (uiCategoriesState as ApiResponse.Success).data
                HomeContent(categories, brands)

                Log.i(TAG, "HomeScreen $categories")
                Log.i(TAG, "HomeScreen $brands")

            }

            is ApiResponse.Failure -> {
                val msg = (uiBrandState as ApiResponse.Failure).toString()
                println("Home Failure Error $msg")
            }

            ApiResponse.Loading -> ProgressShow()
        }

    }

}


@Composable
private fun HomeContent(
    categories: List<CustomCollectionsItem>,
    brands: List<SmartCollectionsItem>
) {

    Column {
        LazyRow(
            modifier = Modifier
                .background(Color.Blue)
                .height(200.dp)
                .fillMaxWidth()
        ) {
            items(7) { _ ->
                SliderItem()
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImageWithTitle(categories[1],R.drawable.img_kid)
            CircularImageWithTitle(categories[2],R.drawable.img_man)
            CircularImageWithTitle(categories[4],R.drawable.img_women)
            CircularImageWithTitle(categories[3],R.drawable.img_sale)
            }

        Spacer(Modifier.height(20.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 6.dp)
        ) {
            items(brands.size) { index ->
                RoundedImageWithTitle(brands[index])
                Spacer(Modifier.width(6.dp))
            }
        }
    }

}

@Preview
@Composable
private fun SliderItem() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_sale),
            contentDescription = "Coupons",
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun CircularImageWithTitle(category: CustomCollectionsItem, imgId : Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imgId),
            contentDescription = "category",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(category.title ?: "No Title")
        //category.title?.let { Text(text = it, fontSize = 14.sp) }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RoundedImageWithTitle(brand: SmartCollectionsItem) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GlideImage(
                model = brand.image?.src, contentDescription = brand.title, modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )
            Text(brand.title ?: "No Title")
          //  brand.title?.let { Text(text = it, fontSize = 22.sp) }
        }
    }
}

@Composable
fun ProgressShow() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(350.dp))
        LinearProgressIndicator()
        Text("Waiting", fontSize = 22.sp)

    }

}