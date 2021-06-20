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

public class Player extends Entity{
   public Rectangle redBlood;
   public Rectangle bloodbarBase;
   public ImageView bloodbar;
   public boolean hitByBullet = false,hitBySpike = false;
   public boolean newBornInGame = true;
   public double health_value;
   public double health_base_value=650;

   private final double PI = 3.1415926535;
   private static double[] Blood_pos = {10,50};
   private Image blood;
   private List<Image> idle,run,rWeapon,hair,cloth,idleFlip,runFlip,rWeaponFlip,hairFlip,clothFlip;
   private boolean flip = false,fliped = false;
   private int anim_timer=0,anim_type=0,preAct=0,preIndex=0,atk_timer=0,preweapon=0,rollinX=0,rollinY=0;;
   private int timer=0;//timer for damaged animation
   private double atkAngle=0,atkX=0,atkY=0;

   public Player(double x,double y) throws FileNotFoundException{

      weaponKind = 1;
      hairKind = 9;
      clothKind = 9; 

      hair = splitSprite(new Image(new FileInputStream("pic/Player/hair/hair.png")),1,12,400,600);
      cloth = splitSprite(new Image(new FileInputStream("pic/Player/cloth/cloth.png")),1,12,400,600);
      rWeapon = splitSprite(new Image(new FileInputStream("pic/Player/weapon/weapon.png")),1,11,400,600);
      idle = splitSprite(new Image(new FileInputStream("pic/Player/p_idle.png")),1,1,400,600);
      run = splitSprite(new Image(new FileInputStream("pic/Player/p_run.png")),1,8,400,600);
      idleFlip=flipSprite(idle);
      runFlip=flipSprite(run);
      rWeaponFlip=flipSprite(rWeapon);
      hairFlip=flipSprite(hair);
      clothFlip=flipSprite(cloth);

      image = idle.get(0);
      weaponImage = rWeapon.get(weaponKind-1);
      hairImage = hair.get(hairKind-1);
      clothImage = cloth.get(clothKind-1);
      flipimage = (WritableImage)idleFlip.get(0);
      weaponFlipImage=(WritableImage)rWeaponFlip.get(weaponKind-1);
      hairFlipImage=(WritableImage)hairFlip.get(hairKind-1);
      clothFlipImage=(WritableImage)clothFlip.get(clothKind-1);
      sprite = new ImageView(image);
      weapon= new ImageView(weaponImage);
      partZero = new ImageView(clothImage);
      partOne = new ImageView(hairImage);

      redBlood = new Rectangle();
      redBlood.setFill(Color.RED);
      bloodbarBase = new Rectangle();
      bloodbarBase.setFill(Color.GRAY);
      blood = new Image(new FileInputStream("pic/health_bar.png"));
      bloodbar = new ImageView(blood);
      hitbox = new Rectangle();

      hitbox.setFill(Color.TRANSPARENT);
      hitbox.setStroke(Color.TRANSPARENT);
      hitbox.setStrokeWidth(2);
      sprite.setSmooth(true);
      bloodbar.setSmooth(true);
      //sprite.setPreserveRatio(true);
      setSize(150,225);
      setPos(x,y);
   }

   @Override
   public void setPos(double x,double y){
      Pos[0] = x;
      Pos[1] = y;

      hitbox.setX((Pos[0]-Width/2/2-Cam[0])*ratio[0]);
      hitbox.setY((1080-Pos[1]-Height/1.7+Cam[1])*ratio[1]);
      hitbox.setWidth(Width/2*ratio[0]); 
      hitbox.setHeight(Height/1.7*ratio[1]);

      sprite.setFitWidth(Width*ratio[0]);
      sprite.setFitHeight(Height*ratio[1]);
      sprite.setX((Pos[0]-Width/2-Cam[0])*ratio[0]); 
      sprite.setY((1090-Pos[1]-Height+Cam[1]+27)*ratio[1]);

      partZero.setFitWidth(Width*ratio[0]);
      partZero.setFitHeight(Height*ratio[1]);
      partZero.setX((Pos[0]-Width/2-Cam[0])*ratio[0]); 
      partZero.setY((1090-Pos[1]-Height+Cam[1]+27)*ratio[1]);

      partOne.setFitWidth(Width*ratio[0]);
      partOne.setFitHeight(Height*ratio[1]);
      partOne.setX((Pos[0]-Width/2-Cam[0])*ratio[0]); 
      partOne.setY((1090-Pos[1]-Height+Cam[1]+27)*ratio[1]);
      
      int posFlip=1;
      if(flip) posFlip=-1;
      
      double radiousAngle=atkAngle*posFlip*PI/180,anchorX=0,anchorY=100,length=Math.sqrt(anchorX*anchorX+anchorY*anchorY);
      
      weapon.setRotate(atkAngle*posFlip);
      weapon.setFitWidth(Width*ratio[0]);
      weapon.setFitHeight(Height*ratio[1]);
      weapon.setX((Pos[0]-Width/2-Cam[0]-posFlip*(10-atkX)+length*Math.sin(radiousAngle)+anchorX)*ratio[0]); 
      weapon.setY((1090-Pos[1]-Height+Cam[1]-atkY-45-length*Math.cos(radiousAngle)+anchorY)*ratio[1]);

      redBlood.setX((Blood_pos[0]+54)*ratio[0]);
      redBlood.setY((Blood_pos[1]+27)*ratio[1]);
      redBlood.setWidth(Inject()*ratio[0]);
      redBlood.setHeight(22*ratio[1]);

      bloodbarBase.setX((Blood_pos[0]+54)*ratio[0]);
      bloodbarBase.setY((Blood_pos[1]+27)*ratio[1]);
      if(health_base_value>Inject()) health_base_value-=0.75;
      else health_base_value=Inject();
      bloodbarBase.setWidth(health_base_value*ratio[0]);
      bloodbarBase.setHeight(22*ratio[1]);

      bloodbar.setX(Blood_pos[0]*ratio[0]);
      bloodbar.setY(Blood_pos[1]*ratio[1]);
      bloodbar.setFitWidth(750*ratio[0]);
      bloodbar.setFitHeight(75*ratio[1]);
   }


