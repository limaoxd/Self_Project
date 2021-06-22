import entity.*;
import map.*;
import music.*;
import java.util.*;
import java.math.BigInteger;
import static java.lang.System.out;

import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application; 

import javafx.scene.Group; 
import javafx.scene.Scene; 
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Rectangle;

public class Main extends Application {
   List<Entity> group = new ArrayList<>();
   List<Entity> high_level = new ArrayList<>();
   List<Entity> low_level = new ArrayList<>();
   List<Entity> entity = new ArrayList<>();
   List<Entity> obj = new ArrayList<>();
   //List<Trigger> trigger = new ArrayList<>();
   List<Background> backGround = new ArrayList<>();
   public static Player p;
   public static Stirnava boss;
   //public static Savepoint s;
   public static double frameRate;
   private boolean compliteBoss = false;
   private final long[] frameTimes = new long[100];
   private int frameTimeIndex = 0 ;
   private double frameRatio = 0;
   private boolean arrayFilled = false ;
   private int Ablock_now=7,Ablock_pre=0;
   private Rectangle fogDoor;

   public Main() throws FileNotFoundException{
      p = new Player(1300,300);
      boss = new Stirnava(6000,950);
      //Read map and build

      Background.createBg(backGround);
   }

   public static void main(String args[]) throws FileNotFoundException{
      launch(args);
   }

   @Override
   public void start(Stage stage) throws FileNotFoundException {

      //Creating a Group object
      Group root = new Group();

      fogDoor = new Rectangle();
      //Creating a scene object
      Scene scene = new Scene(root, 1920, 1080);
      
      scene.setFill(Color.web("#000000"));
      //scene.setFill(Color.web("#A7C064"));

      Dressing dressing = new Dressing();

      scene.setOnKeyPressed(ke -> {
         if(dressing.isDressing()){
            if (ke.getCode() == KeyCode.LEFT) dressing.LeftPress = true;
            else if (ke.getCode() == KeyCode.RIGHT) dressing.RightPress = true;
            else if (ke.getCode() == KeyCode.UP) dressing.UpPress = true;
            else if (ke.getCode() == KeyCode.DOWN) dressing.DownPress = true;
            else if (ke.getCode() == KeyCode.ENTER) {
               dressing.EnterPress = true;
               p.hairKind = dressing.getHair()+1;
               p.clothKind = dressing.getCloth()+1;
               p.weaponKind = dressing.getWeapon()+1;
            }
         }else {
            if (ke.getCode() == KeyCode.LEFT) p.Leftpress = true;
            else if (ke.getCode() == KeyCode.RIGHT) p.Rightpress = true;
            else if (ke.getCode() == KeyCode.UP) p.Up = true;
            else if (ke.getCode() == KeyCode.DOWN) p.Down = true;
            else if(ke.getCode() == KeyCode.X) p.AtkPressed =true;
            else if (ke.getCode() == KeyCode.SHIFT) p.Shift = true;
            else if (ke.getCode() == KeyCode.CONTROL) p.Ctrl = true;
            else if (ke.getCode() == KeyCode.DIGIT0) p.weaponKind = 1;
            else if (ke.getCode() == KeyCode.DIGIT1) p.weaponKind = 2;
            else if (ke.getCode() == KeyCode.DIGIT2) p.weaponKind = 3;
            else if (ke.getCode() == KeyCode.DIGIT3) p.weaponKind = 4;
            else if (ke.getCode() == KeyCode.DIGIT4) p.weaponKind = 5;
            else if (ke.getCode() == KeyCode.DIGIT5) p.weaponKind = 6;
            else if (ke.getCode() == KeyCode.DIGIT6) p.weaponKind = 7;
            else if (ke.getCode() == KeyCode.DIGIT7) p.weaponKind = 8;
            else if (ke.getCode() == KeyCode.DIGIT8) p.weaponKind = 9;
            else if (ke.getCode() == KeyCode.DIGIT9) p.weaponKind = 10;
         }
      });

      scene.setOnKeyReleased(ke -> {
         if(dressing.isDressing()){
            if (ke.getCode() == KeyCode.LEFT) dressing.LeftPress = false;
            else if (ke.getCode() == KeyCode.RIGHT) dressing.RightPress = false;
            else if (ke.getCode() == KeyCode.UP) dressing.UpPress = false;
            else if (ke.getCode() == KeyCode.DOWN) dressing.DownPress = false;
            else if (ke.getCode() == KeyCode.ENTER) dressing.EnterPress = false;
         }else {
            if (ke.getCode() == KeyCode.LEFT) p.Leftpress = false;
            else if (ke.getCode() == KeyCode.RIGHT) p.Rightpress = false;
            else if (ke.getCode() == KeyCode.UP) p.Up = false;
            else if (ke.getCode() == KeyCode.DOWN) p.Down = false;
            else if (ke.getCode() == KeyCode.SHIFT) {
               p.Shift = false;
               p.ShiftRelease=true;
            }
            else if (ke.getCode() == KeyCode.CONTROL) p.Ctrl = false;
         }
      });

      stage.setFullScreen(true);
      stage.setTitle("Soul_Like");
      stage.setScene(scene);
      stage.getIcons().add(new Image("pic/icon.png"));
      stage.show();
      addE();
      forEach(root,stage);

      dressing.initScreen(root,stage);

      //It can refresh screen
      AnimationTimer mainloop = new AnimationTimer() {
         @Override
         public void handle(long t) {
            if(dressing.isDressing()){
               dressing.showScreen(stage);
            }
            else{
               Music.play();
               scene.setFill(Color.web("#A7C064"));
               sortgroup(root);
               doorCheck();
            }
            Entity.setScreenSize(stage.getWidth(),stage.getHeight());
            //calculate the framerate
            long oldFrameTime = frameTimes[frameTimeIndex] ;
            frameTimes[frameTimeIndex] = t ;
            frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;

            if (frameTimeIndex == 0) {
               arrayFilled = true ;
            }

            if (arrayFilled) {
               long elapsedNanos = t - oldFrameTime ;
               long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
               frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
               frameRate = Math.round(frameRate);
               if(frameRate <=60) frameRatio = 1;
               else if(frameRate>=130) frameRatio = 0.416667;
               else frameRatio = 60/frameRate;
            }

            //Dealing entity and obj collide(falling)
            for(Entity E : entity){
               E.collideh=0;
               E.collidev=0;
               //Divide falling speed
               for(Entity B : obj){
                  if(E.hitbox.intersects(B.hitbox.getBoundsInLocal())){
                     boolean inside = false;
                     if(!(E.getY()>B.getBoxY()+B.getBoxH()-5)&&!(E.getY()+E.getBoxH()<B.getBoxY()+5)){
                        inside = true;
                     }
                     if(E.getBoxX()<B.getBoxX()&&inside) {
                        E.collideh=1;
                        E.setPos(B.getBoxX()-B.getBoxW()/2-E.getBoxW()/2-0.5,E.getY());
                     }
                     else if(E.getBoxX()>B.getBoxX()&&inside) {
                        E.collideh=2;
                        E.setPos(B.getBoxX()+B.getBoxW()/2+E.getBoxW()/2+0.5,E.getY());
                     }
                     else if(E.getBoxY()+E.getBoxH()<B.getBoxY()+5&&E.Isinrange(B)) {
                        E.collidev=1;
                        E.setPos(E.getX(),B.getBoxY()-E.getBoxH()-0.5);
                     }
                     else if(E.getBoxY()>B.getBoxY()&&E.Isinrange(B)) {
                        E.collidev=2;
                        E.setPos(E.getX(),B.getBoxY()+B.getBoxH()+0.5);
                     }
                  }
               }
            }

            if(p.attacking&&p.atkbox.intersects(boss.hitbox.getBoundsInLocal())){
               if(boss.damaged||boss.rolling) {
               }
               else{
                  boss.health_value=boss.health_value-p.damage;
                  //out.println(boss.health_value);
                  boss.damaged=true;
               }
            }
            
            if(boss.attacking&&boss.atkbox.intersects(p.hitbox.getBoundsInLocal())){
               if(p.damaged||p.rolling) {
                  
               }
               else{
                  p.health_value=p.health_value-100;
                  p.damaged=true;
               }
            }

            if(boss.health_value<=0&&!compliteBoss){
               compliteBoss=true;
               entity.remove(boss);
               group.remove(boss);
            }
            //Acting everthing
            Entity.frameRate = frameRatio;
            entity.forEach(E -> E.act());
            obj.forEach(B -> B.act());
            backGround.forEach(G -> G.act());
         }
      };
      mainloop.start();
   }

