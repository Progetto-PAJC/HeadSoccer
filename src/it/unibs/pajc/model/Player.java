package it.unibs.pajc.model;

import it.unibs.pajc.helpers.HelperClass;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends DinamicObject {
    public static final double JUMP_STRENGTH = 8;  //potenza del calcio, con quale velocità parte

    private int playerID; //Indica il numero del player (1 => sinistra || 2 => destra)
    private BufferedImage pngImg;

    private boolean isKicking;

    public Player(double posX, double posY, double speedX, double speedY, BufferedImage pngImg) {
        //this.playerID = playerID
        this.position[0] = posX;
        this.position[1] = posY;
        this.speed[0] = speedX;
        this.speed[1] = speedY;
        this.pngImg = HelperClass.flipVerticallyImage(pngImg);
        createSkeleton();
    }

    public BufferedImage getPngImg() {
        return pngImg;
    }

    public void kick(int playerID) {
        isKicking = true;               //viene reimpostato a false in key released
        /*
            if(player == 1)
                +angolo
            else
                -angolo
         */
    }

    public void move(boolean isMovingRight) {
        if(isMovingRight) {
            position[0] += speed[0];
        } else {
            position[0] -= speed[0];
        }
    }

    public void jump() {
        if(position[1] == 0) {
            accelerateY(JUMP_STRENGTH);
        }
    }

    /**
     * Si nota che il metodo move() viene richiamatosì solo in presenza di un input, altrimenti si muoverebbe a caso
     * anche senza premere tasti.
     */
    @Override
    public void update() {
        if(position[1] == 0 && speed[1] < 0) {
            speed[1] = 0;
        } else {
            position[1] += speed[1];
        }

        accelerateY(-GRAVITY);
    }

    @Override
    public void collisionDetected(GameObject o) {
        if(o instanceof Ball){
            //se ci si mette sopra la palla ferma non succede niente
            Ball ball = (Ball)o;
            //se la palla è a terra ferma
            if(ball.getSpeed(0) == 0 && ball.getPosY() == 0 && ball.getSpeed(1) == 0)
                if(this.speed[1] < 0){   //se il giocatore salta sulla palla mentre è ferma
                    this.speed[1] = 0;  //si ferma sulla palla
                    this.position[1] = ball.getShape().getBounds().height;
                }
        }
    }

    //metodo che imposta le forme di riferimento che fanno da struttura per il personaggio
    @Override
    public void createSkeleton() {
        //lo disegna prendendo di riferimento il sistema di coordinate che abbiamo messo adesso -> disegna le forme
        //partendo dal loro angolo in basso a sinistra
        Shape legs = new Rectangle(15, 0, 15, 30);
        Shape body = new Rectangle(0, legs.getBounds().y+30, 30, 50);

        super.objectShape.add(legs);
        super.objectShape.add(body);
    }

    //GETTERS e SETTERS
    public boolean isKicking(){return isKicking;}
}
