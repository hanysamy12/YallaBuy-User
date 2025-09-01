package com.example.yallabuy_user.domainLayer.userCases

import com.example.yallabuy_user.dataLayer.data.models.CategoryResponse
import com.example.yallabuy_user.dataLayer.data.models.ProductResponse
import com.example.yallabuy_user.domainLayer.repoInterface.RepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesUseCase(
    private val repo : RepositoryInterface
) {
    suspend operator fun invoke() : Flow<CategoryResponse>
    {
        return repo.getAllCategories()
    }
}

class GetAllProductsUseCase(private val repo: RepositoryInterface)
{
    suspend operator fun invoke(): Flow<ProductResponse> {
        return repo.getAllProducts()
    }
}

class GetProductsByCategoryUseCase(private val repo: RepositoryInterface)
{
    suspend operator fun invoke(categoryId : Long): Flow<ProductResponse> {
        return repo.getCategoryProducts(categoryId)
    }
}