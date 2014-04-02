Doodle3Dprocessing

A library used to make processing communicate easily to your 3D printer through the Doodle3D WiFi-Box.
Site: http://www.doodle3d.com
 
Author: Bart Zuidervaart
Author site: http://www.bartzuidervaart.nl
Modified: 28-03-2014
Version: 1.0.0




IMPORTANT

It is important that you declare the printer class in your processing sketch.
An example of how to declare the printer class is: Printer printer = new Printer("10.0.0.188");.
Note that you need to add the IP address of your own WiFi-Box
 
Make sure you start the printer correctly by typing printer.startUp(); in the setup()
Wait patiently before the printer is 'homed' before sending any codes
 
Make sure you type printer.update(); in your void draw();
Please note that you can NOT print in your void setup(); in this library (might change in later editions)
 
It is important that the Doodle3D WiFi-Box has power and is connected to your device through the internet. 
Without it the sketch won't start. It is not necessary to have the printer on to start the sketch.
 
 -----------
 
Most of the printer settings will be automatically defined by your Doodle3D WiFi-Box settings, make sure you selected the right printer and information or adjust it manually.
 
To print, type printer.printLine(x,y,x,y,z);, it works just like the line(); function but with an additional Z axis. The printer will use your sketch size(); as borders for the printer
You can also use the printer.moveTo(x,y,z); and printer.lineTo(x,y,z); for easier interactions with the line tool.
It is advised to start your print with a print.cleanNozzle();, this will make a start print-line
This will cleaning your nozzle and makes sure you can print filament.
 
To cancel a print and return the print-head home use the printer.stopPrint(); function



 
FUNCTIONS:
 
NEEDED:
startUp() 		Will make your printer start ready. 
You add startUp() in your setup().

update() 		Is needed to buffer the print that is send. You add update() in your draw().
  
OPTIONAL:
printLine(x,y,x2,y2,z) 		prints a line just like the line() tool but with an additional Z-axis.

stopPrint() 		Stop the current print and will return to the homing position.

endPrint()			After printing a model this is a perfect way to return the print-head to the starting position, so your print can remain clean. 

cleanNozzle()   		Draw an line along the left side of your printer to clean the nozzle and wont lift up and retract filament after, which makes it a nice way to start a print.

startStopFan()		Turn the fan on when it is off and will turn it of when it is on.

myTranslate(x,y)  	place a new x and y position to start from.

moveTo(x,y,z)   		places a new start position to start the next line from.

lineTo(x,y,z)  		place a new end position to end the next line from.
  
receiveConfigAll()	define and print the dimensions of the printer.

printlnReceivedConfig() 	only print the dimensions of the printer.
  
PARAGRAPHS:
[boolean]	hopping		jumps after a serie of lines is drawn to remove the chance to print accidentaly. "FALSE" by default.
[boolean]	forceHeatTarget Waits until the printer is properly heated before printing any more lines. "TRUE" by default.
[boolean] 	bufferWaiting   risky because of a API bug at the moment. It is advised to keep the frameRate at a maximum of 15fps. "TRUE" by default.

[boolean]	printerLinesVisible    	Makes your printlines visible in the sketch. "TRUE" by default.
[boolean]	printlnConfigVisible	 	Prints the dimensions of the printer to processing.
[boolean]	printlnGcodeVisible	   	Prints the gcode that is produced.

[int]		printer_x		in mm Printable dimension X. Defined by your WiFi-Box settings by default.
[int] 	printer_y		in mm Printable dimension Y. Defined by your WiFi-Box settings by default.
[int]		printer_z		in mm Printable dimension Z. Defined by your WiFi-Box settings by default.
[int] 	feedrate		Feedrate in 1000mm per minute. 2000 by default. Prints would be advised to start slower.
[float] 	filamentThickness	mm Thickness of the filement. Defined by your WiFi-Box settings by default.
[float] 	layerHeight	mm Thickness of the line you want to print. Defined by your WiFi-Box settings by default.
[float]	layerThickness	approximation of your filament thickness. 0.4mm by default.
[float] 	amount_of_filament	amount of filament in percents %. 100% by default.
