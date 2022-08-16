#!/bin/bash

#
#	Statistics bash script for DSOL code statistics.
#
#	(c) copyright 2004 Delft University of Technology, the Netherlands.
#	See for project information www.simulation.tudelft.nl
#	License of use: General Public License (GPL), no warranty
#
#	version 1.0 02.08.2004
#	author Peter Jacobs <p.h.m.jacobs@tbm.tudelft.nl>
#

### ENVIRONMENT
ROOT_DIR=/c/development
FILES_TOT=0
LINES_TOT=0

### STATISTICS FUNCTION
stats()
{
	cd $ROOT_DIR/$1
	local FILES=`find ./ -name *.java | wc -l`
	local LINES=`fgrep -r --include="*.java" "" * | wc -l`
	let "FILES_TOT += $FILES"
	let "LINES_TOT += $LINES"
	echo "$1:  $FILES / $LINES"
}

echo ""
echo "STATISTICS ON DSOL"
echo "name: files / lines"
echo "-------------------"
stats language
stats dsol
stats dsol-gui
stats dsol-xml
stats naming
stats logger
stats jstats
stats introspection
stats interpreter
stats gisbeans
stats dsol-tutorial
stats event
echo "-------------------"
echo "TOTAL:  $FILES_TOT / $LINES_TOT"
