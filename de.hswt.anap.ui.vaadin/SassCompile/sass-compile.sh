#!/bin/bash

# usage:
# ./compile-sass.sh [scss-input] [css-output]
# - scss-input:  default is "styles.scss"
# - scss-output: default is using the same filename as 
#                scss-input, but instead with .css extension.

# change if your configuration differs from the default Eclipse project
THEMEDIR="../src/main/resources/VAADIN/themes"

THEMEFILE="styles.scss"
TARGETFILE=${THEMEFILE/\.scss/\.css}

for VAR in "$@"
do	
echo "Compiling, theme: $VAR, source: $THEMEFILE, target: $TARGETFILE"
echo $THEMEDIR/$VAR/$THEMEFILE
java -cp "../themelibs/*" com.vaadin.sass.SassCompiler $THEMEDIR/$VAR/$THEMEFILE $THEMEDIR/$VAR/$TARGETFILE && echo "done!"
done
