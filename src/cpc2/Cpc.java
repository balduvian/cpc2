package cpc2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Cpc {
	
	String impath = "C:\\Users\\ecoughlin7190\\Desktop\\green.jpg";
	BufferedImage impo;
	
	public static void main(String[] args) {
		new Cpc();
	}
	
	public Cpc(){
		try{
			impo = ImageIO.read(new File(impath));
		}catch(Exception ex){}
		impo = resample(impo);
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
				stem[y][x][1] = cn.getBlue();
				stem[y][x][2] = cn.getGreen();
			}
		}
		return stem;
	}
	
	public BufferedImage resample(BufferedImage b){
		int wac = (int)Math.ceil(b.getWidth()/8);
		int hac = (int)Math.ceil(b.getHeight()/8);
		BufferedImage out = new BufferedImage(wac*8,hac*8,BufferedImage.TYPE_INT_RGB);
		int[][][][][] matrix = new int[hac][wac][8][8][3];
		int[][][] cspace = tricolore(b);
		for(int ty=0;ty<hac;ty++){
			for(int tx=0;tx<wac;tx++){
				for(int c=0;c<3;c++){
					for(int py=0;py<8;py++){
						for(int px=0;px<8;px++){
							int fit = 0;
							for(int y=0;y<8;y++){
								for(int x=0;x<8;x++){
									int sx = px+x;
									int sy = py+y;
									int rs = (((int)(0.125*(sx+(int)(sx/8)))+(int)(0.125*(sy+(int)(sy/8))))%2)*255;
									int comp = cspace[(ty*8)+y][(tx*8)+x][c];
									fit += Math.abs(rs-comp);
								}
							}
							fit = fit/64;
							matrix[ty][tx][py][px][c] = fit;
						}
					}
				}
				for(int y=0;y<8;y++){
					for(int x=0;x<8;x++){
						int[] tot = new int[3];
						for(int c=0;c<3;c++){
							for(int p0=0;p0<8;p0++){
								for(int p1=0;p1<8;p1++){
									int sx = p0+x;
									int sy = p1+y;
									tot[c] += (((int)(0.125*(sx+x+(int)(sx/8)))+(int)(0.125*(sy+(int)(sy/8))))%2) * matrix[ty][tx][p0][p1][c];
								}
							}
							tot[c] = tot[c]/64;
						}
						int cn = new Color(tot[0],tot[1],tot[2]).getRGB();
						//System.out.println(tot[0]+" "+tot[1]+" "+tot[2]);
						out.setRGB((tx*8)+x, (ty*8)+y, cn);
					}
				}
			}
		}
		return out;
	}
	
	public BufferedImage ttest(){
		BufferedImage b = new BufferedImage(64,64,BufferedImage.TYPE_INT_RGB);
		for(int y=0;y<64;y++){
			for(int x=0;x<64;x++){
				int rs = (((int)(0.125*(x+(int)(x/8)))+(int)(0.125*(y+(int)(y/8))))%2)*99999;
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
