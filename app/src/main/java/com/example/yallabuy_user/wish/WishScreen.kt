package com.example.yallabuy_user.wish

import DraftOrderLineItem
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yallabuy_user.R
import com.example.yallabuy_user.authentication.login.CustomerIdPreferences
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel


@Composable
fun WishScreen(navController: NavController, wishListViewModel: WishViewModel = koinViewModel()) {

    val allWishListProduct = wishListViewModel.allWishListProduct.collectAsState().value
    val context = LocalContext.current
    val showLoading = remember { mutableStateOf(false) }
    LaunchedEffect(allWishListProduct) {
        wishListViewModel.getAllProductFromWishList(WishListIdPref.getWishListId(context))
    }

    when (allWishListProduct) {
        is ApiResponse.Failure -> {
            Log.i("TAG", "WishScreen fail")
        }

        ApiResponse.Loading -> {
            showLoading.value = true
        }

        is ApiResponse.Success -> {
            WishListItems(allWishListProduct.data, navController , wishListViewModel)
        }
    }
}

@Composable
fun WishListItems(
    draftOrderLineItems: List<DraftOrderLineItem>,
    navController: NavController,
    wishListViewModel: WishViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(draftOrderLineItems) { _, product ->
            WishListItemCard(product, navController , wishListViewModel  )
        }
    }
}

@Composable
fun WishListItemCard(
    product: DraftOrderLineItem,
    navController: NavController,
    wishListViewModel: WishViewModel,
) {
    Card(
        modifier = Modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            Log.i("ids", "WishListItemCard ${product.properties?.get(1)?.value?.toLong()} ")
            navController.navigate(ScreenRoute.ProductInfo(product.properties?.get(1)?.value?.toLong() ?: 0))
        }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = product.properties?.get(0)?.value,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
                HeartInCircle(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .zIndex(1f),
                    wishListViewModel ,
                    product.title ,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(product.title ?: "No Title", fontSize = 18.sp, maxLines = 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("EG")
                    Spacer(Modifier.width(3.dp))
                    Text(product.price ?: "NO Price", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DeleteProductAlert(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    wishListViewModel: WishViewModel,
    title: String?,

    ) {

    val failComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.questionmark))
    val context = LocalContext.current
    val failProgress by animateLottieCompositionAsState(
        composition = failComposition,
        iterations = LottieConstants.IterateForever
    )

    val icon: @Composable () -> Unit = {
        LottieAnimation(
            composition = failComposition,
            progress = { failProgress },
            modifier = Modifier.size(100.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Alert Dialog Box
        AlertDialog(
            dismissButton = {
                TextButton (
                    onClick = {
                       onDismissRequest()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Cancel")
                }
            } ,
            // set dismiss request
            onDismissRequest = {
                onDismissRequest()
            },
            // configure confirm button
            confirmButton = {
                TextButton (
                    onClick = {
                        onConfirmation()
                        wishListViewModel.deleteProductFromWishList(
                            WishListIdPref.getWishListId(context) , CustomerIdPreferences.getData(context)
                        ,title?:"not found")
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            // set icon
            icon = icon,
            // set title text
            title = {
                Text(text = "Delete $title", color = Color.Black)
            },
            // set description text
            text = {
                Text(
                    text = "Are u sure u want to delete $title",
                    color = Color.DarkGray
                )
            },
            // set padding for contents inside the box
            modifier = Modifier.padding(16.dp),
            // define box shape
            shape = RoundedCornerShape(16.dp),
            // set box background color
            containerColor = Color.White,
            // set icon color
            iconContentColor = Color.Red,
            // set title text color
            titleContentColor = Color.Black,
            // set text color
            textContentColor = Color.DarkGray,
            // set elevation
            tonalElevation = 8.dp,
            // set properties
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}

@Composable
fun HeartInCircle(
    modifier: Modifier = Modifier,
    wishListViewModel: WishViewModel,
    title: String?,

    ) {

    val showErrorDialog = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(32.dp)
            .border(BorderStroke(1.dp, Color.Black), shape = CircleShape)
            .background(Color.White, shape = CircleShape)
            .clickable {
                showErrorDialog.value = true
            },
        contentAlignment = Alignment.Center

    ) {

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Heart Icon",
            modifier = Modifier.size(18.dp)
        )
    }

    if (showErrorDialog.value) {
        DeleteProductAlert (
            onConfirmation = {
                showErrorDialog.value = false
            },
            onDismissRequest = {
                showErrorDialog.value = false
            } ,
            wishListViewModel ,
            title ,
        )
    }

}