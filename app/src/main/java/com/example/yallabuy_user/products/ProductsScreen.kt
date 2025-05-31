package com.example.yallabuy_user.products

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.example.yallabuy_user.collections.CollectionsViewModel
import com.example.yallabuy_user.collections.Product
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.utilities.ApiResponse
import org.koin.androidx.compose.koinViewModel

private const val TAG = "ProductsScreen"
@Composable
fun ProductsScreen(collectionId : Long?,viewModel: CollectionsViewModel = koinViewModel()) {
    val uiProductsState by viewModel.products.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    var sliderPosition by remember { mutableFloatStateOf(50f) }

    LaunchedEffect(Unit) {
        viewModel.getProducts(collectionId)
    }

    Box(){
         Column(modifier = Modifier.padding(6.dp)) {
             OutlinedTextField(
                 value = searchQuery.value, onValueChange = { query ->
                     searchQuery.value = query
                     Log.i(TAG, "Search: $query")
                 },
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(vertical = 16.dp, horizontal = 22.dp)
                     .heightIn(min = 40.dp),
                 shape = RoundedCornerShape(14.dp),
                 label = { Text("What are you looking for ?") },

                 leadingIcon = {
                     Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                 },
                 singleLine = true,
                 colors = OutlinedTextFieldDefaults.colors(
                     focusedContainerColor = MaterialTheme.colorScheme.background, // Background when focused
                     unfocusedContainerColor = Color.LightGray,   // Background when unfocused
                     focusedTextColor = Color.Black,
                     unfocusedTextColor = Color.Black, // Force same color always
                     disabledTextColor = Color.Black,
                     errorTextColor = Color.Red

                 )
             )
                 //set range from min price to max prise
             Slider(modifier = Modifier.padding(horizontal = 22.dp).height(22.dp),
                 value = sliderPosition,
                 onValueChange = { sliderPosition = it },
                 valueRange = 0f..100f,
                 colors = SliderDefaults.colors(
                     thumbColor = MaterialTheme.colorScheme.primary,
                     activeTrackColor = MaterialTheme.colorScheme.primary,
                     inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                 )
             )
            when (uiProductsState) {
                is ApiResponse.Success -> {
                    val products = (uiProductsState as ApiResponse.Success).data
                   // Log.i(TAG, "Products Screen: Products $products")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products.size) { index ->
                            Product(products[index])
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
