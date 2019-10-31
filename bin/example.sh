#!/usr/bin/env sh

git_base_path=$(git rev-parse --show-toplevel)

$git_base_path/target/scala-2.12/authorizer.jar < $git_base_path/bin/input
