import java.util.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.Scanner;





public class Streak{

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
	//private static HashMap<List<Integer>, Integer> colorHash = new HashMap<List<Integer>, Integer>();
	private static int[][][] changeBuffer;
	private static Random randomGen = new Random();
	private static int delay;

	private static boolean isGreen(Color color){

		if(color.getGreen()>105 && color.getRed()<115 && color.getBlue()<115){
			return true;
		}else{
			return false;
		}

	}

	private static Color setRandomColor(BufferedImage image, int x, int y){

		int red = randomGen.nextInt(256);
		int green = randomGen.nextInt(256);
		int blue = randomGen.nextInt(256); 

		Color randomColor = new Color(red, green, blue);

		image.setRGB(x,y,randomColor.getRGB());
		return randomColor;

	}

	



	/*private static boolean cycleBuffer(BufferedImage subject, int x, int y, int i){

		Color subjectColor = new Color(subject.getRGB(x,y), true);

		for(int j=0; j<bufferColors.size(); j++){

			int[] bufferItem = bufferColors.get(j);

			if((i-bufferItem[0])<delay && bufferItem[1]==x && bufferItem[2]==y){

				imgProduct.setRGB(x,y,bufferItem[3]);
				return true;

			}
		}

		return false;
	}*/

	public static void main(String[] args){



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
		stillsFolder.deleteOnExit();

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

		//imageFolder = new File(args[0]);
		//System.out.println(stillsFolder.list(new myFilter()));
		inputArray = stillsFolder.listFiles(new myFilter());
		//System.out.println(Arrays.toString(inputArray));
		//productFolder = new File(args[1]);
		productFolder = new File(outputVideo.getParentFile(),"ProcessedImages");
		productFolder.mkdir();
		productFolder.deleteOnExit();
		


		productFolder.mkdir();

		BufferedImage templateImage = null;

		try{
			templateImage = ImageIO.read(inputArray[1]);
		} catch(IOException e){
			System.out.println("There was an error in reading the image: " + inputArray[1]);
		}

		//System.out.println("The image is: " + templateImage.getWidth() + templateImage.getHeight());

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
				File fullProductPath = new File(productFolder, ""+i+".png");
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