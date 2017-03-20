package cpc2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class Cpc {
	
	String impath = "C:\\Users\\emmett\\Desktop\\source - texture\\compress\\memnarch.jpg";
	//String impath = "C:\\Users\\ecoughlin7190\\Desktop\\green.jpg";
	BufferedImage impo;
	
	public static void main(String[] args) {
		new Cpc();
	}
	
	public int efunc(int y, int x){
		return((int)Math.abs(Math.cos(x)+Math.cos(y)+Math.sin(x)+Math.sin(y)));
	}
	
	public int func(int y, int x){
		return (((int)(0.125*(x+(int)(x/8)))+(int)(0.125*(y+(int)(y/8))))%2);
	}
	
	public double dfunc(int y, int x){
		return (( ((y*Math.PI)/(32-(Math.floor(y/8)*4))) * ((x*Math.PI)/(32-(Math.floor(x/8)*4)))  )%2);
	}
	
	public double nextfunc(int y, int x){
		if(y<8 && x<8){
			return 0;
		}
		return (int)(( ((y*Math.PI)/(8-(y/8))) * ((x*Math.PI)/(8-(x/8)))  )%2);
	}
	
	public double dctfunc(int y, int x){
		int n = y/8;
		int m = x/8;
		return (0.5*(Math.cos(((2*(7-x)+1)*(m)*Math.PI)/16.0)*Math.cos(((2*(7-y)+1)*(n)*Math.PI)/16.0)))+0.5;
	}
	
	public Cpc(){
		try{
			impo = ImageIO.read(new File(impath));
		}catch(Exception ex){}
		impo = resample(impo);
		//impo = ttest();
		save(impo);
	}
	
	public double dif(double a, double b){
		return (0.5-Math.abs(a-b))*2;
	}
	
	public int[][][] tricolore(BufferedImage b){
		int w = b.getWidth();
		int h = b.getHeight();
		int[][][] stem = new int[h][w][3];
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				Color cn = new Color(b.getRGB(x,y));
				stem[y][x][0] = cn.getRed();
				stem[y][x][1] = cn.getGreen();
				stem[y][x][2] = cn.getBlue();
			}
		}
		return stem;
	}
	
	public BufferedImage resample(BufferedImage b){
		int res = 8;//restriction
		int wac = (int)Math.ceil(b.getWidth()/8);
		int hac = (int)Math.ceil(b.getHeight()/8);
		BufferedImage out = new BufferedImage(wac*8,hac*8,BufferedImage.TYPE_INT_RGB);
		double[][][][][] matrix = new double[hac][wac][3][8][8];
		int[][][] cspace = tricolore(b);
		for(int ty=0;ty<hac;ty++){
			for(int tx=0;tx<wac;tx++){
				for(int c=0;c<3;c++){
					for(int py=0;py<res;py++){
						for(int px=0;px<res;px++){
							double fit = 0;
							for(int y=0;y<8;y++){
								for(int x=0;x<8;x++){
									int sx = px*8+x;
									int sy = py*8+y;
									double rs = func(sy,sx);
									double comp = (double)(cspace[(ty*8)+y][(tx*8)+x][c]/255.0);
									fit += dif(comp,rs);
								}
							}
							fit = fit/(res*res);
							matrix[ty][tx][c][py][px] = fit;
						}
					}
				}
				/*double[][][] atr = new double[3][8][8];
				for(int c=0;c<3;c++){
					for(int y=0;y<8;y++){
						for(int x=0;x<8;x++){
							atr[c][y][x] = 0;
						}
					}
				}
				matrix[0][0] = atr;*/
				/*matrix[0][0][0] = new double[][]{
						{1, 0.0, 0.0, 0.0,      0.0,      0.0,      0.0, 0.0},
						{0.0, 0.0, 0.0, 0.0,      0.0,      0.0,      0.0, 0.0},
						{0.0, 0.0, 0.0, 0.0,      0.0,      0.0,      0.0, 0.0},
						{0.0, 0.0, 0.0, 0.0,      0.0,      0.0,      0.0, 0.0},
						{0.0, 0.0, 0.0, 0.0,      0.0,      0.0,      0.0, 0.0},
						{0.0, 0.0, 0.0, 0.0,      0.0,      0.0,      0.0, 1},
						{0.0, 0.0, 0.0, 0.0,      0.0,      0.0,      1, 1},
						{0.0, 0.0, 0.0, 0.0,      0.0,      1,      1, 1}
				};*/
				for(int y=0;y<8;y++){
					for(int x=0;x<8;x++){
						double[] tot = new double[3];
						for(int c=0;c<3;c++){
							tot[c] = 0;
							for(int p0=0;p0<res;p0++){
								for(int p1=0;p1<res;p1++){
									int sy = p0*8+y;
									int sx = p1*8+x;
									double ad = ( ((func(sy,sx))*2-1) * matrix[ty][tx][c][p0][p1]);
									tot[c] += ad;
								}
							}
							//System.out.println(tot[c]);///////////////////////////////////
							tot[c] = ((tot[c]*128)+128);
							//System.out.println(tot[c]);
							if(tot[c]>255){
								tot[c] = 255;
							}
							if(tot[c]<0){
								tot[c] = 0;
							}
						}
						int cn = new Color((int)tot[0],(int)tot[1],(int)tot[2]).getRGB();
						out.setRGB((tx*8)+x, (ty*8)+y, cn);
					}
				}
			}
		}
		for(int y=0;y<8;y++){
			for(int x=0;x<8;x++){
				String t = ((int)(matrix[0][0][0][y][x]*100)/100.0)+" ";
				int u = t.length();
				for(int i=0;i<9-u;i++){
					t+= " ";
				}
				System.out.print(t);
			}
			System.out.println();
		}
		return out;
	}
	
	public BufferedImage ttest(){
		BufferedImage b = new BufferedImage(64,64,BufferedImage.TYPE_INT_RGB);
		for(int y=0;y<64;y++){
			for(int x=0;x<64;x++){
				int ay = (int)(func(y,x)*255);
				double rs = new Color(ay,ay,ay).getRGB();
				b.setRGB(x, y, (int)rs);
			}
		}
		return b;
	}
	
	public void save(BufferedImage b){
		String[] sb = bases(impath);
		File f = new File(sb[0]+sb[1]+"peen.png");
		try{
			ImageIO.write(b, "PNG", f);
		}catch(Exception ex){}
	}
	
	public String[] bases(String s){
		String[] l = new String[3];
		int[] mark = new int[l.length];
		String temp = "";
		for(int i=s.length()-1;i >= 0;i--){
			char u = s.charAt(i);
			temp += u;
			if(u=='.'){
				mark[1] = i;
				l[2] = rev(temp);
				temp = "";
			}
			if(u=='\\'){
				mark[0] = i;
				temp = "";
				break;
			}
		}
		for(int i=0;i<s.length();i++){
			char u = s.charAt(i);
			temp += u;
			if(i==mark[0]){
				l[0] = temp;
				temp = "";
			}
			if(i==mark[1]-1){
				l[1] = temp;
				break;
			}
		}
		return l;
	}
	
	public String rev(String s){
		String temp = "";
		for(int i=s.length()-1;i >= 0;i--){
			temp += s.charAt(i);
		}
		return temp;
	}
}
