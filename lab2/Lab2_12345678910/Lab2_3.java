/**

In this task you will implement the method logTransformation of the class Lab2_3 which will apply the log transformation on the image.

To pass the test case please use the following value for c.

double c = 255 / Math.log(256);

The expected output is provided in the file solution.png.

You may use the following command to check if your output is identical to ours.

cmp solution.png out.png

If this command has no output, it implies that your solution has produced the same file as ours.

**/

import java.util.Scanner;
public class Lab2_3 {
	public Lab2_3() {
		Img img = new Img("Fig03164.png");
		logTransformation(img);
		img.save();
	}
	/**
     * Applies the log transformation.
     */
	public void logTransformation(Img i) {
		double c = 255 / Math.log(256);
		for(int j=0; j< i.img.length; j++){
			i.img[j] = (byte)(c* Math.log(1+(int)(i.img[j]&0XFF)));
		}

	}

	public static void main(String[] args) {
		new Lab2_3();
	}
}
