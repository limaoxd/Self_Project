import entity.*;
import map.*;
//import loadSave.*;
import java.util.*;
import java.math.BigInteger;
import static java.lang.System.out;
import javafx.application.Application; 
import java.io.FileNotFoundException;
import java.io.FileInputStream; 
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.Group; 
import javafx.scene.Scene; 
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate; 
import javafx.stage.Stage; 
import javafx.scene.paint.Color;


public class Dressing {
   private boolean isDressing = true;
   private List<Image> idle;
   private ImageView idleView;
   private double idleWidth;
   private double idleHeight;

   private List<Image> platform = new ArrayList<Image>();
   private List<ImageView> platformView = new ArrayList<ImageView>();
   private double platformWidth;
   private double platformHeight;

   private Image idleTest;
   private ImageView idleTestView;

   private List<Image> hair;
   private ImageView hairView;

   private List<Image> cloth;
   private ImageView clothView;

   private List<Image> weapon;
   private ImageView weaponView;
   
   private List<ImageView> choosingList = new ArrayList<ImageView>();
   
   private Rectangle choosing = new Rectangle();

   public boolean LeftPress = false;
   public boolean RightPress = false;
   public boolean UpPress = false;
   public boolean DownPress = false;
   public boolean EnterPress = false;
   public boolean previousLeftPress = false;
   public boolean previousRightPress = false;
   public boolean previousUpPress = false;
   public boolean previousDownPress = false;
   public boolean previousEnterPress = false;

   //to memorize which item player choose now
   private int whatToChange = 0;
   private int curHair = 0;
   private int curCloth = 0;
   private int curWeapon = 0;

   public boolean isDressing(){
      return isDressing;
   }

   //load pictures into imageViews
   public boolean loadPic() throws FileNotFoundException{
      idle = splitSprite(new Image(new FileInputStream("pic/Player/p_idle.png")),1,1,400,600);
      Image tmpIdle = getFlip(idle.get(0));
      idleView = new ImageView(tmpIdle);
      idleWidth = idle.get(0).getWidth();
      idleHeight = idle.get(0).getHeight();

      hair = splitSprite(new Image(new FileInputStream("pic/Player/hair/hair.png")),1,12,400,600);
      Image tmpHair = getFlip(hair.get(0));
      hairView = new ImageView(tmpHair);


      cloth = splitSprite(new Image(new FileInputStream("pic/Player/cloth/cloth.png")),1,12,400,600);
      Image tmpCloth = getFlip(cloth.get(0));
      clothView = new ImageView(tmpCloth);

      weapon = splitSprite(new Image(new FileInputStream("pic/Player/weapon/weapon.png")),1,11,400,600);
      Image tmpWeapon = getFlip(weapon.get(0));
      weaponView = new ImageView(tmpWeapon);
      weaponView.setRotate(350);

      choosing.setFill(Color.TRANSPARENT);
      choosing.setStroke(Color.web("#FFFFFF"));
      choosing.setStrokeWidth(5);

      return true;
   }

   public void showScreen(Stage stage){
      if(EnterPress){
         isDressing = false;
         return;
      }
      switch(whatToChange){
         case 0:
            if(!previousLeftPress && LeftPress){
               curHair--;
            }else if(!previousRightPress && RightPress){
               curHair++;
            }
            if(curHair < 0) curHair = 11;
            if(curHair > 11) curHair = 0;
            break;
         case 1:
            if(!previousLeftPress && LeftPress){
               curCloth--;
            }else if(!previousRightPress && RightPress){
               curCloth++;
            }
            if(curCloth < 0) curCloth = 11;
            if(curCloth > 11) curCloth = 0;
            break;
         case 2:
            if(!previousLeftPress && LeftPress){
               curWeapon--;
            }else if(!previousRightPress && RightPress){
               curWeapon++;
            }
            if(curWeapon < 0) curWeapon = 10;
            if(curWeapon > 10) curWeapon = 0;
            break;
      }
      if(!previousUpPress && UpPress){
         whatToChange--;
      }else if(!previousDownPress && DownPress){
         whatToChange++;
      }

      if(whatToChange < 0) whatToChange = 2;
      if(whatToChange > 2) whatToChange = 0;

      Image tmpImage;
      tmpImage = getFlip(hair.get(curHair));
      hairView.setImage(tmpImage);
      tmpImage = getFlip(cloth.get(curCloth));
      clothView.setImage(tmpImage);
      tmpImage = getFlip(weapon.get(curWeapon));
      weaponView.setImage(tmpImage);

      setChoosing(stage,curHair,curCloth,curWeapon);

      previousLeftPress = LeftPress;
      previousRightPress = RightPress;
      previousUpPress = UpPress;
      previousDownPress = DownPress;
   }
   
