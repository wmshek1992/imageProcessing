/**
  * In this task you will implement the method cornerResponseImage of the class P2_3 which will change the image to the response map R of the Harris corner detector. As usual, ignore the boundary.
  *
  * Set pixels to 255 if R > threshold and otherwise set pixels to 0.
  *
  *  The solution files are provided for qualitative comparison. Output could be different because of differences in floating point arithmetic.
  **/
import java.util.*;
public class P2_3 {
	public P2_3() {
		Img img = new Img("chessmat.png");
		System.out.print("Sigma: ");
		Scanner in = new Scanner(System.in);
		double s = in.nextDouble();
		System.out.print("Threshold: ");
		double t = in.nextDouble();
		cornerResponseImage(img, s, t);
		img.save();
	}

	public void cornerResponseImage(Img i, double sigma, double threshold) {

		double[] fx = new double[i.img.length];
		double[] fy = new double[i.img.length];
		double[] fxy = new double[i.img.length];

		int[] yFilter = new int[]{-1,0,1};
		int[] xFilter = new int[]{-1,0,1};
		int overhead = (xFilter.length-1)/2;
		for(int x = overhead; x <i.height-overhead; x++ ){
			for(int y = overhead; y < i.width-overhead; y++){
					int sumY = 0;
					for(int k =0; k< yFilter.length; k++){
						int value = (int) (i.img[x*i.width+y-overhead+k]&0XFF);
						sumY += value * yFilter[k];
					}

					int sumX =0;
					for(int k =0; k< xFilter.length; k++){
						int value = (int) (i.img[(x-overhead+k  )*i.width+y]&0XFF);
						sumX += value * xFilter[k];
					}
					fx[x * i.width + y]=sumY*sumY;
					fy[x * i.width + y]=sumX*sumX;
					fxy[x * i.width + y]=sumX*sumY;
			}
		}

		//do gaussianSmooth
		fx = gaussianSmooth(i, fx, sigma);
		fy = gaussianSmooth(i, fy, sigma);
		fxy = gaussianSmooth(i, fxy, sigma);

		byte[] byteResult = new byte[i.img.length];
		for(int x = overhead; x <i.height-overhead; x++ ){
			for(int y = overhead; y < i.width-overhead; y++){
					int index = x * i.width + y;
					double detA = fx[index]*fy[index] - fxy[index]*fxy[index];
					double traceA = fx[index] + fy[index] ;
					double r =  detA - 0.04* traceA*traceA;
					if (r>threshold) r=255;
					else r=0;
					byteResult[index] = (byte)r;
			}
		}
		i.img = byteResult;

	}
	public double[] gaussianSmooth(Img i,double[] inputImage ,double sigma) {

		double[] gaussianFilter = findFilter(sigma);
		double[] result_y = new double[i.img.length];
		int overhead= (gaussianFilter.length-1)/2;
		for(int x=0; x < i.height; x++ ){
			// boundary
			if(x<overhead || x>= i.height-overhead){
				for(int y =0; y< i.width; y++)
					result_y[x*i.width+y] = inputImage[x*i.width+y];
				continue;
			}
			//not boundary
			double[] inputByte = Arrays.copyOfRange(inputImage, x * i.width, (x+1) * i.width);
			double[] oneRowResult = oneDfilter(inputByte, gaussianFilter);
			for(int y =0; y< i.width;y++ ){
				result_y[x*i.width+y] = oneRowResult[y];
			}
		}

		double[] result = new double[i.img.length];
		for(int y = 0; y< i.width;y++ ){
			// boundary
			if(y<overhead || y>= i.width-overhead){
				for(int x =0; x< i.height; x++)
					result[x*i.width+y] = i.img[x*i.width+y];
				continue;
			}
			//not boundary
			double[] inputDouble = new double[i.height];
			for(int x=0; x< i.height; x++ )
				inputDouble[x] = result_y[x*i.width+y];
			double[] oneRowResult = oneDfilter(inputDouble, gaussianFilter);
			for(int x =0; x< i.height;x++ )
				result[x*i.width+y] = oneRowResult[x];
		}
		return result;

	}
	public double[] oneDfilter(double[] input, double[] filter){
		int overhead= (filter.length-1)/2;
		double[] result = new double[input.length];
		for(int x =0; x< input.length; x++){
			if(x<overhead || x>= input.length-overhead ){
				result[x] = input[x];
				continue;
			}
			double sum=0;
			for(int j=0; j< filter.length; j++){
					sum += input[x-overhead+j] *filter[j];
			}
			result[x]= sum;
		}
		return result;
	}
	public double[] findFilter(double sigma){
			int size = (int)(sigma *  Math.sqrt(6* Math.log(10)));
			size = size*2+1;
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

			return gaussianFilter;
		}

	public static void main(String[] args) {
		new P2_3();
	}
}
