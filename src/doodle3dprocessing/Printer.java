/**
* Doodle3Dprocessing
* A library making your 3D printer easily controlable through the Doodle3D WiFi-Box
* Site: http://www.doodle3d.com
* 
* Author: Bart Zuidervaart
* Author site: http://www.bartzuidervaart.nl
* Modified: 02-04-2014
* Version: 1.0.0
*
*/

package doodle3dprocessing;

import processing.core.*;
import processing.data.JSONObject;
import processing.data.StringList;
import httprocessing.*;

/** //IMPORTANT//----------
*
* It is important that you declare the printer class in your processing sketch
* An example of how to declare the printer class is: Printer printer = new Printer("10.0.0.188");
* Note that you need to add the IP address of your own WiFi-Box
* 
* Make sure you start the printer correctly by typing printer.startUp(); in the setup()
* Wait patiently before the printer is 'homed' before sending any codes
* 
* Make sure you type printer.update(); in your void draw();
* Please note that you can NOT print in your void setup(); in this library (might change in later editions)
* 
* It is important that the Doodle3D WiFi-Box has power and is connected to your device through the internet. 
* Without it the sketch won't start. It is not necessary to have the printer on to start the sketch.
* 
* -----------
* 
* Most of the printer settings will be automatically defined by your Doodle3D WiFi-Box settings, make sure you selected the right printer and information or adjust it manually.
* 
* To print, type printer.printLine(x,y,x,y,z);, it works just like the line(); function but with an additional Z axis. The printer will use your sketch size(); as borders for the printer
* You can also use the printer.moveTo(x,y,z); and printer.lineTo(x,y,z); for easier interactions with the line tool.
* It is advised to start your print with a print.cleanNozzle();, this will make a start print-line
* This will cleaning your nozzle and makes sure you can print filament.
* 
* To cancel a print and return the print-head home use the printer.stopPrint(); function
* 
* 
* FUNCTIONS:
* 
*  NEEDED:
*  startUp()                 				//Will make your printer start ready. You add startUp() in your setup()
*  update()                  				//Is needed to buffer the print that is send. You add update() in your draw()
*  
*  OPTIONAL:
*  printLine(x,y,x2,y2,z)              		//prints a line just like the line() tool but with an additional Z-axis
*  stopPrint()             					//Stop the current print and will return to the homing position
*  endPrint()								//After printing a model this is a perfect way to return the print-head to the starting position, so your print can remain clean. 
*  cleanNozzle()            				//Draw an line along the left side of your printer to clean the nozzle and wont lift up and retract filament after, which makes it a nice way to start a print
*  startStopFan()           				//Turn the fan on when it is off and will turn it of when it is on
*  myTranslate(x,y)            				//place a new x and y position to start from
*  moveTo(x,y,z)                			//places a new start position to start the next line from
*  lineTo(x,y,z)                 			//place a new end position to end the next line from
*  
*  receiveConfigAll()       				//define and print the dimensions of the printer
*  printlnReceivedConfig()  				//only print the dimensions of the printer
*  
*  PARAGRAPHS:
*  
* [boolean] 	hopping                    //jumps after a serie of lines is drawn to remove the chance to print accidentaly. "FALSE" by default.
* [boolean] 	forceHeatTarget            //Waits until the printer is properly heated before printing any more lines. "TRUE" by default.
* [boolean] 	bufferWaiting              //risky because of a API bug at the moment. It is advised to keep the frameRate at a maximum of 15fps. "TRUE" by default.
*
* [boolean] 	printerLinesVisible        //Makes your printlines visible in the sketch. "TRUE" by default.
* [boolean]		printlnConfigVisible	   //Prints the dimensions of the printer to processing
* [boolean]		printlnGcodeVisible		   //Prints the gcode that is produced
*
* [int] 		printer_x                  //in mm Printable dimension X. Defined by your WiFi-Box settings by default.
* [int] 		printer_y                  //in mm Printable dimension Y. Defined by your WiFi-Box settings by default.
* [int] 		printer_z                  //in mm Printable dimension Z. Defined by your WiFi-Box settings by default.
* [int] 		feedrate                   //Feedrate in 1000mm per minute. 2000 by default. Prints would be advised to start slower.
* [float] 		filamentThickness          //mm Thickness of the filement. Defined by your WiFi-Box settings by default.
* [float] 		layerHeight                //mm Thickness of the line you want to print. Defined by your WiFi-Box settings by default.
* [float]		layerThickness             //approximation of your filament thickness. 0.4mm by default.
* [float] 		amount_of_filament         //amount of filament in percents %. 100% by default.
*
**/




