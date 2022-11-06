@echo off
    rem A modification of MC ND's answer at https://stackoverflow.com/a/31358421/786740
    setlocal enableextensions disabledelayedexpansion
    set "var="

    rem Determine call origin
    setlocal enabledelayedexpansion
    call :detectDrop !cmdcmdline!
    endlocal
    if not errorlevel 1 goto :dropped

:commandLine
    rem Invoked from command line
    set "dropped="
    if "%~1"=="" goto done
    set var=%*
    set "var=%var:"=""%"
    goto :process

:dropped
    rem Invoked from explorer
    set "dropped=1"
    set "var=%cmdcmdline:"=""%"
    set "var=%var:*/c """"=%"
    set "var=%var:*"" =%"
    set "var=%var:~0,-2%"

:process
    if not defined var goto :done

    rem Adapted from dbenham's answer at https://stackoverflow.com/a/7940444/2861476

    set "var=%var:^=^^%"
    set "var=%var:&=^&%"
    set "var=%var:|=^|%"
    set "var=%var:<=^<%"
    set "var=%var:>=^>%"
    set "var=%var: =^ ^ %"
    set var=%var:""="%
    set "var=%var:"=""Q%"
    set "var=%var:  ="S"S%"
    set "var=%var:^ ^ = %"
    set "var=%var:""="%"
    setlocal enabledelayedexpansion
    set "var=!var:"Q=!"
    for %%a in ("!var:"S"S=" "!") do ( 
        if "!!"=="" endlocal

        rem Here we have a reference to the passed/dropped element
        if %%a neq "" java -jar spotvox.jar -t 24 "%%~fa"

    )
    goto :done

:detectDrop cmdcmdline
    if /i "%~1"=="%comspec%" if /i "%~2"=="/c" exit /b 0
    exit /b 1

:done    
    if defined dropped ( pause & exit )
