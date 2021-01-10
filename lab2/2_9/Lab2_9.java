/**

In this task you will implement the method medianFilter of the class Lab2_9 which applies the median filter on the image.

You should handle the boundary case by keeping the pixels unchanged.

The expected output is provided in the files solution3.png and solution7.png, where the digit in the filename is the threshold.

You may use the following command to check if your output is identical to ours.

cmp solution7.png out.png

If this command has no output, it implies that your solution has produced the same file as ours.

**/

import java.util.Scanner;
import java.util.Arrays;
import java.time.Instant;
import java.time.Duration;
public class Lab2_9 {
	public Lab2_9() {
		Img img = new Img("Fig0441.png");
		System.out.print("Size: ");
		Scanner in = new Scanner(System.in);
		int size = in.nextInt();
		Instant start = Instant.now();
		medianFilter(img, size);
		Instant stop = Instant.now();
		//System.out.println("Elapsed time: "+Duration.between(start, stop).toMillis()+"ms");
		img.save();
	}

	public void medianFilter(Img i, int size) {
		byte[] result = new byte[i.img.length];
		int boundaryOffset = (size-1)/2;
		for (int x = 0 ; x < i.height; x ++){
			for (int y = 0 ; y < i.width ; y ++){
				int sum =0;
				if(x<boundaryOffset || x>i.height-boundaryOffset-1 || y< boundaryOffset || y > i.width-boundaryOffset-1){
					sum = i.img[x*i.width+y];
				}
				else{
					int[] receptiveField = new int[size*size];
					int index =0;
					for(int filterX = -boundaryOffset; filterX <= boundaryOffset; filterX++ ){
						for(int filterY = -boundaryOffset; filterY <= boundaryOffset; filterY++ ){
							receptiveField[index] = (int)(i.img[ (x+filterX) * i.width + y + filterY]&0XFF);
							index++;
						}
					}
					Arrays.sort(receptiveField);
					sum = receptiveField[receptiveField.length/2];
				}
				result[x*i.width+y]= (byte)sum;
			}
		}

		i.img = result;

	}

	public static void main(String[] args) {
		new Lab2_9();
	}
}