   public void Camera(){
      World[0]=Pos[0]-960;
      World[1]=Pos[1]-520;

      if(World[0]-Cam[0]>150*ratio[0]){
         Cam[0]=World[0]-150*ratio[0];
      }
      else if(World[0]-Cam[0]<-150*ratio[0]){
         Cam[0]=World[0]+150*ratio[0];
      }
      if(World[1]-Cam[1]>150*ratio[1]){
         Cam[1]=World[1]-150*ratio[1];
      }
      else if(World[1]-Cam[1]<-150*ratio[1]){
         Cam[1]=World[1]+150*ratio[1];
      }
   }

   public double Inject(){
      if(newBornInGame){
         health_value = 650;
         newBornInGame = false;
      }else{
         if(hitByBullet||hitBySpike){
            if(hitBySpike)
               health_value = 0;
            if(hitByBullet)
               health_value = redBlood.getWidth()-200;

            redBlood.setWidth(health_value);
            hitByBullet = false;
            hitBySpike  = false;
            damaged = true;
            if(redBlood.getWidth()<=0){
               setMy(0);
               Cam[0]=0;
               Cam[1]=0;
               World[0]=0;
               World[1]=0;
               newBornInGame = true;
               damaged = false;
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

      atkAnim();

      hairImage = hair.get(hairKind-1);
      hairFlipImage = (WritableImage)hairFlip.get(hairKind-1);

      clothImage = cloth.get(clothKind-1);
      clothFlipImage=(WritableImage)clothFlip.get(clothKind-1);

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

      if(anim_type==0){
         image=idle.get(0);
         flipimage=(WritableImage)idleFlip.get(0);
      }
      else if(anim_type==1){
         if(index==run.size()) {
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
         double index_D=((double)anim_timer)*frameRate;
         int indexSixty = (int)index_D;
         if(indexSixty<30){
            double speed = 7;
            double slowRate=1;
            double rollAngle =0;
            double flipRoll =1;

            if(flip) flipRoll =-1;
            
            if(indexSixty>10){
               rollAngle = 150+Math.pow(210,((double)indexSixty-10)/20);
               slowRate=Math.pow(0.3,((double)indexSixty-10)/20);
            }
            else{
               rollAngle=indexSixty*15;
            }

            if(rollinX == 1) Motion[0] = speed*slowRate;
            else if(rollinX == -1) Motion[0] = -speed*slowRate;
            if(rollinY == 1) Motion[1] = speed*slowRate;
            else if(rollinY == -1) Motion[1] = -speed*slowRate;

            sprite.setRotate(rollAngle*flipRoll);
            partZero.setRotate(rollAngle*flipRoll);
            partOne.setRotate(rollAngle*flipRoll);
         }
         else {
            sprite.setRotate(0);
            partZero.setRotate(0);
            partOne.setRotate(0);
            anim_timer=0;
            rolling = false;
         }
      }

      if(flip){
         fliped = true;
         sprite.setImage(flipimage);
         partZero.setImage(clothFlipImage);
         partOne.setImage(hairFlipImage);
         weapon.setImage(weaponFlipImage);
      }else{
         fliped = false;
         sprite.setImage(image);
         partZero.setImage(clothImage);
         partOne.setImage(hairImage);
         weapon.setImage(weaponImage);
      }

      anim_timer++;
   }

   int Limit(int a,int b){
      return a>=b ? b :a;
   }

   void atkAnim(){
      int atk_type=0;
      int atkTimeLimit[]={35,48,65,52,90};

      if(weaponKind==1||weaponKind==6) atk_type =1;
      else if(weaponKind==2||weaponKind==3||weaponKind==7) atk_type =2;
      else if(weaponKind==4||weaponKind==5) atk_type =3;
      else if(weaponKind==8||weaponKind==9) atk_type =4;
      else if(weaponKind==10||weaponKind==11) atk_type =5;

      if(weaponKind==0) return;

      if(!AtkPressed||preweapon!=weaponKind||rolling){
         preweapon=weaponKind;
         AtkPressed=false;
         atkX=0;
         atkY=0;
         if(atk_type ==1) atkAngle = 75;
         else if(atk_type ==2) atkAngle = -30;
         else if(atk_type ==3) atkAngle = -70;
         else if(atk_type ==4) {
            atkAngle = 90;
            atkX = -70;
         }
         else if(atk_type ==5)  {
            atkAngle = 0;
            atkX = -20;
            atkY = -50;
         }
         atk_timer = 0;
      }
      else{
         double index_DOUBLE=((double)atk_timer)*frameRate;
         int index = (int)index_DOUBLE;
         if(index>=atkTimeLimit[atk_type-1]){
            atk_timer=0;
            AtkPressed=false;
         }
         else{
            if(atk_type ==1){
               if(index<=21){
                  atkAngle = 75- (double)Limit(index,3)/3*105 +Math.pow(130,(double)Limit(cancel(index-3),17)/17);
                  atkX = Math.pow(20,(double)Limit(cancel(index-3),17)/17);
                  atkY = -Math.pow(7,(double)Limit(cancel(index-3),17)/17);
               }
               else if(index>26){
                  atkAngle = 100-(double)(index-27)/7*25;
                  atkX = 20-(double)(index-27)/7*20;
                  atkY = -7+(double)(index-27)/7*7;
               }
            }
            else if(atk_type ==2){
               if(index<=25){
                  atkAngle = -30 - (double)Limit(index,5)/5*50 + Math.pow(190,(double)Limit(cancel(index-5),20)/20);
                  atkX = Math.pow(45,(double)Limit(cancel(index-5),20)/20);
                  atkY = -Math.pow(15,(double)Limit(cancel(index-5),20)/20);
               }
               else if(index>37){
                  atkAngle = 110-(double)(index-37)/11*140;
                  atkX = 45-(double)(index-37)/11*50;
                  atkY = -15+(double)(index-37)/11*15;
               }
            }
            else if(atk_type ==3){
               if(index<=28){
                  atkAngle = -70-(double)Limit(index,13)/13*30+Math.pow(203,(double)Limit(cancel(index-13),15)/15);
                  atkX = Math.pow(45,(double)Limit(cancel(index-13),15)/15);
                  atkY = -Math.pow(15,(double)Limit(cancel(index-13),15)/15);
               }
               else if(index>50){
                  atkAngle = 103-(double)(index-50)/15*170;
                  atkX = 45-(double)(index-50)/15*45;
                  atkY = -15+(double)(index-50)/15*15;
               }
            }
            else if(atk_type ==4){
               if(index<=38){
                  atkAngle = 90+(double)Limit(index,20)/20*15-Math.pow(20,(double)Limit(cancel(index-20),18)/18);
                  atkX = -70-(double)Limit(index,20)/20*30+Math.pow(170,(double)Limit(cancel(index-20),18)/18);
               }
               else if(index>42){
                  atkAngle = 85+(double)(index-42)/10*5;
                  atkX = 50-(double)(index-42)/10*140;
               }
            }
            else if(atk_type ==5){
               if(index<=40){
                  atkAngle = (double)cancel(index-10)/30*10;
                  atkY = -50+(double)Limit(index,40)/40*50;
               }
               else if(index>70){
                  atkAngle = 10-(double)(index-70)/20*10;
                  atkY = -(double)(index-70)/20*50;
               }
            }
         }
         atk_timer++;
      }
      weaponImage = rWeapon.get(weaponKind-1);
      weaponFlipImage=(WritableImage)rWeaponFlip.get(weaponKind-1);
   }

   @Override
   public double getBoxW(){
      return Width/2;
   }

   @Override
   public double getBoxH(){
      return Height/1.7;
   }

   @Override
   public void act(){
      if (Shift && !rolling && ShiftRelease && !AtkPressed) {
         ShiftRelease = false;
         rolling = true;
         if(Rightpress == true)rollinX=1;
         else if(Leftpress == true) rollinX=-1;
         if(Up == true) rollinY=1;
         else if(Down == true) rollinY=-1;
         if(rollinX==0&&rollinY==0){
            if(flip) rollinX=-1;
            else rollinX=1;
         }
      }

      if (rolling){
         anim_type = 2;
      }

      else if(!damaged){
         rollinX=0;
         rollinY=0;
         if(Rightpress == true){
            if(Ctrl) Motion[0] = 7;
            else Motion[0] = 4;
            flip=false;
         }
         else if(Leftpress == true){
            if(Ctrl) Motion[0] = -7;
            else Motion[0] = -4;
            flip=true;
         }
         if(Down == true){
            if(Ctrl) Motion[1] = -7;
            else Motion[1] = -4;
         }
         else if(Up == true){
            if(Ctrl) Motion[1] = 7;
            else Motion[1] = 4;
         }

         if(Up||Down||Leftpress||Rightpress){
            anim_type = 1;
         }
         else{
            anim_type = 0;
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
      Camera();
      //System.out.println(frameRate);
      setPos(getX()+Motion[0]*frameRate,getY()+Motion[1]*frameRate);
   }
}