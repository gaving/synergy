#!/bin/sh

# generate javadoc documents 
# usage: ./javadoc.sh

cd ../src
javadoc -d ../doc `find net -name "CVS" -prune -or -type d -print | sed "s,/,.,g"`
