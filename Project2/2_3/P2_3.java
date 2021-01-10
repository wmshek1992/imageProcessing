/**
  * In this task you will implement the method cornerResponseImage of the class P2_3 which will change the image to the response map R of the Harris corner detector. As usual, ignore the boundary.
  *
  * Set pixels to 255 if R > threshold and otherwise set pixels to 0.
  *
  *  The solution files are provided for qualitative comparison. Output could be different because of differences in floating point arithmetic.
  **/
import java.util.Scanner;
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


	public double[] gaussianFilter(double sigma){
		double sum = 0;
		double peak_value = 1/(sigma*Math.sqrt(2*Math.PI)) * Math.exp(0);
		sum = sum + peak_value;

		int cnt = 0;
		double value = 1;
		while(value > peak_value * 0.001) {
		  cnt ++;
		  value = 1/(sigma*Math.sqrt(2*Math.PI)) * Math.exp(- cnt*cnt/ (2*sigma*sigma));

		  if(value > peak_value * 0.001){
			sum = sum + value*2;
		  }
		}

	//	System.out.printf("Size: %d%n", 2*(cnt-1)+1);

	//	System.out.printf("Mask: [");

		double[] res = new double[2*(cnt-1)+1];

		for(int i = -(cnt-1); i <= (cnt-1); i++){
		  res[i+cnt-1] =  (1/(sigma*Math.sqrt(2*Math.PI)) * Math.exp(- i*i/ (2*sigma*sigma))) * 1 / sum;
		  if(i != (cnt-1)){
		//	System.out.printf("%.17f, " , res[i+cnt-1]);
		  }else{
		//	System.out.printf("%.17f]%n", res[i+cnt-1]);
		  }
		}

		return res;
	}


	public double[] gaussianSmooth(double[] img, int i_width, int i_height, double sigma) {
		//Your code here
		double[] img_copy = new double[img.length];

		for(int y = 0; y < i_width; y++){
			for(int x = 0; x < i_height; x++){
			  img_copy[x*i_width+y] =  img[x*i_width+y];
			}
		}

		double[] filter = gaussianFilter(sigma);

		int size = filter.length;

		int a = (size -1) / 2;

		for(int y = a; y < i_width-a; y++){
		  for(int x = a; x < i_height-a; x++){
			double tmp_s = 0;
			for(int s = -a; s <= a; s++){
			  double tmp_t = 0;
			  for(int t = -a; t <= a; t++){
				tmp_t = tmp_t + filter[t+a] * img[(x-s)*i_width+y-t];
			  }
			  tmp_s = tmp_s + tmp_t * filter[s+a];
			}

			img_copy[x*i_width+y]  = tmp_s;
		  }
		}

		return img_copy;
	}

	public double[] gradientImage(Img i, int dir) {
		//Your code here
		double[] img_copy = new double[i.height * i.width];
		for(int x = 0; x < i.height; x++){
			for(int y = 0; y < i.width; y++){
				img_copy[x*i.width+y] = (double)(i.img[x*i.width+y]&0xff);
			}
		}
		for(int x =1; x < i.height-1; x++){
			for(int y = 1; y < i.width-1; y++){
				double tmp_x = 0;
				tmp_x = tmp_x - 1 * (double)(i.img[(x-1)*i.width+y]&0xff);
				tmp_x = tmp_x + (double)(i.img[(x+1)*i.width+y]&0xff);

				double tmp_y = 0;
				tmp_y = tmp_y -1 * (double)(i.img[x*i.width+y-1]&0xff);
				tmp_y = tmp_y + (double)(i.img[x*i.width+y+1]&0xff);

				if(dir == 0){
					img_copy[x*i.width+y] = tmp_x*tmp_x;
				}else if(dir == 1){
					img_copy[x*i.width+y] = tmp_y*tmp_y ;
				}else{
					double tmp = Math.sqrt(tmp_x*tmp_x + tmp_y*tmp_y);
					img_copy[x*i.width+y] = tmp;
				}
			}
		}
		return img_copy;
	}


	public void cornerResponseImage(Img i, double sigma, double threshold) {
		//Your code here
		double K = 0.04;

	/*	double[] fx2_img = gradientImage(i, 0);
		double[] fy2_img = gradientImage(i, 1);
		double[] fxy_img = gradientImage(i, 2);*/
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

		double[] gs_fx2_img = gaussianSmooth(fx, i.width, i.height, sigma);
		double[] gs_fy2_img = gaussianSmooth(fy, i.width, i.height, sigma);
		double[] gs_fxy_img = gaussianSmooth(fxy, i.width, i.height, sigma);

		for(int x = 0; x < i.height; x++){
			for(int y = 0; y < i.width; y++){
				int p_id = x*i.width+y;
				double det_A = gs_fx2_img[p_id] * gs_fy2_img[p_id] - gs_fxy_img[p_id] * gs_fxy_img[p_id];
				double trace_A = gs_fx2_img[p_id] + gs_fy2_img[p_id];
				//if(det_A != 0){
				//System.out.printf("det = %f, trace = %f%n", det_A, trace_A);
			//	}
				double R = det_A - K * trace_A * trace_A;

				if(R  > threshold){
					i.img[x*i.width+y] = (byte)255;
				}else{
					i.img[x*i.width+y] = 0;
				}
			}
		}
	}

	public static void main(String[] args) {
		new P2_3();
	}
}
