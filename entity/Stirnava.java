package entity;

import java.util.*;
import java.lang.*;
import java.io.*; 
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
import javafx.scene.transform.Rotate; 

public class Stirnava extends Entity{
   public Rectangle blood;
   public Rectangle bloodBase;
   public boolean fight=false;
   public boolean hitByBullet = false,hitBySpike = false;
   public double health_value=2500;
   public double health_base=2500;
   
   private final double PI = 3.1415926535;
   private static double[] blood_pos = {195,960};
   private List<Image> idle,roll,run,atk1,idleFlip,rollFlip,runFlip,atk1Flip;
   private boolean flip = false,fliped = false,animating=false;
   private int anim_timer=0,anim_type=0,preAct=0,preIndex=0,rollinX=0,rollinY=0;
   private double xDrift=0,yDrift=0;
   public Rectangle atkbox;
   private double timer=0;//timer for damaged animation

   public Stirnava(double x,double y) throws FileNotFoundException{

      idle = splitSprite(new Image(new FileInputStream("pic/Stirnava/idle.png")),1,1,1280,960);
      roll = splitSprite(new Image(new FileInputStream("pic/Stirnava/roll.png")),1,1,1280,960);
      run = splitSprite(new Image(new FileInputStream("pic/Stirnava/walksheet.png")),1,8,1280,960);
      atk1 = splitSprite(new Image(new FileInputStream("pic/Stirnava/atksheet.png")),1,5,2560,1280);
      idleFlip=flipSprite(idle);
      rollFlip=flipSprite(roll);
      runFlip=flipSprite(run);
      atk1Flip=flipSprite(atk1);

      image = idle.get(0);
      flipimage = (WritableImage)idleFlip.get(0);
      sprite = new ImageView(image);

      hitbox = new Rectangle();
      atkbox = new Rectangle();

      blood = new Rectangle();
      blood.setFill(Color.rgb(192,83,68));
      bloodBase = new Rectangle();
      bloodBase.setFill(Color.rgb(154,44,30));

      hitbox.setFill(Color.TRANSPARENT);
      hitbox.setStroke(Color.TRANSPARENT);
      hitbox.setStrokeWidth(2);
      atkbox.setFill(Color.TRANSPARENT);
      atkbox.setStroke(Color.TRANSPARENT);
      hitbox.setStrokeWidth(2);
      sprite.setSmooth(true);
      //sprite.setPreserveRatio(true);
      setSize(480,360);
      setPos(x,y);
   }

   @Override
   public void setPos(double x,double y){
      Pos[0] = x;
      Pos[1] = y;

      int posFlip=1;
      if(flip) posFlip=0;

      hitbox.setX((Pos[0]-(Width-300)/2-Cam[0])*ratio[0]);
      hitbox.setY((1080-Pos[1]-(Height-100)+Cam[1])*ratio[1]);
      hitbox.setWidth((Width-300)*ratio[0]); 
      hitbox.setHeight((Height-100)*ratio[1]);

      atkbox.setWidth(500*ratio[0]);
      atkbox.setHeight(75*ratio[1]);
      atkbox.setX((Pos[0]-posFlip*500-Cam[0])*ratio[0]); 
      atkbox.setY((1080-Pos[1]-75+Cam[1])*ratio[1]);

      sprite.setFitWidth((Width+xDrift)*ratio[0]);
      sprite.setFitHeight((Height+yDrift)*ratio[1]);
      sprite.setX((Pos[0]-Width/2-posFlip*xDrift-Cam[0])*ratio[0]); 
      sprite.setY((1080-Pos[1]-Height+Cam[1]-yDrift)*ratio[1]);

      blood.setX((blood_pos[0])*ratio[0]);
      blood.setY((blood_pos[1])*ratio[1]);
      blood.setWidth(0.6*Inject()*ratio[0]);
      blood.setHeight(40*ratio[1]);

      bloodBase.setX((blood_pos[0])*ratio[0]);
      bloodBase.setY((blood_pos[1])*ratio[1]);
      if(health_base>health_value) health_base-=1*frameRate;
      else health_base=health_value;
      bloodBase.setWidth(0.6*health_base*ratio[0]);
      bloodBase.setHeight(40*ratio[1]);
   }

   public double Inject(){
      if(newBornInGame){
         health_value = 2500;
         Pos[0]=6000;
         Pos[1]=950;
         anim_type=0;
         fight=false;
      }
      else{
         if(damaged){
            if(timer>80){
               damaged = false;
               timer=0;
            }
            else{
               timer+=1*frameRate;
            }
         }
      }
      return health_value;
   }

