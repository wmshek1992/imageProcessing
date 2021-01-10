/**

In this task 2 of project 1 you will implement the inverse fast Fourier transform and perform second order (n=2) ButterWorth low pass filtering in the frequency domain. You should reuse your implementation of the fast Fourier transform from the previous task of this project (P1_1).

In the filterImage() method add your code for the second order (n=2) ButterWorth low pass filtering.

Implement the inverse fast Fourier transform in the method inverseFourierTransfrom().

You may use methods declared in the class Complex.java for your convenience.

The solution file is provided for qualitative comparison. It was generated with d0=10, i.e., with the command

java P1_2 10

Output could be different because of differences in floating point arithmetic and differences in the way the rescaling is performed.

**/

import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.stream.*;
public class P1_2 {
	public P1_2(double d0) {
		Img img = new Img("ic512.png");
		Instant start = Instant.now();
		filterImage(img, d0);
		Instant stop = Instant.now();
		System.out.println("Elapsed time: "+Duration.between(start, stop).getSeconds()+"s");
		img.save();
	}

    public void filterImage(Img i, double d0) {
			int n =2;
		Complex[] F = fastFourierTransfrom(i);
		//Your code here
		//high pass filter
		//H(u,v) = 1/ (1+d0/D)^2n, n=2
		/*for (int u=0; u< i.width; u++){
			for (int v=0; v < i.height; v++){
				double D = distance(u,v,i);
				double h=	1/ (1 + Math.pow(d0/D,2*n) );
				F[u*i.width+v].mul(h);
			}
		}*/
		//low pass filter
		//H(u,v) = 1/ (1+D/d0)^2n, n=2
		for (int u=0; u< i.width; u++){
			for (int v=0; v < i.height; v++){
				double D = distance(u,v,i);
				double h=	1/ (1 + Math.pow(D/d0,2*n) );
				F[u*i.width+v].mul(h);
			}
		}
		//fourierSpectrum(i);

		inverseFastFourierTransfrom(F, i);
    }


		public void fourierSpectrum(Img i) {
			Complex[] F = fastFourierTransfrom(i);
		double max = Double.NEGATIVE_INFINITY;
		for (int x = 0; x < F.length; x++)
			max = Math.max(F[x].getNorm(), max);
		for (int x = 0; x < i.img.length; x++)
			i.img[x] = (byte)(255 / Math.log(256)*Math.log(255/max*F[x].getNorm()+1));
		}

		public double distance(int u,int v, Img i){
			double p = (double) (Math.pow(v-i.height/2,2) +Math.pow(u-i.width/2,2));
			return Math.sqrt(p);
		}

    public Complex[] fastFourierTransfrom(Img i) {


			Complex[] F = new Complex[i.width*i.height];

			Complex[] F_x_v = new Complex[i.width * i.height];
			//do one-direction FFT first for each X
			for(int x=0; x < i.height ; x++){
				 byte[] inputByte = Arrays.copyOfRange(i.img, x * i.width, (x+1) * i.width);
				 //Complex[] f_x_y = Arrays.stream(inputByte).toArray( b-> new Complex( (double)(b&0XFF),0));
				 Complex[] f_x_y = new Complex[inputByte.length];

				 for (int j =0 ; j<inputByte.length; j++){
					 f_x_y[j] = new Complex((double)(inputByte[j]&0XFF), 0);
				 }
				 Complex[] F_x_v_oneRow = oneDimensianlFFTShifted(f_x_y);
				 //put the one row FFT into F(x,v)
				 for(int j = 0; j < F_x_v_oneRow.length; j++)
				 {
					 F_x_v[j*i.width+x] = F_x_v_oneRow[j];
				 }
			}

			for(int y =0; y< i.width; y++){
			//	System.out.println("Computing second FFT at x = "+y);
				Complex[] f_x_v = Arrays.copyOfRange(F_x_v, y * i.height, (y+1) * i.height);
				Complex[] F_u_v_oneRow = oneDimensianlFFTShifted(f_x_v);
				for(int j = 0; j < F_u_v_oneRow.length; j++)
				{
					F[j*i.width+y] = F_u_v_oneRow[j];
					//i.img[j*i.width+y] = (F_u_v_oneRow[j]);
				}
			}
		return F;
	}

	private void inverseFastFourierTransfrom(Complex[] F, Img i) {
		//Complex[] F = new Complex[i.width*i.height];

		Complex[] F_u_y = new Complex[i.width * i.height];
		//do one-direction FFT first for each X
		for(int u=0; u < i.height ; u++){
				Complex[] F_u_v_oneRow = Arrays.copyOfRange(F, u * i.width, (u+1) * i.width);
			 	Complex[] F_u_y_oneRow = oneDInverseFFT(F_u_v_oneRow);
			 //put the one row FFT into F(u,y)
			 for(int j = 0; j < F_u_y_oneRow.length; j++)
			 {
				// F_x_v_oneRow[j].div(F_x_v_oneRow.length);
				 //F_u_y_oneRow[j].mul(Math.pow(-1,j));
				 //System.out.print(F_x_v_oneRow[j].r + " ");
				 F_u_y[j*i.width+u] = F_u_y_oneRow[j];
			 }
		}

		for(int y =0; y< i.width; y++){
		//	System.out.println("Computing second FFT at x = "+y);
			Complex[] F_u_y_oneRow = Arrays.copyOfRange(F_u_y, y * i.height, (y+1) * i.height);
			Complex[] f_x_y_oneRow = oneDInverseFFT(F_u_y_oneRow);
			for(int j = 0; j < f_x_y_oneRow.length; j++)
			{
				//f_x_y_oneRow[j].mul(Math.pow(-1,j+y));
				f_x_y_oneRow[j].div(i.height*i.width);
				//if(f_x_y_oneRow[j].r<0) f_x_y_oneRow[j].r =0 ;
				//System.out.print(f_x_y_oneRow[j].r + " ");
				//i.img[i.img.length - (j*i.width+y+1)] = (byte)(f_x_y_oneRow[j].getNorm());
				int result = (int)f_x_y_oneRow[j].getNorm();
				i.img[j*i.width+y] = (byte)result;

			}
		}

	}

