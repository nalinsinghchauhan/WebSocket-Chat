# PostgreSQL Setup Guide

Your application has been switched from MySQL to PostgreSQL.

## Changes Made

### 1. pom.xml
- ✅ Removed: `mysql-connector-j`
- ✅ Added: `postgresql` driver

### 2. application.properties
- ✅ Updated connection URL to PostgreSQL format
- ✅ Changed dialect to `PostgreSQLDialect`
- ✅ Updated driver class to `org.postgresql.Driver`
- ✅ Default port changed from 3306 (MySQL) to 5432 (PostgreSQL)

## Local Development Setup

### Option 1: Using Docker (Recommended)

```bash
# Run PostgreSQL in Docker
docker run --name postgres-chat \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=websocket_chat \
  -p 5432:5432 \
  -d postgres:15

# Verify it's running
docker ps
```

Then update `application.properties` if needed:
```properties
spring.datasource.password=postgres  # Your Docker password
```

### Option 2: Install PostgreSQL Locally

**macOS (using Homebrew):**
```bash
brew install postgresql@15
brew services start postgresql@15
createdb websocket_chat
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo -u postgres createdb websocket_chat
```

**Windows:**
- Download from https://www.postgresql.org/download/windows/
- Install and create database using pgAdmin

### Option 3: Use Cloud PostgreSQL (Free Tier)

- **Supabase**: https://supabase.com (free tier available)
- **Neon**: https://neon.tech (free tier available)
- **Railway**: https://railway.app (free tier available)

## Connection String Formats

### Local Development
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/websocket_chat
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Render.com Production
Render automatically provides PostgreSQL connection string in format:
```
jdbc:postgresql://hostname:port/database?sslmode=require
```

Set via environment variable:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://hostname:port/database?sslmode=require
```

### Other Cloud Providers
Most providers use similar format with SSL:
```
jdbc:postgresql://hostname:port/database?sslmode=require
```

## Running the Application

1. **Start PostgreSQL** (if using Docker):
   ```bash
   docker start postgres-chat
   ```

2. **Update application.properties** if needed:
   ```properties
   spring.datasource.password=your_postgres_password
   ```

3. **Build and Run**:
   ```bash
   mvn clean package
   java -jar target/websocket-chat-0.0.1-SNAPSHOT.jar
   ```

   Or with Maven:
   ```bash
   mvn spring-boot:run
   ```

## Testing the Connection

The application will automatically:
- Create the database schema on first run (if `ddl-auto=update`)
- Create tables: `users`, `chat_rooms`, `messages`, `read_receipts`

You can verify by checking the logs for:
```
Hibernate: create table users (...)
```

## Differences from MySQL

| Feature | MySQL | PostgreSQL |
|---------|-------|------------|
| Port | 3306 | 5432 |
| Driver | `com.mysql.cj.jdbc.Driver` | `org.postgresql.Driver` |
| Dialect | `MySQLDialect` | `PostgreSQLDialect` |
| Connection String | `jdbc:mysql://...` | `jdbc:postgresql://...` |
| SSL Mode | `useSSL=false` | `sslmode=require` |

## Deployment to Render.com

Render.com natively supports PostgreSQL, so no additional configuration needed!

1. Create PostgreSQL database on Render
2. Environment variables will be automatically set
3. Connection string includes SSL automatically

## Troubleshooting

### Connection Refused
- Verify PostgreSQL is running: `docker ps` or `brew services list`
- Check port: Should be 5432, not 3306
- Check firewall settings

### Authentication Failed
- Verify username/password in `application.properties`
- Default PostgreSQL user is usually `postgres`

### SSL Required
For production (Render, etc.), add to connection string:
```
?sslmode=require
```

### Schema Not Created
- Check `spring.jpa.hibernate.ddl-auto=update` is set
- Check database exists: `psql -l` or `\l` in psql

## Useful PostgreSQL Commands

```bash
# Connect to PostgreSQL
psql -U postgres -d websocket_chat

# List databases
psql -U postgres -c "\l"

# List tables
psql -U postgres -d websocket_chat -c "\dt"

# Drop database (if needed)
psql -U postgres -c "DROP DATABASE websocket_chat;"

# Create database
psql -U postgres -c "CREATE DATABASE websocket_chat;"
```

## Migration Complete! ✅

Your application is now configured for PostgreSQL and ready for:
- Local development
- Render.com deployment
- Other PostgreSQL-compatible platforms

