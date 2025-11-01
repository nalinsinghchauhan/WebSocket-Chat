# Fix Database Connection URL

## Problem

The error shows:
```
JDBC URL invalid port number: Uj1Q7zKcfYHJAEYO0RM4r30oGG1dGpoj@dpg-d42s77ur433s73dv52hg-a
```

This means the JDBC URL is malformed. PostgreSQL JDBC driver does NOT accept credentials in the URL like:
```
❌ jdbc:postgresql://username:password@host/database  # WRONG!
```

## Solution

**PostgreSQL JDBC requires separate username and password properties!**

### Correct Format

The JDBC URL should be:
```
jdbc:postgresql://hostname:port/database?sslmode=require
```

And username/password should be **separate environment variables**.

## How to Fix in Render

### Step 1: Extract Components from Render's Connection String

Render gives you something like:
```
postgresql://websocket_chat_db_kqyt_user:Uj1Q7zKcfYHJAEYO0RM4r30oGG1dGpoj@dpg-d42s77ur433s73dv52hg-a:5432/websocket_chat_db_kqyt
```

Parse it:
- **Host**: `dpg-d42s77ur433s73dv52hg-a`
- **Port**: `5432` (usually, check if different)
- **Database**: `websocket_chat_db_kqyt`
- **Username**: `websocket_chat_db_kqyt_user`
- **Password**: `Uj1Q7zKcfYHJAEYO0RM4r30oGG1dGpoj`

### Step 2: Set Environment Variables Correctly

In Render dashboard → Your Service → Environment:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d42s77ur433s73dv52hg-a:5432/websocket_chat_db_kqyt?sslmode=require

SPRING_DATASOURCE_USERNAME=websocket_chat_db_kqyt_user

SPRING_DATASOURCE_PASSWORD=Uj1Q7zKcfYHJAEYO0RM4r30oGG1dGpoj
```

**Important:** 
- ✅ URL should NOT contain `username:password@`
- ✅ URL format: `jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require`
- ✅ Username and password are SEPARATE variables

### Step 3: Verify Format

Your `SPRING_DATASOURCE_URL` should look like:
```
jdbc:postgresql://dpg-xxxxx.oregon-postgres.render.com:5432/websocket_chat_xxxx?sslmode=require
```

NOT like:
```
❌ jdbc:postgresql://user:pass@host/database
❌ postgresql://user:pass@host/database
❌ jdbc:postgresql://host/database?user=...&password=...
```

## Quick Fix Steps

1. **Go to Render Dashboard** → Your Service → Environment Variables

2. **Update SPRING_DATASOURCE_URL:**
   - Remove any `username:password@` from the URL
   - Format: `jdbc:postgresql://HOST:PORT/DATABASE?sslmode=require`
   - Example: `jdbc:postgresql://dpg-d42s77ur433s73dv52hg-a:5432/websocket_chat_db_kqyt?sslmode=require`

3. **Verify SPRING_DATASOURCE_USERNAME:**
   - Should be: `websocket_chat_db_kqyt_user` (just the username, no URL)

4. **Verify SPRING_DATASOURCE_PASSWORD:**
   - Should be: `Uj1Q7zKcfYHJAEYO0RM4r30oGG1dGpoj` (just the password, no URL)

5. **Save and Redeploy**

## Example: Correct Environment Variables

```
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d42s77ur433s73dv52hg-a:5432/websocket_chat_db_kqyt?sslmode=require
SPRING_DATASOURCE_USERNAME=websocket_chat_db_kqyt_user
SPRING_DATASOURCE_PASSWORD=Uj1Q7zKcfYHJAEYO0RM4r30oGG1dGpoj
JWT_SECRET=<your-jwt-secret>
JWT_EXPIRATION=86400000
```

## Why This Happens

Render's PostgreSQL connection string uses format:
```
postgresql://user:pass@host:port/database
```

But Spring Boot/PostgreSQL JDBC driver expects:
- URL: `jdbc:postgresql://host:port/database?sslmode=require`
- Username: separate property
- Password: separate property

You need to **extract and separate** the components!

## Alternative: URL with Query Parameters (Not Recommended)

You CAN include credentials in URL using query parameters, but it's less secure:
```
jdbc:postgresql://host:port/database?user=username&password=password&sslmode=require
```

But **separate properties are preferred** for security and clarity.

