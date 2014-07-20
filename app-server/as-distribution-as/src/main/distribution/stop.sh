#!/bin/sh
BASEDIR=$(dirname $0)
pkill -U felix -u felix java
rm -rf $BASEDIR/felix*