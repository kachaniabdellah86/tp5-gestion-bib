param([string]$JavaHome)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
if (-not $JavaHome) {
    $localJdk = Get-ChildItem -LiteralPath "$PSScriptRoot/outils/java8" -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($localJdk) { $JavaHome = $localJdk.FullName } else { $JavaHome = $env:JAVA_HOME }
}
if (-not $JavaHome -or -not (Test-Path -LiteralPath "$JavaHome/bin/javac.exe")) {
    throw 'Indiquer un JDK 8 : .\verifier.ps1 -JavaHome C:\chemin\jdk8'
}
$java = "$JavaHome/bin/java.exe"
$javac = "$JavaHome/bin/javac.exe"
$jdeps = "$JavaHome/bin/jdeps.exe"
New-Item -ItemType Directory -Force -Path resultats | Out-Null
$ErrorActionPreference = 'Continue'
& $java -version 2>&1 | ForEach-Object { "$PSItem" } | Set-Content resultats/java-version.txt -Encoding UTF8
$ErrorActionPreference = 'Stop'
foreach ($version in @('avant','apres','controle_sans_packages')) {
    $bin = "$PSScriptRoot/$version/bin"
    # Supprimer seulement les anciens fichiers compiles de cette version du projet.
    if (Test-Path -LiteralPath $bin) {
        $resolved = [IO.Path]::GetFullPath($bin)
        if (-not $resolved.StartsWith($PSScriptRoot + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) { throw 'Chemin de compilation inattendu' }
        Get-ChildItem -LiteralPath $bin -Recurse -File -Filter *.class | Remove-Item -Force
    }
    New-Item -ItemType Directory -Force -Path $bin | Out-Null
    $sources = @(Get-ChildItem "$version/src" -Recurse -Filter *.java | ForEach-Object FullName)
    & $javac -encoding UTF-8 -source 1.8 -target 1.8 -d $bin @sources
    if ($LASTEXITCODE -ne 0) { throw "Compilation echouee : $version" }
    $classes = @(Get-ChildItem -LiteralPath $bin -Recurse -Filter *.class | Sort-Object FullName | ForEach-Object FullName)
    $metrics = & $java -cp "$bin;$PSScriptRoot/outils/ckjm-1.8.jar" gr.spinellis.ckjm.MetricsFilter @classes 2> "resultats/ckjm_${version}_erreurs.txt"
    if ($LASTEXITCODE -ne 0 -or (Get-Item "resultats/ckjm_${version}_erreurs.txt").Length -gt 0) { throw "Analyse CKJM echouee : $version" }
    $metrics | Sort-Object | Set-Content "resultats/metrics_$version.txt" -Encoding UTF8
    if ($version -ne 'controle_sans_packages') {
        & $java -cp $bin library.app.LibraryApp | Set-Content "resultats/execution_$version.txt" -Encoding UTF8
        if ($LASTEXITCODE -ne 0) { throw "Execution echouee : $version" }
        & $jdeps -verbose:class -filter:none -cp $bin $bin | ForEach-Object { $_.TrimEnd() } | Set-Content "resultats/dependances_$version.txt" -Encoding UTF8
        if ($LASTEXITCODE -ne 0) { throw "Analyse des dependances echouee" }
    }
}
New-Item -ItemType Directory -Force -Path apres/test-bin | Out-Null
& $javac -encoding UTF-8 -source 1.8 -target 1.8 -cp apres/bin -d apres/test-bin apres/tests/LibraryTest.java
if ($LASTEXITCODE -ne 0) { throw 'Compilation des tests echouee' }
$testOutput = & $java -cp 'apres/bin;apres/test-bin' LibraryTest
if ($LASTEXITCODE -ne 0) { throw 'Tests echoues' }
$testOutput | Set-Content resultats/tests.txt -Encoding UTF8
$testOutput | Write-Output
$before = Get-Content resultats/execution_avant.txt -Raw
$after = Get-Content resultats/execution_apres.txt -Raw
if ($before -cne $after) { throw 'La sortie de demonstration a change' }
'OK - sortie de demonstration identique avant/apres' | Set-Content resultats/comparaison_execution.txt -Encoding UTF8
$originalMetrics = Get-Content resultats/metrics_avant.txt | ForEach-Object { $_ -replace 'library\.(app|model|service)\.', '' } | Sort-Object
$flatMetrics = Get-Content resultats/metrics_controle_sans_packages.txt | Sort-Object
if (Compare-Object $originalMetrics $flatMetrics) { throw 'Le controle des packages differe' }
'OK - memes metriques CKJM avec et sans packages (controle reconstruit)' | Set-Content resultats/controle_packages.txt -Encoding UTF8
Write-Host 'Compilation, CKJM et tests termines. Voir resultats/.'