	public Complex[] oneDimensianlFFTShifted(Complex[] f ){
		Complex[] F =new Complex[f.length];
		if(f.length==2){
			F[0] = new Complex();
			F[0].plus(f[1]);
			F[0].mul(-1);
			F[0].plus(f[0]);

			F[1] = new Complex();
			F[1].plus(f[1]);
			//F[1].mul(-1);
			F[1].plus(f[0]);
			return F;
		}

		Complex[] evenF = new Complex[f.length/2];
		Complex[] oddF = new Complex[f.length/2];
		for (int index =0 ; index < f.length; index ++){
		//	Complex inputComplex = new Complex((double)(f[index]&OXFF) , 0);

			if(index%2 ==0 ) evenF[index/2]=f[index];
			else oddF[index/2]=f[index];
		}

		//pseudo code
		// for each u , split f into even and odd
		// compute u as FFT(even) + FFT(odd) * Wm^u
		// compute u+K as FFT(even) - FFT(odd) * Wm^u
		Complex[] evenF_result = oneDimensianlFFT(evenF );
		Complex[] oddF_result = oneDimensianlFFT(oddF );
		for(int u =0 ; u < f.length /2; u++ ){
			F[u] = new Complex();
			F[u+f.length/2] = new Complex();
			double theta = (double)(-2 * Math.PI * u / (double)f.length);
			Complex oddPart = new Complex(Math.cos(theta), Math.sin(theta));
			oddPart.mul(oddF_result[u]);
			F[u+f.length/2].plus(evenF_result[u]);
			F[u+f.length/2].plus(oddPart);

			oddPart.mul(-1);


			F[u].plus(evenF_result[u]);
			F[u].plus(oddPart);
		}
		return F;
	}

	public Complex[] oneDimensianlFFT(Complex[] f ){
		Complex[] F =new Complex[f.length];
		if(f.length==2){
			F[0] = new Complex();
			F[1] = new Complex();
			F[0].plus(f[1]);
			//F[0].mul(-1);
			F[0].plus(f[0]);


			F[1].plus(f[1]);
			F[1].mul(-1);
			F[1].plus(f[0]);
			return F;
		}

		Complex[] evenF = new Complex[f.length/2];
		Complex[] oddF = new Complex[f.length/2];
		for (int index =0 ; index < f.length; index ++){
		//	Complex inputComplex = new Complex((double)(f[index]&OXFF) , 0);

			if(index%2 ==0 ) evenF[index/2]=f[index];
			else oddF[index/2]=f[index];
		}

		//pseudo code
		// for each u , split f into even and odd
		// compute u as FFT(even) + FFT(odd) * Wm^u
		// compute u+K as FFT(even) - FFT(odd) * Wm^u
		Complex[] evenF_result = oneDimensianlFFT(evenF );
		Complex[] oddF_result = oneDimensianlFFT(oddF );
		for(int u =0 ; u < f.length /2; u++ ){
			F[u] = new Complex();
			F[u+f.length/2] = new Complex();
			double theta = (double)(-2 * Math.PI * u / (double)f.length);
			Complex oddPart = new Complex(Math.cos(theta), Math.sin(theta));
			oddPart.mul(oddF_result[u]);
			F[u].plus(evenF_result[u]);
			F[u].plus(oddPart);

			oddPart.mul(-1);
			F[u+f.length/2].plus(evenF_result[u]);
			F[u+f.length/2].plus(oddPart);
		}
	return F;
	}

	public Complex[] oneDInverseFFT(Complex[] f ){
		Complex[] F =new Complex[f.length];
		int k = f.length/2;
		if(f.length==2){
			F[0] = new Complex();
			F[1] = new Complex();
			F[0].plus(f[1]);
			//F[0].mul(-1);
			F[0].plus(f[0]);


			F[1].plus(f[1]);
			F[1].mul(-1);
			F[1].plus(f[0]);
			return F;
		}

		Complex[] evenF = new Complex[k];
		Complex[] oddF = new Complex[k];
		for (int index =0 ; index < f.length; index ++){
		//	Complex inputComplex = new Complex((double)(f[index]&OXFF) , 0);

			if(index%2 ==0 ) evenF[index/2]=f[index];
			else oddF[index/2]=f[index];
		}

		//pseudo code
		// for each u , split f into even and odd
		// compute u as FFT(even) + FFT(odd) * Wm^u
		// compute u+K as FFT(even) - FFT(odd) * Wm^u
		Complex[] evenF_result = oneDInverseFFT(evenF );
		Complex[] oddF_result = oneDInverseFFT(oddF );
		for(int u =0 ; u < k; u++ ){
			F[u] = new Complex();
			F[u+k] = new Complex();
			double theta = (double)(2 * Math.PI * u / (double)f.length);
			Complex oddPart = new Complex(Math.cos(theta), Math.sin(theta));
			oddPart.mul(oddF_result[u]);
			F[u].plus(evenF_result[u]);
			F[u].plus(oddPart);

			oddPart.mul(-1);
			F[u+k].plus(evenF_result[u]);
			F[u+k].plus(oddPart);

			// normalize
			//double norm = 1/f.length;
			//F[u].div(f.length);
			//F[u+k].div(f.length);
		}
	return F;
	}
	public static void main(String[] args) {
		new P1_2(Double.parseDouble(args[0]));
	}
}