   public List<Image> splitSprite(Image img,int row,int col,int w,int h){
      int count=0,col_count=0;
      List<Image> anim = new ArrayList<>();
      PixelReader pixelReader = img.getPixelReader();

      for(int r=0; r<row; r++){
         for(int c=0; c<col; c++){
            WritableImage c_img = new WritableImage(w,h);
            PixelWriter writer = c_img.getPixelWriter();
            for(int i=r*h; i<(r+1)*h; i++){
               for(int j=c*w; j<(c+1)*w; j++){
                  Color color = pixelReader.getColor(j,i);
                  writer.setColor(j%w,i%h,color);
               }
            }
            anim.add((Image)c_img);
         }
      }
      return anim;
   }
   public List<Image> flipSprite(List<Image> arr){
      List<Image> fliparr = new ArrayList<>();
      for(int i=0;i<arr.size();i++){
         fliparr.add(getFlip(arr.get(i)));
      }
      return fliparr;
   }

   public void runAnim(){

      if(preAct!=anim_type){
         anim_timer=0;
         fliped = false;
         preAct=anim_type;
      }

      double index_DOUBLE=((double)anim_timer)/7.5*frameRate;
      int index = (int)index_DOUBLE;

      if(preIndex!=index){
         preIndex=index;
      }

      if(anim_type==2){
         xDrift=480;
         yDrift=120;
      }
      else{
         xDrift=0;
         yDrift=0;
      }

      if(anim_type==0){
         animating=false;
         image=idle.get(0);
         flipimage=(WritableImage)idleFlip.get(0);
      }
      else if(anim_type==1){
         animating=false;
         if(index>=run.size()) {
            index=0;
            anim_timer=0;
            image=run.get(0);
            flipimage=(WritableImage)runFlip.get(0);
         }
         else {
            image=run.get(index);
            flipimage=(WritableImage)runFlip.get(index);
         }
      }
      else if(anim_type==2){
         if(index>=atk1.size()) {
            attacking = false;
            if(index>8){
               animating=false;
               index=0;
               anim_timer=0;
            }
         }
         else {
            image=atk1.get(index);
            flipimage=(WritableImage)atk1Flip.get(index);
            if(index>2)
               attacking = true;
            animating=true;
         }
      }
      else if(anim_type==3){
         image=roll.get(0);
         rolling = true;
         flipimage=(WritableImage)rollFlip.get(0);
         double index_D=((double)anim_timer)*frameRate;
         int indexSixty = (int)index_D;
         if(indexSixty<30){
            double speed = 10;
            double slowRate=1;
            double rollAngle =0;
            double flipRoll =1;

            if(indexSixty>10){
               rollAngle = 150+Math.pow(210,((double)indexSixty-10)/20);
               slowRate=Math.pow(0.3,((double)indexSixty-10)/20);
            }
            else{
               rollAngle=indexSixty*15;
            }
      
            if(flip) {
               flipRoll =-1;
               Motion[0] = -speed*slowRate;
            }else{
               Motion[0] = speed*slowRate;
            }
            animating=true;
            sprite.setRotate(rollAngle*flipRoll);
         }
         else {
            sprite.setRotate(0);
            anim_timer=0;
            rolling = false;
            animating=false;
         }
      }

      if(flip){
         fliped = true;
         sprite.setImage(flipimage);
      }else{
         fliped = false;
         sprite.setImage(image);
      }

      anim_timer++;
   }

   int Limit(int a,int b){
      return a>=b ? b :a;
   }
   @Override
   public double getBoxW(){
      return Width-300;
   }

   @Override
   public double getBoxH(){
      return Height-100;
   }

   @Override
   public void act(){
      double xDis=World[0]+960-Pos[0],yDis=World[1]+520-Pos[1];
      double Dis = Math.sqrt(Math.pow(xDis,2)+Math.pow(yDis,2));
      int rollRand =(int) (Math.random()*2);
      if(fight){
         if(xDis<=0&&!animating) flip=false;
         else if(!animating) flip=true;

         if(Dis<150&&rollRand==0){
            if(!animating)
               anim_type=3;
         }
         else if(Dis<300&&Math.abs(yDis)<200){
            if(!animating)
               anim_type=2;
         }
         else{
            Motion[0]=xDis/70;
            Motion[1]=yDis/70;
            if(!animating)
               anim_type=1;
         }
      }

      if(Motion[0] != 0){        //slow down when movement key released
         if(Motion[0]>0.25){
            if(collideh == 1){
               Motion[0]=0;
            }else{
               Motion[0]-=0.5;
            }
         }
         else if(Motion[0]<-0.25){
            if(collideh == 2){
               Motion[0]=0;
            }
            else{
               Motion[0]+=0.5;
            }
         }
         else Motion[0]=0;
      }

      if(Motion[1] != 0){        //slow down when movement key released
         if(Motion[1]>0.25){
            if(collidev == 1){
               Motion[1]=0;
            }else{
               Motion[1]-=0.5;
            }
         }
         else if(Motion[1]<-0.25){
            if(collidev == 2){
               Motion[1]=0;
            }
            else{
               Motion[1]+=0.5;
            }
         }
         else Motion[1]=0;
      }

      runAnim();
      setPos(getX()+Motion[0]*frameRate,getY()+Motion[1]*frameRate);
   }
}