<#
PowerShell helper to check download files and store for com.example.soundwave
Usage:
  .\check_downloads.ps1                      # will try to find adb on PATH
  .\check_downloads.ps1 -adbPath "C:\Users\phare\AppData\Local\Android\Sdk\platform-tools\adb.exe"
#>
param(
    [string]$adbPath = 'C:\Users\phare\AppData\Local\Android\Sdk\platform-tools\adb.exe'
)

function Find-Adb {
    param([string]$candidate)
    if ($candidate -and (Test-Path $candidate)) { return $candidate }
    try {
        $where = (where.exe adb) -join "`n"
        if ($where) { return ($where -split "`n")[0].Trim() }
    } catch {}
    return $null
}

if ($adbPath -and (Test-Path $adbPath)) {
    $adb = $adbPath
} else {
    $found = Find-Adb -candidate $adbPath
    if ($found) { $adb = $found } else { Write-Error "adb not found. Set -adbPath to your platform-tools adb.exe or add it to PATH."; exit 2 }
}

Write-Host "Using adb: $adb"

Write-Host "\n== adb version =="
& $adb version

Write-Host "\n== devices =="
& $adb devices -l

$pkg = 'com.example.soundwave'

Write-Host "\n== External app-specific Music folder (sdcard) =="
& $adb shell ls "sdcard/Android/data/$pkg/files/Music" 2>&1 | ForEach-Object { Write-Host $_ }

Write-Host "\n== downloads.json (via run-as) =="
try {
    & $adb shell "run-as $pkg cat files/downloads.json" 2>&1 | ForEach-Object { Write-Host $_ }
} catch {
    Write-Host "Could not read downloads.json with run-as. Is the app a debug build?"; Write-Host $_
}

Write-Host "\n== Internal files list (files dir) =="
try {
    & $adb shell "run-as $pkg ls -la files" 2>&1 | ForEach-Object { Write-Host $_ }
} catch {
    Write-Host "Could not list internal files with run-as."; Write-Host $_
}

Write-Host "\n== Look for .mp3 anywhere under app external/internal folders =="
& $adb shell "find /sdcard/Android/data/$pkg -type f -name '*.mp3' 2>/dev/null" 2>&1 | ForEach-Object { Write-Host $_ }
try { & $adb shell "run-as $pkg find files -type f -name '*.mp3' 2>/dev/null" 2>&1 | ForEach-Object { Write-Host $_ } } catch {}

Write-Host "\n== Recent logcat lines mentioning DownloadWorker / WorkManager / package =="
# dump last 1000 lines then filter
try {
    $log = & $adb logcat -d 2>$null | Select-String -Pattern 'DownloadWorker|WorkManager|$pkg' -AllMatches
    if ($log) { $log | ForEach-Object { Write-Host $_.Line } } else { Write-Host "No matching logcat lines found." }
} catch {
    Write-Host "Unable to read logcat: $_"
}

Write-Host "\nDone. If you want, paste the outputs here and I will help interpret them."
