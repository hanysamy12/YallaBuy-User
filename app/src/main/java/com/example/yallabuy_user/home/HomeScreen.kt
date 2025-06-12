package com.example.yallabuy_user.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.SmartCollectionsItem
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = koinViewModel()) {
    LaunchedEffect(Unit) {
        homeViewModel.getAllCategories()
        homeViewModel.getAllBrands()
    }
    val uiCategoriesState by homeViewModel.categories.collectAsState()
    val uiBrandState by homeViewModel.brands.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiBrandState) {
            is ApiResponse.Success -> {
                val brands = (uiBrandState as ApiResponse.Success).data
                val categories = (uiCategoriesState as ApiResponse.Success).data
                HomeContent(categories, brands, onCatClicked = { catId ->
                      Log.i(TAG, "HomeScreen: Collection ID = $catId")
                    navController.navigate(
                        ScreenRoute.ProductsScreen.createRoute(
                            vendorName = null,
                            categoryID = catId
                        )
                    )
                }, onBrandClicked = { brandName ->
                    navController.navigate(
                        ScreenRoute.ProductsScreen.createRoute(
                            vendorName = brandName,
                            categoryID = null
                        )
                    )
                })

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
    brands: List<SmartCollectionsItem>,
    onCatClicked: (Long?) -> Unit,
    onBrandClicked: (String) -> Unit
) {

    val couponImages = listOf(
        R.drawable.sale1,
        R.drawable.sale2,
        R.drawable.sale3,
        R.drawable.sale4,
        R.drawable.sale5,
        R.drawable.img_sale
    )

    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Please tap on the image to get the coupon:",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            fontSize = 16.sp,
          //  color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        CouponsCarousel(imageResIds = couponImages)

        Spacer(Modifier.height(20.dp))


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularImageWithTitle(categories[1], R.drawable.img_kid, onCatClicked = { catID ->
                onCatClicked(catID)
            })
            CircularImageWithTitle(categories[2], R.drawable.img_man, onCatClicked = { catID ->
                onCatClicked(catID)
            })
            CircularImageWithTitle(categories[4], R.drawable.img_women, onCatClicked = { catID ->
                onCatClicked(catID)
            })
            CircularImageWithTitle(categories[3], R.drawable.img_sale, onCatClicked = { catID ->
                onCatClicked(catID)
            })
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Show All Products",
            modifier = Modifier
                .padding(start = 6.dp)
                .clickable {
                    Log.i(TAG, "All Products Clicked")
                    onCatClicked(null)
                },
            color = Color.DarkGray,
            fontSize = 20.sp,
            textDecoration = TextDecoration.Underline
        )
        Spacer(
            Modifier.height(20.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 6.dp)
        ) {
            items(brands.size) { index ->
                RoundedImageWithTitle(brands[index], onBrandClicked = { brandId ->
                    onBrandClicked(brandId)
                    Log.i(TAG, "BrandClicked: $brandId")
                })
                Spacer(Modifier.width(6.dp))
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CouponsCarousel(imageResIds: List<Int>) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { imageResIds.size }
    )

    LaunchedEffect(key1 = true) {
        while (true) {
            delay(3000L)
            val nextPage = (pagerState.currentPage + 1) % imageResIds.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            CouponImage(imageResId = imageResIds[page])
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(imageResIds.size) { index ->
                val color = if (pagerState.currentPage == index) Color.Black else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun CouponImage(imageResId: Int) {
    Box(
        modifier = Modifier
            //.fillMaxHeight()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Coupon Image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCouponsCarousel() {
    val previewImages = listOf(
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_gallery
    )

    //https://developer.android.com/develop/ui/compose/components/carousel
    MaterialTheme {
        CouponsCarousel(imageResIds = previewImages)
    }
}


@Composable
fun CircularImageWithTitle(
    category: CustomCollectionsItem, imgId: Int, onCatClicked: (Long) -> Unit
) {
    Column(
        modifier = Modifier.clickable {
            category.id?.let { onCatClicked(it) }
        }, horizontalAlignment = Alignment.CenterHorizontally
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
    }
}


@Composable
fun RoundedImageWithTitle(brand: SmartCollectionsItem, onBrandClicked: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .clickable {
                brand.title?.let { onBrandClicked(it) }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier

                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                model = brand.image?.src,
                contentDescription = brand.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
                // contentScale = ContentScale.Crop
            )
            Text(
                brand.title ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3B9A94)),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFFF8EBD9)
            )
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