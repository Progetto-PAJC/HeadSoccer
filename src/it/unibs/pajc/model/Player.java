package it.unibs.pajc.model;
import it.unibs.pajc.helpers.HelperClass;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends DinamicObject {
    public static final double JUMP_STRENGTH = 15;  //potenza del calcio, con quale velocità parte
    public static final double CONST_SPEED_X = 3.0;

    private int playerID; //Indica il numero del player (1 => sinistra || 2 => destra)

    private boolean isKicking;

    public Player(double posX, double posY, double speedX, double speedY, BufferedImage pngImg) {
        //this.playerID = playerID
        this.position[0] = posX;
        this.position[1] = posY;
        this.speed[0] = speedX;
        this.speed[1] = speedY;
        this.images.add(HelperClass.flipVerticallyImage(pngImg));
        createSkeleton();
    }

    //dovremo toglierlo
    public BufferedImage getPngImg() {
        return this.images.get(0);
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
            speed[0] = CONST_SPEED_X;
        } else {
            speed[0] = - CONST_SPEED_X;
        }
        position[0] += speed[0];
    }

    public void jump() {
        if(speed[1] == 0) {
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
        } else{
            position[1] += speed[1];
        }
        gravityApplication();
    }

    @Override
    public void collisionDetected(GameObject o) {
        if(o instanceof Ball ball){
            //se la palla è a terra ferma
            if(ball.getSpeed(1) == 0) {
                if (this.speed[1] < 0) {   //se il giocatore salta sulla palla mentre è ferma
                    this.speed[1] = 0;  //si ferma sulla palla
                }
            }
        }
        if(o instanceof FootballGoal footballGoal) {
            if((this.getPosX() + this.getTotalShape().getBounds().width) >= footballGoal.getPosX()) {
                if(this.getPosX() >= footballGoal.getPosX() && this.speed[1] == 0) {
                    this.setPosX(footballGoal.getPosX() - this.getTotalShape().getBounds().width);
                } else {
                    this.speed[1] = 0;
                    this.setPosY(footballGoal.getTotalShape().getBounds().height);
                }
            }
            else if(this.getPosY() < footballGoal.getTotalShape().getBounds().height) {
                if(footballGoal.isLeft())
                    this.setPosX(footballGoal.getPosX() + footballGoal.getTotalShape().getBounds().width);
                else
                    this.setPosX(footballGoal.getPosX() - this.getTotalShape().getBounds().width);
            }
        }
    }

    //metodo che imposta le forme di riferimento che fanno da struttura per il personaggio
    @Override
    public void createSkeleton() {
        //lo disegna prendendo di riferimento il sistema di coordinate che abbiamo messo adesso -> disegna le forme
        //partendo dal loro angolo in basso a sinistra
        //Shape legs = new Rectangle(15, 0, 15, 30);
        Shape bodyAndlegs = new Rectangle(0, 0, 30, 80);

        super.objectShape.add(bodyAndlegs);
    }

    //GETTERS e SETTERS
    public boolean isKicking(){return isKicking;}
    public void setSpeed(int speedIndex, double newSpeed){
        speed[speedIndex] = newSpeed;
    }
}
