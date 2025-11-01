# Render Environment Variables - EXACT VALUES

Based on your error log, here are the EXACT values you need:

## Environment Variables for Render

Go to: Render Dashboard → Your Service → Environment → Add/Edit Variables

```
SPRING_PROFILES_ACTIVE=production

SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d42s77ur433s73dv52hg-a:5432/websocket_chat_db_kqyt?sslmode=require

SPRING_DATASOURCE_USERNAME=websocket_chat_db_kqyt_user

SPRING_DATASOURCE_PASSWORD=Uj1Q7zKcfYHJAEYO0RM4r30oGG1dGpoj

JWT_SECRET=<generate-with-openssl-rand-base64-32>

JWT_EXPIRATION=86400000
```

## Key Points:

1. **SPRING_DATASOURCE_URL** - NO credentials in URL!
   - ✅ Correct: `jdbc:postgresql://host:port/database?sslmode=require`
   - ❌ Wrong: `jdbc:postgresql://user:pass@host/database`

2. **SPRING_DATASOURCE_USERNAME** - Just the username, nothing else

3. **SPRING_DATASOURCE_PASSWORD** - Just the password, nothing else

## Step-by-Step:

1. Copy each value exactly as shown above
2. Paste into Render environment variables
3. Save
4. Service will auto-redeploy
5. Check logs - should see successful database connection

