package com.example.yallabuy_user.productInfo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.yallabuy_user.data.models.productInfo.Image
import com.example.yallabuy_user.data.models.productInfo.Product
import com.example.yallabuy_user.data.models.productInfo.ProductInfoResponse
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ProductInfoViewModelTest {

    private lateinit var productInfoViewModel: ProductInfoViewModel
    private lateinit var repo: RepositoryInterface

    @Before
    fun setUp() {
        repo = mockk(relaxed = true)
        productInfoViewModel = ProductInfoViewModel(repo)
    }

    @Test
    fun `get ProductInfo By Id sending Id result state is success`() = runTest {

        // Given
        val productInfoResponse = ProductInfoResponse(
            Product(
                admin_graphql_api_id = "gid://shopify/Product/1234567890",
                body_html = "<strong>This is a great product</strong>",
                created_at = "2025-06-18T12:00:00Z",
                handle = "dummy-product",
                id = 1234567890L,
                image = Image(
                    admin_graphql_api_id = "gid://shopify/ProductImage/1",
                    alt = "Main image",
                    created_at = "2025-06-18T12:00:00Z",
                    height = 600,
                    id = 1L,
                    position = 1,
                    product_id = 1234567890L,
                    src = "https://dummyimage.com/800x600",
                    updated_at = "2025-06-18T12:00:00Z",
                    variant_ids = emptyList(),
                    width = 800
                ),
                images = listOf(
                    Image(
                        admin_graphql_api_id = "gid://shopify/ProductImage/2",
                        alt = "Additional image",
                        created_at = "2025-06-18T12:01:00Z",
                        height = 600,
                        id = 2L,
                        position = 2,
                        product_id = 1234567890L,
                        src = "https://dummyimage.com/800x600/ffffff/000000",
                        updated_at = "2025-06-18T12:01:00Z",
                        variant_ids = emptyList(),
                        width = 800
                    )
                ),
                options = emptyList(), // assuming no options for now
                product_type = "T-Shirt",
                published_at = "2025-06-18T12:00:00Z",
                published_scope = "global",
                status = "active",
                tags = "dummy,sample,product",
                template_suffix = "null",
                title = "Dummy Product",
                updated_at = "2025-06-18T12:00:00Z",
                variants = emptyList(), // assuming no variants for now
                vendor = "Dummy Vendor"
            )
        )

        coEvery { repo.getProductById(0) } returns flowOf(productInfoResponse)

        // When

        productInfoViewModel.getProductInfoById(0)

        // Then
        val result = productInfoViewModel.productInfo.value
        val expectedResult = productInfoResponse
        val actualResult = (result as ApiResponse.Success).data

        assertThat(actualResult , `is` (expectedResult) )
    }

}