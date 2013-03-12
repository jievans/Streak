Streak
======

Streak is a Java program that allows you to transform your smartphone into a paintbrush that will color your video with a temporary "Streak" of multi-colored pixels.  

Requirements
------------

* Streak is currently only designed for use on Macs. 
* Streak requires an installation of Java 6 or higher to run (older Java installations have not been tested).    
* Streak requires an installation of ffmpeg.  If you don't have ffmpeg installed, you can get it from [Macports](http://www.macports.org/).  Make sure that running the command "ffmpeg" at the command line results in a readout verifying your ffmpeg installation.
* Streak has currently been tested while allotting 2GB of memory to the Java Virtual Machine (JVM).  This large amount of memory is required for storing the pixel information that creates the "streak" effect within your video clip.  At this time, the 2GB of memory has been used to run the program while providing a "streak" delay of up to 24 frames at a resolution of 1920x1080.  If you wish to set a higher frame delay with higher resolution, you may need to allocate more memory to the JVM.

How-To
------

To run Streak, download the class files into a single directory, and then run the command:


