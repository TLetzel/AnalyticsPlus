:: usage:
:: ./compile-sass.sh [scss-input] [css-output]
:: - scss-input:  default is "styles.scss"
:: - scss-output: default is using the same filename as 
::                scss-input, but instead with .css extension.

:: change if your configuration differs from the default Eclipse project

SET THEMEDIR=%~dp0\..\src\main\resources\VAADIN\themes
SET VAADIN_DIR=%~dp0

SET THEMEFILE=styles.scss
SET TARGETFILE=%THEMEFILE:.scss=.css%


for %%x in (%*) do (
echo "Compiling, theme: %%x, source: %THEMEFILE%, target: %TARGETFILE%"
java  -classpath "..\themelibs\*" com.vaadin.sass.SassCompiler %THEMEDIR%\%%x\%THEMEFILE% %THEMEDIR%\%%x\%TARGETFILE%
echo "done"

)


