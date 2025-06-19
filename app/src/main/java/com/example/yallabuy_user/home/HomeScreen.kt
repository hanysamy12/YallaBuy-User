package com.example.yallabuy_user.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.bumptech.glide.gifdecoder.GifDecoder
import com.example.yallabuy_user.R
import com.example.yallabuy_user.data.models.Coupon.CouponItem
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.SmartCollectionsItem
import com.example.yallabuy_user.ui.navigation.ScreenRoute
import com.example.yallabuy_user.utilities.ApiResponse
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val TAG = "HomeScreen"

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = koinViewModel(),
    setTopBar: (@Composable () -> Unit) -> Unit
) {
    LaunchedEffect(Unit) {
        setTopBar {
            CenterAlignedTopAppBar(
                title = { Text("Yalla Buy") },
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
        homeViewModel.getAllCategories()
        homeViewModel.getAllBrands()
        // Coupons are fetched in ViewModel's init.
    }

    val uiCategoriesState by homeViewModel.categories.collectAsState()
    val uiBrandState by homeViewModel.brands.collectAsState()
    val uiAllCouponsState by homeViewModel.allCoupons.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiBrandState) {
            is ApiResponse.Success -> {
                val brands = (uiBrandState as ApiResponse.Success).data

                when (uiCategoriesState) {
                    is ApiResponse.Success -> {
                        val categories = (uiCategoriesState as ApiResponse.Success).data

                        when (uiAllCouponsState) {
                            is ApiResponse.Success -> {
                                val discountCoupons =
                                    (uiAllCouponsState as ApiResponse.Success).data

                                val couponItems =
                                    discountCoupons.mapIndexed { index, discountCodeCoupon ->
                                        val imageResId = when (index % 4) {
                                            0 -> R.drawable.coupon22
                                            1 -> R.drawable.coupon30
                                            2 -> R.drawable.coupon20
                                            3 -> R.drawable.coupon10p
                                            else -> R.drawable.coupon22
                                        }
                                        CouponItem(imageResId, discountCodeCoupon.code)
                                    }

                                HomeContent(
                                    categories = categories,
                                    brands = brands,
                                    coupons = couponItems,
                                    onCatClicked = { catId,title ->
                                        navController.navigate(
                                            ScreenRoute.ProductsScreen(
                                                vendorName = null, categoryID = catId, title = title
                                            )
                                        )
                                    },
                                    onBrandClicked = { brandName ->
                                        navController.navigate(
                                            ScreenRoute.ProductsScreen(
                                                vendorName = brandName,
                                                categoryID = null,
                                                title = brandName,
                                            )
                                        )
                                    })
                            }

                            is ApiResponse.Failure -> {
                                val errorMsg = (uiAllCouponsState as ApiResponse.Failure).toString()
                                Log.e(TAG, "Coupons Error: $errorMsg")
                                Text(
                                    text = "Failed to load coupons: $errorMsg",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            }

                            ApiResponse.Loading -> ProgressShow()
                        }
                    }

                    is ApiResponse.Failure -> {
                        val errorMsg = (uiCategoriesState as ApiResponse.Failure).toString()
                        Text(
                            text = "Failed to load categories: $errorMsg",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )

                    }

                    ApiResponse.Loading -> ProgressShow()


                }
            }

            is ApiResponse.Failure -> {
                val errorMsg = (uiBrandState as ApiResponse.Failure).toString()
                Text(
                    text = "Failed to load brands: $errorMsg",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }

            ApiResponse.Loading -> ProgressShow()
        }
    }
}


@Composable
private fun HomeContent(
    categories: List<CustomCollectionsItem>,
    brands: List<SmartCollectionsItem>,
    coupons: List<CouponItem>,
    onCatClicked: (Long?,String?) -> Unit,
    onBrandClicked: (String) -> Unit
) {
    val context = LocalContext.current
    Column {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Please tap on the image to get the coupon:",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        CouponsCarousel(coupons = coupons, onCouponClick = { couponCode ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Coupon Code", couponCode)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Coupon code copied: $couponCode", Toast.LENGTH_SHORT).show()
        })

        Spacer(Modifier.height(20.dp))

        if (categories.size >= 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularImageWithTitle(categories[0], R.drawable.img_kid, onCatClicked)
                CircularImageWithTitle(categories[1], R.drawable.img_man, onCatClicked)
                CircularImageWithTitle(categories[3], R.drawable.img_women, onCatClicked)
                CircularImageWithTitle(categories[2], R.drawable.img_sale, onCatClicked)
            }
        } else {
            Text(
                text = "Not enough categories available",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Show All Products",
            modifier = Modifier
                .padding(start = 6.dp)
                .clickable {
                    Log.i(TAG, "All Products Clicked")
                    onCatClicked(null,null)
                },
            color = colorResource(R.color.teal_80),
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
            items(brands) { brand ->
                RoundedImageWithTitle(brand, onBrandClicked = { brandId ->
                    onBrandClicked(brandId)
                    Log.i(TAG, "BrandClicked: $brandId")
                })
                Spacer(Modifier.width(6.dp))
            }
        }
    }
}


@Composable
fun CouponsCarousel(
    coupons: List<CouponItem>, onCouponClick: (String) -> Unit
) {
    if (coupons.isEmpty()) {
        Text(
            text = "No coupons available at the moment.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        return
    }

    val pagerState = rememberPagerState(
        initialPage = 0, pageCount = { coupons.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000L)
            val nextPage = (pagerState.currentPage + 1) % coupons.size
            if (nextPage != pagerState.currentPage) {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            state = pagerState, pageSpacing = 8.dp, modifier = Modifier.fillMaxSize()
        ) { page ->
            val coupon = coupons[page]
            CouponImage(
                imageResId = coupon.imageResId,
                couponCode = coupon.code,
                onClick = { onCouponClick(coupon.code) })
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(coupons.size) { index ->
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
fun CouponImage(
    imageResId: Int, couponCode: String, onClick: () -> Unit
) {
    Box(modifier = Modifier
        .height(200.dp)
        .clip(RoundedCornerShape(12.dp))
        .clickable { onClick() }
        .background(Color.LightGray)) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Coupon Image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.FillBounds
        )

    }
}


@Composable
fun CircularImageWithTitle(
    category: CustomCollectionsItem, imgId: Int, onCatClicked: (Long,String) -> Unit
) {
    Column(
        modifier = Modifier.clickable {
            category.id?.let { onCatClicked(it,category.title?:"") }
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
        CircularProgressIndicator()
        Text("Loading", fontSize = 22.sp)
    }

}