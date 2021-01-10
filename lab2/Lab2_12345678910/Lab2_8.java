/**

In this task you will implement the method boxFilter of the class Lab2_8 which applies the box smooth filter on the image i.

To pass the test case you should handle the boundary case by keeping the pixels unchanged.

The expected output is provided in the files solution3.png and solution7.png, where the digit in the filename is the threshold.

The solution files are provided for qualitative comparison. Output could be slightly different because of differences in floating point arithmetic.

**/

import java.util.Scanner;
import java.time.Instant;
import java.time.Duration;
public class Lab2_8 {
	public Lab2_8() {
		Img img = new Img("Fig0441.png");
		System.out.print("Size: ");
		Scanner in = new Scanner(System.in);
		int size = in.nextInt();
		Instant start = Instant.now();
		boxFilter(img, size);
		Instant stop = Instant.now();
		System.out.println("Elapsed time: "+Duration.between(start, stop).toMillis()+"ms");
		img.save();
	}

	public void boxFilter(Img i, int size) {
		float[] filter = new float[size*size];
		for(int j=0; j < size* size ; j++)
			filter[j] = (float)1/(size*size);
		byte[] result = new byte[i.img.length];
		int boundaryOffset = (size-1)/2;
		for (int x = 0 ; x < i.height; x ++){
			for (int y = 0 ; y < i.width ; y ++){
				float sum =0;
				if(x<boundaryOffset || x>i.height-boundaryOffset-1 || y< boundaryOffset || y > i.width-boundaryOffset-1){
					sum = i.img[x*i.width+y];
				}
				else{

					for(int filterX = -boundaryOffset; filterX <= boundaryOffset; filterX++ ){
						for(int filterY = -boundaryOffset; filterY <= boundaryOffset; filterY++ ){
							float or = (i.img[ (x+filterX) * i.width + y + filterY]&0XFF);
							float r = or *  filter[ (filterX+boundaryOffset) *size +  filterY + boundaryOffset ];
							sum += r;
						}
					}
				}
				result[x*i.width+y]= (byte)sum;
			}
		}

		i.img = result;

	}

	public static void main(String[] args) {
		new Lab2_8();
	}
}
