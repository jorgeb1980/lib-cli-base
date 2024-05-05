#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
$JAVA_HOME/bin/java -cp "$DIR/../libs/*" <<JVM_ARGS>> -Djava.util.logging.config.class=cli.LogUtils cli.EntryPoint <<COMMAND_CLASS>> $@