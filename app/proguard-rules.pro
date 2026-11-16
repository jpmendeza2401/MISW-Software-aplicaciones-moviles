# Gson: los modelos se (de)serializan por reflexión con @SerializedName.
-keep class com.example.aplicacionvinilos.data.model.** { *; }
-keep class com.example.aplicacionvinilos.data.session.** { *; }
-keepattributes Signature, *Annotation*
# Retrofit + suspend functions (Continuation en la firma genérica).
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keepattributes Exceptions, InnerClasses, EnclosingMethod
