/**

In this task 3 of project 1 you will design an appropriate filter and apply it to the car image to attenuate the impulse-like bursts in the image. You may reuse existing code from your tasks 1 and 2.

Hint: Note that the spatial resolution of the car.png is not a power of 2, i.e., it is not of the form 2^m x 2^n. You should create a new suitable image with padded black pixels by changing code in the constructor of the class P1_3.

To avoid reverse engineering, we do not provide a sample solution for this task.

**/

import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.stream.*;
public class P1_3 {
	public P1_3() {
		//Change this code.
		Img img = new Img("car.png");
		Instant start = Instant.now();
		filterImage(img);
		Instant stop = Instant.now();
		System.out.println("Elapsed time: "+Duration.between(start, stop).getSeconds()+"s");
		img.save();
	}

    public void filterImage(Img i) {
			int originalWidth=i.width;
			int originalHeight=i.height;
		padding(i);

		Complex[] F = fastFourierTransfrom(i);
		shiftedHigherPassFilter(F,i,2,20);
		inverseFastFourierTransfrom(F, i);
		removePadding(originalHeight, originalWidth,i);
		//fourierSpectrum(F,i);

    }

		public void removePadding(int originalHeight, int originalWidth, Img i){
			byte[] oringnalImg = new byte[originalHeight*originalWidth];

			for(int x=(i.height - originalHeight)/2; x<(i.height + originalHeight)/2;x++){
				for(int y=(i.width - originalWidth)/2; y<(i.width + originalWidth)/2;y++){
						//System.out.println(x + " " + y);
						oringnalImg[ (x-(i.height - originalHeight)/2)*originalWidth + (y- (i.width - originalWidth)/2)] = i.img[x*i.width+y];
				}
			}
			i.height=originalHeight;
			i.width=originalWidth;
			i.img = oringnalImg;
		}
		public void shiftedHigherPassFilter(Complex[] F, Img i, int n, int d0){

			//H(u,v) = 1/ (1+d0/D)^2n, n=2
			for (int u=0; u< i.width; u++){
				for (int v=0; v < i.height; v++){
					int a = Math.abs(u-128);
					int b = Math.abs(v-128);
					a = Math.abs(a-42);
					b = Math.abs(b-45);
					double D = Math.sqrt( a*a+b*b);
					double h=	1/ (1 + Math.pow(d0/D,2*n) );
					F[u*i.width+v].mul(h);

					a = Math.abs(u-128);
					a = Math.abs(a-84);
					D = Math.sqrt( a*a+b*b);
					h=	1/ (1 + Math.pow(d0/D,2*n) );
					F[u*i.width+v].mul(h);
				}
			}
		}

		public void padding(Img im){
			int higherDimension = im.width;
			if(im.width< im.height) higherDimension = im.height;
			int finalLength=2;
			while(finalLength < higherDimension){
				finalLength*=2;
			}
			System.out.println("image width:"+im.width);
			System.out.println("image height:"+im.height);
			System.out.println("padding length:"+finalLength);
			byte[] newImg = new byte[finalLength*finalLength];
			for(byte b : newImg)
				b=(byte)0;

			for(int x = (finalLength-im.height)/2;x<(finalLength+im.height)/2 ;x++){
				for(int y = (finalLength-im.width)/2;y<(finalLength+im.width)/2 ;y++){

					newImg[x*finalLength+y] = im.img[(x-(finalLength-im.height)/2) *im.width+ (y-(finalLength-im.width)/2)];
				}
			}
			im.img = newImg;
			im.width = finalLength;
			im.height = finalLength;
		}
		public void fourierSpectrum(Complex[] F,Img i) {
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
			System.out.println("transform image width : "+i.width);
			System.out.println("transform image height : "+i.height);
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
				double result = f_x_y_oneRow[j].getNorm();
				if(result>255)
				{
					result = 255;
				}
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
		new P1_3();
	}
}
