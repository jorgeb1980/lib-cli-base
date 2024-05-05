@echo off
%JAVA_HOME%\bin\java -cp "%~dp0..\libs\*" <<JVM_ARGS>> -Djava.util.logging.config.class=cli.LogUtils cli.EntryPoint <<COMMAND_CLASS>> %*