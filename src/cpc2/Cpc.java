package cpc2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class Cpc {
	
	//String impath = "C:\\Users\\emmett\\Desktop\\source - texture\\compress\\memnarch.jpg";
	String impath = "C:\\Users\\ecoughlin7190\\Desktop\\abc.png";
	BufferedImage impo;
	
	public static void main(String[] args) {
		new Cpc();
	}
	
	public int efunc(int y, int x){
		return((int)Math.abs(Math.cos(x)+Math.cos(y)+Math.sin(x)+Math.sin(y)));
	}
	
	public int sdfunc(int y, int x){
		return (((int)(0.125*(x+(int)(x/8)))+(int)(0.125*(y+(int)(y/8))))%2);
	}
	
	public int func(int y, int x){
		return((int)( (y*Math.E)*(x*Math.PI)-(y*x) )%2);
	}
	
	public Cpc(){
		try{
			impo = ImageIO.read(new File(impath));
		}catch(Exception ex){}
		impo = resample(impo);
		//impo = ttest();
		save(impo);
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
		int[][][][][] matrix = new int[hac][wac][8][8][3];
		int[][][] cspace = tricolore(b);
		for(int ty=0;ty<hac;ty++){
			for(int tx=0;tx<wac;tx++){
				for(int c=0;c<3;c++){
					for(int py=0;py<res;py++){
						for(int px=0;px<res;px++){
							int fit = 0;
							for(int y=0;y<8;y++){
								for(int x=0;x<8;x++){
									int sx = px+x;
									int sy = py+y;
									int rs = func(sy,sx)*255;
									int comp = cspace[(ty*8)+y][(tx*8)+x][c];
									fit += (128-Math.abs(comp-rs));
								}
							}
							fit = fit/(res*res);
							matrix[ty][tx][py][px][c] = fit;
						}
					}
				}
				
				

				for(int y=0;y<8;y++){
					for(int x=0;x<8;x++){
						int[] tot = new int[3];
						for(int c=0;c<3;c++){
							tot[c] = 128;
							for(int p0=0;p0<res;p0++){
								for(int p1=0;p1<res;p1++){
									int sy = p0+y;
									int sx = p1+x;
									tot[c] += ( (func(sy,sx)*2-1) * (matrix[ty][tx][p0][p1][c]));
								}
							}
							if(tot[c]<0){
								tot[c] = 0;
							}
							if(tot[c]>255){
								tot[c] = 255;
							}
						}
						int cn = new Color(tot[0],tot[1],tot[2]).getRGB();
						out.setRGB((tx*8)+x, (ty*8)+y, cn);
					}
				}
			}
		}
		for(int y=0;y<8;y++){
			String a = "";
			for(int x=0;x<8;x++){
				String t = matrix[0][0][y][x][1]+"";
				int u = t.length();
				for(int i=0;i<3-u;i++){
					t+= " ";
				}
				a += t+" ";
			}
			System.out.println(a);
		}
		return out;
	}
	
	public BufferedImage ttest(){
		BufferedImage b = new BufferedImage(64,64,BufferedImage.TYPE_INT_RGB);
		for(int y=0;y<64;y++){
			for(int x=0;x<64;x++){
				int rs = func(y,x)*255;
				b.setRGB(x, y, rs);
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
