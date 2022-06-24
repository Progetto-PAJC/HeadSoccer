package it.unibs.pajc.model;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import javax.swing.Timer;

public class Player extends DinamicObject implements Serializable {
    public static final double DEFAULT_POS_X = 300; //posizione iniziale del giocatore e d
    public static final double JUMP_STRENGTH = 10;  //potenza del calcio, con quale velocità parte
    public static final double CONST_SPEED_X = 3.0;

    private static final int MAX_SCORE = 3;

    private int playerID; //Indica il numero del player (1 => sinistra || 2 => destra)
    private boolean kickStatus;
    private boolean isWinner;
    private int currentIMG = 0;    //indice dell'immagine da visualizzare
    private Timer timerAnimation;
    private int playerScore = 0;


    public Player(int playerID) {
        this.playerID = playerID;

        setDefault();

        createSkeleton();
        calculateCdm();

        this.timerAnimation = new Timer(100, e -> {
            setCurrentIMGIndex();
        });
    }

    public void setDefault() {
        if(playerID == 1) {
            this.position[0] = -DEFAULT_POS_X;
        } else {
            this.position[0] = DEFAULT_POS_X;
        }
        this.position[1] = 0;
        this.speed[0] = 0;
        this.speed[1] = 0;
    }


    public void startAnimation(){
        timerAnimation.start();
    }

    public void stopAnimation() {
        this.timerAnimation.stop();
        this.currentIMG = 0;
    }

    /*
    in base allo stato in cui si trova il player.
     */
    public void setCurrentIMGIndex(){
        if(this.currentIMG == 0)
            this.currentIMG = 1;
        else
            this.currentIMG = 0;
    }

    public void kick(boolean isKicking) {
        if(!kickStatus && isKicking) {  //viene reimpostato a false in key released, serve per le collisioni nella palla
            kickStatus = isKicking;  //viene reimpostato a false in key released, serve per le collisioni nella palla
            legRotation(1);   //posizione del calcio
        }
    }

    //riporta la gamba calciante nello stato iniziale
    public void stopKicking(){
        legRotation( -1);
        kickStatus = false;
        this.currentIMG = 0;
    }

    /*
    ruota la gamba in base al giocatore che si sta usando, se il coefficiente è 1 -> si calcia, se è -1 ri riporta la gamba alla posizione originale
     */
    private void legRotation(int rotationCoefficient){

        if (rotationCoefficient == 1) {  //se sta calciando
            Shape rotatedLeg;
            AffineTransform tx;

            //se il giocatore è il sinistro -> si ruota la shape[2], ossia la gamba destra
            if(playerID == 1){
                tx = rotateLeg(Math.PI / 4);
                rotatedLeg = tx.createTransformedShape(this.getSingleShape(2));
                this.setSingleShape(2,rotatedLeg);
            }

            else{
                tx = rotateLeg(-Math.PI / 4);
                rotatedLeg = tx.createTransformedShape(this.getSingleShape(1));
                this.setSingleShape(1,rotatedLeg);
            }
        }
        else {   //se ha smesso di calciare

            if(playerID == 1){
                Shape rightLeg = new Rectangle(15, 0, 15, 32);
                this.setSingleShape(2, rightLeg);
            }
            else{
                Shape leftLeg = new Rectangle(0, 0, 15, 32);
                this.setSingleShape(1, leftLeg);
            }
        }
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
            //se la palla è ferma in qualsiasi posto
            if(ball.getSpeed(1) == 0) {
                if (this.speed[1] < 0) {   //se il giocatore salta sulla palla mentre è ferma
                    this.speed[1] = 0;  //si ferma sulla palla
                    this.setPosY(ball.getPosY() + ball.getTotalShape().getBounds().height);
                }
            }
        }
        /*
        controllo collisioni player e porta -> nel primo if sottraiamo la velocità in Y perché altrimenti succedono macelli: quando il player atterra sulla porta in realtà gli
        viene sottratta ancora la velocità e quindi risulterebbe in ogni caso più in basso rispetto all'altezza effettiva della porta. Per questo settiamo anche la posizione
        oltre che impostare la velocità in Y = 0.
         */
        if(o instanceof FootballGoal footballGoal) {
            if(this.getPosY() - speed[1] < footballGoal.getTotalShape().getBounds().height) {    //controlla se il giocatore sta al di sotto della porta
                if(footballGoal.isLeft())
                    this.setPosX(footballGoal.getPosX() + footballGoal.getTotalShape().getBounds().width);
                else
                    this.setPosX(footballGoal.getPosX() - this.getTotalShape().getBounds().width);
            }
            else{
                this.speed[1] = 0;
                this.setPosY(footballGoal.getTotalShape().getBounds().height);
            }
        }

