# Render Build Fix: "mvn: command not found"

## Problem
Render is detecting your project as Node.js instead of Java/Maven, causing:
```
bash: line 1: mvn: command not found
```

## Solution 1: Add system.properties (Recommended) ✅

I've created a `system.properties` file that tells Render to use Java 17:

```properties
java.runtime.version=17
```

**This file is already created in your project.** Just commit and push:

```bash
git add system.properties
git commit -m "Add system.properties for Java runtime"
git push origin main
```

Render will automatically detect this and use Java instead of Node.js.

## Solution 2: Update Render Service Settings

If Solution 1 doesn't work, manually update in Render dashboard:

1. Go to your service in Render dashboard
2. Click "Settings"
3. Find "Build & Deploy" section
4. Ensure:
   - **Runtime**: `Java` or `Maven` (not Node.js)
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/websocket-chat-0.0.1-SNAPSHOT.jar`

## Solution 3: Use Dockerfile (Alternative)

If Maven still isn't available, create a Dockerfile:

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/websocket-chat-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Then in Render:
- **Environment**: `Docker`
- Build and start commands are automatic

## Solution 4: Use render.yaml (Infrastructure as Code)

The `render.yaml` file I created should work, but make sure:
- You're using "Blueprint" deployment method
- Or manually create service and ensure runtime is set correctly

## Verify Fix

After deploying with `system.properties`:

1. Check build logs - should see:
   ```
   ==> Using Java version 17
   ==> Running build command 'mvn clean package -DskipTests'...
   ```

2. Should NOT see:
   ```
   ==> Using Node.js version 22.16.0
   ```

## Quick Fix Steps

1. ✅ `system.properties` file is already created
2. Commit and push:
   ```bash
   git add system.properties
   git commit -m "Add system.properties for Java runtime"
   git push origin main
   ```
3. Render will auto-redeploy
4. Check logs - should now use Java/Maven

## Still Having Issues?

If `system.properties` doesn't work:

1. **Check Render Service Settings:**
   - Go to Settings → Build & Deploy
   - Force Runtime to "Java" or "Maven"

2. **Use Dockerfile** (Solution 3 above)

3. **Contact Render Support:**
   - Sometimes they need to manually set the runtime

## Files Created

- ✅ `system.properties` - Tells Render to use Java 17
- This should fix the issue automatically on next deploy

