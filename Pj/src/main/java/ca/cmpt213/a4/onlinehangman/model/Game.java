package ca.cmpt213.a4.onlinehangman.model;
import javax.validation.constraints.NotNull;
/**
 * A class for Game
 * @YIXU YE
 * @SFU ID： 301368702
 * @SFU EMAIL: yixuy@sfu.ca
 */
public class Game {
    // Status initialize as active
    private String status = "Active";
    private long id;
    private String targetWord;
    private String showWord;
    private int totalGuessTimes = 0;
    private int wrongGuessTimes = 0;
    @NotNull
    // Must using String type, otherwise typeMatch error
    private String guessChar = "";

    public Game(){
    }
    public Game(String status, long id, String targetWord, String showWord, int totalGuessTimes, int wrongGuessTimes, String guessChar) {
        this.status = status;
        this.id = id;
        this.targetWord = targetWord;
        this.showWord = showWord;
        this.totalGuessTimes = totalGuessTimes;
        this.wrongGuessTimes = wrongGuessTimes;
        this.guessChar = guessChar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(String targetWord) {
        this.targetWord = targetWord;
    }

    public String getShowWord() {
        return showWord;
    }

    public void setShowWord(String showWord) {
        this.showWord = showWord;
    }

    public int getTotalGuessTimes() {
        return totalGuessTimes;
    }

    public void setTotalGuessTimes(int totalGuessTimes) {
        this.totalGuessTimes = totalGuessTimes;
    }

    public int getWrongGuessTimes() {
        return wrongGuessTimes;
    }

    public void setWrongGuessTimes(int wrongGuessTimes) {
        this.wrongGuessTimes = wrongGuessTimes;
    }

    public String getGuessChar() {
        return guessChar;
    }
    public void setGuessChar(String guessChar) {
        if(!guessChar.equals("")){
            this.guessChar = guessChar;
        }else {
            this.guessChar = " ";
        }
    }
    @Override
    public String toString() {
        return "Game{" +
                "status='" + status + '\'' +
                ", id=" + id +
                ", targetWord='" + targetWord + '\'' +
                ", showWord='" + showWord + '\'' +
                ", totalGuessTimes=" + totalGuessTimes +
                ", wrongGuessTimes=" + wrongGuessTimes +
                ", guessChar=" + guessChar +
                '}';
    }
    public void getWin(){
        status = "Win";
    }
    public void getLost(){
        status = "Lose";
    }
    // Check and Update the showWord
    public void checkContainChar(){
        int index = targetWord.indexOf(guessChar);
        if(index == -1){
            wrongGuessTimes++;
            return;
        }
        while (index >= 0) {
            showWord = showWord.substring(0,index*2)+ guessChar +showWord.substring(index*2+1);
            index = targetWord.indexOf(guessChar, index + 1);
        }
    }
    // Update Game object's status and showWord based the inputChar
    public void updateGame(String inputChar){
        totalGuessTimes++;
        setGuessChar(inputChar);
        checkContainChar();
        guessChar = "";
        // Check Lost
        if(wrongGuessTimes >= 7){
            getLost();
        }
        String temp = "";
        for(int i = 0; i < showWord.length(); i+=2){
            temp += showWord.charAt(i);
        }
        // Check Win
        if (targetWord.equals(temp)){
            getWin();
        }
    }
}// Game.java
