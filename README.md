Streak
======

Streak is a Java program that allows you to transform your smartphone into a paintbrush that will color your video with a temporary "streak" of multi-colored pixels.  

Requirements
------------

* Streak is currently only designed for use on Macs. 
* Streak requires an installation of Java 6 or higher to run (older Java installations have not been tested).    
* Streak requires an installation of ffmpeg.  If you don't have ffmpeg installed, you can get it from [Macports](http://www.macports.org/).  Make sure that running the command "ffmpeg" at the command line results in a readout verifying your ffmpeg installation.
* Streak has currently been tested while allotting 2GB of memory to the Java Virtual Machine (JVM).  This large amount of memory is required for storing the pixel information that creates the "streak" effect within your video clip.  At this time, the 2GB of memory has been used to run the program while providing a "streak" delay of up to 24 frames at a resolution of 1920x1080.  If you wish to set a higher frame delay with higher resolution, you may need to allocate more memory to the JVM. Read on to *The Concept* section to see what is meant by "frame delay."
* Streak has currently only been tested with input .MOV files and output .mp4 files, but other file formats are anticipated to work as well.  


The Concept
-----------
* You bring up an image of the color green on your phone's screen.
* You point the phone's screen towards the camera, and move the phone in the path that you'd like your streak to take (possibly including music or other fun happenings in the background for dramatic effect).  
* You take your video file and process it with Streak, setting a *delay* value that represents the number of frames that the "streak" will stay on the screen.  If your video is 24 frames per second (fps), then setting the delay value at 24 will result in streaks staying on the screen for 1 second.  
* Enjoy your trippy video (i.e., the power of making pixel dust spring from your hand).  


How-To
------

First, navigate to an image of the color green on your phone.  I've been using this [image](http://2.bp.blogspot.com/-2sqlpdOHQzU/TzvySTSe-QI/AAAAAAAABXk/UhcaaKL_n9c/s1600/iphone-wallpaper-flashy-green.jpg) for testing.  Then record your video as described in the previous section *The Concept*. 

To process your video with Streak, download the class files into a single directory, and then navigate to that directory on the command line (using cd). Your input will then take on the following structure:

	sudo java -Xmx2g Streak "/AbsolutePathNameOfYourInputVideo" "/AbsolutePathNameOfYourDesiredOutputVideo" DelayIntegerValue

As described in the previous section, *DelayIntegerValue* is a whole number that represents the number of video frames in which your streaks appear once they're formed. So if your video is 24 frames per second (fps), then setting the delay value at 24 will result in streaks staying on the screen for 1 second. 

The flag -Xmx2g sets the maximum memory provided to the Java Virtual Machine (JVM) at 2GB.  As explained in the requirements, you may want to increase or decrease this number depending on your desired delay value and/or your machine specifications. For example, entering -Xmx512m as a flag would set the maximum memory of the JVM at 512MB.  

Here is an example of input that could be given to Streak at the command line.  

	sudo java -Xmx2g Streak "/Volumes/My External Drive/Testing/myclip.MOV" "/Volumes/My External Drive/MyCoolVideos/streakedvideo.mp4" 24


To-Do List
----------
* Adding comments to current code.
* Changing the Delay value from a frame value to a much more intuitive time value (with time expressed in seconds).
* Adding options for different color effects. Streak currently only supports replacing original video pixels with pixels of a random color.  
* Providing users the option to vary their paint brush color.
