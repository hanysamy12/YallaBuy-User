package com.example.yallabuy_user.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.yallabuy_user.data.models.CategoryResponse
import com.example.yallabuy_user.data.models.CustomCollectionsItem
import com.example.yallabuy_user.data.models.Image
import com.example.yallabuy_user.repo.RepositoryInterface
import com.example.yallabuy_user.utilities.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    private lateinit var repository: RepositoryInterface
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun testGetAllCategories_returnThreeCategories() = runTest {
        val fakeResponse = CategoryResponse(
            customCollections = listOf(
                CustomCollectionsItem(
                    id = 1L,
                    title = "Category 1",
                    image = Image(src = "https://example.com/category1.jpg")
                ),
                CustomCollectionsItem(
                    id = 2L,
                    title = "Category 2",
                    image = Image(src = "https://example.com/category2.jpg")
                ),
                CustomCollectionsItem(
                    id = 3L,
                    title = "Category 3",
                    image = Image(src = "https://example.com/category3.jpg")
                )
            )
        )
        coEvery { repository.getAllCategories() } returns flowOf(fakeResponse)

        viewModel.getAllCategories()
        val result =  viewModel.categories.value
        val expectedList = fakeResponse.customCollections
        val actualList = (result as ApiResponse.Success).data
        assertThat(actualList, `is`(expectedList))
    }

}