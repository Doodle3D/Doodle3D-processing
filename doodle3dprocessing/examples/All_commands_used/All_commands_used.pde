import doodle3dprocessing.*;

//You need to define your WiFi-Box IP. 
//It is possible to find out your WiFi-Box IP by 
//browsing to connect.doodle3D.com and select your WiFi-Box.
//Your WiFi-Box IP will be showed in URL of your browser.
Printer printer = new Printer(this, "10.0.0.188");

void setup() {
  printer = new Printer(this, "10.0.0.188"); //again...
  size(300, 300);
  printer.startUp(); //needs to run to get the printer fixed in place and heating up
}

void draw() {
  printer.update(); //is needed for now
}

void keyReleased() {
  if (key == 'q' || key == 'Q') { //press q to stop printing
    printer.stopPrint(); //stops the printer from printing and forces it to go home
  }
  if (key == 'p' || key == 'P') { //press p to print a square tower  
    squareTower(10,10,20,20,10);//creates a square tower (start x, start y, width, height, number of layers)
  }
  if (key == 'h' || key == 'H') { //press h to print the word 'hi'
    hi();//prints the word HI in lines
  }
  if (key == 's' || key == 'S') { //press s to print a star with a twist
    drawStar(100, 100, 25, 50, 10, 50);//prints a star (start x, start y, inner radius, outer radius, number of spikes, number of layers)
  }
  if (key == 'i' || key == 'I') { //press i to print a line to your mouse current location on the screen
    mousePrint();//will print at your mouseX and mouseY if pressed
  }
}


void squareTower(int cx, int cy, int w, int h, int z) { //everything to print a square tower
  first3Layers();
  printer.printerLinesVisible = false;
  printer.cleanNozzle();
  printer.printerLinesVisible = true;
  printer.myTranslate(cx, cy);
  printer.moveTo(0, 0, 1);
  for (int i = 0; i<z; i++) {
    if (i == 3) {
      layersAfter();
    }
    printer.lineTo(w, 0, i);
    printer.lineTo(w, h, i);
    printer.lineTo(0, h, i);
    printer.lineTo(0, 0, i);
    if (i==z-1) {
      printer.endPrint();
    }
  }
  printer.myTranslate(-cx, -cy);
}


void hi() { //everything to write HI in lines
  first3Layers();
  printer.printerLinesVisible = false;
  printer.cleanNozzle();
  printer.printerLinesVisible = true;
  //H
  printer.printLine(10, 10, 10, 50, 1);
  printer.printLine(10, 30, 25, 30, 1);
  printer.printLine(25, 10, 25, 50, 1);
  //I
  printer.printLine(35, 20, 45, 20, 1);
  printer.printLine(40, 20, 40, 50, 1);
  printer.printLine(35, 50, 45, 50, 1);

  printer.endPrint();
}


void drawStar(float cx, float cy, float innerRadius, float outerRadius, int numSpikes, int numLayers) {
  printer.cleanNozzle();
  first3Layers();
  printer.myTranslate(cx, cy);
  printer.moveTo(innerRadius, 0, 0);
  for (int z=0; z<numLayers; z++) {
    if (z==3) {
      layersAfter();
    }
    for (int spike=0; spike<=numSpikes; spike++) {
      boolean even = spike%2==0;
      float progress = float(spike)/numSpikes;
      float x = cos(progress * TWO_PI) * (even ? innerRadius : outerRadius);
      float y = sin(progress * TWO_PI) * (even ? innerRadius : outerRadius);

      float f = float(z)/numLayers/(TWO_PI);

      float rx = x*cos(f) - y*sin(f);
      float ry = y*cos(f) + x*sin(f);

      printer.lineTo(rx, ry, z);
    }
    if (z==numLayers) {
      printer.endPrint();
    }
  }
  printer.myTranslate(-cx, -cy);
}


void mousePrint() {
  printer.hopping = false;
  printer.forceHeatTarget = false;
  printer.bufferWaiting = false;
  printer.lineTo(mouseX, mouseY, 1);
}


void first3Layers() { //makes a start bottom with a slow speed and more filament
  printer.hopping = false;
  printer.forceHeatTarget = true;
  printer.bufferWaiting = true;
  printer.amount_of_filament = 200; //200% filament
  printer.feedrate = 1000;
}


void layersAfter() { //more speed and minimum filament
  printer.amount_of_filament = 150; //150% filament
  printer.startStopFan();
  printer.feedrate = 2000;
}
