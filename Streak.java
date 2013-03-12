import java.util.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.Scanner;

/*
Welcome to Streak! Step through the comments below to see how Streak works.
Note: the comments are currently incomplete and only run up to the main method.  
*/

public class Streak{

/*
Here we declare most of the variables that will be used throughout the program. 
Their use will become more apparent as we initialize them later on.
*/
	private static String stringInput;
	private static String stringOutput;
	private static File inputVideo;
	private static File outputVideo;
	private static File parentOutput;
	private static File stillsFolder;
	private static File audioLocation;
	private static BufferedImage currentImage;
	private static File imageFolder;
	private static File productFolder;
	private static File[] inputArray;
	private static ArrayList<int[]> bufferColors = new ArrayList<int[]>();
	private static int[][][] changeBuffer;
	private static Random randomGen = new Random();
	private static int delay;

/*
The isGreen method takes in a Java color object (associated with a given pixel of a given frame in our video) and decides if we consider the color to be Green. We have to analyze the separate RGB components of each pixel, because the color Green is so specific in RGB terms that few pixels would be considered purely Green in a natural environment (i.e., many pixels on our smartphone's image of Green wouldn't be considereded purely Green when we shine different types of light on it and reflect different surfaces off the image). This is why we set more general terms for what we consider to be Green.  This method could be improved to produce more accurate capturing of the Green color.  
*/
	private static boolean isGreen(Color color){

		if(color.getGreen()>105 && color.getRed()<115 && color.getBlue()<115){
			return true;
		}else{
			return false;
		}

	}

/*
This method is used to generate a Random color for a pixel.  The Streak effect is obtained by setting pixels to these Random colors after they have been "marked" for color change by the Green image on the smartphone.   
*/

	private static Color setRandomColor(BufferedImage image, int x, int y){

		int red = randomGen.nextInt(256);
		int green = randomGen.nextInt(256);
		int blue = randomGen.nextInt(256); 

		Color randomColor = new Color(red, green, blue);

		image.setRGB(x,y,randomColor.getRGB());
		return randomColor;

	}
/*
Comments for the main method will be coming soon!
*/
	