   public void addE(){
      entity.add(p);
      entity.add(boss);
      //trigger.add(t);
   }

   public void sortgroup(Group root){
      root.getChildren().clear();
      Collections.sort(group,new groupCmp());
      low_level.forEach(L-> root.getChildren().add(L.sprite));
      group.forEach(E-> {
         root.getChildren().add(E.sprite);
         root.getChildren().add(E.hitbox);
         if(E instanceof Player){
            root.getChildren().add(E.partZero);
            root.getChildren().add(E.partOne);
            if(!E.rolling)
               root.getChildren().add(p.atkbox);
               root.getChildren().add(E.weapon);
         }
         if(E instanceof Stirnava){
            root.getChildren().add(boss.atkbox);
         }
      });
      root.getChildren().add(p.bloodBase);
      root.getChildren().add(p.blood);
      root.getChildren().add(p.staminaBase);
      root.getChildren().add(p.stamina);
      if(boss.fight&&!compliteBoss){
         root.getChildren().add(boss.bloodBase);
         root.getChildren().add(boss.blood);
      }
   }

   public void forEach(Group root, Stage stage){
      backGround.forEach(G -> {
         if(G.level==1){
            obj.add(G);
            group.add(G);
         }
         else
            low_level.add(G);
      });
      entity.forEach(E-> group.add(E));
   }

   public void doorCheck(){
      int w=100,h=200;
      fogDoor.setX((4900-w/2-Entity.Cam[0])*Entity.ratio[0]);
      fogDoor.setY((1080-900-h+Entity.Cam[1])*Entity.ratio[1]);
      fogDoor.setWidth(w*Entity.ratio[0]); 
      fogDoor.setHeight(h*Entity.ratio[1]);
      if(p.hitbox.intersects(fogDoor.getBoundsInLocal())){
         boss.fight=true;
      }
   }

}

class groupCmp implements Comparator<Entity>
{
   public int compare(Entity a,Entity b){
      double aY=a.getY();
      double bY=b.getY();
      if(a instanceof Background)
         if(((Background)a).getType()>=13||((Background)a).getType()<=14) aY-=51;

      else if(b instanceof Background)
         if(((Background)b).getType()>=13||((Background)b).getType()<=14) bY-=51;

      return (int)(bY-aY);
   }
}