   private void setChoosing(Stage stage, int hairIndex, int clothIndex, int weaponIndex){
      Image tmpImage;

      if(hairIndex == 0){      
         tmpImage = getFlip(hair.get(11));
      }else {
         tmpImage = getFlip(hair.get(hairIndex-1));
      }
      choosingList.get(0).setImage(tmpImage);
      tmpImage = getFlip(hair.get(hairIndex));
      choosingList.get(1).setImage(tmpImage);
      if(hairIndex == 11){
         tmpImage = getFlip(hair.get(0));
      }else {
         tmpImage = getFlip(hair.get(hairIndex+1));
      }
      choosingList.get(2).setImage(tmpImage);

      if(clothIndex == 0){
         tmpImage = getFlip(cloth.get(11));
      }else {
         tmpImage = getFlip(cloth.get(clothIndex-1));
      }
      choosingList.get(3).setImage(tmpImage);
      tmpImage = getFlip(cloth.get(clothIndex));
      choosingList.get(4).setImage(tmpImage);
      if(clothIndex == 11){
         tmpImage = getFlip(cloth.get(0));
      }else {
         tmpImage = getFlip(cloth.get(clothIndex+1));
      }
      choosingList.get(5).setImage(tmpImage);

      if(weaponIndex == 0){
         tmpImage = getFlip(weapon.get(10));
      }else {
         tmpImage = getFlip(weapon.get(weaponIndex-1));
      }
      choosingList.get(6).setImage(tmpImage);
      tmpImage = getFlip(weapon.get(weaponIndex));
      choosingList.get(7).setImage(tmpImage);
      if(weaponIndex == 10){
         tmpImage = getFlip(weapon.get(0));
      }else {
         tmpImage = getFlip(weapon.get(weaponIndex+1));
      }
      choosingList.get(8).setImage(tmpImage);

      if(whatToChange == 0){
         choosing.setX(stage.getWidth()/20+140+30);
         choosing.setY(stage.getHeight()/8+25);
         choosing.setWidth(140);
         choosing.setHeight(200);
      }else if(whatToChange == 1){
         choosing.setX(stage.getWidth()/20+140+30);
         choosing.setY(stage.getHeight()/8+25+210);
         choosing.setWidth(140);
         choosing.setHeight(200);
      }else {
         choosing.setX(stage.getWidth()/20+140+30);
         choosing.setY(stage.getHeight()/8+25+420);
         choosing.setWidth(140);
         choosing.setHeight(200);
      }
      
   }

   //show imageViews
   public void initScreen(Group root, Stage stage) throws FileNotFoundException{
      loadPic();

      idleView.setX(stage.getWidth()/3*2-idleWidth/2);
      idleView.setY(stage.getHeight()/2-idleHeight/2);
      idleView.setFitWidth(idleWidth);
      idleView.setFitHeight(idleHeight);

      hairView.setX(stage.getWidth()/3*2-idleWidth/2);
      hairView.setY(stage.getHeight()/2-idleHeight/2);

      clothView.setX(stage.getWidth()/3*2-idleWidth/2);
      clothView.setY(stage.getHeight()/2-idleHeight/2);

      weaponView.setX(stage.getWidth()/3*2-idleWidth/2-150);
      weaponView.setY(stage.getHeight()/2-idleHeight/2-100);
      //size of chcater's image is 400*600, so i set the platform to 400*400

      choosingList.add(new ImageView());
      choosingList.get(0).setX(stage.getWidth()/20);//140
      choosingList.get(0).setY(stage.getHeight()/8+30);//210
      choosingList.get(0).setFitWidth(160);
      choosingList.get(0).setFitHeight(240);

      choosingList.add(new ImageView());
      choosingList.get(1).setX(stage.getWidth()/20+140);//140
      choosingList.get(1).setY(stage.getHeight()/8-50+30);//210
      choosingList.get(1).setFitWidth(200);
      choosingList.get(1).setFitHeight(300);

      choosingList.add(new ImageView());
      choosingList.get(2).setX(stage.getWidth()/20+320);//140
      choosingList.get(2).setY(stage.getHeight()/8+30);//210
      choosingList.get(2).setFitWidth(160);
      choosingList.get(2).setFitHeight(240);

      choosingList.add(new ImageView());
      choosingList.get(3).setX(stage.getWidth()/20);//140
      choosingList.get(3).setY(stage.getHeight()/8+210);//210
      choosingList.get(3).setFitWidth(160);
      choosingList.get(3).setFitHeight(240);

      choosingList.add(new ImageView());
      choosingList.get(4).setX(stage.getWidth()/20+140);//140
      choosingList.get(4).setY(stage.getHeight()/8+210-50);//210
      choosingList.get(4).setFitWidth(200);
      choosingList.get(4).setFitHeight(300);

      choosingList.add(new ImageView());
      choosingList.get(5).setX(stage.getWidth()/20+320);//140
      choosingList.get(5).setY(stage.getHeight()/8+210);//210
      choosingList.get(5).setFitWidth(160);
      choosingList.get(5).setFitHeight(240);

      choosingList.add(new ImageView());
      choosingList.get(6).setX(stage.getWidth()/20);//140
      choosingList.get(6).setY(stage.getHeight()/8+420);//210
      choosingList.get(6).setFitWidth(160);
      choosingList.get(6).setFitHeight(240);

      choosingList.add(new ImageView());
      choosingList.get(7).setX(stage.getWidth()/20+140);//140
      choosingList.get(7).setY(stage.getHeight()/8+420-50);//210
      choosingList.get(7).setFitWidth(200);
      choosingList.get(7).setFitHeight(300);

      choosingList.add(new ImageView());
      choosingList.get(8).setX(stage.getWidth()/20+320);//140
      choosingList.get(8).setY(stage.getHeight()/8+420);//210
      choosingList.get(8).setFitWidth(160);
      choosingList.get(8).setFitHeight(240);
      
      setChoosing(stage,0,0,0);

      for(int i=0;i<9;i++){
         root.getChildren().add(choosingList.get(i));
      }
      
      root.getChildren().add(idleView);
      root.getChildren().add(clothView);
      root.getChildren().add(weaponView);
      root.getChildren().add(hairView);
      root.getChildren().add(choosing);

      
      //root.getChildren().add(idleTestView);
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
   
   public int getHair(){
      return curHair;
   }

   public int getCloth(){
      return curCloth;
   }

   public int getWeapon(){
      return curWeapon;
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
}
