$ErrorActionPreference = 'Stop'
$buildDir = Join-Path $PSScriptRoot 'build/web-classes'
$jarPath = Join-Path $PSScriptRoot 'docs/UnbeatableTicTacToe.jar'
$javac = (Get-Command javac -ErrorAction Stop).Source
$jar = Join-Path (Split-Path $javac) 'jar.exe'
if (-not (Test-Path $jar)) {
    # Oracle's javapath shim does not always include jar.exe.
    $javaSettings = & java -XshowSettings:properties -version 2>&1 | Out-String
    $javaHome = [regex]::Match($javaSettings, 'java.home = (.+)').Groups[1].Value.Trim()
    $jar = Join-Path $javaHome 'bin/jar.exe'
}
New-Item -ItemType Directory -Force $buildDir | Out-Null
$sources = Get-ChildItem (Join-Path $PSScriptRoot 'src') -Filter '*.java' | ForEach-Object FullName
& $javac --release 8 -d $buildDir $sources
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
& $jar --create --file $jarPath --main-class TicTacToe -C $buildDir .
if ($LASTEXITCODE -ne 0) { throw 'JAR packaging failed.' }
Write-Host "Built $jarPath"
