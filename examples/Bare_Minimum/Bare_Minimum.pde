import doodle3dprocessing.*;

//You need to define your WiFi-Box IP. 
//It is possible to find out your WiFi-Box IP by 
//browsing to connect.doodle3D.com and select your WiFi-Box.
//Your WiFi-Box IP will be showed in URL of your browser.
Printer printer = new Printer(this,"your WiFi-Box IP");

void setup() {
  printer = new Printer(this,"your WiFi-Box IP"); //again...
  size(100,100);
  printer.startUp(); //needs to run to get the printer fixed in place and heating up
}

void draw() {
  printer.update(); //is needed for now
}

void mouseReleased(){
 printer.lineTo(mouseX, mouseY,1); //not needed, but will make the printer print when you click on the screen
 //which is a neat example
}


