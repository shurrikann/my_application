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
# --- 1. 基础指令 ---
-optimizationpasses 5                         # 代码优化迭代次数
-dontusemixedcaseclassnames                   # 混淆时不生成大小写混合的类名（Windows 必选）
-dontskipnonpubliclibraryclasses              # 不跳过非公共库的类
-verbose                                      # 打印混淆详细日志

# --- 2. 保留四大组件和系统类 ---
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# --- 3. 保留自定义 View ---
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# --- 4. 重要：保留 Native 方法 (JNI) ---
-keepclasseswithmembernames class * {
    native <methods>;
}

# --- 5. 重要：保留实体类 (Bean/Model) ---
# 如果你的实体类被 Gson/FastJson 解析，必须保留，否则字段名变了，JSON 就解析失败了
-keep class com.your.package.model.** { *; }