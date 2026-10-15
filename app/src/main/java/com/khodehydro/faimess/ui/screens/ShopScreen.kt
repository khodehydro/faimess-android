package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.data.SHOP_CATEGORIES
import com.khodehydro.faimess.model.OrderReceipt
import com.khodehydro.faimess.model.ShopProduct
import com.khodehydro.faimess.ui.NewsShopViewModel
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.Subtle

@Composable
fun ShopScreen(vm: NewsShopViewModel, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()

    when {
        state.orderReceipt != null -> OrderReceiptScreen(receipt = state.orderReceipt!!, onDone = vm::clearReceipt, modifier = modifier)
        state.selectedProduct != null -> ProductDetailScreen(product = state.selectedProduct!!, cartCount = state.cartItems.size, onBack = vm::clearProductSelection, onAdd = { vm.addToCart(state.selectedProduct!!) }, modifier = modifier)
        else -> ShopListScreen(products = state.shopProducts, filter = state.shopFilter, cartCount = state.cartItems.size, onFilter = vm::filterShop, onSelect = vm::selectProduct, modifier = modifier)
    }
}

@Composable
private fun ShopListScreen(products: List<ShopProduct>, filter: String, cartCount: Int, onFilter: (String) -> Unit, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Shop", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
                BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                    Icon(Icons.Default.ShoppingCart, "Cart", tint = Ink)
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SHOP_CATEGORIES) { cat ->
                    FilterChip(selected = filter == cat, onClick = { onFilter(cat) }, label = { Text(cat.replaceFirstChar { it.uppercase() }, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary, selectedLabelColor = Color.White))
                }
            }
        }
        // 2-column grid
        item {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height((products.size / 2 * 260).dp.coerceAtMost(2000.dp)), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), userScrollEnabled = false) {
                items(products) { product -> ProductCard(product, onClick = { onSelect(product.id) }) }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun ProductCard(product: ShopProduct, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(140.dp).background(Primary.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
                Text(product.name.take(2), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Primary)
                product.badge?.let { badge ->
                    Surface(Modifier.align(Alignment.TopEnd).padding(6.dp), shape = RoundedCornerShape(6.dp), color = when (badge) { "new" -> Color(0xFF10B981); "bestseller" -> Primary; else -> Color(0xFFF59E0B) }) {
                        Text(badge.uppercase(), Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Ink)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${formatToman(product.price)} تومان", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Primary)
                    product.wasPrice?.let {
                        Spacer(Modifier.width(6.dp))
                        Text("${formatToman(it)}", fontSize = 11.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreen(product: ShopProduct, cartCount: Int, onBack: () -> Unit, onAdd: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Product") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            actions = { BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }, modifier = Modifier.padding(end = 16.dp)) { Icon(Icons.Default.ShoppingCart, "Cart") } })
        Box(Modifier.fillMaxWidth().height(280.dp).background(Primary.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
            Text(product.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
        }
        Column(Modifier.padding(20.dp)) {
            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Ink)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${formatToman(product.price)} تومان", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Primary)
                product.wasPrice?.let { Spacer(Modifier.width(8.dp)); Text("${formatToman(it)} تومان", fontSize = 16.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough) }
            }
            Spacer(Modifier.height(8.dp))
            Text("Category: ${product.category.replaceFirstChar { it.uppercase() }}", color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Icon(Icons.Default.ShoppingCart, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Add to Cart", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun OrderReceiptScreen(receipt: OrderReceipt, onDone: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🎉", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("Order Placed!", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Ink)
        Spacer(Modifier.height(8.dp))
        Text("Order #${receipt.orderNumber}", fontSize = 16.sp, color = Primary)
        Text("${receipt.itemsCount} items · ${formatToman(receipt.totalAmount)} تومان", color = Color.Gray)
        Text("Status: ${receipt.status}", color = Color.Gray)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onDone, shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) { Text("Continue Shopping") }
    }
}

fun formatToman(value: Int): String = "%,d".format(value)