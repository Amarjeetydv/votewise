# VoteWise Security Guidelines

## Critical: SMTP Credentials Exposure

**⚠️ IMPORTANT**: If this repository was ever pushed to GitHub with actual SMTP credentials in `config/smtp.properties`, those credentials are **compromised and must be rotated immediately**.

### Immediate Actions Required

1. **Rotate Gmail App Password**:
   - Go to https://myaccount.google.com/security
   - Find "App passwords" under 2-Step Verification
   - Delete the old VoteWise app password
   - Generate a **new** app password
   - Update `config/smtp.properties` ONLY in your local environment (not git)

2. **Never Commit Credentials**:
   - ✅ `config/smtp.properties` is now in `.gitignore`
   - ✅ Never add `config/`, `.env`, `secrets/`, or `*.key` files to git
   - ✅ Use environment variables or admin UI only

---

## Secure Credential Setup

### Option 1: Environment Variables (Recommended for CI/CD & Local Dev)

Set before running the app:

```bash
# Windows PowerShell
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"
$env:SMTP_USER = "your-email@gmail.com"
$env:SMTP_PASS = "your-app-password-only-16-chars"
$env:SMTP_FROM = "your-email@gmail.com"
```

```bash
# Linux/Mac
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"
export SMTP_USER="your-email@gmail.com"
export SMTP_PASS="your-app-password-only-16-chars"
export SMTP_FROM="your-email@gmail.com"
```

### Option 2: Admin Dashboard (Runtime Configuration)

1. Launch VoteWise app
2. Admin Portal → SMTP Settings
3. Enter credentials
4. Click **Test SMTP** to verify before saving
5. Settings stored locally in `config/smtp.properties` (not committed)

**⚠️ Warning**: After saving via admin UI, `config/smtp.properties` will contain credentials on disk. Ensure:
- The `config/` folder is in `.gitignore` ✅
- No one pushes the config folder to git
- Only authorized admins have filesystem access

### Option 3: Production Deployment

For production servers:
- Use Docker secrets or orchestration platform (Kubernetes Secrets, AWS Secrets Manager)
- Never hardcode or store credentials in config files
- Use secure vaults for credential rotation
- Implement audit logging for all credential access

---

## Code-Level Security

### Best Practices Implemented

✅ **Sensitive Data Never Logged**: SMTP credentials are not logged to console or files
✅ **Hashed Passwords**: Voter and admin passwords use PBKDF2-SHA256 hashing
✅ **No Credentials in Source Code**: Credentials only come from environment variables or admin UI
✅ **Secure Email**: OTP delivery uses TLS 1.2+ with validated SMTP certificates
✅ **Read-Only CSV Exports**: Election results are marked read-only to prevent tampering

### What NOT to Do

❌ Don't hardcode credentials in Java source files
❌ Don't commit config files with real passwords to version control
❌ Don't use plain-text password storage
❌ Don't log authentication attempts with full credentials
❌ Don't share SMTP credentials via email or Slack

---

## Credential Rotation Schedule

| Credential | Rotation | Trigger |
|-----------|----------|---------|
| SMTP App Password | Every 90 days | Security best practice |
| Database Password | Every 180 days | After key personnel changes |
| Admin Accounts | When staff leaves | Immediate |

---

## Audit Trail

If credentials were exposed on GitHub:

1. **Identify**: Check git log for `config/smtp.properties` commits
   ```bash
   git log --all -- config/smtp.properties
   ```

2. **Revoke**: Rotate the exposed credential immediately (see above)

3. **Clean History** (if necessary):
   ```bash
   git filter-branch --tree-filter 'rm -f config/smtp.properties' HEAD
   git push origin --force-with-lease
   ```
   ⚠️ Force push affects all clones; notify team

4. **Verify**: Confirm `config/` is in `.gitignore` before next push

---

## Questions or Concerns?

- Contact your security team if credentials may have been viewed by unauthorized parties
- Review GitHub audit logs for any suspicious access
- Consider enabling branch protection rules to prevent direct pushes

---

**Last Updated**: May 11, 2026
**Status**: ✅ Credentials cleared, .gitignore updated
