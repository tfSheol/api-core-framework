-injars server_http.jar
-outjars server_http_proguard.jar

-dontoptimize

-libraryjars <java.home>/lib/rt.jar

-keepattributes *Annotation*
-keepattributes *Signature*
-keepattributes *InnerClasses*

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keep,includedescriptorclasses public class !Core.** {*;}

-keep,includedescriptorclasses public class Core.Http.Map {*;}
-keep,includedescriptorclasses public class Core.Http.Code {*;}

-keep,includedescriptorclasses public abstract class Core.Http.Job {*;}
-keep,includedescriptorclasses public abstract class Core.Model {}

-keep,includedescriptorclasses public class Core.Singleton.** {*;}
-keep,includedescriptorclasses public class Core.Http.Tools {*;}
-keep,includedescriptorclasses public class Core.Http.Header {*;}
-keep,includedescriptorclasses public class Core.Http.Oauth2 {*;}
-keep,includedescriptorclasses public class Core.Http.Oauth2Model {*;}
-keep,includedescriptorclasses public class Core.Database.** {*;}

-keep,includedescriptorclasses public @interface Core.Controller {*;}
-keep,includedescriptorclasses public @interface Core.Methode {*;}
-keep,includedescriptorclasses public @interface Core.Route {*;}
-keep,includedescriptorclasses public @interface Core.Task {*;}
-keep,includedescriptorclasses public @interface Core.SQLDriver {*;}

-dontwarn Core.**
-dontwarn Plugin.**

-dontnote Core.**

-dontwarn com.**
-dontnote com.**

-dontwarn javassist.**
-dontnote javassist.**

-dontwarn javax.**
-dontnote javax.**

-dontwarn org.**
-dontnote org.**