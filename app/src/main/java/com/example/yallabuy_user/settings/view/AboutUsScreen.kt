package com.mariammuhammad.yallabuy.View.Settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yallabuy_user.R

@Composable
fun AboutUsScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AboutUsTopAppBar(onNavigateBack = onNavigateBack)
        },
        containerColor = Color.White
    ) { paddingValues ->
        AboutUsContent(paddingValues = paddingValues, function = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray), // Placeholder background
                contentAlignment = Alignment.Center
            ) {
                Text("Image Placeholder", color = Color.DarkGray)
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsTopAppBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("About Us", color = Color.White, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.dark_blue)
        )
    )
}

@Composable
fun AboutUsContent(paddingValues: PaddingValues, function: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp) 
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally 
    ) {

        Image(
            painter = painterResource(id = R.drawable.yallabuy_banner),
            contentDescription = "YallaBuy Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentScale = ContentScale.FillBounds //crop, fillWidth, fillHeight
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "YallaBuy",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.dark_blue)
        )

        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "It is an m-Commerce Application that presents products from different vendors and enables the authenticated users to add/remove products to/from their shopping carts and complete the whole shopping cycle online through the app.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.dark_blue),
            modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
        )


    }
}


@Preview(showBackground = true, name = "About Us Preview")
@Composable
fun AboutUsScreenPreview() {
    Scaffold(
        topBar = {
            AboutUsTopAppBar(onNavigateBack = {})
        },
        containerColor = Color.White
    ) { paddingValues ->
        // Pass a placeholder Box for the image in the preview
        AboutUsContent(paddingValues = paddingValues) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray), // Placeholder background
                contentAlignment = Alignment.Center
            ) {
                Text("Image Placeholder", color = Color.DarkGray)
            }
        }
    }
}
