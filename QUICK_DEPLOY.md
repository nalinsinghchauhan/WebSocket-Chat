# Quick Deploy to Render.com - 5 Minute Guide

## Prerequisites Checklist
- [ ] GitHub account
- [ ] Code pushed to GitHub repository
- [ ] Render.com account (free tier works)

## Quick Steps

### 1. Push Code to GitHub (2 minutes)
```bash
git init
git add .
git commit -m "Ready for deployment"
git remote add origin <your-github-repo-url>
git push -u origin main
```

### 2. Create Database on Render (1 minute)
- Go to https://render.com
- Click "New +" → "PostgreSQL" (free tier)
- Name it: `websocket-chat-db`
- Note the connection details (you'll need them)

**IMPORTANT**: Render offers PostgreSQL, not MySQL. You have two options:

**Option A**: Switch to PostgreSQL (Recommended)
1. Update `pom.xml` - Replace MySQL dependency with:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
2. Update `application.properties` - Change dialect:
   ```properties
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

**Option B**: Use External MySQL Service
- Use PlanetScale, Railway, or Aiven for MySQL
- Use their connection string in environment variables

### 3. Deploy Web Service (2 minutes)

1. In Render dashboard, click "New +" → "Web Service"
2. Connect GitHub and select your repository
3. Configure:
   - **Name**: `websocket-chat`
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/websocket-chat-0.0.1-SNAPSHOT.jar`

4. **Add Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=production
   SPRING_DATASOURCE_URL=<from-database-settings>
   SPRING_DATASOURCE_USERNAME=<from-database-settings>
   SPRING_DATASOURCE_PASSWORD=<from-database-settings>
   JWT_SECRET=<generate-random-string>
   JWT_EXPIRATION=86400000
   ```

5. **Generate JWT_SECRET**:
   ```bash
   openssl rand -base64 32
   ```
   Copy the output as your JWT_SECRET value.

6. Click "Create Web Service"

### 4. Wait and Verify (1 minute)

- Render will build your app (takes 2-5 minutes)
- Check "Logs" tab for progress
- When status shows "Live", your app is ready!
- Visit your service URL to test

## Connection String Format

For PostgreSQL (Render):
```
jdbc:postgresql://<host>:<port>/<database>?sslmode=require
```

For MySQL (External):
```
jdbc:mysql://<host>:<port>/<database>?useSSL=true&serverTimezone=UTC
```

## Quick Troubleshooting

**Build fails?**
- Check logs for Java version (needs Java 17)
- Verify Maven commands work locally

**Database connection error?**
- Verify connection string format
- Check SSL settings (Render PostgreSQL requires SSL)

**WebSocket not working?**
- Frontend uses relative URLs (should work automatically)
- Verify service is "Live" status

## Your App URLs

After deployment:
- **Web Service**: `https://websocket-chat.onrender.com` (or your custom name)
- **WebSocket**: `wss://websocket-chat.onrender.com/ws` (automatically secure)

## Need More Details?

See [DEPLOYMENT.md](DEPLOYMENT.md) for complete step-by-step instructions.

