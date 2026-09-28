# Add this global rule
-keepattributes Signature

# This rule will properly ProGuard all the model classes in
# the package com.example.globalindustriesattendance.
# Modify this rule to fit the structure of your app.
-keepclassmembers class com.example.globalindustriesattendance.** {
    *;
}
