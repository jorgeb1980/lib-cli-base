@echo off
%JAVA_HOME%\bin\java -cp "%~dp0..\libs\*" cli.EntryPoint <<COMMAND_CLASS>> %*