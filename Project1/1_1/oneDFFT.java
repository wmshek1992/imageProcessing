/**

In this task 1 of project 1 you will implement the fast Fourier transform and change the image to the Fourier spectrum in the method fourierSpectrum(). Your task is to implement the missing code in the method fastFourierTransform(). The implementation details of the FFT can be obtained in section 4.11 of our Textbook.

Use the log transformation and ensure that all values are in the range 0 ... 255.

You may use methods declared in the class Complex.java for your convenience and you may add new methods to that class if necessary.

The solution files are provided for qualitative comparison. Output could be different because of differences in floating point arithmetic and differences in the way the rescaling is performed.

For your reference, we are able generate the Fourier spectrum of the file rectangle1024.png in < 1 seconds.

**/

import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.stream.*;
public class oneDFFT {
	public oneDFFT() {
	/*	Img img = new Img("ic512.png");
		Instant start = Instant.now();
		fourierSpectrum(img);
		Instant stop = Instant.now();
		System.out.println("Elapsed time: "+Duration.between(start, stop).getSeconds()+"s");
		img.save();*/
		Complex[] input = new Complex[4];
		input[0] = new Complex(1,0);
		input[1] = new Complex(2,0);
		input[2] = new Complex(3,0);
		input[3] = new Complex(4,0);
	/*	input[4] = new Complex(5,0);
		input[5] = new Complex(6,0);
		input[6] = new Complex(7,0);
		input[7] = new Complex(8,0);*/
	/*	input[8] = new Complex(9,0);
		input[9] = new Complex(10,0);
		input[10] = new Complex(11,0);
		input[11] = new Complex(12,0);
		input[12] = new Complex(13,0);
		input[13] = new Complex(14,0);
		input[14] = new Complex(15,0);
		input[15] = new Complex(16,0);*/
		Complex[] result = oneDimensianlFFTShifted(input);
		//Complex[] result = oneDimensianlFFT(input);
		for (Complex c: result){
			System.out.println(c.r +" "+ c.i );
		}


	}

    public void fourierSpectrum(Img i) {
    	Complex[] F = fastFourierTransfrom(i);
		double max = Double.NEGATIVE_INFINITY;
		for (int x = 0; x < F.length; x++)
			max = Math.max(F[x].getNorm(), max);
		for (int x = 0; x < i.img.length; x++)
			i.img[x] = (byte)(255 / Math.log(256)*Math.log(255/max*F[x].getNorm()+1));
    }

    public Complex[] fastFourierTransfrom(Img i) {
    	//Change this code
    	Complex[] F = new Complex[i.width*i.height];

			Complex[] F_x_v = new Complex[i.width * i.height];
			//do one-direction FFT first for each X
			for(int x=0; x < i.height ; x++){
				 System.out.println("Computing first FFT at x = "+x);
				 byte[] inputByte = Arrays.copyOfRange(i.img, x * i.width, (x+1) * i.width);
				 //Complex[] f_x_y = Arrays.stream(inputByte).toArray( b-> new Complex( (double)(b&0XFF),0));
				 Complex[] f_x_y = new Complex[inputByte.length];
				 for (int j =0 ; j<inputByte.length; j++){
					 f_x_y[j] = new Complex((double)(inputByte[j]&0XFF), 0);

				 }

				 Complex[] F_x_v_oneRow = oneDimensianlFFT(f_x_y);
				 //put the one row FFT into F(x,v)
				 for(int j = 0; j < F_x_v_oneRow.length; j++)
				 {
					 F_x_v[j*i.width+x] = F_x_v_oneRow[j];
				 }
			}
			for (Complex c: F_x_v){
				System.out.println(c.r);
			}

			for(int y =0; y< i.width; y++){
				System.out.println("Computing second FFT at x = "+y);
				Complex[] f_x_v = Arrays.copyOfRange(F_x_v, y * i.height, (y+1) * i.height);
				Complex[] F_u_v_oneRow = oneDimensianlFFT(f_x_v);
				for(int j = 0; j < F_u_v_oneRow.length; j++)
				{
					F[j*i.width+y] = F_u_v_oneRow[j];
				}

			}

			for (Complex c: F){
				System.out.println(c.r);
			}



		return F;
	}


	public Complex[] oneDimensianlFFT(Complex[] f ){
			System.out.print("input:");
		for (Complex c: f){
			System.out.print(c.r + " ");
		}
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
		System.out.println("even part : ");
		for (Complex c: evenF_result){
			System.out.println(c.r + " " +c.i);
		}
		System.out.println("odd part : ");
		Complex[] oddF_result = oneDimensianlFFT(oddF );
		for (Complex c: oddF_result){
			System.out.println(c.r + " " +c.i);
		}

		for(int u =0 ; u < f.length /2; u++ ){

			F[u] = new Complex();
			F[u+f.length/2] = new Complex();
			double theta = (double)(-2 * Math.PI * u / (double)f.length);
			Complex oddPart = new Complex(Math.cos(theta), Math.sin(theta));
			System.out.println("\r\nu = "+ u);
			System.out.println("even result : "+ evenF_result[u].r + " "+ evenF_result[u].i);
			System.out.println("odd result : "+oddF_result[u].r + " "+ oddF_result[u].i);
			System.out.println("theta : "+theta);
			System.out.println("odd part : "+oddPart.r + " "+ oddPart.i);
			oddPart.mul(oddF_result[u]);
			System.out.println("odd part : "+oddPart.r + " "+ oddPart.i);

			F[u].plus(evenF_result[u]);
			F[u].plus(oddPart);



			oddPart.mul(-1);
			F[u+f.length/2].plus(evenF_result[u]);
			F[u+f.length/2].plus(oddPart);

		}
	return F;
}

public Complex[] oneDimensianlFFTShifted(Complex[] f ){
		System.out.print("input:");
	for (Complex c: f){
		System.out.print(c.r + " ");
	}
	Complex[] F =new Complex[f.length];
	if(f.length==2){
		F[0] = new Complex();
		F[1] = new Complex();
		F[0].plus(f[1]);
		F[0].mul(-1);
		F[0].plus(f[0]);

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
	Complex[] evenF_result = oneDimensianlFFTShifted(evenF );
	System.out.println("even part : ");
	for (Complex c: evenF_result){
		System.out.println(c.r + " " +c.i);
	}
	System.out.println("odd part : ");
	Complex[] oddF_result = oneDimensianlFFTShifted(oddF );
	for (Complex c: oddF_result){
		System.out.println(c.r + " " +c.i);
	}

	for(int u =0 ; u < f.length /2; u++ ){

		F[u] = new Complex();
		F[u+f.length/2] = new Complex();
		double theta = (double)(-2 * Math.PI * u / (double)f.length);
		Complex oddPart = new Complex(Math.cos(theta), Math.sin(theta));
		System.out.println("\r\nu = "+ u);
		System.out.println("even result : "+ evenF_result[u].r + " "+ evenF_result[u].i);
		System.out.println("odd result : "+oddF_result[u].r + " "+ oddF_result[u].i);
		System.out.println("theta : "+theta);
		System.out.println("odd part : "+oddPart.r + " "+ oddPart.i);
		oddPart.mul(oddF_result[u]);
		System.out.println("odd part : "+oddPart.r + " "+ oddPart.i);

		F[u+f.length/2].plus(evenF_result[u]);
		F[u+f.length/2].plus(oddPart);

		oddPart.mul(-1);

		F[u].plus(evenF_result[u]);
		F[u].plus(oddPart);

	}
return F;
}

	public static void main(String[] args) {
		new oneDFFT();
	}
}
