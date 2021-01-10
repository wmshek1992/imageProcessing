	/**
  * In this task you will implement the method gaussianSmooth of the class P2_2 which will apply 2D Gaussian smoothing to the image.
  *
  * You should implement the 2D convolution using 1D masks (first x then y) for performance reasons. You should also output the size of the mask and the values used for the smoothing mask.
  *
  * Note that you should cut off the Gaussian, as discussed in class. Consider the following input/output:
  *
  * Sigma: 0.5
  * Size: 3
  * Mask: [0.10650697891920077, 0.7869860421615985, 0.10650697891920077]
  *
  * Sigma: 1
  * Size: 7
  * Mask: [0.004433048175243746, 0.05400558262241449, 0.24203622937611433, 0.3990502796524549, 0.24203622937611433, 0.05400558262241449, 0.004433048175243746]
  *
  * Note that the mask is always symmetric and sums to one.
  * Don't worry if you cannot generate the exact values. We will manually check the correctness of your solution.
  * For simplicity, you should handle the boundary case simply by using the original intensities there.
  *
  * The solution files are provided for qualitative comparison. They have been generated with input 1 and 0.5. Output could be different because of differences in floating point arithmetic.
  **/
import java.util.*;
public class P2_2 {
	public P2_2() {
		Img img = new Img("Fig0457.png");
		System.out.print("Sigma: ");
		Scanner in = new Scanner(System.in);
		double s = in.nextDouble();
		gaussianSmooth(img, s);
		img.save();
	}

	public void gaussianSmooth(Img i, double sigma) {

		double[] gaussianFilter = findFilter(sigma);
		byte[] result_y = new byte[i.img.length];
		int overhead= (gaussianFilter.length-1)/2;
		for(int x=0; x < i.height; x++ ){
			// boundary
			if(x<overhead || x>= i.height-overhead){
				for(int y =0; y< i.width; y++)
					result_y[x*i.width+y] = i.img[x*i.width+y];
				continue;
			}
			//not boundary
			byte[] inputByte = Arrays.copyOfRange(i.img, x * i.width, (x+1) * i.width);
			byte[] oneRowResult = oneDfilter(inputByte, gaussianFilter);
			for(int y =0; y< i.width;y++ ){
				result_y[x*i.width+y] = oneRowResult[y];
			}
		}

		byte[] result = new byte[i.img.length];
		for(int y = 0; y< i.width;y++ ){
			// boundary
			if(y<overhead || y>= i.width-overhead){
				for(int x =0; x< i.height; x++)
					result[x*i.width+y] = i.img[x*i.width+y];
				continue;
			}
			//not boundary
			byte[] inputByte = new byte[i.height];
			for(int x=0; x< i.height; x++ )
				inputByte[x] = result_y[x*i.width+y];
			byte[] oneRowResult = oneDfilter(inputByte, gaussianFilter);
			for(int x =0; x< i.height;x++ )
				result[x*i.width+y] = oneRowResult[x];


		}
		i.img = result;

	}

	public byte[] oneDfilter(byte[] input, double[] filter){
		int overhead= (filter.length-1)/2;
		byte[] result = new byte[input.length];
		for(int x =0; x< input.length; x++){
			if(x<overhead || x>= input.length-overhead ){
				result[x] = input[x];
				continue;
			}
			double sum=0;
			for(int j=0; j< filter.length; j++){
					sum += (double)(input[x-overhead+j]&0XFF) *filter[j];
			}
			result[x]= (byte)sum;
		}
		return result;
	}

	public double[] findFilter(double sigma){
		int size = (int)(sigma *  Math.sqrt(6* Math.log(10)));
		size = size*2+1;
		System.out.println("size : " + size);
		double[] gaussianFilter = new double[size];


		for(int x=0;x< size/2+1; x++ ){
			double theta = -x*x/(2*sigma*sigma);
			gaussianFilter[size/2 + x] = Math.exp(theta)/(sigma * Math.sqrt(2*Math.PI));
			gaussianFilter[size/2 -x] = gaussianFilter[size/2 + x];
		}

		//find the norm
		double norm = 0;
	 	for(int j=0;j< gaussianFilter.length; j++)
			norm+= gaussianFilter[j];

		//normalize
		for(int j=0;j< gaussianFilter.length; j++)
			gaussianFilter[j]/=norm;

		System.out.print("[" + gaussianFilter[0]);
		for(int j=1;j< gaussianFilter.length; j++){
			System.out.print("," + gaussianFilter[j]);
		}
		System.out.print("]");
		return gaussianFilter;
	}

	public static void main(String[] args) {
		new P2_2();
	}
}
