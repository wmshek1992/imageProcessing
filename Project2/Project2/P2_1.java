/**
  * In this task you will implement the method gradientImage of the class P2_1 which will calculate the gradient image.
  *
  * To determine the gradient in images in x and y directions use the masks [-1 0 1]^T and [-1 0 1], respectively.
  *
  * Note that values should be scaled to [0, 255]. This can be done by multiplying with 1 / sqrt(2).
  *
  * The solution files are provided for qualitative comparison. Output could be different because of differences in floating point arithmetic.
  **/
public class P2_1 {
	public P2_1() {
		Img img = new Img("Fig0314a.png");
		gradientImage(img);
		img.save();
	}

	public void gradientImage(Img i) {
		//Your code here
		int[] yFilter = new int[]{-1,0,1};
		int[] xFilter = new int[]{-1,0,1};
		byte[] result = new byte[i.img.length];
		int overhead = (xFilter.length-1)/2;
		for(int x = overhead; x <i.height-overhead; x++ ){
			for(int y = overhead; y < i.width-overhead; y++){
					double gradient =0 ;
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
					gradient =  Math.sqrt(sumX*sumX+sumY*sumY);
					gradient /=  Math.sqrt(2);
					//if(gradient>255) System.out.println(gradient);

					//sum /= Math.sqrt(2);
					result[x*i.width+y] = (byte)gradient;
			}
		}
		i.img = result;

	}




	public static void main(String[] args) {
		new P2_1();
	}
}