public class Printer {
	PApplet parent;
	String boxIP;
	
	  public boolean hopping = false; //jumps after a set of lines is drawn to remove the chance to print accidentally.
	  public boolean forceHeatTarget = true; //waits until the printer is properly heated before printing any more lines.
	  public boolean bufferWaiting = true; //risky to turn to false because of a API bug at the moment. It is advised to keep the frameRate at a maximum of 15fps if you do.

	  public boolean filamentRetraction = true; //retracts filament if the printer has to leave a space between lines
	  public boolean printerLinesVisible = true; //Makes your print-lines visible in the processing sketch screen
	  public boolean printlnConfigVisible = true; //Prints the dimensions of the printer to processing
	  public boolean printlnGcodeVisible = false; //Prints the gcode that is produced
	  

	  //Dimensions of your printer
	  public int printer_x; //in mm Printable dimension X. Defined by your WiFi-Box settings by default.
	  public int printer_y; //in mm Printable dimension Y. Defined by your WiFi-Box settings by default.
	  public int printer_z; //in mm Printable dimension Z. Defined by your WiFi-Box settings by default.
	  public int feedrate = 2000; //Feedrate in 1000mm per minute. 2000 by default. Prints would be advised to start slower.
	  public float filamentThickness; //mm Thickness of the filament. Defined by your WiFi-Box settings by default.
	  public float layerHeight; //mm Thickness of the line you want to print. Defined by your WiFi-Box settings by default.
	  public float layerThickness = (float)0.40; //approximation of your filament thickness. 0.4 by default.
	  public float amount_of_filament = 100; 	//amount of filament in percents %. 100% by default. Some printers might print to accurate for starter lines for example.
	  //In this case it is advised to add more than 100% for a steady line. Don't over do it though, the printer can't force to much filament at the same time.
	
	  //Post addresses where the WiFi-Box can post to
	  PostRequest stop;
	  PostRequest post;
	  PostRequest config; 
	  PostRequest heatup;

	  //creates a JSONObject to get results from the WiFi-Box
	  JSONObject json;
	  JSONObject info_status;

	  //calculation result for the amount of filament used in a single line
	  float filament_calculation = 2;

	  //reads if the fan is on or off
	  boolean fanTurning = true;

	  //Gcode buffer, gets filled with a list of Gcode and is released after a couple of seconds.
	  String gcode_buffer = "";
	  int gcode_buffer_seconds = 0;
	  
	  //The following code is defined to use through the entire library
	  int temperature;
	  boolean cleanNozzle_state = false;
	  float current_Zaxis = 0;

	  float prevgcodeX, prevgcodeY;
	  float startgcodeX, startgcodeY;
	  float gcodeX, gcodeY;
	  float printZconstrain;
	  float moveX=0, moveY=0, moveZ=0;
	  float translateX=0, translateY=0;

	  StringList bufferList;
	  StringList bufferList500;
	  
	public Printer(PApplet p,String ip){
		this.parent = p;
		boxIP = ip;
		
	    //Post addresses where the WiFi-Box can post to
	    stop = new PostRequest("http://"+boxIP+"/d3dapi/printer/stop");
	    post = new PostRequest("http://"+boxIP+"/d3dapi/printer/print");
	    config = new PostRequest("http://"+boxIP+"/d3dapi/config");
	    heatup = new PostRequest("http://"+boxIP+"/d3dapi/printer/heatup");
	    bufferList = new StringList();
	    bufferList500 = new StringList();
	}
	
