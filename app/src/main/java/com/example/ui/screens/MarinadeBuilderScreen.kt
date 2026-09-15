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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import com.example.ui.theme.KurdishCrimson
import com.example.ui.theme.SafetyAlertContainer
import com.example.ui.theme.SafetyAlertRed
import com.example.ui.theme.SaffronGold

data class ProteinOption(val id: String, val nameFa: String, val saltPct: Double, val oilPct: Double, val maxMarinHrs: String)
data class FlavorStyle(val id: String, val nameFa: String, val acidName: String, val acidPct: Double, val spiceFormula: List<Pair<String, Double>>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarinadeBuilderScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val proteins = remember {
        listOf(
            ProteinOption("chicken_breast", "سینه و فیله مرغ", 0.013, 0.06, "۶ تا ۱۲ ساعت"),
            ProteinOption("chicken_thigh", "ران و کتف و بال مرغ", 0.014, 0.05, "۱۲ تا ۲۴ ساعت"),
            ProteinOption("beef_steak", "راسته و فیله گوساله", 0.012, 0.07, "۴ تا ۸ ساعت"),
            ProteinOption("lamb_chops", "دنده و چنجه بره", 0.013, 0.04, "۸ تا ۱۶ ساعت"),
            ProteinOption("turkey", "فیله بوقلمون و شترمرغ", 0.014, 0.06, "۱۲ تا ۱۸ ساعت"),
            ProteinOption("salmon", "سالمون و قزل‌آلا", 0.011, 0.04, "۳۰ تا ۶۰ دقیقه")
        )
    }

    val flavors = remember {
        listOf(
            FlavorStyle("saffron", "زعفرانی شاهانه مجلسی", "آب لیمو ترش تازه شیرازی", 0.035, listOf("زعفران سرگل دم‌کرده غلیظ" to 0.003, "پودر پیاز زرد خالص" to 0.01, "فلفل سفید سابیده" to 0.002, "روغن مایع کانولا" to 0.06)),
            FlavorStyle("sour_pomegranate", "ترش گیلکی با چوچاق و انار", "رب انار ملس غلیظ", 0.06, listOf("مغز گردوی پودر شده" to 0.04, "چوچاق و گشنیز ساییده" to 0.012, "فلفل سیاه" to 0.003, "روغن زیتون فرابکر" to 0.05)),
            FlavorStyle("lari", "لاری با ماست چکیده و گشنیز", "آب لیمو ترش و ماست ترش چکیده", 0.08, listOf("ماست پرچرب چکیده" to 0.08, "تخم گشنیز نیم‌کوب" to 0.006, "فلفل سیاه دانه درشت" to 0.004, "روغن مایع" to 0.04)),
            FlavorStyle("smoky_bbq", "باربیکیو دودی کاراملی", "سرکه سیب طبیعی", 0.03, listOf("پاپریکا دودی اعلا" to 0.012, "شکر قهوه‌ای / شیره خرما" to 0.015, "سیر پوره شده" to 0.01, "پودر خردل دیژون" to 0.005, "عصاره دود طبیعی" to 0.002)),
            FlavorStyle("fiery_chipotle", "چیپوتله تند آتشین", "سرکه سفید و لیمو", 0.04, listOf("پرک فلفل کاپیا و چیلی" to 0.01, "پودر زیره سبز بو داده" to 0.004, "سیر تازه له شده" to 0.012, "روغن هسته انگور" to 0.05)),
            FlavorStyle("mediterranean", "سیر و سبزیجات زاگرس", "سرکه بالزامیک ملایم", 0.03, listOf("رزماری و آویشن کوهی" to 0.008, "سیر حبه‌ای رنده شده" to 0.015, "روغن زیتون بکر" to 0.06, "فلفل سیاه" to 0.003))
        )
    }

    var selectedProtein by remember { mutableStateOf(proteins.first()) }
    var selectedFlavor by remember { mutableStateOf(flavors.first()) }
    var meatWeightKg by remember { mutableDoubleStateOf(5.0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "فرمولاتور و مرینیت‌ساز هوشمند",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_marinade")
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

            // Introduction
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
                            Icon(Icons.Default.Science, contentDescription = null, tint = KurdishCrimson)
                            Text(
                                text = "محاسبه نسبت‌های بیوشیمیایی مرینیت گوشت",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "مرینیت حرفه‌ای بر پایه ۳ اصل استوار است: ۱. نمک دقیق جهت باز کردن میوفیبریل‌های عضلانی بدون خروج آب ۲. اسیدیته کنترل‌شده برای لطافت بافت بدون لهیدگی ۳. چربی و روغن برای انتقال مواد محلول در چربی ادویه‌ها به مغز گوشت.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Protein Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۱. نوع گوشت و برش پروتئینی:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SaffronGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(proteins) { p ->
                                FilterChip(
                                    selected = (selectedProtein.id == p.id),
                                    onClick = { selectedProtein = p },
                                    label = { Text(p.nameFa) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KurdishCrimson,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Flavor Profile Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۲. سبک و طعم‌دهی مرینیت:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = SaffronGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(flavors) { f ->
                                FilterChip(
                                    selected = (selectedFlavor.id == f.id),
                                    onClick = { selectedFlavor = f },
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

            // Weight Slider
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
                                text = "۳. وزن گوشت خام جهت آماده‌سازی:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                color = SaffronGold,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "%.1f کیلوگرم".format(meatWeightKg),
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
                            value = meatWeightKg.toFloat(),
                            onValueChange = { meatWeightKg = it.toDouble() },
                            valueRange = 1f..40f,
                            steps = 39,
                            colors = SliderDefaults.colors(
                                thumbColor = SaffronGold,
                                activeTrackColor = SaffronGold
                            )
                        )
                    }
                }
            }

            // Computed Marinade Recipe
            val meatGrams = meatWeightKg * 1000.0
            val saltGrams = meatGrams * selectedProtein.saltPct
            val acidGrams = meatGrams * selectedFlavor.acidPct
            val onionPureeGrams = meatGrams * 0.09 // 9% onion juice/puree

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "فرمول دقیق مرینیت برای ${meatWeightKg.toInt()} کیلو ${selectedProtein.nameFa}:",
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
                            MarinadeIngRow("گوشت خالص خام پاک شده", meatWeightKg, "کیلوگرم")
                            MarinadeIngRow("نمک طعام استاندارد (۱.۲-۱.۴٪)", saltGrams, "گرم")
                            MarinadeIngRow("پوره پیاز صاف شده بدون تفاله", onionPureeGrams, "گرم")
                            MarinadeIngRow(selectedFlavor.acidName, acidGrams, "گرم")

                            selectedFlavor.spiceFormula.forEach { (spName, pct) ->
                                MarinadeIngRow(spName, meatGrams * pct, "گرم")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailParamRow("حداکثر زمان خواباندن در سرما", selectedProtein.maxMarinHrs)
                        DetailParamRow("دمای مجاز نگهداری حین مرینیت", "۰ تا ۳ درجه سانتی‌گراد (یخچال)")
                    }
                }
            }

            // Quality Control Warning
            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SafetyAlertRed),
                    colors = CardDefaults.outlinedCardColors(containerColor = SafetyAlertContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = SafetyAlertRed)
                            Text(
                                text = "دستورالعمل کنترل کیفی مرینیت",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SafetyAlertRed
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "هرگز مرینیت حاوی آب پیاز یا لیمو را بیش از زمان توصیه شده در یخچال نگه ندارید؛ تجمع اسید باعث 'پخت شیمیایی سرد' و کدر شدن بافت پروتئین می‌شود. همیشه آب پیاز را پس از رنده با پارچه تمیز صاف کنید تا ذرات تفاله روی زغال نسوزند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarinadeIngRow(name: String, amount: Double, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = if (amount == amount.toLong().toDouble()) "${amount.toLong()} $unit" else "%.1f %s".format(amount, unit),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
}
