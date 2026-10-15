package com.khodehydro.faimess.model

/* ---- Shop ---- */

typealias ShopCategoryId = String   // "all" | "apparel" | "accessories" | "collectibles"

data class ShopProduct(
    val id: String,
    val name: String,
    val category: String,       // "apparel" | "accessories" | "collectibles"
    val price: Int,             // Toman
    val wasPrice: Int? = null,
    val badge: String? = null,  // "new" | "bestseller" | "low"
    @androidx.annotation.DrawableRes val photoRes: Int = 0,
)

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Int,
    val color: String? = null,
    val size: String? = null,
)

data class CreateOrderDto(
    val customerName: String,
    val customerPhone: String,
    val shippingAddress: String,
    val items: List<OrderItem>,
    val notes: String? = null,
)

data class OrderReceipt(
    val orderId: String,
    val orderNumber: String,
    val totalAmount: Int,
    val status: String,         // "pending" | "processing" | "paid" | "shipped" | "delivered"
    val createdAt: String,
    val itemsCount: Int,
)