package com.example.yallabuy_user.cart.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.cart.viewmodel.CartViewModel
import com.example.yallabuy_user.data.models.cart.DraftOrderBody
import com.example.yallabuy_user.home.HomeViewModel
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel

@Composable
fun CartScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel()
) {
    val cartState by cartViewModel.cartState.collectAsState()
    val draftOrderId = 123456789L

    LaunchedEffect(Unit) {
        cartViewModel.fetchCart(draftOrderId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (cartState) {
            is ApiResponse.Failure -> {
                Text(
                    text = "Failed to load cart",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            is ApiResponse.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ApiResponse.Success -> {
                val draftOrder = (cartState as ApiResponse.Success<DraftOrderBody>).data

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    items(items = draftOrder.draftOrder.lineItems) { item ->
                        CartItemCard(
                            title = item.title,
                            price = item.price,
                            imageUrl = item.properties.find { it.name == "Image" }?.value ?: ""
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

  //              CheckoutSection(total = "${draftOrder.draftOrder.totalPrice} EGP")
            }
        }
    }
}

@Composable
fun CartItemCard(title: String, price: String, imageUrl: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(90.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorResource(R.color.dark_blue)
                )
                Text(price, fontSize = 14.sp, color = Color.Gray)
            }

            QuantitySelector()
        }
    }
}

@Composable
fun QuantitySelector(
    modifier: Modifier = Modifier,
    initialQuantity: Int = 1,
    onQuantityChange: (Int) -> Unit = {}
) {
    var quantity by remember { mutableStateOf(initialQuantity) }

    Row(
        modifier = modifier
            .background(color = colorResource(R.color.dark_blue), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "-",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .clickable(enabled = quantity > 1) {
                    quantity--
                    onQuantityChange(quantity)
                }
                .padding(8.dp)
        )

        Text(
            text = quantity.toString(),
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "+",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .clickable {
                    quantity++
                    onQuantityChange(quantity)
                }
                .padding(8.dp)
        )
    }
}

//@Composable
//fun CheckoutSection(total: String) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text("Total (1 item)", fontSize = 14.sp)
//            Text(total, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//        }
//
//        Button(
//            onClick = { },
//            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp)
//        ) {
//            Text("Checkout", color = Color.White)
//        }
//    }
//}