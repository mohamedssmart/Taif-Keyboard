Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Taif Keyboard - GitHub Upload Helper  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check git status
if (!(Test-Path .git)) {
    Write-Host "Initializing local git repository..." -ForegroundColor Yellow
    git init
    git branch -M main
}

# Create .gitignore
$gitignoreContent = @"
# Gradle files
.gradle/
/build/
/*/build/
local.properties
*.apk

# iOS files
.DS_Store
DerivedData/
*.xcworkspace
xcuserdata/
Pods/
build/
TaifiOS/TaifiOS.xcodeproj
"@
Set-Content -Path .gitignore -Value $gitignoreContent

# Add files
Write-Host "Staging files..." -ForegroundColor Yellow
git add .

# Commit
Write-Host "Committing files..." -ForegroundColor Yellow
git commit -m "Initial commit for Taif Keyboard (Android & iOS)"

# Get URL
Write-Host ""
Write-Host "Please create a new repository on GitHub (https://github.com/new)" -ForegroundColor Green
Write-Host "Then, enter your repository URL (e.g., https://github.com/yourusername/taif.git):" -ForegroundColor Green
$repoUrl = Read-Host "GitHub Repo URL"

if ([string]::IsNullOrEmpty($repoUrl)) {
    Write-Host "Error: Repository URL cannot be empty." -ForegroundColor Red
    Exit
}

# Set remote and push
git remote remove origin 2>$null
git remote add origin $repoUrl
Write-Host "Pushing code to GitHub..." -ForegroundColor Yellow
git push -u origin main

Write-Host ""
Write-Host "Done! Your code is now on GitHub." -ForegroundColor Green
Write-Host "Go to your repository and click the 'Actions' tab to see the iOS build running!" -ForegroundColor Green
