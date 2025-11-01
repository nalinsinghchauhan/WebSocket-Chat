# Deployment Guide for Render.com

This guide will walk you through deploying your WebSocket Chat application on Render.com.

## Prerequisites

1. A GitHub account
2. Your project pushed to a GitHub repository
3. A Render.com account (free tier works)

## Step-by-Step Deployment Instructions

### Step 1: Prepare Your Project for Git

1. **Initialize Git (if not already done)**
   ```bash
   git init
   git add .
   git commit -m "Initial commit"
   ```

2. **Create a GitHub Repository**
   - Go to GitHub and create a new repository
   - Don't initialize with README, .gitignore, or license
   - Copy the repository URL

3. **Push Your Code to GitHub**
   ```bash
   git remote add origin <your-github-repo-url>
   git branch -M main
   git push -u origin main
   ```

### Step 2: Create MySQL Database on Render

1. **Log in to Render.com**
   - Go to https://render.com and sign up/login

2. **Create a New MySQL Database**
   - Click "New +" → "PostgreSQL" (Render offers PostgreSQL, but we'll use MySQL alternative)
   - **OR** use Render's MySQL (if available) or use a different service like:
     - **PlanetScale** (recommended for MySQL)
     - **Railway** (offers MySQL)
     - **Aiven** (offers MySQL)

   **Alternative: Use PostgreSQL instead of MySQL**
   - If you want to use Render's native PostgreSQL, you'll need to:
     - Update dependencies in `pom.xml` (change MySQL to PostgreSQL)
     - Update JPA dialect in `application.properties`

### Step 3: Update Application Configuration

The application is already configured to use environment variables. Make sure:

- `application.properties` uses `${VARIABLE_NAME:default_value}` syntax
- Server port uses `${PORT:8080}` (Render sets PORT automatically)
- Database credentials come from environment variables

### Step 4: Deploy Web Service on Render

#### Option A: Using Render Dashboard (Recommended for First Time)

1. **Create a New Web Service**
   - In Render dashboard, click "New +" → "Web Service"
   - Connect your GitHub account if not already connected
   - Select your repository

2. **Configure the Service**
   - **Name**: `websocket-chat` (or your preferred name)
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/websocket-chat-0.0.1-SNAPSHOT.jar`

3. **Set Environment Variables**
   Click "Environment" tab and add:
   ```
   SPRING_PROFILES_ACTIVE=production
   SPRING_DATASOURCE_URL=<your-database-connection-string>
   SPRING_DATASOURCE_USERNAME=<database-username>
   SPRING_DATASOURCE_PASSWORD=<database-password>
   JWT_SECRET=<generate-a-long-random-string-here>
   JWT_EXPIRATION=86400000
   ```

   **To generate a secure JWT_SECRET:**
   ```bash
   # On Linux/Mac:
   openssl rand -base64 32
   
   # Or use an online generator
   ```

4. **Important Settings**
   - **Region**: Choose closest to your users
   - **Branch**: `main` (or your default branch)
   - **Auto-Deploy**: `Yes` (deploys on every push)

5. **Create Service**
   - Click "Create Web Service"
   - Render will start building and deploying

#### Option B: Using render.yaml (Infrastructure as Code)

If you prefer using `render.yaml`:

1. The `render.yaml` file is already created in your project root
2. Push your code to GitHub
3. In Render dashboard:
   - Go to "Blueprint" section
   - Click "New Blueprint"
   - Connect your GitHub repository
   - Render will automatically detect `render.yaml` and create services

**Note**: The render.yaml might need adjustment if you're using external MySQL instead of Render's database.

### Step 5: Configure Database Connection

If using Render's PostgreSQL (instead of MySQL):

1. **Update pom.xml** - Change MySQL dependency:
   ```xml
   <!-- Remove MySQL -->
   <!-- <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
   </dependency> -->
   
   <!-- Add PostgreSQL -->
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

2. **Update application.properties**:
   ```properties
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

3. **Update connection string** - Render's PostgreSQL connection string format:
   ```
   jdbc:postgresql://<host>:<port>/<database>?sslmode=require
   ```

### Step 6: Verify Deployment

1. **Check Build Logs**
   - In Render dashboard, go to your service
   - Click "Logs" tab
   - Verify build completes successfully

2. **Check Service Status**
   - Service should show "Live" status
   - Note your service URL (e.g., `https://websocket-chat.onrender.com`)

3. **Test the Application**
   - Visit your service URL
   - Try registering a new user
   - Test chat functionality

### Step 7: WebSocket Configuration on Render

**Important**: Render supports WebSockets, but ensure:

1. **No Reverse Proxy Issues**
   - Render handles this automatically
   - WebSocket connections should work out of the box

2. **CORS Configuration**
   - Already set to `*` in `application.properties`
   - Update if you need specific origins:
     ```properties
     spring.web.cors.allowed-origins=https://your-frontend-domain.com
     ```

3. **WebSocket Endpoint**
   - Your WebSocket endpoint is at: `wss://your-service.onrender.com/ws`
   - Frontend should connect using: `wss://` (secure WebSocket) not `ws://`

### Step 8: Update Frontend for Production

If your frontend HTML uses hardcoded URLs, update `index.html`:

```javascript
// Instead of:
const socket = new SockJS('/ws?token=' + jwtToken);

// Use:
const socket = new SockJS(`${window.location.protocol === 'https:' ? 'https:' : 'http:'}//${window.location.host}/ws?token=${jwtToken}`);
```

Or detect automatically:
```javascript
const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
const socket = new SockJS(`${protocol}//${window.location.host}/ws?token=${jwtToken}`);
```

### Step 9: Set Up Custom Domain (Optional)

1. In Render dashboard, go to your service
2. Click "Settings" → "Custom Domains"
3. Add your domain and follow DNS configuration instructions

## Troubleshooting

### Build Fails

- **Check Java version**: Ensure Maven uses Java 17
- **Check logs**: Review build logs for specific errors
- **Verify dependencies**: Ensure all dependencies are available

### Database Connection Issues

- **Verify credentials**: Double-check environment variables
- **Check connection string**: Ensure format is correct
- **SSL requirements**: Render databases may require SSL

### WebSocket Not Working

- **Check protocol**: Use `wss://` for HTTPS sites
- **Verify endpoint**: Ensure WebSocket endpoint is accessible
- **Check CORS**: Verify CORS settings allow your origin

### Service Crashes

- **Check logs**: Review application logs in Render dashboard
- **Verify environment variables**: Ensure all required vars are set
- **Check database**: Ensure database is running and accessible

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database connection string | `jdbc:mysql://host:3306/db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `websocket_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `secure_password` |
| `JWT_SECRET` | Secret key for JWT tokens | `long-random-string` |
| `JWT_EXPIRATION` | JWT token expiration (ms) | `86400000` |
| `PORT` | Server port (auto-set by Render) | `8080` |

## Additional Tips

1. **Free Tier Limitations**
   - Services spin down after 15 minutes of inactivity
   - First request after spin-down may be slow
   - Consider upgrading for production use

2. **Performance**
   - Use connection pooling for database
   - Monitor service usage in Render dashboard
   - Set up alerts for service issues

3. **Security**
   - Never commit secrets to Git
   - Use Render's environment variables
   - Rotate JWT_SECRET periodically

4. **Monitoring**
   - Use Render's built-in metrics
   - Set up health checks
   - Monitor error rates

## Support Resources

- Render Documentation: https://render.com/docs
- Spring Boot Deployment: https://spring.io/guides/gs/spring-boot-for-azure-web-apps/
- WebSocket Guide: https://render.com/docs/websockets

