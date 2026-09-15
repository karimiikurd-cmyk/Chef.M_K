package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Recipe
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SaffronGold
import com.example.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductCardScreen(
    recipeId: String,
    viewModel: RecipeViewModel,
    onBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit,
    onNavigateToPrepList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allRecipes by viewModel.allAvailableRecipes.collectAsStateWithLifecycle()
    val recipe = remember(recipeId, allRecipes) { allRecipes.find { it.uniqueId == recipeId } }

    if (recipe == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("اطلاعات محصول مورد نظر یافت نشد.")
        }
        return
    }

    // Determine flavor profile tags based on ingredients and name
    val flavorTags = remember(recipe) { deriveFlavorProfile(recipe) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_product_card_back")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "شناسنامه و کارت محصول ویترینی",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Chef.M_K Product Sheet • کد: ${recipe.uniqueId}",
                                style = MaterialTheme.typography.bodySmall.copy(color = SaffronGold)
                            )
                        }

                        IconButton(
                            onClick = {
                                val printSheet = buildPrintableSheet(recipe, flavorTags)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("کارت محصول ${recipe.name}", printSheet)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "متن شناسنامه محصول برای چاپ یا ارسال در کلیپ‌بورد کپی شد.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("btn_share_product_card")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "اشتراک / چاپ", tint = SaffronGold)
                        }
                    }
                    KurdishMotifBorder()
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main Product Header Sheet
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().testTag("product_card_main_sheet"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recipe.name,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = KurdishCrimson
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${recipe.categoryNameFa} • گوشت پایه: ${recipe.mainProduct}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                color = SaffronGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = recipe.uniqueId,
                                    color = SaffronGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        if (recipe.shortDescription.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = recipe.shortDescription,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Flavor Profile Chips
                        Text(
                            text = "پروفایل طعم و چاشنی (Flavor Profile):",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            flavorTags.forEach { tag ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Technical Specs Tiles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecTile(
                        title = "دمای نگهداری",
                        value = "۱ تا ۳ درجه",
                        subtitle = "سردخانه استاندارد",
                        icon = Icons.Default.AcUnit,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    )
                    SpecTile(
                        title = "ماندگاری ویترین",
                        value = recipe.recommendedStorageTime.ifBlank { "۴۸ تا ۷۲ ساعت" },
                        subtitle = "حفظ بافت و طعم",
                        icon = Icons.Default.Restaurant,
                        tint = SaffronGold,
                        modifier = Modifier.weight(1f)
                    )
                    SpecTile(
                        title = "دمای داخلی پخت",
                        value = recipe.cookingTemperature.ifBlank { "۷۴°C مغزپخت" },
                        subtitle = recipe.cookingMethod.ifBlank { "گریل / باربیکیو" },
                        icon = Icons.Default.DeviceThermostat,
                        tint = KurdishCrimson,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Raw Display & Presentation in Store
            item {
                SectionCard(
                    title = "راهنمای چیدمان و جلوه بصری در ویترین (Raw Display Advice)",
                    icon = Icons.Default.Storefront,
                    content = if (recipe.storeUsage.isNotBlank()) {
                        "${recipe.storeUsage}\n\nنکات ویترینی: برش‌ها را با شیب یکنواخت روی سینی‌های استیل قرار داده و از گارنیش رزماری تازه، لیموترش حلقه‌شده و پاپریکای دودی در حاشیه سینی استفاده فرمایید. نور ویترین ترجیحاً گرم با دمای ۳۰۰۰ کلوین باشد تا براقیت مرینیت به حداکثر برسد."
                    } else {
                        "برش‌های این محصول را در سینی‌های کم‌عمق استیل ضدزنگ بچینید. سطح گوشت را مرتباً با برس روغن زیتون زعفرانی براق نگه دارید تا رطوبت سطحی در مجاورت جریان فن یخچال از دست نرود. دورچین پیشنهادی ویترین: فلفل کبابی، گوجه گیلاسی و شاخه‌های مرزه."
                    }
                )
            }

            // Customer Cooking Guide & Finished Presentation
            item {
                SectionCard(
                    title = "راهنمای مشتری و شیوه سرو (Cooked Presentation Advice)",
                    icon = Icons.Default.OutdoorGrill,
                    content = "روش پخت به مشتری: گوشت را ۱۵ دقیقه قبل از کباب کردن از یخچال خارج کنید تا به دمای محیط برسد. روی زغال روشن و یکدست با حرارت متوسط بادقت مغزپخت کنید. پس از برداشتن از روی شعله، ۳ تا ۵ دقیقه زمان استراحت دهید تا آب گوشت بازتوزیع شود و بافتی بسیار آبدار ارائه نماید."
                )
            }

            // Allergens & Food Safety Warning
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = KurdishCrimson.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = KurdishCrimson, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "آلرژن‌ها و هشدارهای بهداشتی و ایمنی غذا",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = KurdishCrimson
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (recipe.foodSafetyAlert.isNotBlank()) {
                                recipe.foodSafetyAlert
                            } else {
                                "رعایت زنجیره سرد (دمای کمتر از ۴ درجه سانتی‌گراد) در کلیه مراحل الزامی است. عدم تماس چاقو و تخته کار گوشت خام با محصولات آماده مصرف جهت جلوگیری از آلودگی متقاطع."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Actions (Add to Prep list, View Full Recipe)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.createPrepList("تولید ${recipe.name}", recipe, 5.0) {
                                onNavigateToPrepList()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronGold, contentColor = DarkCharcoal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("btn_add_to_prep_from_card")
                    ) {
                        Icon(imageVector = Icons.Default.PlaylistAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("افزودن به تدارکات", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigateToRecipe(recipe.uniqueId) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("مشاهده رسپی کامل")
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun deriveFlavorProfile(recipe: Recipe): List<String> {
    val tags = mutableListOf<String>()
    val allText = "${recipe.name} ${recipe.shortDescription} ${recipe.ingredients.joinToString { it.name }} ${recipe.searchTags.joinToString()}".lowercase()

    if (allText.contains("زعفران") || allText.contains("زرشک")) tags.add("زعفرانی اصیل")
    if (allText.contains("دود") || allText.contains("اسموک") || allText.contains("دودی")) tags.add("دودی و باربیکیو")
    if (allText.contains("سیر") || allText.contains("موسیر")) tags.add("سیر و آلیوم")
    if (allText.contains("فلفل") || allText.contains("تند") || allText.contains("چیلی") || allText.contains("هالوپینو")) tags.add("اسپایسی و تند")
    if (allText.contains("لیمو") || allText.contains("نارنج") || allText.contains("انار") || allText.contains("ترش")) tags.add("ترش و مرکباتی")
    if (allText.contains("رزماری") || allText.contains("آویشن") || allText.contains("جعفری") || allText.contains("گشنیز") || allText.contains("ریحان")) tags.add("گیاهی و معطر (Herbal)")
    if (allText.contains("ماست") || allText.contains("خامه") || allText.contains("کره")) tags.add("خامه‌ای و لطیف")
    if (allText.contains("خردل") || allText.contains("دیژون")) tags.add("خردل ملایم")
    if (allText.contains("کنجد") || allText.contains("سویا")) tags.add("آسیایی و اومامی")

    if (tags.isEmpty()) {
        tags.add("کلاسیک و ملایم")
        tags.add("پروتئینی خالص")
    }
    return tags.distinct()
}

private fun buildPrintableSheet(recipe: Recipe, flavorTags: List<String>): String {
    val sb = StringBuilder()
    sb.appendLine("════════════════════════════════════════")
    sb.appendLine("🥩 شناسنامه و کارت محصول ویترینی — Chef.M_K")
    sb.appendLine("════════════════════════════════════════")
    sb.appendLine("🏷️ نام محصول: ${recipe.name}")
    sb.appendLine("🆔 کد محصول: ${recipe.uniqueId}")
    sb.appendLine("📂 دسته‌بندی: ${recipe.categoryNameFa}")
    sb.appendLine("🥩 گوشت و برش پایه: ${recipe.mainProduct}")
    sb.appendLine("🌿 پروفایل طعم: ${flavorTags.joinToString(" • ")}")
    sb.appendLine("❄️ شرایط نگهداری: ۱ تا ۳ درجه سانتی‌گراد")
    sb.appendLine("⏳ مدت ماندگاری ویترین: ${recipe.recommendedStorageTime.ifBlank { "۴۸ تا ۷۲ ساعت" }}")
    sb.appendLine("🔥 روش و دمای پخت: ${recipe.cookingMethod} (${recipe.cookingTemperature})")
    sb.appendLine("\n✨ راهنمای چیدمان ویترین:")
    sb.appendLine(recipe.storeUsage.ifBlank { "چیدمان یکدست در سینی استیل با گارنیش رزماری و روغن‌مالی زعفرانی سطحی." })
    sb.appendLine("\n⚠️ هشدارهای بهداشتی:")
    sb.appendLine(recipe.foodSafetyAlert.ifBlank { "حفظ دمای کمتر از ۴ درجه سانتی‌گراد و جلوگیری از آلودگی متقاطع." })
    sb.appendLine("════════════════════════════════════════")
    return sb.toString()
}
