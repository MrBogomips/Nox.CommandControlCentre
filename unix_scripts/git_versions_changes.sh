#!/bin/bash

[ $# -eq 0 ] && { 
	echo "Usage: $0 <REF-START> <REF-END>";
	echo "  samples:"
	echo "$0 VER.0.0.1 VER.0.0.9      (two tags)"
	echo "$0 1231c1d1 123v2f1231      (two commits)"
	exit 1; 
}

echo Software changes from $1 to $2
echo ==========================================
git --no-pager log $1..$2 --oneline|grep -v "\bMerge branch"

echo
echo == Modules' changes statistics ============
git --no-pager diff $1 $2 --dirstat --no-color

echo
echo == File statistics ========================
git --no-pager diff $1 $2 --stat --no-color

 