	public static void main(String[] args){

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				ProcessBuilder deleteTempFrames  = new ProcessBuilder("rm","-r",stillsFolder.getAbsolutePath(),productFolder.getAbsolutePath());


				deleteTempFrames.redirectErrorStream(true);


				try{

					Process process = deleteTempFrames.start();
					System.out.println(":)");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null){

						System.out.println(line);

					}
					process.waitFor();


				} catch(IOException e){

					System.out.println("There was a problem in removing the temporary frames.  Please check that you have the program rm properly installed on your Mac.");

				} catch(InterruptedException e){
					System.out.println("There was an interrupted exception.");
				}
			}
		}, "Shutdown-thread"));



		inputVideo = new File(args[0]);

		while(!inputVideo.exists()){

			System.out.println("Your input video does not exist.  Please re-enter the path of your input video.");
			Scanner keyboard = new Scanner(System.in);
			args[0] = keyboard.next();
			inputVideo = new File(args[0]);

		}

		outputVideo = new File(args[1]);

		while(outputVideo.exists()){

			System.out.println("Your output video already exists.  Please re-enter the path of your output video.");
			Scanner keyboard = new Scanner(System.in);
			args[1] = keyboard.next();
			outputVideo = new File(args[1]);

		}

		while(delay==0){

			try{

				delay = Integer.parseInt(args[2]);

			} catch(NumberFormatException e){



				System.out.println("Your specified delay value cannot be recognized as an integer.  Please re-enter your delay integer value and hit Enter.");
				Scanner keyboard = new Scanner(System.in);
				args[2] = keyboard.next();



			}

		}

		stringInput = args[0];
		stringOutput = args[1];

	 
		stillsFolder = new File(outputVideo.getParentFile(),"StillImages");
		stillsFolder.mkdir();
		
		double frameRate = Streaming.frameRate(stringInput);

		String stringFrameRate = "" + frameRate;


		audioLocation = new File(outputVideo.getParentFile(),"tempAudio.mp3");
		audioLocation.deleteOnExit();

		ProcessBuilder generateStillsAudio  = new ProcessBuilder("ffmpeg","-i",stringInput,"-acodec","mp3",audioLocation.getAbsolutePath(), stillsFolder.getAbsolutePath()+"/%5d.png");


		generateStillsAudio.redirectErrorStream(true);


		try{
			
			Process process = generateStillsAudio.start();
			System.out.println("Please wait while ffmpeg creates your still images...");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null){

				System.out.println("Creating still images: " + line);

			}
			process.waitFor();
			
		
		} catch(IOException e){

			System.out.println("There was a problem in starting ffmpeg.  Please make sure the software is properly installed and that typing 'ffmpeg' at the command line executes without error.");

		} catch(InterruptedException e){
			System.out.println("There was an interrupted exception");
		}


		
		System.out.println("Still image processing completed successfully...");

	
		inputArray = stillsFolder.listFiles(new myFilter());
		productFolder = new File(outputVideo.getParentFile(),"ProcessedImages");
		productFolder.mkdir();
		
		


		productFolder.mkdir();

		BufferedImage templateImage = null;

		try{
			templateImage = ImageIO.read(inputArray[1]);
		} catch(IOException e){
			System.out.println("There was an error in reading the image: " + inputArray[1]);
		}

		changeBuffer = new int[delay][templateImage.getWidth()][templateImage.getHeight()];

		for(int i=0; i<inputArray.length; i++){

			System.out.println(inputArray[i] + " of " + inputArray.length + " total");


			BufferedImage imgSubject = null;
			try{
				imgSubject = ImageIO.read(inputArray[i]);
			} catch(IOException e){
				System.out.println("There was an error in reading the image: " + inputArray[i]);
			}


			BufferedImage imgProduct = new BufferedImage(imgSubject.getWidth(), imgSubject.getHeight(), BufferedImage.TYPE_INT_ARGB);


			for(int x=0; x<imgSubject.getWidth(); x++){

				for(int y=0; y<imgSubject.getHeight(); y++){


					Color subjectColor = new Color(imgSubject.getRGB(x,y), true);
					boolean pixelSet = false;

					for(int j=0; j<delay && !pixelSet; j++ ){

							if(changeBuffer[j][x][y]!=0){

								imgProduct.setRGB(x,y,changeBuffer[j][x][y]);
								pixelSet = true;

							}
						
					}

					
					if(isGreen(subjectColor) && !pixelSet){

						int randomColor = setRandomColor(imgProduct,x,y).getRGB();
						changeBuffer[delay-1][x][y] = randomColor;

					}else if (!pixelSet){
						imgProduct.setRGB(x,y,imgSubject.getRGB(x,y));
					}

					if(!isGreen(subjectColor)){
						changeBuffer[delay-1][x][y] = 0;
					}

					for(int k=0; k < delay-1; k++ ){

						changeBuffer[k][x][y] = changeBuffer[k+1][x][y];

					}

				}

			}

			try{
				File fullProductPath = new File(productFolder, ""+(i+1)+".png");
				ImageIO.write(imgProduct, "png", fullProductPath);
			} catch (IOException e) {
				System.out.println("There was an error writing the saved file.");
			}

		}

		System.out.println(stringFrameRate);
		ProcessBuilder buildFinalVideo  = new ProcessBuilder("ffmpeg","-r",stringFrameRate,"-i",productFolder.getAbsolutePath()+"/%d.png","-i",audioLocation.getAbsolutePath(),"-strict","-2","-r",stringFrameRate,stringOutput);
		buildFinalVideo.redirectErrorStream(true);

		try{
			
			Process process = buildFinalVideo.start();
			System.out.println("Please wait while ffmpeg compiles the images and audio to create the final video...");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null){

				System.out.println("Compiling images: " + line);

			}
			process.waitFor();
			
		
		} catch(IOException e){

			System.out.println("There was a problem in starting ffmpeg.  Please make sure the software is properly installed and that typing 'ffmpeg' at the command line executes without error.");

		} catch(InterruptedException e){
			System.out.println("There was an interrupted exception");
		}



	
		System.out.println("Streak completed successfully!  Have fun with your new video!");
		


	}



}