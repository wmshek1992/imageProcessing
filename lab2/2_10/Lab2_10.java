/**

In this task you will implement the method laplacianFilter of the class Lab2_10 which applies the Laplacian filter on the image.

Implement the isotropic mask for rotations in  increments of 45 degrees with a positive weight at the center. Clip all values to be within 0 to 255.

The expected output is provided in the file solution.png.

You may use the following command to check if your output is identical to ours.

cmp solution.png out.png

If this command has no output, it implies that your solution has produced the same file as ours.

**/

import java.util.Scanner;
public class Lab2_10 {
	public Lab2_10() {
		Img img = new Img("Fig0338.png");
		laplacianFilter(img);
		img.save();
		check();
	}

	public void laplacianFilter(Img i) {
		int[] filter = new int[]{-1,-1,-1,-1,8,-1,-1,-1,-1};

		int size = 3;
		byte[] result = new byte[i.img.length];
		int boundaryOffset = (size-1)/2;
		for (int x = 0 ; x < i.height; x ++){
			for (int y = 0 ; y < i.width ; y ++){
				int sum =0;
				if(x<boundaryOffset || x>i.height-boundaryOffset-1 || y< boundaryOffset || y > i.width-boundaryOffset-1){
					sum = i.img[x*i.width+y];
				}
				else{

					for(int filterX = -boundaryOffset; filterX <= boundaryOffset; filterX++ ){
						for(int filterY = -boundaryOffset; filterY <= boundaryOffset; filterY++ ){
							int or = (int)(i.img[ (x+filterX) * i.width + y + filterY]&0XFF);
							int r = or *  filter[ (filterX+boundaryOffset) *size +  filterY + boundaryOffset ];
							sum += r;
						}
					}

					//sum = sum/9;

					if(sum<0) sum=0;
					else if (sum > 255) sum=255;
				}
				result[x*i.width+y]= (byte)sum;
			}
		}

		i.img = result;
	}

	public void check(){
			Img solImg = new Img("solution.png");
			Img out = new Img("out.png");
		for(int j=0; j< solImg.img.length; j++){
				if(solImg.img[j] != out.img[j])
				{
						System.out.println("*** incorret byte:" + j);
						System.out.println("solution : "+(int)(solImg.img[j]&0XFF));
						System.out.println("out : "+ (int)(out.img[j]&0XFF));
						break;
				}
		}
	}


	public static void main(String[] args) {
		new Lab2_10();
	}
}
