@echo off
%JAVA_HOME%\bin\java -cp "%~dp0..\libs\*" unxutils.common.EntryPoint <<COMMAND_CLASS>> %*