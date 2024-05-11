@echo off
<<START_COMMAND>> %JAVA_HOME%\bin\<<JAVA_COMMAND>> -cp "%~dp0..\libs\*" <<JVM_ARGS>> -Djava.util.logging.config.class=cli.LogUtils cli.EntryPoint <<COMMAND_CLASS>> %*