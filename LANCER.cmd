@echo off
cd /d "%~dp0"
for /d %%J in ("outils\java8\*") do "%%J\bin\java.exe" -cp apres\bin library.app.LibraryApp
pause
