package com.khodehydro.faimess.ui

import androidx.lifecycle.ViewModel
import com.khodehydro.faimess.data.*
import com.khodehydro.faimess.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/* ------------------------------------------------------------------ *
 *  News & Shop ViewModel
 * ------------------------------------------------------------------ */

data class NewsShopUiState(
    val news: List<NewsItem> = ALL_NEWS,
    val selectedNews: NewsItem? = null,
    val newsFilter: String = "all",
    val shopProducts: List<ShopProduct> = SHOP_PRODUCTS,
    val selectedProduct: ShopProduct? = null,
    val shopFilter: String = "all",
    val cartItems: List<OrderItem> = emptyList(),
    val orderReceipt: OrderReceipt? = null,
)

class NewsShopViewModel : ViewModel() {
    private val _state = MutableStateFlow(NewsShopUiState())
    val state: StateFlow<NewsShopUiState> = _state.asStateFlow()

    // News
    fun selectNews(id: String) = _state.update { it.copy(selectedNews = ALL_NEWS.find { n -> n.id == id }) }
    fun clearNewsSelection() = _state.update { it.copy(selectedNews = null) }

    fun filterNews(category: String) {
        val filtered = if (category == "all") ALL_NEWS else ALL_NEWS.filter { it.tag.equals(category, ignoreCase = true) }
        _state.update { it.copy(newsFilter = category, news = filtered) }
    }

    fun likeNews(newsId: String) {
        _state.update { state ->
            val updated = state.news.map { if (it.id == newsId) it.copy(likes = it.likes + 1) else it }
            val selected = if (state.selectedNews?.id == newsId) state.selectedNews.copy(likes = state.selectedNews.likes + 1) else state.selectedNews
            state.copy(news = updated, selectedNews = selected)
        }
    }

    fun addNewsComment(newsId: String, text: String) {
        val comment = NewsComment("comm-${System.currentTimeMillis()}", "Sori", "@sori", 0, "Just now", text, 0)
        _state.update { state ->
            val updated = state.news.map { n ->
                if (n.id == newsId) n.copy(comments = listOf(comment) + n.comments, commentsCount = n.commentsCount + 1) else n
            }
            val selected = if (state.selectedNews?.id == newsId) state.selectedNews.copy(comments = listOf(comment) + state.selectedNews.comments, commentsCount = state.selectedNews.commentsCount + 1) else state.selectedNews
            state.copy(news = updated, selectedNews = selected)
        }
    }

    // Shop
    fun selectProduct(id: String) = _state.update { it.copy(selectedProduct = SHOP_PRODUCTS.find { p -> p.id == id }) }
    fun clearProductSelection() = _state.update { it.copy(selectedProduct = null) }

    fun filterShop(category: String) {
        val filtered = if (category == "all") SHOP_PRODUCTS else SHOP_PRODUCTS.filter { it.category == category }
        _state.update { it.copy(shopFilter = category, shopProducts = filtered) }
    }

    fun addToCart(product: ShopProduct, quantity: Int = 1) {
        _state.update { state ->
            val existing = state.cartItems.find { it.productId == product.id }
            if (existing != null) {
                state.copy(cartItems = state.cartItems.map { if (it.productId == product.id) it.copy(quantity = it.quantity + quantity) else it })
            } else {
                state.copy(cartItems = state.cartItems + OrderItem(product.id, quantity, product.price))
            }
        }
    }

    fun removeFromCart(productId: String) {
        _state.update { it.copy(cartItems = it.cartItems.filter { item -> item.productId != productId }) }
    }

    fun createOrder(name: String, phone: String, address: String) {
        val items = _state.value.cartItems
        val total = items.sumOf { it.price * it.quantity }
        val receipt = OrderReceipt("ord-${System.currentTimeMillis()}", "FM-${(100000..999999).random()}", total, "pending", "Just now", items.sumOf { it.quantity })
        _state.update { it.copy(orderReceipt = receipt, cartItems = emptyList()) }
    }

    fun clearReceipt() = _state.update { it.copy(orderReceipt = null) }
}