	//This void must be written in the void draw(), it is needed to time the amount of data being send to the printer.
	  public void update() {
	    //Uses your PC's clock seconds as a buffer for gcode
	    gcode_buffer_seconds = this.parent.second();

	    //Creates a buffer to prevent to many gcodes with the "start, true" being send at the same time.
	    if (gcode_buffer_seconds % 2 == 0 && bufferWaiting == true) {
	      if (bufferList.size() > 0 || bufferList500.size() > 0) {
	        printerReady();
	      }
	    } 
	    else if (bufferWaiting == false) {
	      if (bufferList.size() > 0 || bufferList500.size() > 0) {
	        printerReady();
	      }
	    }
	  }
	  public void stopPrint() {
		    //stop.addData("gcode", json.getString("printer.endcode")); //might be added later to use the WiFi-Box data about printers to stop every printer on their own way
		    stop.addData("gcode", "G28 X0.0 Y0.0 Z0.0 \n G1 "+(filament_calculation-5)+" -5.0 F"+feedrate+"\n M107"); 
		    stop.addData("start", "true");
		    stop.send();
		    bufferList.clear();
		    bufferList500.clear();
		    gcode_buffer = "";
		  }
	  public void endPrint() {
		  bufferList500.append("G28 X0.0 Y0.0 Z0.0 \n G1 "+(filament_calculation-5)+" -5.0 F"+feedrate+"\n M107 \n");
		  }

		  //Will give the printer a basic start up before going
		  public void startUp() {
		    receiveConfigAll();
		    heatup.send();
		    //post.addData("gcode", json.getString("printer.startcode"));//might be added later to use the WiFi-Box data about printers to start every printer on their own way
		    fanTurning = false;
		    post.addData("gcode", "G28 X0.0 Y0.0 Z0.0 \n G92 E0\n G1 E 5\n G92 E0\n M107\n M104 S"+temperature+"\n"); //line 1: Homing, line 2: defines current filament as 0, line 3: stops the fan from turning if does
		    post.addData("start", "true");
		    post.send();
		    bufferList.clear();
		    bufferList500.clear();
		    gcode_buffer = "";
		  }

		  //Makes a print sample. Can be used to make sure the printer has a clean nozzle and prints filament.
		  public void cleanNozzle() {
		    cleanNozzle_state = true;
		    printLine(0, 0, 0, this.parent.width/2, 1);
		    printLine(1, this.parent.width/2, 1, 0, 1);
		  }

		  //Makes the Printer follow the line draw
		  //Generating the Gcode for a single line, inclusive the calculations needed for the line.
		  public void printLine(float printX, float printY, float printX2, float printY2, float printZ) {

		    lineCalculations(printX, printY, printX2, printY2, printZ);

		    currentZaxis();

		    if (printerLinesVisible == true) {
		      //Creates a line in the processing screen to show how the print will look like.
		      this.parent.stroke(255, this.parent.map(printZ, 0, printer_z/layerHeight, 255, 0));//makes lines further off in the Z-axis more transparent.
		      this.parent.line(printX, printY, printX2, printY2);//draws a line in the screen using the line() tool.
		    }

		    gcodeString();
		  }

		  public void receiveConfigAll() {
		    //Directs the JSONObject to config/all to get the printer settings
		    json = this.parent.loadJSONObject("http://"+boxIP+"/d3dapi/config/all");
		    json = json.getJSONObject("data");

		    //Basic printer settings received from the WiFi-Box
		    layerHeight = json.getFloat("printer.layerHeight");
		    filamentThickness = json.getFloat("printer.filamentThickness");
		    printer_x = json.getInt("printer.dimensions.x");
		    printer_y = json.getInt("printer.dimensions.y");
		    printer_z = json.getInt("printer.dimensions.z");
		    
		    //Receive target temperature
		    temperature = json.getInt("printer.temperature");
		    if(printlnConfigVisible){
		    printlnReceivedConfig();
		    }
		  }