        //collisione giocatore e giocatore
        if(o instanceof Player otherPlayer){
            if(this.playerID == 1) {
                if(Math.abs(this.getActualCdmY() - otherPlayer.getActualCdmY()) + Math.abs(speed[1]) > this.getObjHeight()) {
                    if(this.getActualCdmY() > otherPlayer.getActualCdmY()) {
                        this.position[1] = otherPlayer.getPosY() + otherPlayer.getObjHeight();
                    }
                    else if(this.getActualCdmY() < otherPlayer.getActualCdmY()) {
                        this.position[1] = this.getPosY();
                    }
                    this.speed[1] = 0;
                }
                else {
                    if(this.getActualCdmX() < otherPlayer.getActualCdmX()) {
                        this.position[0] = otherPlayer.position[0] - otherPlayer.getObjWidth();
                    }
                    else if(this.getActualCdmX() > otherPlayer.getActualCdmX()) {
                        this.position[0] = otherPlayer.position[0] + otherPlayer.getObjWidth();
                    }
                }
            }
        }

    }

    /*
    Altro metodo che anzi ché usare una forma singola ne usa 3: una per il busto e le altre due per le gambe.
    All'array viene aggiunta prima la gamba sinistra e poi la gamba destra (dal punto di vista dell'osservatore esterno).
    Il player1 (a sinistra) utilizzerà la gamba destra per calciare
    viceversa.
    Considero la gamba scheletro come metà porzione dell rettangolo in basso che identifica la parte bassa del corpo
     */
    @Override
    public void createSkeleton() {
        Shape body = new Rectangle(0, 32 , 30, 48);   //busto (19 = altezza gambe)
        Shape leftLeg = new Rectangle(0, 0, 15, 32);
        Shape rightLeg = new Rectangle(15, 0, 15, 32);

        super.objectShape.add(body);
        super.objectShape.add(leftLeg);
        super.objectShape.add(rightLeg);
    }


    private AffineTransform rotateLeg(double radius) {
        AffineTransform rotationTransform;

        if(this.playerID == 1) {
            rotationTransform = AffineTransform.getRotateInstance(radius, this.getSingleShape(2).getBounds().x + this.getSingleShape(2).getBounds().width /2.0,
                    this.getSingleShape(2).getBounds().height);
        }
        else {
            rotationTransform = AffineTransform.getRotateInstance(radius, this.getSingleShape(1).getBounds().x + this.getSingleShape(1).getBounds().width /2.0,
                    this.getSingleShape(1).getBounds().height);
        }

        return rotationTransform;
    }

    /*
    public void initializeTimerAnimation() {
        this.timerAnimation = new Timer(100, e -> {
            setCurrentIMGIndex();
        });
    }
    */

    //GETTERS e SETTERS
    public boolean getKickStatus(){return kickStatus;}

    public int getPlayerID() {
        return playerID;
    }

    public Shape getKickLegTransformed() {
        AffineTransform t = new AffineTransform(); //inizialmente coincide con la matrice identità
        Shape legTransformedShape;

        if(playerID == 1) {
            t.translate(position[0] + this.getObjWidth() / 2, position[1]); //applicazione della trasformata
            legTransformedShape = t.createTransformedShape(this.getSingleShape(2));
        }
        else {
            t.translate(position[0], position[1]); //applicazione della trasformata
            legTransformedShape = t.createTransformedShape(this.getSingleShape(1));
        }

        return legTransformedShape;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public boolean isWinner() {
        return this.isWinner;
    }

    public void setWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    public void setSpeed(int speedIndex, double newSpeed){
        speed[speedIndex] = newSpeed;
    }

    public int getCurrentImageIndex() {
        if(kickStatus) {
            this.timerAnimation.stop();
            this.currentIMG = 2;
        }

        return this.currentIMG;

    }

    public int getPlayerScore() {
        return playerScore;
    }

    private void setPlayerScore(int score) {
        this.playerScore = score;
        if(score == MAX_SCORE)
            isWinner = true;
    }

    public void incrementPlayerScore() {
        setPlayerScore(this.playerScore + 1);
    }

}