@echo off
set project=%~dp0
%ANT_ROOT%\ant -f %project% release