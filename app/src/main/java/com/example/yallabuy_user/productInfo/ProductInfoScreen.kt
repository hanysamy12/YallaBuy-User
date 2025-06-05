package com.example.yallabuy_user.productInfo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.HorizontalPagerIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.yallabuy_user.R


@Composable
fun ProductInfoScreen(productId : Long , productInfoViewModel: ProductInfoViewModel = koinViewModel()){

    val productInfo = productInfoViewModel.productInfo.collectAsState().value

    LaunchedEffect(Unit) {
        productInfoViewModel.getProductInfoById(productId)
    }

    when(productInfo){
        is ApiResponse.Failure -> {
            Log.i("TAG", "getProductInfoById error ")
        }
        ApiResponse.Loading -> {
            Log.i("TAG", "getProductInfoById loading ")
        }
        is ApiResponse.Success -> {
            Column {
                ProductImage(productInfo.data)
                Spacer(Modifier.height(5.dp))
                ProductDetail(productInfo.data)

            }
        }
    }
}


@Composable
fun ProductImage(data: ProductInfoResponse) {
    val images = data?.product?.images ?: emptyList()
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    count = images.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = images[page].src,
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(id = R.drawable.broken_image),
                        error = painterResource(id = R.drawable.broken_image)
                    )
                }

//                HeartInCircle(
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(8.dp)
//                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = Color.Black,
            inactiveColor = Color.LightGray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun ProductDetail(data: ProductInfoResponse) {

    var productCounter by  remember { mutableIntStateOf(1) }

    Column {
        Text(
            data?.product?.title ?: "No Title Available",
            fontSize = 20.sp, fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            "Description", Modifier.padding(5.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            data?.product?.body_html ?: "No Description Available",
            color = Color.Black,
            modifier = Modifier.padding(5.dp)
        )

        SizesDropDownMenu(data)
        Spacer(modifier = Modifier.height(3.dp))
        ColorDropDownMenu(data)

        Price(data , count = productCounter)

        Row {
            QuantitySelectorCard(productCounter = productCounter,
                onIncrease = { productCounter += 1 },
                onDecrease = { if (productCounter > 1) productCounter -= 1 }
            )
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 5.dp, vertical = 10.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Add To Cart" , fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun QuantitySelectorCard(
    productCounter: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onDecrease,
                enabled = productCounter > 1
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Decrease",
                    tint = Color.Black
                )
            }

            Text(
                text = productCounter.toString(),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            IconButton(onClick = onIncrease) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.Black
                )
            }
        }
    }
}
@Composable
fun SizesDropDownMenu(data: ProductInfoResponse) {
    val isDropDownSelected = remember { mutableStateOf(false) }
    val itemSelectedIndex = remember { mutableIntStateOf(0) }

    val variants = data?.product?.variants ?: emptyList()
    val selectedSize = variants.getOrNull(itemSelectedIndex.intValue)?.option1 ?: "No Sizes Available"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Select Size",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable { isDropDownSelected.value = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedSize,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.Black
                )
            }

            DropdownMenu(
                expanded = isDropDownSelected.value,
                onDismissRequest = { isDropDownSelected.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                variants.forEachIndexed { index, variant ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = variant.option1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            itemSelectedIndex.value = index
                            isDropDownSelected.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ColorDropDownMenu(data: ProductInfoResponse) {
    val isDropDownSelected = remember { mutableStateOf(false) }
    val itemSelectedIndex = remember { mutableIntStateOf(0) }

    val variants = data?.product?.variants ?: emptyList()
    val selectedSize = variants.getOrNull(itemSelectedIndex.intValue)?.option2 ?: "No Colors Available"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Select Color",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable { isDropDownSelected.value = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedSize,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.Black
                )
            }

            DropdownMenu(
                expanded = isDropDownSelected.value,
                onDismissRequest = { isDropDownSelected.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                variants.forEachIndexed { index, variant ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = variant.option1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            itemSelectedIndex.value = index
                            isDropDownSelected.value = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Price(data: ProductInfoResponse, count: Int) {
    var priceInBound = remember { mutableDoubleStateOf(0.0) }
    priceInBound.doubleValue = ((data?.product?.variants?.get(0)?.price?.toDoubleOrNull() ?: 0.0) * 50 ) * count
    Text(text = "  Total Price :  ${priceInBound.doubleValue}  EGP" , modifier = Modifier.padding(10.dp)
        , fontSize = 15.sp , fontWeight = FontWeight.Bold  )
}



