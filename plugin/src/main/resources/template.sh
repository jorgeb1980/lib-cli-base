#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
$JAVA_HOME/bin/java -cp "$DIR/../libs/*" unxutils.common.EntryPoint <<COMMAND_CLASS>> $@