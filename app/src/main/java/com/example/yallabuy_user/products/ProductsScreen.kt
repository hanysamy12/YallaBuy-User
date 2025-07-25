package com.example.yallabuy_user.products

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yallabuy_user.R
import com.example.yallabuy_user.collections.Product
import com.example.yallabuy_user.data.models.ProductsItem
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.Common
import org.koin.androidx.compose.koinViewModel

private const val TAG = "ProductsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    navController: NavController,
    vendorName: String? = null,
    categoryID: Long? = null,
    viewModel: ProductsViewModel = koinViewModel(),
    setTopBar: (@Composable () -> Unit) -> Unit,
    title: String?
) {
    val uiProductsState by viewModel.products.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

    var isFilterBarShown by remember { mutableStateOf(false) }
    var maxPrice by remember { mutableFloatStateOf(0f) }
    var minPrice by remember { mutableFloatStateOf(0f) }
    var currentPrice by remember { mutableFloatStateOf(0f) }


    var isPriceSet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {

        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        title ?: "All Products", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3B9A94)
                ),
                actions = {
                    IconButton(
                        onClick = { isFilterBarShown = !isFilterBarShown },
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.ic_filter),
                                contentDescription = "Search Icon"
                            )
                        }

                    )
                }
            )
        }
        categoryID?.let { viewModel.getCategoryProducts(categoryID) } ?: viewModel.getProducts(
            vendorName
        )
    }
    Log.i(TAG, "ProductsScreen: $categoryID /// $vendorName")

    Box {
        Column(modifier = Modifier.padding(6.dp)) {
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { query ->
                    searchQuery.value = query
                    viewModel.searchForProduct(searchQuery.value)
                    Log.i(TAG, "Search: $query")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp, horizontal = 22.dp)
                    .heightIn(min = 40.dp),
                shape = RoundedCornerShape(14.dp),
                label = { Text("What are you looking for ?") },

                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = Color.LightGray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    errorTextColor = Color.Red
                )
            )
            if (isFilterBarShown) {
                if (!isPriceSet && uiProductsState is ApiResponse.Success) {
                    val (min, max) = getMinAMxPrice(products = (uiProductsState as ApiResponse.Success).data)
                    minPrice = min
                    maxPrice = max
                    currentPrice = max
                    isPriceSet = true
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        "Price:${currentPrice.toInt()} ${Common.currencyCode.getCurrencyCode()}",
                        fontSize = 13.sp
                    )
                    Slider(
                        modifier = Modifier
                            .height(22.dp),
                        value = currentPrice,
                        onValueChange = {
                            currentPrice = it
                            viewModel.showFilteredProduct(minPrice, it)
                        },
                        //steps = 10,
                        valueRange = minPrice..maxPrice,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                        )
                    )
                }
            }
            when (uiProductsState) {
                is ApiResponse.Success -> {
                    val products = (uiProductsState as ApiResponse.Success).data
                    // Log.i(TAG, "Products Screen: Products $products")

                    Log.i(TAG, "ProductsScreen: Max Min $maxPrice > $minPrice")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products.size) { index ->
                            Product(products[index], navController)
                            Log.i(
                                TAG,
                                "ProductsScreen: Product Price ${products[index].variants?.get(0)?.price}"
                            )
                            Product(products[index], navController)
                        }
                    }
                }

                is ApiResponse.Failure -> {
                    val msg = (uiProductsState as ApiResponse.Failure).toString()
                    Log.i(TAG, "Products Failure Error $msg")
                }

                ApiResponse.Loading -> {
                    ProgressShow()
                }
            }
        }
    }
}

private fun getMinAMxPrice(products: List<ProductsItem>): Pair<Float, Float> {
    val prices = products.flatMap { product ->
        product.variants?.mapNotNull { it?.price?.toFloatOrNull() } ?: emptyList()
    }

    val minPrice = prices.minOrNull() ?: 0f
    val maxPrice = prices.maxOrNull() ?: 0f

    return Pair(minPrice, maxPrice)
}