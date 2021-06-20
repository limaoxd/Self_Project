package entity;

import map.*;
import static java.lang.System.out;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Background extends Entity{
    public int level=0;
    private int blockType=0;
    private double boxHRate=0.75;
    private double boxY=50;

    public Background(int w,int h,double x,double y) throws FileNotFoundException{
        setSize(w,h);
        setPos(x,y);
    }

    public static void createBg(List<Background> obj) throws FileNotFoundException{
        int i=0;
        for(String col : MAP.background1){
            double length_1=0;
            double length_2=0;
            double length_3=0;
            double length_4=0;
            for(int j = 0;j<col.length();j++){
               //Type 1(normal block)
                if(col.charAt(j)=='.'){// count rectangle length
                    length_1++;
                    //If col end but number is "1"
                    if(j==col.length()-1){
                        Background bg = new Background((int)length_1*100,200,100*(j+1-length_1/2)+50,99*(MAP.background1.length-1-i));
                        obj.add(bg);
                        bg.setType(0);
                        length_1 = 0;
                    }
                }
               //If read not 0 stop counting
                else if(length_1>0){
                    Background bg = new Background((int)length_1*100,200,100*(j-length_1/2)+50,99*(MAP.background1.length-1-i));
                    obj.add(bg);
                    bg.setType(0);
                    length_1 = 0;
                }

                if(col.charAt(j)=='-'){
                    length_2++;
                    //If col end but number is "1"
                    if(j==col.length()-1){
                        Background bg = new Background((int)length_2*100,200,100*(j+1-length_2/2)+50,99*(MAP.background1.length-1-i));
                        obj.add(bg);
                        bg.setType(-1);
                        length_2 = 0;
                    }
                }
                else if(length_2>0){
                    Background bg = new Background((int)length_2*100,200,100*(j-length_2/2)+50,99*(MAP.background1.length-1-i));
                    obj.add(bg);
                    bg.setType(-1);
                    length_2 = 0;
                }

                if(col.charAt(j)=='7'){
                    length_3++;
                    //If col end but number is "1"
                    if(j==col.length()-1){
                        Background bg = new Background((int)length_3*100,200,100*(j+1-length_3/2)+50,99*(MAP.background1.length-1-i));
                        obj.add(bg);
                        bg.setType(7);
                        length_3 = 0;
                    }
                }
                else if(length_3>0){
                    Background bg = new Background((int)length_3*100,200,100*(j-length_3/2)+50,99*(MAP.background1.length-1-i));
                    obj.add(bg);
                    bg.setType(7);
                    length_3 = 0;
                }

                if(col.charAt(j)=='8'){
                    length_4++;
                    //If col end but number is "1"
                    if(j==col.length()-1){
                        Background bg = new Background((int)length_4*100,200,100*(j+1-length_4/2)+50,99*(MAP.background1.length-1-i));
                        obj.add(bg);
                        bg.setType(8);
                        length_4 = 0;
                    }
                }
                else if(length_4>0){
                    Background bg = new Background((int)length_4*100,200,100*(j-length_4/2)+50,99*(MAP.background1.length-1-i));
                    obj.add(bg);
                    bg.setType(8);
                    length_4 = 0;
                }
                //Type 1
                if(col.charAt(j)>'0'&&col.charAt(j)<='>'){
                    int num = (int)col.charAt(j)-'0';
                    if(num!=7&&num!=8){
                        Background bg = new Background(100,200,100*(j+0.5)+50,99*(MAP.background1.length-1-i));
                        obj.add(bg);
                        bg.setType(num);
                    }
                }

                if(col.charAt(j)=='l'){
                    Background bg = new Background(100,200,100*(j+0.5)+50,99*(MAP.background1.length-1-i));
                    obj.add(bg);
                    bg.setType(-2);
                }
                if(col.charAt(j)=='r'){
                    Background bg = new Background(100,200,100*(j+0.5)+50,99*(MAP.background1.length-1-i));
                    obj.add(bg);
                    bg.setType(-3);
                }
            }
            i++;
         }
    }

    public Image extendSprite(Image img,int w){
        w*=2.5;
        int width = 250,height = 500;

        PixelReader pixelReader = img.getPixelReader();

        WritableImage c_img = new WritableImage(w,height);
        PixelWriter writer = c_img.getPixelWriter();
        for(int i=0; i<height; i++){
            for(int j=0; j<w; j++){
                Color color = pixelReader.getColor(j%width,i);
                writer.setColor(j,i,color);
            }
        }
        
        return (Image)c_img;
    }

    @Override
    public void setPos(double x,double y){
        Pos[0] = x;
        Pos[1] = y;
        if(sprite!=null){
            sprite.setFitWidth(Width*ratio[0]);
            sprite.setFitHeight(Height*ratio[1]);
            sprite.setX((Pos[0]-Width/2-Cam[0])*ratio[0]); 
            sprite.setY((1080-Pos[1]-Height+Cam[1])*ratio[1]);
        }
        if(hitbox!=null){
            hitbox.setX((Pos[0]-Width/2-Cam[0])*ratio[0]);
            hitbox.setY((1080-Pos[1]-Height*boxHRate-boxY+Cam[1])*ratio[1]);
            hitbox.setWidth(Width*ratio[0]); 
            hitbox.setHeight(Height*boxHRate*ratio[1]);
        }
    }

    @Override
    public double getBoxH(){
        return Height*boxHRate;
    }

    @Override
    public double getBoxY(){
        return Pos[1]+boxY;
    }
    
    @Override
    public void act(){
        setPos(getX(),getY());
    }

    private void setType(int t) throws FileNotFoundException{
        this.blockType=t;

        if(blockType==-3) image=new Image(new FileInputStream("pic/map/grass_s_2.png"));
        else if(blockType==-2) image=new Image(new FileInputStream("pic/map/grass_s_1.png"));
        else if(blockType==-1) image=extendSprite(new Image(new FileInputStream("pic/map/grass_s_0.png")),Width);
        else if(blockType==0) image=extendSprite(new Image(new FileInputStream("pic/map/grass.png")),Width);
        else if(blockType==1) image=new Image(new FileInputStream("pic/map/route_0.png"));
        else if(blockType==2) image=new Image(new FileInputStream("pic/map/route_1.png"));
        else if(blockType==3) image=new Image(new FileInputStream("pic/map/route_2.png"));
        else if(blockType==4) image=new Image(new FileInputStream("pic/map/route_3.png"));
        else if(blockType==5) image=new Image(new FileInputStream("pic/map/route_4.png"));
        else if(blockType==6) image=new Image(new FileInputStream("pic/map/route_5.png"));
        else if(blockType==7) image=extendSprite(new Image(new FileInputStream("pic/map/wall_u.png")),Width);
        else if(blockType==8) image=extendSprite(new Image(new FileInputStream("pic/map/wall_d.png")),Width);
        else if(blockType==9) image=new Image(new FileInputStream("pic/map/wall_l.png"));
        else if(blockType==10) image=new Image(new FileInputStream("pic/map/wall_r.png"));
        else if(blockType==11) image=new Image(new FileInputStream("pic/map/wall_lu.png"));
        else if(blockType==12) image=new Image(new FileInputStream("pic/map/wall_ru.png"));
        else if(blockType==13) image=new Image(new FileInputStream("pic/map/wall_ld.png"));
        else if(blockType==14) image=new Image(new FileInputStream("pic/map/wall_rd.png"));

        if(blockType>6) {
            level=1;
            hitbox = new Rectangle();
            /*hitbox.setStroke(Color.LIGHTGREEN);
            hitbox.setStrokeWidth(2);*/
            hitbox.setFill(Color.TRANSPARENT);
            if(blockType<11||blockType>12) {
                boxHRate=0.25;
                boxY=0;
                if(blockType==7) boxY=50;
            }
        }
        sprite = new ImageView(image);
    }

    public int getType(){
        return blockType;
    }
}