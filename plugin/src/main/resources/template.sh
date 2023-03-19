#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
$JAVA_HOME/bin/java -cp "$DIR/../libs/*" cli.EntryPoint <<COMMAND_CLASS>> $@