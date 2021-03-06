package it.unibs.pajc.model;

import java.io.Serializable;

/**
 * Sottoparte del model. Rappresenta il pacchetto dati che viene mandata dal server al client.
 */

public class ExchangeDataClass extends BaseModel implements Serializable{

    private Player player1;
    private Player player2;
    private Ball ball;
    private Score score;
    private SoundClipIdentifier soundClipIdentifierPl1;
    private SoundClipIdentifier soundClipIdentifierPl2;

    public ExchangeDataClass() {

    }

    public ExchangeDataClass(GameField gameField) {
        this.player1 = gameField.getPlayer1();
        this.player2 = gameField.getPlayer2();
        this.ball = gameField.getBall();
        this.score = gameField.getScore();
        this.soundClipIdentifierPl1 = gameField.getSoundClipIdentifierPl1();
        this.soundClipIdentifierPl2 = gameField.getSoundClipIdentifierPl2();
    }

    /* ===================
    GETTERS AND SETTERS
    ====================*/

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Ball getBall() {
        return ball;
    }

    public Score getScore() {
        return score;
    }

    public SoundClipIdentifier getSoundClipIdentifierPl1() {
        return soundClipIdentifierPl1;
    }
    public SoundClipIdentifier getSoundClipIdentifierPl2() {
        return soundClipIdentifierPl2;
    }
}
