# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
##########################################
#  React Native 필수 보호
##########################################
-keep class com.facebook.react.** { *; }
-keep class com.facebook.hermes.** { *; }
-keep class com.facebook.soloader.** { *; }
-dontwarn com.facebook.react.**

-keepclassmembers class * {
    @com.facebook.react.bridge.ReactMethod <methods>;
}
-keepclassmembers class * {
    @com.facebook.react.uimanager.annotations.ReactProp <methods>;
}
-keepattributes *Annotation*

##########################################
#  Hermes 사용 시 필수
##########################################
-keep class com.facebook.hermes.** { *; }
-keepclassmembers class * {
    native <methods>;
}
-keep class com.facebook.proguard.annotations.DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
}

##########################################
#  WebView나 JSInterface 관련
##########################################
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

##########################################
#  Navigation 관련 (react-navigation)
##########################################
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

##########################################
#  react-native-reanimated
##########################################
-keep class com.facebook.react.turbomodule.** { *; }
-keep class com.swmansion.reanimated.** { *; }
-keep class com.swmansion.gesturehandler.** { *; }
-dontwarn com.swmansion.**

##########################################
#  react-native-gesture-handler
##########################################
-keep class com.swmansion.gesturehandler.** { *; }

##########################################
#  react-native-screens
##########################################
-keep class com.swmansion.rnscreens.** { *; }

##########################################
#  react-native-svg
##########################################
-keep class com.horcrux.svg.** { *; }

##########################################
#  react-native-vector-icons
##########################################
-keep class com.oblador.vectoricons.** { *; }

##########################################
#  react-native-skia (Shopify Skia)
##########################################
-keep class com.shopify.reactnative.skia.** { *; }

##########################################
#  react-native-pager-view
##########################################
-keep class com.reactnativepagerview.** { *; }

##########################################
#  기타
##########################################
# Victory Native (간접적으로 react-native-svg 사용 중이므로 포함 완료)
# Select Dropdown, Keyboard Aware ScrollView, Modal 등은 Java 코드 없음 → 추가 X
