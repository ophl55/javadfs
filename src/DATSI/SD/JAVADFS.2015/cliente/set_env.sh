#!/bin/sh
S="localhost"
P=$1

export SERVIDOR=$S
export PUERTO=$P

printenv | grep SERVIDOR
printenv | grep PUERTO
