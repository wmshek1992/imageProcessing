/**

In this task you will implement the method histogramEqualization of the class Lab2_7 which will perform histogram equalization.

The expected output is provided in the files solution1.png and solution2.png.

You may use the following command to check if your output is identical to ours.

cmp solution1.png out.png

If this command has no output, it implies that your solution has produced the same file as ours.

**/

import java.util.Scanner;
public class Lab2_7 {
	public Lab2_7() {
		Img img = new Img("Fig03161.png");
		histogramEqualization(img);
		img.save("out1.png");
		//check(1);
		img = new Img("HawkesBay.png");
		histogramEqualization(img);
		img.save("out2.png");
		//check(2);
	}

	public void histogramEqualization(Img i) {
		//Your code here
		int[] cumulativeHistogram = cumulativeHistogram(i);
		for(int j=0; j< i.img.length; j++){
			i.img[j] = (byte)(cumulativeHistogram[ (i.img[j]&0XFF) ] * 255 / i.img.length);
		}
	}

	public int[] cumulativeHistogram(Img i) {
		//Your code here
		int[] returnValue = new int[256];
		int[] histogram = histogram(i);
		returnValue[0]=histogram[0];
		for(int j=1; j<256; j++)
			 returnValue[j] = returnValue[j-1] + histogram[j];
		return returnValue;
	}

	public int[] histogram(Img i) {
		//Your code here
		int[] histogram = new int[256];
		for(int j=0; j< i.img.length; j++){
			histogram[ (i.img[j]&0XFF)]++;
		}
		return histogram;
	}

	public void check(int mask){
			String solFileName = "solution"+mask+".png";
			String outFileName = "out"+mask+".png";
			Img solImg = new Img(solFileName);
			Img out = new Img(outFileName);
		for(int j=0; j< solImg.img.length; j++){
				if(solImg.img[j] != out.img[j])
				{
						System.out.println("*** incorret byte:" + j);
						System.out.println("solution : "+(int)(solImg.img[j]&0XFF));
						System.out.println("out : "+ (int)(out.img[j]&0XFF));
						break;
				}
		}
		System.out.println(solFileName + " & " + outFileName + " are identical");
	}

	public static void main(String[] args) {
		new Lab2_7();
	}
}
