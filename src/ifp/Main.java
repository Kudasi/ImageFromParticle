package ifp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;

public class Main extends PApplet {
	
	ArrayList<Dot> points = new ArrayList<Dot>();
	
	public void settings() {
		size(1000,700,P2D);
	}
	
	String imagePath = "https://pbs.twimg.com/profile_images/954662123958947840/vq0XYxxq.jpg";
	String outputPath = "C:\\Users\\" + System.getProperty("user.name") + "\\Pictures\\";
	float res, sizem, blending;
	
	public void fileSelected(File f) {
		imagePath = f.getAbsolutePath();
	}
	
	public void setup() {
		frameRate(100);
		colorMode(RGB,255,255,255);
		strokeWeight(3);
		background(0);
		noStroke();
		blendMode(BLEND);
		
		JSONObject data = loadJSONObject("cfg");
		imagePath = data.getString("imagePath", imagePath);
		outputPath = data.getString("outputFolder", outputPath);
		res = data.getFloat("resolutionDivisor");
		sizem = data.getFloat("radiusMultiplier");
		blending = data.getFloat("blending");
		
		PImage img = loadImage(imagePath);		
		float mult = max(img.width/(float)width, img.height/(float)height);
		for(float x = 0;x < img.width;x+=res) {
			for(float y = 0;y < img.height;y+=res) {
				PVector p1 = getRandomPoint();
				points.add(new Dot(p1.x, p1.y, width/2+(x+random(res/-2f,res/2f)-img.width/2)/mult, height/2+(y+random(res/-2f,res/2f)-img.height/2)/mult, random(res/mult,res*2/mult), img.get((int)x, (int)y)));
			}
		}
	}
	
	public void draw() {
		for(int i = points.size()-1;i>=0;i--) {
			Dot d = points.get(i);
			d.p.lerp(d.p2, 1f/60);
			d.draw();
		}
	}
	
	public void keyPressed() {
		if(key==' ') save(outputPath+"heartImage" + System.currentTimeMillis() + ".png");	
	}
	
	private PVector getRandomPoint() {
		float x = random(0,width);
		float y = random(0,height);
		
		while(heart((x-width/2f)/width*10, (height/4f-y)/height*8)>1) {
			x = random(0,width);
			y = random(0,height);
		}
		
		return new PVector(x,y);
	}	
	
	private float heart(float x, float y) {
		float t = atan2(y,x);
		float r = sin(t)*sqrt(abs(cos(t)))/(sin(t)+7f/5f)-2*sin(t)+2;
		
		return (x*x+y*y)/(r*r);
	}

	public static void main(String[] args) {
		PApplet.main(Main.class, args);
	}
	
	private class Dot {
		
		public PVector p1, p2, p;
		public int color;
		public float size;
		
		public Dot(float x1, float y1, float x2, float y2, float size, int color) {
			p1 = new PVector(x1, y1);
			p2 = new PVector(x2, y2);
			p = new PVector(x1, y1);	
			this.size = size;
			this.color = color;			
		}
		
		public void draw() {
			fill(color, 256/(1+blending)-1);
			circle(p.x, p.y, size*sizem);
		}
	}
}