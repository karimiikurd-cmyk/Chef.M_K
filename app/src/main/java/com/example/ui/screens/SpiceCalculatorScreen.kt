package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.KurdishMotifBorder
import com.example.ui.theme.SaffronGold

data class SpiceFormulaDef(
    val id: String,
    val nameFa: String,
    val burnThresholdNote: String,
    val items: List<Triple<String, Double, String>> // Name, SharePct, FunctionalRole
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpiceCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formulas = remember {
        listOf(
            SpiceFormulaDef(
                "kebab_koobideh",
                "ادویه تخصصی کباب کوبیده اعلا",
                "سماق و فلفل سیاه حرارت مستقیم ملایم نیاز دارند؛ از تماس مستقیم با شعله باز بدون چربی خودداری شود.",
                listOf(
                    Triple("سماق قهوه‌ای خالص تبریز", 0.35, "ایجاد اسیدیته ملایم و تعادل چربی"),
                    Triple("فلفل سیاه دانه‌سای معطر", 0.25, "گرما و تندی پویا"),
                    Triple("پودر پیاز سفید دهیدراته", 0.20, "تثبیت آرومای گوشت"),
                    Triple("زعفران پودر شده خالص", 0.05, "عطر سلطنتی و رنگ"),
                    Triple("پودر هل سبز", 0.05, "بوزدایی از دنبه گوسفندی"),
                    Triple("جوز هندی بسیار کم", 0.10, "اومامی عمیق و طعم گوشتی")
                )
            ),
            SpiceFormulaDef(
                "joojeh_saffron",
                "ادویه جوجه کباب زعفرانی",
                "فلفل پاپریکا در دمای بالای ۱۵۰ درجه کاراملی شده و اگر زیاد بماند تلخ می‌شود.",
                listOf(
                    Triple("پودر پیاز زرد خالص", 0.30, "طعم پایه مرغ"),
                    Triple("فلفل سفید سابیده", 0.20, "تندی بدون لکه سیاه روی پوست مرغ"),
                    Triple("پاپریکا شیرین دودی", 0.20, "رنگ طلایی-سرخ زیبا"),
                    Triple("پودر سیر همدان", 0.15, "طعم‌دهنده مکمل"),
                    Triple("تخم گشنیز ساییده", 0.10, "عطر باطراوت مرکباتی"),
                    Triple("زنجبیل تازه خشک", 0.05, "هضم آسان و گرما")
                )
            ),
            SpiceFormulaDef(
                "steak_cajun",
                "ادویه مالشی استیک کاجان و قهوه",
                "پودر قهوه و پاپریکا در شعله مستقیم بیش از ۲ دقیقه بسوزد طعم کربن می‌گیرد؛ مناسب تابه چدن با کره.",
                listOf(
                    Triple("پاپریکا دودی اسپانیایی", 0.25, "رنگ سرخ دودی"),
                    Triple("پودر دانه قهوه اسپرسو رست تیره", 0.15, "ایجاد کراست کاراملی و تلخی جذاب"),
                    Triple("پودر سیر برشته", 0.20, "سیر کاراملی"),
                    Triple("فلفل سیاه درشت آسیاب", 0.20, "بافت ترد و فلفلی زیر دندان"),
                    Triple("پودر خردل زرد", 0.10, "امولسیون چربی گوشت"),
                    Triple("اورگانو خشک کوهی", 0.10, "عطر علفی مدیترانه‌ای")
                )
            ),
            SpiceFormulaDef(
                "burger_prime",
                "ادویه برگر گورمه پرایم",
                "در برگرهای دست‌ساز، ادویه در مرحله آخر چرخ اضافه شود تا میوسین گوشت بافت را لاستیکی نکند.",
                listOf(
                    Triple("فلفل سیاه آسیاب درشت", 0.35, "تندی گزنده و خوشایند"),
                    Triple("پودر پیاز تفت داده", 0.25, "شیرینی طبیعی"),
                    Triple("پودر سیر حبه‌ای خالص", 0.20, "تقویت اومامی"),
                    Triple("پاپریکا دودی ملایم", 0.15, "رنگ اشتهاآور مغز برگر"),
                    Triple("دانه خردل پودر شده", 0.05, "اتصال چربی به بافت")
                )
            )
        )
    }

    var selectedFormula by remember { mutableStateOf(formulas.first()) }
    var targetBatchGrams by remember { mutableDoubleStateOf(500.0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "فرمولاتور و ادویه‌سنج کارگاهی",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_spice")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                KurdishMotifBorder()
            }

            // Introduction Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = SaffronGold)
                            Text(
                                text = "محاسبه نسبت دقیق ترکیب ادویه‌جات",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "فرمول‌های استاندارد ادویه فروشگاه پروتئینی برای ترکیب در حجم‌های ۵۰۰ گرم تا ۵ کیلوگرم با ترازوی دیجیتال گرمی دقیق.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Formula Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "انتخاب ترکیب ادویه:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SaffronGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(formulas) { f ->
                                FilterChip(
                                    selected = (selectedFormula.id == f.id),
                                    onClick = { selectedFormula = f },
                                    label = { Text(f.nameFa) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SaffronGold,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Weight Controls
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "وزن کل بچ پودر ادویه:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = SaffronGold,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (targetBatchGrams >= 1000) "%.1f کیلوگرم".format(targetBatchGrams / 1000.0) else "${targetBatchGrams.toInt()} گرم",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Slider(
                            value = targetBatchGrams.toFloat(),
                            onValueChange = { targetBatchGrams = it.toDouble() },
                            valueRange = 100f..5000f,
                            steps = 49,
                            colors = SliderDefaults.colors(
                                thumbColor = SaffronGold,
                                activeTrackColor = SaffronGold
                            )
                        )
                    }
                }
            }

            // Breakdown Table
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "جدول مقادیر برای ${selectedFormula.nameFa}:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SaffronGold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedFormula.items.forEachIndexed { idx, (spName, share, role) ->
                                val calculatedWeight = targetBatchGrams * share
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${idx + 1}. $spName",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "نقش: $role",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "%.1f گرم (%.0f٪)".format(calculatedWeight, share * 100),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }
                                }
                                if (idx < selectedFormula.items.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                }
                            }
                        }
                    }
                }
            }

            // Burn Temperature Warning
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFE65100))
                            Text(
                                text = "آستانه دمای سوختگی و تلخی ادویه",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = selectedFormula.burnThresholdNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
