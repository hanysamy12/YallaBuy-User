package com.example.yallabuy_user.collections

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.ProductsItem
import com.example.yallabuy_user.home.ProgressShow
import com.example.yallabuy_user.products.ProductsViewModel
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.example.yallabuy_user.utilities.ApiResponse
import com.example.yallabuy_user.utilities.Common
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val TAG = "CollectionsScreen"

//@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    navController: NavController,
    setFilterMeth: (filter: (String) -> Unit) -> Unit,
    viewModel: ProductsViewModel = koinInject(),
    setTopBar: (@Composable () -> Unit) -> Unit
) {

    LaunchedEffect(Unit) {
        setTopBar {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Categories", color = Color.White,
                        fontFamily = FontFamily(Font(R.font.caprasimo_regular)),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3B9A94)
                ),
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app),
                        contentDescription = "App Icon",
                        tint = Color.Unspecified, // Optional: set tint if needed
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
        viewModel.getAllCategories()
    }
    val coroutineScope = rememberCoroutineScope()
    val uiCategoriesState by viewModel.categories.collectAsState()
    val uiProductsState by viewModel.products.collectAsState()

    setFilterMeth { subCategory ->
        viewModel.showSubCategoryProduct(subCategory)
    }

    Column(modifier = Modifier.padding(8.dp)) {
        when (uiCategoriesState) {
            is ApiResponse.Success -> {
                val categories = (uiCategoriesState as ApiResponse.Success).data
                LaunchedEffect(Unit) {
                    categories[0].id?.let { viewModel.getCategoryProducts(it) }

                }
                CategoriesChips(categories, onChipClicked = { categoryId ->
                    coroutineScope.launch {
                        categoryId?.let {
                            viewModel.getCategoryProducts(
                                categoryId
                            )
                        }
                    }
                })
            }

            is ApiResponse.Failure -> {
                val msg = (uiCategoriesState as ApiResponse.Failure).toString()
                Log.i(TAG, "Categories Failure Error $msg")
            }

            ApiResponse.Loading -> ProgressShow()
        }

        when (uiProductsState) {
            is ApiResponse.Success -> {
                val products = (uiProductsState as ApiResponse.Success).data
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
                    }
                }
            }

            is ApiResponse.Failure -> {
                val msg = (uiProductsState as ApiResponse.Failure).toString()
                Log.i(TAG, "CollectionsScreen: $msg")
            }

            ApiResponse.Loading -> {
                ProgressShow()
            }
        }
    }
}

@Composable
private fun CategoriesChips(
    categories: List<CustomCollectionsItem>,
    onChipClicked: (categoryId: Long?) -> Unit
) {
    val chipsLabels = categories.map { it.title }

    ///val chipsLabels = listOf("KIDS", "Women", "Men", "Sale")
    var selectedIndex by remember { mutableIntStateOf(0) }

    LazyRow(modifier = Modifier.padding(8.dp)) {
        itemsIndexed(chipsLabels) { index, _ ->
            AssistChip(
                onClick = {
                    selectedIndex = index
                    categories[index].id?.let { onChipClicked(it) }
                    //  Log.i(TAG, "CategoriesChipsID: ${categories[index].id}")
                },
                label = {
                    categories[index].title?.let { Text(it) }
                },
                modifier = Modifier.padding(horizontal = 4.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedIndex == index) Color(0xFF3B9A94)
                    else Color(0xFFF8EBD9)
                )
            )
        }
    }
}

@Composable
fun Product(product: ProductsItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            navController.navigate(ScreenRoute.ProductInfo(product.id ?: 0))
        }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            ///Image for the first product may not changes (log the right image url )
            AsyncImage(
                model = product.image?.src,
                contentDescription = product.title,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.ic_app)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(product.title ?: "No Title", fontSize = 18.sp, maxLines = 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(Common.currencyCode.getCurrencyCode())
                    Spacer(Modifier.width(3.dp))
                    Text(
                        product.variants?.get(0)?.price ?: "NO Price",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}
