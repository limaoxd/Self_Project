package entity;

import static java.lang.System.out;
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application; 
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class  Entity {
   protected Image image,weaponImage,hairImage,clothImage;
   protected WritableImage flipimage,weaponFlipImage,hairFlipImage,clothFlipImage;
   protected double[] Pos = {0,0};
   protected double[] Motion = {0,0};
   protected int Width = 0,Height = 0;
   public int collideh;
   public int collidev;
   public int weaponKind=0,hairKind=0,clothKind=0;
   public boolean Rightpress = false ,Leftpress = false ,Up = false ,Down = false ,Shift = false,ShiftRelease=true,Ctrl = false,AtkPressed = false;
   public boolean damaged =false,rolling =false;
   public ImageView sprite,partZero,partOne,weapon;
   public Rectangle hitbox;
   public static double frameRate;
   public static double[] World = {0,0};
   public static double[] Cam = {0,0};
   public static double[] ratio={1,1};
   public double health_value;


   public Entity() throws FileNotFoundException{}

   public void setPos(double x,double y){
      Pos[0] = x;
      Pos[1] = y;

      hitbox.setX((Pos[0]-Width/2-Cam[0])*ratio[0]);
      hitbox.setY((1080-Pos[1]-Height+Cam[1])*ratio[1]);
      hitbox.setWidth(Width*ratio[0]); 
      hitbox.setHeight(Height*ratio[1]);

      sprite.setFitWidth(Width*ratio[0]);
      sprite.setFitHeight(Height*ratio[1]);
      sprite.setX((Pos[0]-Width/2-Cam[0])*ratio[0]); 
      sprite.setY((1080-Pos[1]-Height+Cam[1])*ratio[1]);

   }

   public void setSize(int w,int h){
      Width = w;
      Height = h;
   }

   public static void setScreenSize(double x,double y){
      ratio[0]=x/1920;
      ratio[1]=y/1080;
   }
   
   public double getX(){
      return Pos[0];
   }

   public double getY(){
      return Pos[1];
   }

   public double getW(){
      return Width;
   }
   
   public double getH(){
      return Height;
   }

   public double getBoxW(){
      return Width;
  }

  public double getBoxH(){
      return Height;
  }

  public double getBoxX(){
      return Pos[0];
  }

  public double getBoxY(){
      return Pos[1];
  }

   public double getMx(){
      return Motion[0];
   }

   public double getMy(){
      return Motion[1];
   }

   public void setMx(double mx){
      Motion[0]=mx;
   }

   public void setMy(double my){
      Motion[1]=my;
   }

   public int cancel(int my){
      if (my<0) my=0;
      return my;
   }

   public WritableImage getFlip(Image img){
      int w=(int)img.getWidth(),h=(int)img.getHeight();
      WritableImage flipimg = new WritableImage(w,h);
      PixelReader pixelReader = img.getPixelReader();
      PixelWriter writer = flipimg.getPixelWriter();

      for(int col=0;col<h;col++){
         for(int row=0;row<w;row++){
            Color color = pixelReader.getColor(row,col);
            writer.setColor(w-row-1, col, color);
         }
      }
      return flipimg;
   }

   public boolean Isinrange(Entity B){
      boolean inrange  = false;
      if(this.getBoxX()-this.getBoxW()/2+7.5<B.getBoxX()+B.getBoxW()/2&&this.getBoxX()+this.getBoxW()/2-7.5>B.getBoxX()-B.getBoxW()/2){
         inrange = true;
      }
      return inrange;
   }

   public void act(){}
}