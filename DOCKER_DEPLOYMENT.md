# Docker Deployment Guide for Render.com

## Overview

Using Docker ensures consistent builds and eliminates runtime detection issues. This is the most reliable way to deploy Java/Spring Boot applications on Render.

## Files Created

✅ **Dockerfile** - Multi-stage build that:
- Builds your app with Maven
- Creates a lightweight runtime image with JRE only
- Runs as non-root user for security
- Includes health checks

✅ **.dockerignore** - Excludes unnecessary files from Docker build

✅ **render.yaml** - Updated to use Docker

## Dockerfile Explanation

### Multi-Stage Build

**Stage 1 (Build):**
- Uses `maven:3.9-eclipse-temurin-17` image
- Downloads dependencies (cached layer)
- Builds the JAR file

**Stage 2 (Runtime):**
- Uses lightweight `eclipse-temurin:17-jre` image
- Only includes JRE (not full JDK)
- Runs as non-root user
- Much smaller final image (~150MB vs ~500MB)

## Deployment on Render

### Option 1: Using render.yaml (Automatic)

1. Push your code with Dockerfile to GitHub
2. In Render dashboard:
   - Go to "Blueprints"
   - Click "New Blueprint"
   - Connect your repository
   - Render will detect `render.yaml` and use Docker

### Option 2: Manual Setup

1. **Create Web Service:**
   - Click "New +" → "Web Service"
   - Connect GitHub repository

2. **Configure:**
   - **Name**: `websocket-chat`
   - **Environment**: `Docker` ⭐ (This is the key!)
   - **Dockerfile Path**: `./Dockerfile` (or leave default)
   - **Docker Context**: `.` (or leave default)

3. **Environment Variables:**
   Add all the same variables as before:
   ```
   SPRING_PROFILES_ACTIVE=production
   SPRING_DATASOURCE_URL=<from-database>
   SPRING_DATASOURCE_USERNAME=<from-database>
   SPRING_DATASOURCE_PASSWORD=<from-database>
   JWT_SECRET=<generate-random-string>
   JWT_EXPIRATION=86400000
   ```

4. **Port:**
   - Render auto-detects port 8080 from Dockerfile
   - No need to set manually

## Advantages of Docker Approach

✅ **Reliable**: Always uses Java/Maven (no detection issues)
✅ **Faster Builds**: Dependency caching
✅ **Smaller Images**: Multi-stage builds reduce size
✅ **Security**: Runs as non-root user
✅ **Consistent**: Same environment everywhere
✅ **Portable**: Works on any Docker platform

## Testing Locally

Before deploying, test the Docker image locally:

```bash
# Build the image
docker build -t websocket-chat .

# Run locally
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/websocket_chat \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e JWT_SECRET=test-secret \
  websocket-chat
```

Visit: http://localhost:8080

## Build Time Optimization

The Dockerfile uses layer caching:
1. **pom.xml copied first** - Dependencies download cached
2. **Source code copied last** - Only rebuilds when code changes

This means:
- First build: ~3-5 minutes
- Subsequent builds: ~30 seconds (if only code changed)

## Troubleshooting

### Build Fails: "Cannot find pom.xml"
- Ensure Dockerfile is in project root
- Check Docker context is set to `.` (current directory)

### Build Fails: "Cannot find src directory"
- Ensure `.dockerignore` doesn't exclude `src/`
- Check Docker context includes source files

### Application Fails to Start
- Check environment variables are set correctly
- Verify database is accessible from Render network
- Check logs: `docker logs <container-id>`

### Port Issues
- Dockerfile exposes port 8080
- Render auto-maps to external port
- No PORT environment variable needed (unlike Node.js)

## Health Check

The Dockerfile includes a health check, but Spring Boot Actuator might not be configured. To enable:

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Add to `application.properties`:
```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.enabled=true
```

Or remove health check from Dockerfile if not using Actuator:
```dockerfile
# Remove this line if not using health checks
# HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
#   CMD curl -f http://localhost:8080/actuator/health || exit 1
```

## Next Steps

1. ✅ Dockerfile created
2. ✅ .dockerignore created  
3. ✅ render.yaml updated
4. Commit and push:
   ```bash
   git add Dockerfile .dockerignore render.yaml
   git commit -m "Add Dockerfile for Render deployment"
   git push origin main
   ```
5. Update Render service to use Docker
6. Deploy!

## Comparison: Docker vs system.properties

| Feature | Docker | system.properties |
|---------|--------|-------------------|
| Reliability | ✅ Always works | ⚠️ Depends on Render detection |
| Build Speed | ✅ Fast (caching) | ✅ Fast |
| Image Size | ✅ Small (multi-stage) | N/A |
| Portability | ✅ Works anywhere | ❌ Render only |
| Debugging | ✅ Test locally | ⚠️ Must deploy to test |

**Recommendation**: Use Docker for production deployments! 🐳

