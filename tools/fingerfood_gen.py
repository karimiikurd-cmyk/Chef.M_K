# -*- coding: utf-8 -*-
"""
Generates exactly 40 unique, professional Finger Foods for protein store retail.
"""

def generate_fingerfood():
    recipes = []
    
    iconic_fingerfoods = [
        ("FF-001", "مینی اسلایدر برگر گوشت گوساله و پنیر چدار (پارتی پک)", "پتی برگر ۵۰ گرمی، نان بریوش مینی، چدار", "تابه یا گریل ۲ دقیقه هر سمت", "پارتی پک ۱۲ عددی آماده سرو مجالس"),
        ("FF-002", "اسکوییر مینی فیله مرغ ساتای با سس بادام‌زمینی", "سیخ‌های چوبی ۱۵ سانتی با فیله مرغ مزه‌دار ساتای", "گریل ۵ دقیقه", "سیخ‌های کوچک شکیل برای سینی فینگرفود"),
        ("FF-003", "بیف ولینگتون بایتس مینیاتوری در خمیر هزارلا", "لقمه‌های فیله گوساله با پوره قارچ داکسل در خمیر پفکی", "فر ۲۱۰°C به مدت ۱۲ دقیقه", "فوق‌العاده لوکس و ترند در سفارشات مهمانی"),
        ("FF-004", "مینی رپ ترتیلا مرغ گریل و سس سزار", "نان ترتیلا پیچیده با فیله مرغ، کاهو و سزار", "سرو سرد بدون پخت", "ساندویچ‌های برشی رولتی بسیار محبوب"),
        ("FF-005", "پاپ‌کورن چیکن ترد با ادویه کاجان لوئیزیانا", "حبه‌های ۱ سانتی سینه مرغ سوخاری دابل کرانچ", "سرخ‌کن ۲ دقیقه", "اسنک پروتئینی پرطرفدار کودکان و نوجوانان")
    ]
    
    # 35 additional finger foods
    bases = [
        ("مینی اسلایدر بریوش", "نان بریوش کره‌ای با مغز پروتئینی", "ساندویچ کوچک لقمه‌ای"),
        ("سیخ‌های چوبی یاکیتوری و فینگرفود", "سیخ‌های کوتاه بامبو با گوشت فرآوری شده", "سیخی بدون کثیف شدن دست"),
        ("تارتلت و کاناپه پروتئینی", "پایه تارت نمکی با فیلینگ گوشتی", "شیک و مدرن روی میز بار"),
        ("لقمه‌های خمیر پیراشکی و یوفکا", "خمیر ترد کره‌ای با مغز گوشت و مرغ", "ترد و داغ از فر"),
        ("بایتس سوخاری دابل کرانچ", "مکعب‌های فیله با روکش سوخاری پانکو", "سوخاری طلایی کریسپی")
    ]
    
    fillings = [
        ("با فیله مرغ زعفرانی و پنیر گودا", "مرغ و پنیر"),
        ("با گوشت چرخ‌کرده قلوه‌گاه و پیاز کاراملی", "گوشت و پیاز"),
        ("با بوقلمون دودی و سس کرنبری", "بوقلمون و کرنبری"),
        ("با ماهی سالمون دودی و پنیر خامه‌ای شوید", "سالمون و پنیر"),
        ("با میت‌بال تند مکزیکی و هالوپینو", "گوشت اسپایسی"),
        ("با استیک فیله گوساله و سس خردل دیژون", "استیک بایتس"),
        ("با سوسیس دست‌ساز کوکتل دودی و چدار", "سوسیس گورمه")
    ]
    
    idx = len(iconic_fingerfoods) + 1
    for b in bases:
        for f in fillings:
            if len(recipes) + len(iconic_fingerfoods) >= 40:
                break
            uid = f"FF-{idx:03d}"
            name = f"{b[0]} {f[0]}"
            recipe_dict = {
                "uniqueId": uid,
                "name": name,
                "category": "FINGER_FOOD",
                "categoryNameFa": "فینگرفودهای مجالس",
                "shortDescription": f"فینگرفود مهمانی {name} آماده گرم‌کردن یا سرو سرد در رویدادها و دورهمی‌ها.",
                "mainProduct": f"{b[1]} با {f[1]}",
                "baseWeight": 1000.0,
                "ingredients": [
                    {"name": f"پایه نان یا خمیر ({b[1]})", "amount": 400.0, "unit": "گرم", "isScalable": True},
                    {"name": f"فیلینگ پروتئینی فرموله ({f[1]})", "amount": 500.0, "unit": "گرم", "isScalable": True},
                    {"name": "سس و پنیر اتصالی", "amount": 100.0, "unit": "گرم", "isScalable": True}
                ],
                "preparationSteps": [
                    "فیلینگ پروتئینی را به قطعات لقمه‌ای ۲۰ تا ۳۰ گرمی تقسیم کنید.",
                    "درون خمیر یا نان مینیاتوری قرار داده و فرم‌دهی کنید.",
                    "در سینی‌های مهمانی ۶، ۱۲ یا ۲۴ عددی با جداکننده‌های شیک بچینید.",
                    "سلفون حرارتی کشیده و در دمای ۲ درجه نگهداری کنید.",
                    "راهنمای گرم‌کردن سریع ۳ دقیقه‌ای روی جعبه قید شود."
                ],
                "preparationTime": "۳۰ دقیقه",
                "marinationTime": "آماده سرو یا پخت سریع",
                "cookingTime": "۵ تا ۱۰ دقیقه در فر ۲۰۰°C",
                "cookingTemperature": "۲۰۰°C",
                "cookingMethod": "پخت در فر یا سرخ‌کن ایرفرایر",
                "professionalTips": f"{b[2]}. مناسب پذیرایی سریع و تمیز.",
                "commonMistakes": "فیلینگ مرطوب که کف خمیر را خمیر و شل می‌کند.",
                "storageInstructions": "دمای ۲ درجه سانتی‌گراد در جعبه‌های کیپ.",
                "recommendedStorageTime": "۲ روز در یخچال / قابلیت فریز خمیرهای خام تا ۲ ماه",
                "storeUsage": "محصول سفارش محور بسیار سودآور برای جشن‌های تولد، ایونت‌ها و پک‌های پذیرایی شرکتی.",
                "searchTags": ["فینگرفود", "پارتی‌پک", "اسلایدر", "مهمانی", name],
                "foodSafetyAlert": "در زمان سرو روی میز مهمانی نباید بیش از ۲ ساعت در دمای محیط بماند.",
                "relatedChickenIds": ["CK-001"],
                "relatedSauceIds": ["SC-001"]
            }
            recipes.append(recipe_dict)
            idx += 1
            
    # Combine initial
    all_ff = []
    for ifi in iconic_fingerfoods:
        uid, name, comp, cook, use = ifi
        all_ff.append({
            "uniqueId": uid,
            "name": name,
            "category": "FINGER_FOOD",
            "categoryNameFa": "فینگرفودهای مجالس",
            "shortDescription": f"فینگرفود لوکس {name} مناسب سینی‌های پذیرایی.",
            "mainProduct": comp,
            "baseWeight": 1000.0,
            "ingredients": [
                {"name": comp, "amount": 800.0, "unit": "گرم", "isScalable": True},
                {"name": "سس اختصاصی همراه", "amount": 150.0, "unit": "گرم", "isScalable": True},
                {"name": "ادویه و چاشنی", "amount": 50.0, "unit": "گرم", "isScalable": True}
            ],
            "preparationSteps": [
                "آماده‌سازی لقمه‌های یک‌اندازه.",
                "چیدمان در سینی پذیرایی.",
                "بسته‌بندی بهداشتی با تهویه مناسب."
            ],
            "preparationTime": "۲۵ دقیقه",
            "marinationTime": "آماده مصرف",
            "cookingTime": "۵ دقیقه",
            "cookingTemperature": "۱۸۰°C",
            "cookingMethod": cook,
            "professionalTips": use,
            "commonMistakes": "سرد شدن نامناسب قبل از جعبه‌بندی.",
            "storageInstructions": "دمای ۲ تا ۴ درجه سانتی‌گراد.",
            "recommendedStorageTime": "۲ روز",
            "storeUsage": use,
            "searchTags": ["فینگرفود", "پارتی", name],
            "foodSafetyAlert": "کنترل فسادپذیری در هوای آزاد.",
            "relatedChickenIds": ["CK-001"],
            "relatedSauceIds": ["SC-002"]
        })
        
    all_ff.extend(recipes)
    return all_ff[:40]

if __name__ == "__main__":
    ff = generate_fingerfood()
    print(f"Generated {len(ff)} finger food recipes. First: {ff[0]['name']}, Last: {ff[-1]['name']}")