		  public void printlnReceivedConfig() {
		    this.parent.println("Printer type selected: "+json.getString("printer.type"));
		    this.parent.println("layer height is: "+layerHeight);
		    this.parent.println("filamentThickness is: "+filamentThickness);
		    this.parent.println("Printer X: "+printer_x+"  Printer Y: "+printer_y+"  Printer Z: "+printer_z);
		  }
		  
		  //starts or stops the Fan, depending on the current position
		  public void startStopFan() {
		    if (fanTurning == true) {
		      fanTurning = false;
		      bufferList500.append("M107\n");
		    } 
		    else {
		      fanTurning = true;
		      bufferList500.append("M106\n");
		    }
		  }
		  void printGcode() {
		    //release the remaining buffer
		    for (int i= 0; i<bufferList500.size();i++) {
		      gcode_buffer += bufferList500.get(i);
		    }
		    bufferList.append(gcode_buffer);

		    //release the list with Gcode Strings
		    for (int i = 0; i < bufferList.size(); i++) {
		      post.addData("gcode", bufferList.get(i));
		      if(printlnGcodeVisible){ //printlnGcodeVisible is true, show the gcode in the processing print screen
		      this.parent.println(bufferList.get(i));
		      }
		      if (i == bufferList.size()-1) {
		        post.addData("start", "true");
		      }
		      post.send();
		    }
		    //Posts gcode for the printer to print
		    if (cleanNozzle_state == false) {
		      if (hopping == true) {
		        post.addData("gcode", "G1 Z"+(current_Zaxis+10)+" E"+(filament_calculation-5)+" F"+feedrate);
		      }
		      post.send();
		    } 
		    else {
		      cleanNozzle_state = false;
		    }
		    bufferList.clear();
		    bufferList500.clear();
		    gcode_buffer = "";
		  }

		  void printerReady() {
		    //Get data from the printer, needed to see if the printer is hot enough
		    info_status = this.parent.loadJSONObject("http://"+boxIP+"/d3dapi/info/status");
		    info_status = info_status.getJSONObject("data");
		    
		    if (info_status.getString("state").equals("disconnected") == false) {//dubble checks if the printer is even connected or not, to prevent error messages
		      if (forceHeatTarget == true) {
		        if (info_status.getInt("hotend")+2 > info_status.getInt("hotend_target")) { //checks if the printer is hot enough. 
		          //!!! If JSONObject["hotend"] or ["hotend_target"] is not found, the chance is high that you printer isn't on or connected.
		        	this.parent.println("hot enough!");

		          printGcode();
		        } 
		        else {
		        	this.parent.println("Temperature is: "+info_status.getInt("hotend")+" Target temperature is: "+info_status.getInt("hotend_target")+"heating up... ");
		        }
		      } 
		      else {
		        printGcode();
		        this.parent.println("Temperature is: "+info_status.getInt("hotend")+" printer is Printing...");
		      }
		    } 
		    else {
		    	this.parent.println("Printer is not found, either it is off or it is not connected");
		    }
		  }

