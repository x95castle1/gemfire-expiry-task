#!/bin/bash
# shellcheck disable=SC2155

function cleanupTargetDir() {
    rm -rf ./custom-expiry-tasks/target
}

function main() {
    cleanupTargetDir

    javac -cp "$GEMFIRE_HOME/lib/*" \
      -d ./custom-expiry-tasks/target/classes \
      custom-expiry-tasks/src/com/broadcom/expiry/*.java

    if [ $? -ne 0 ]; then
        echo "Compilation failed"
        exit 1
    fi

    pushd ./custom-expiry-tasks/target/classes || exit 1
    jar cf ../custom-expiry-tasks.jar ./com/broadcom/expiry/*.class
    popd || exit 1
}

main
