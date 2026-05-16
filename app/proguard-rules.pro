# ProGuard rules for ApexSense
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\HP\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any custom rules here that might be required for your libraries (e.g. Supabase, Ktor, etc.)
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.apexsense.domain.model.** { *; }
-keep class kotlinx.serialization.json.** { *; }

# Ignore SLF4J missing classes
-dontwarn org.slf4j.**

# Ignore other common library warnings
-dontwarn io.ktor.**
-dontwarn kotlinx.serialization.**
-ignorewarnings

