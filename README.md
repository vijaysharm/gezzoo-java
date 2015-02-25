Gezzoo - Java
==================

This is a java implementation of the gezzoo game server, using google end-points running on google app engine

# Building
Requires Constants.java
```java
package com.vijaysharma.gezzoo;

import com.google.api.server.spi.Constant;

public class Constants {
    public static final String WEB_CLIENT_ID = "From dev console: 'API & auth' > 'Credentials' CLIENT_ID";
    public static final String ANDROID_CLIENT_ID = "replace this with your Android client ID";
    public static final String IOS_CLIENT_ID = "replace this with your iOS client ID";
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
    public static final String EMAIL_SCOPE = Constant.API_EMAIL_SCOPE;
    public static final String API_EXPLORER_CLIENT_ID = Constant.API_EXPLORER_CLIENT_ID;
}

```

From root
```
# export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_71.jdk/Contents/Home/
# mkdir target/generated-sources/appengine-endpoints
# cp -R src/main/webapp/ target/generated-sources/appengine-endpoints/.
# mvn clean appengine:devserver # for development
# mvn -e appengine:update # for production
```
