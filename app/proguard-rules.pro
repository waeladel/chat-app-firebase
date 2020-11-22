# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#To ensure that retracing stack traces is unambiguous
-keepattributes LineNumberTable,SourceFile

#To Keep my model calsses untouched
-keepclassmembers class com.trackaty.chat.models.** { *; }
#-keep class com.trackaty.chat.models.Chat { *; }
#-keep class com.trackaty.chat.models.ChatMember {*; }
#-keep class com.trackaty.chat.models.DatabaseNotification {*; }
#-keep class com.trackaty.chat.models.FirebaseListeners {*; }
#-keep class com.trackaty.chat.models.Message {*; }
#-keep class com.trackaty.chat.models.Profile {*; }
#-keep class com.trackaty.chat.models.Relation {*; }
#-keep class com.trackaty.chat.models.Social {*; }
#-keep class com.trackaty.chat.models.SocialObj {*; }
#-keep class com.trackaty.chat.models.User {*; }
#-keep class com.trackaty.chat.models.UserId {*; }
#-keep class com.trackaty.chat.models.Variables {*; }

# matisse for gallery and camera, becuase i use Picasso as your image engine
-dontwarn com.squareup.picasso.**

#  ArthurHub / Android-Image-Cropper
-keep class androidx.appcompat.widget.** { *; }


#  To keep chirp working as normal
-keep class io.chirp.chirpsdk.ChirpSDKNative { *; }

#  hdodenhof / CircleImageView: If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {public *;}

# FirebaseUI 3P providers are optional
#  For Twitter login using firnase ui
#-keep class com.twitter.sdk.android.core** { *;}
-keep class com.twitter.sdk.android.core.identity.TwitterAuthClient { *;}
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#  For facebook login using firnase ui
-dontwarn com.facebook.**
# Keep the class names used to check for availablility
-keepnames class com.facebook.login.LoginManager

# Don't note a bunch of dynamically referenced classes
-dontnote com.google.**
-dontnote com.facebook.**
-dontnote com.squareup.okhttp.**
-dontnote okhttp3.internal.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn rx.**

# Recommended flags for Firebase Auth, storage
-keepattributes Signature
-keepattributes *Annotation*
# Recommended flags cloud masseging, functions, analytics storage Auth
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-dontwarn com.firebase.ui.auth.data.remote.**

# Retrofit config
-dontwarn retrofit.**
-dontnote retrofit2.Platform
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Exceptions

-dontwarn com.google.gson.Gson$6
