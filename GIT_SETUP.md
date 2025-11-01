# Git Authentication Fix for GitHub

## Problem
You're getting a 403 error because macOS Keychain has stored credentials for a different GitHub account (NSC23).

## Solution Options

### Option 1: Clear Keychain Credentials (Recommended)

1. **Clear old credentials:**
   ```bash
   git credential-osxkeychain erase
   host=github.com
   protocol=https
   ```
   (Press Enter twice after pasting)

2. **Or use Keychain Access:**
   - Open "Keychain Access" app
   - Search for "github.com"
   - Delete entries for "github.com"

3. **Push again:**
   ```bash
   git push -u origin main
   ```
   - GitHub will prompt for credentials
   - Enter your GitHub username (nalinsinghchauhan)
   - Use a Personal Access Token (not password) - see below

### Option 2: Use Personal Access Token (Recommended for HTTPS)

1. **Create a Personal Access Token:**
   - Go to GitHub.com → Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Click "Generate new token (classic)"
   - Name: "Render Deployment"
   - Scopes: Check `repo` (all)
   - Generate token
   - **Copy the token immediately** (you won't see it again)

2. **Update remote URL to include username:**
   ```bash
   git remote set-url origin https://nalinsinghchauhan@github.com/nalinsinghchauhan/WebSocket-Chat.git
   ```

3. **Push (use token as password):**
   ```bash
   git push -u origin main
   ```
   - Username: `nalinsinghchauhan`
   - Password: `<paste your personal access token>`

### Option 3: Switch to SSH (Best for Long-term)

1. **Check if you have SSH keys:**
   ```bash
   ls -la ~/.ssh/id_*.pub
   ```

2. **If no SSH key, generate one:**
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   # Press Enter to accept defaults
   ```

3. **Add SSH key to GitHub:**
   ```bash
   cat ~/.ssh/id_ed25519.pub
   # Copy the output
   ```
   - Go to GitHub.com → Settings → SSH and GPG keys
   - Click "New SSH key"
   - Paste the key and save

4. **Update remote to use SSH:**
   ```bash
   git remote set-url origin git@github.com:nalinsinghchauhan/WebSocket-Chat.git
   ```

5. **Push:**
   ```bash
   git push -u origin main
   ```

## Quick Fix (Choose One)

### Fastest: Use Personal Access Token
```bash
# Update remote URL
git remote set-url origin https://nalinsinghchauhan@github.com/nalinsinghchauhan/WebSocket-Chat.git

# Clear old credentials
git credential-osxkeychain erase <<EOF
host=github.com
protocol=https
EOF

# Push (will prompt for token)
git push -u origin main
# Username: nalinsinghchauhan
# Password: <paste personal access token>
```

## Verify Setup

After fixing, verify:
```bash
git remote -v
git push -u origin main
```

If successful, you should see your code pushing to GitHub!

