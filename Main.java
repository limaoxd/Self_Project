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

public class Main extends Application {
   List<Entity> group = new ArrayList<>();
   List<Entity> high_level = new ArrayList<>();
   List<Entity> low_level = new ArrayList<>();
   List<Entity> entity = new ArrayList<>();
   List<Entity> obj = new ArrayList<>();
   //List<Trigger> trigger = new ArrayList<>();
   List<Background> backGround = new ArrayList<>();
   public static Player p;
   //public static Savepoint s;
   public static double frameRate;
   private final long[] frameTimes = new long[100];
   private int frameTimeIndex = 0 ;
   private double frameRatio = 0;
   private boolean arrayFilled = false ;
   private int Ablock_now=7,Ablock_pre=0;

   public Main() throws FileNotFoundException{
      p = new Player(1300,300);
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

      //Creating a scene object
      Scene scene = new Scene(root, 1920, 1080);
      scene.setFill(Color.web("#A7C064"));

      //Music.play();

      scene.setOnKeyPressed(ke -> {
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
         //else if (ke.getCode() == KeyCode.E)
      });

      scene.setOnKeyReleased(ke -> {
         if (ke.getCode() == KeyCode.LEFT) p.Leftpress = false;
         else if (ke.getCode() == KeyCode.RIGHT) p.Rightpress = false;
         else if (ke.getCode() == KeyCode.UP) p.Up = false;
         else if (ke.getCode() == KeyCode.DOWN) p.Down = false;
         else if (ke.getCode() == KeyCode.SHIFT) {
            p.Shift = false;
            p.ShiftRelease=true;
         }
         else if (ke.getCode() == KeyCode.CONTROL) p.Ctrl = false;
      });

      stage.setFullScreen(true);
      stage.setTitle("Soul_Like");
      stage.setScene(scene);
      stage.getIcons().add(new Image("pic/project_icon.png"));
      stage.show();
      //openning.setImage(stage);
      addE();
      forEach(root,stage);

      //It can refresh screen
      AnimationTimer mainloop = new AnimationTimer() {
         @Override
         public void handle(long t) {
            Entity.setScreenSize(stage.getWidth(),stage.getHeight());
            //calculate the framerate
            long oldFrameTime = frameTimes[frameTimeIndex] ;
            frameTimes[frameTimeIndex] = t ;
            frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;

            if (frameTimeIndex == 0) {
               arrayFilled = true ;
            }
            
            sortgroup(root);

            /*if (openning.isStart == true) {
               openning.time = (openning.time + 1) % frameTimes.length ;

               //lag the loading
               if(openning.time == 0 && openning.step == 0) {
                  addE();
                  openning.step++;
                  openning.loadingIn(root,stage);
               }
               else if(openning.time >= 20 && openning.step == 1) forEach(root,openning,stage);
               else if(openning.time >= 40 && openning.step == 2) {
                  openning.step++;
                  openning.beforestory(root,stage);
               }
               else if(openning.isBefore == true && openning.step == 3){
                  openning.step++;
                  root.getChildren().addAll(p.bloodbarBase,p.redBlood,p.bloodbar);
                  root.getChildren().addAll(sw.E_key);
                  LoadSave.reset(s,p);
                  Music.change("ProjectMusic.mp3");
               }

               //for dead screen
               if(openning.isDead == true){
                  if(openning.time >= 3 && openning.lightDegree <= 0.8 && openning.isReborn == false){
                     openning.deadSdarker();
                  }
                  if(openning.time >= 1 && openning.isReborn == true){
                     openning.rebornLoading(root);
                  }
               }

               if(openning.isWin == true && openning.isAfter == true){
                  if(openning.time >= 3 && openning.lightDegree >= 0){
                     openning.winstart(root);
                  }
               }
            }*/

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

            /*if(p.redBlood.getWidth()<=0){
               LoadSave.load();
               //openning.deadScreen(root,stage);
               p.Leftpress = false;
               p.Rightpress = false;
               p.Shift = false;
            }*/

            //Acting everthing
            Entity.frameRate = frameRatio;
            entity.forEach(E -> E.act());
            obj.forEach(B -> B.act());
            backGround.forEach(G -> G.act());
            //trigger.forEach(T -> T.act(p.getX(),p.getY()));
         }
      };
      mainloop.start();
   }

   public void addE(){
      entity.add(p);
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
               root.getChildren().add(E.weapon);
         }
      });
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
      //trigger.forEach((T -> root.getChildren().addAll(T.sprite,T.exclamationMark)));
      entity.forEach(E-> group.add(E));
      //subtitle should above the player
      //trigger.forEach((T -> root.getChildren().addAll(T.E_key,T.messageBase,T.information)));

      /*openning.step++;
      openning.loadingIn(root,stage);*/
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