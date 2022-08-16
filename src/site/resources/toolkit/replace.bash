#!/bin/bash

#
#	Recursive find/replace bash script for Java code
#
#	(c) copyright 2004 Delft University of Technology, the Netherlands.
#	See for project information www.simulation.tudelft.nl
#	License of use: General Public License (GPL), no warranty
#
#	version 1.0 02.08.2004
#	author Peter Jacobs <p.h.m.jacobs@tbm.tudelft.nl>
#

for file in $( find $1 -type f -name '*.java' | sort )
do
  echo $file
  sed -i "s%$2%$3%g" $file
done  

exit 0