		  void gcodeString() {
			
			//hopping and retract filament actions
		    String retractFilamentGcode = "G1 Z"+this.parent.abs(layerHeight*printZconstrain)+" E"+(filament_calculation-5)+" F3000 \n";
		    String hoppingGcode = "G1 "+" Z"+(current_Zaxis+10)+" F"+feedrate+"\n";
		    String moveToGcode = "G1  X"+startgcodeX+"Y"+startgcodeY+" F"+feedrate+"\n";
		    String returnFilamentGcode = "G1 Z"+this.parent.abs(layerHeight*printZconstrain)+" E"+(filament_calculation)+" F3000 \n";

		    String printGcode = "G1  X"+gcodeX+" Y"+gcodeY+" Z"+this.parent.abs(layerHeight*printZconstrain)+" E"+(filament_calculation)+" F"+feedrate+"\n"; //Prints the line drawn

		    //If there are more than 450 string lines in the bufferList500 it sends the list to bufferList to prevent sending to much data to the WiFi-Box at once
		    if (bufferList500.size() > 450) {
		      for (int i= 0; i<bufferList500.size();i++) {
		        gcode_buffer += bufferList500.get(i);
		      }
		      bufferList.append(gcode_buffer);
		      gcode_buffer = "";
		      bufferList500.clear();
		    }
		  //Decides whether or not the printer should retract filament and or should hop
		    if (this.parent.dist(prevgcodeX, prevgcodeY, startgcodeX, startgcodeY)>1) {
		      //adds the gcode to a buffer, ready to get released;
		      if(filamentRetraction == true){
		    	  bufferList500.append(retractFilamentGcode);
		    	  if(hopping == true){
		    		  bufferList500.append(hoppingGcode);
		    	  }
		    	  bufferList500.append(moveToGcode);
		    	  bufferList500.append(returnFilamentGcode);
		      }

		      bufferList500.append(moveToGcode);
		      bufferList500.append(printGcode);
		    } 
		    else {
		      //adds the gcode to a buffer, ready to get released;
		      bufferList500.append(printGcode);
		    }

		    prevgcodeX = gcodeX;
		    prevgcodeY = gcodeY;
		  }

		  //Used to calculate the amount of filament used for the printer and the length of the line that is drawn
		  void lineCalculations(float printX, float printY, float printX2, float printY2, float printZ) {
		    startgcodeX = this.parent.constrain(this.parent.map(printX, 0, this.parent.width, 0, printer_x), 0, printer_x);
		    startgcodeY = this.parent.constrain(this.parent.map(printY, this.parent.height, 0, 0, printer_y), 0, printer_y);

		    gcodeX = this.parent.constrain(this.parent.map(printX2, 0, this.parent.width, 0, printer_x), 0, printer_x);
		    gcodeY = this.parent.constrain(this.parent.map(printY2, this.parent.height, 0, 0, printer_y), 0, printer_y);

		    printZconstrain = this.parent.constrain(printZ, 1, printer_z/layerHeight);//Makes you unable to break the limit of your printer Z-axis.

		    //calculate the amount of filament needed for a single line (Pythagorean theorem & dimensional math)
		    float lineLength = this.parent.sqrt(this.parent.sq(this.parent.abs(startgcodeX-gcodeX))+this.parent.sq(this.parent.abs(startgcodeY-gcodeY))); //Using Pythagorean theorem to calculate the length of the line for the printer
		    filament_calculation += this.parent.abs((lineLength*layerHeight*layerThickness)/this.parent.sq(filamentThickness))*(amount_of_filament/100); //Calculating the amount of filament needed for the printline
		  }

		  //needed to hop and prevent the printer from hopping to far
		  void currentZaxis() {
		    current_Zaxis = this.parent.abs(layerHeight*printZconstrain); //Remembers the Z-axis to remove the print nozzle after a print.
		    if (current_Zaxis > printer_z/layerHeight-10) { //secures the printer to be unable to exceed the maximum height.
		      current_Zaxis = printer_z/layerHeight-10;
		    }
		  }

		  //Gives the print a new start point to draw a line from with moveTo() and lineTo()
		  public void myTranslate(float x, float y) {
		    translateX+=x;
		    translateY+=y;
		  }
		  
		  //Gives lineTo a start position
		  public void moveTo(float x, float y, float z) {
		    moveX = x;
		    moveY = y;
		    moveZ = z;
		  }
		  
		  //Prints from start position to the given x,y,z
		  public void lineTo(float x, float y, float z) {
		    printLine(moveX+translateX, moveY+translateY, x+translateX, y+translateY, z);
		    moveTo(x, y, z);
		  }
		}

