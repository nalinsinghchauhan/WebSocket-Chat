# Render.com Environment Variables Guide

## Where to Get Database Credentials

### Step 1: Create PostgreSQL Database

1. Go to Render dashboard → "New +" → "PostgreSQL"
2. Name it: `websocket-chat-db`
3. Select "Free" plan
4. Click "Create Database"
5. Wait for database to be created (takes 1-2 minutes)

### Step 2: Get Connection Information

1. **Click on your database** in the Render dashboard
2. Look for the **"Connections"** section (usually at the top)
3. You'll see:
   - **Internal Database URL**: Something like `postgresql://username:password@hostname:port/database`
   - **External Database URL**: For connections from outside Render
   - **Hostname**: e.g., `dpg-xxxxx-a.oregon-postgres.render.com`
   - **Port**: Usually `5432`
   - **Database Name**: e.g., `websocket_chat_xxxx`
   - **Username**: Usually the same as database name or `websocket_user`
   - **Password**: Click "Show" to reveal

### Step 3: Convert to Environment Variables

Render gives you a PostgreSQL URL like:
```
postgresql://websocket_user:abc123xyz@dpg-xxxxx-a.oregon-postgres.render.com:5432/websocket_chat_abcd
```

**For Spring Boot, you need:**

#### SPRING_DATASOURCE_URL
Convert the PostgreSQL URL to JDBC format:
```
jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/websocket_chat_abcd?sslmode=require
```

**Formula:**
- Take the hostname and port from Internal Database URL
- Take the database name from Internal Database URL
- Format: `jdbc:postgresql://<hostname>:<port>/<database>?sslmode=require`

**Example:**
```
Internal URL: postgresql://user:pass@dpg-abc123.oregon-postgres.render.com:5432/websocket_chat_xyz
JDBC URL:    jdbc:postgresql://dpg-abc123.oregon-postgres.render.com:5432/websocket_chat_xyz?sslmode=require
```

#### SPRING_DATASOURCE_USERNAME
Extract from the Internal Database URL (the part before `:`)
- From: `postgresql://websocket_user:password@host`
- Use: `websocket_user`

#### SPRING_DATASOURCE_PASSWORD
Extract from the Internal Database URL (between `:` and `@`)
- Click "Show" next to password in Render dashboard
- Copy the password exactly (it's usually a long random string)

## Complete Example

**What Render shows:**
```
Internal Database URL:
postgresql://websocket_chat_user:AbC123XyZ789@dpg-abc123def456-a.oregon-postgres.render.com:5432/websocket_chat_xyz789

Hostname: dpg-abc123def456-a.oregon-postgres.render.com
Port: 5432
Database: websocket_chat_xyz789
Username: websocket_chat_user
Password: AbC123XyZ789 (click "Show" to see)
```

**What you put in Environment Variables:**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-abc123def456-a.oregon-postgres.render.com:5432/websocket_chat_xyz789?sslmode=require
SPRING_DATASOURCE_USERNAME=websocket_chat_user
SPRING_DATASOURCE_PASSWORD=AbC123XyZ789
```

## Quick Reference: Environment Variables

| Variable | Where to Get | Example |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Convert Internal Database URL to JDBC format | `jdbc:postgresql://host:port/db?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | From Internal Database URL (before `:`) | `websocket_user` |
| `SPRING_DATASOURCE_PASSWORD` | Click "Show" in database settings | `long-random-string` |
| `JWT_SECRET` | Generate with: `openssl rand -base64 32` | `random-32-char-string` |
| `JWT_EXPIRATION` | Set to: `86400000` | `86400000` (24 hours) |
| `SPRING_PROFILES_ACTIVE` | Set to: `production` | `production` |

## Alternative: Using Render's Auto-Connection

Render can automatically link your database to your web service:

1. When creating the web service, scroll to "Environment Variables"
2. Instead of manually entering database vars, click "Link Database"
3. Select your PostgreSQL database
4. Render will automatically add:
   - `DATABASE_URL` (but Spring expects `SPRING_DATASOURCE_URL`)
   
**You still need to:**
- Add `SPRING_DATASOURCE_URL` and convert from `DATABASE_URL`
- Or use Render's connection string format

## Troubleshooting

### "Environment: Java" Not Found
- Render changed their interface
- Use "Web Service" instead - it auto-detects Java from `pom.xml`
- Or select "Docker" if you have a Dockerfile

### Can't Find Database Settings
- Make sure database is created and shows "Available" status
- Click on the database name (not the service)
- Look for "Connections" or "Connection Info" section
- If you see "Internal Database URL", that's what you need

### Connection String Format Wrong
- Must use JDBC format: `jdbc:postgresql://...`
- Must include `?sslmode=require` for SSL
- Remove `postgresql://` prefix, add `jdbc:postgresql://` prefix

### Still Confused?
Take a screenshot of your database "Connections" section and:
- Look for "Internal Database URL"
- Extract: hostname, port, database, username, password
- Convert to JDBC format as shown above

