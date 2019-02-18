/**
 * Created by willi on 4/7/2016.
 */

import java.math.BigInteger;
import java.util.ArrayList;

class PrimesAndDragons {
    private StageCreation st = new StageCreation();
    private BGMusic bgm = new BGMusic();

    public EnemyPanel ep = new EnemyPanel();
    public BoardPanel bp = new BoardPanel();
    private Player player;
    public ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
    private boolean lose = false;
    public int stageNumber = 0;

    public PrimesAndDragons(){}
    public void startGame(int stage){
        stageNumber = stage;
        player = new Player(initPlayerHealth());
        bp.toggleEnabled(false);  //disable input while initiating boards
        bp.init(stageNumber);
        ep.init();  //initialize the enemy panel
        while(stageNumber <= 50){  //50 LEVELS OF DOOOOOOOOOOOOOOOOM
            ep.setEnemyList(enemyList);  //pass the enemylist to the enemypanel
            startStage();  //start the stage
            if(lose)return;  //if they are a loser they lose
        }
    }
    public void startStage(){                                                   //DETERMINES WHICH DIFFICULTY TO SET BASED ON WHICH STAGE IT, creates a stage
        if(stageNumber == 0) {  //stage 0 is the tutorial lv
            st.createStage(enemyList,0);
            ep.setBackgroundFile("0");
            bgm.playBGMusic("resources/music/kAsLAM.wav",0);
            //scriptTutorial();
        }else if(stageNumber > 0 && stageNumber <= 15){
            playMusic("resources/music/Stage 1.wav",0);
            ep.setBackgroundFile("1-15");
            st.createStage(enemyList,1);
        }else if(stageNumber > 15 && stageNumber <= 30){
            playMusic("resources/music/Stage 3.wav",0);
            ep.setBackgroundFile("16-30");
            st.createStage(enemyList,2);
        }else if(stageNumber > 30 && stageNumber <= 45) {
            playMusic("resources/music/Stage 4.wav",0);
            ep.setBackgroundFile("31-45");
            st.createStage(enemyList,3);
        }else if(stageNumber == 46){
            playMusic("resources/music/DFC.wav",65000);
            st.createStage(enemyList,4);
            ep.setBackgroundFile("46-49");
        }else if(stageNumber == 47){
            st.createStage(enemyList,5);
        }else if(stageNumber == 48) {
            st.createStage(enemyList,6);
        }
        else if(stageNumber == 49){
            playMusic("resources/music/mogolovonio.wav",0);
            st.createStage(enemyList,7);
        }
        else if(stageNumber == 50){
            playMusic("resources/music/FinalDes.wav",0);
            ep.setBackgroundFile("50");
            st.createStage(enemyList,8);
        }
        bp.toggleEnabled(false); //make sure the player doesn't change the board while text is running
        ep.scrollText("",400);  //reset the "terminal"
        ep.scrollText("",400);  //after every turn
        if(stageNumber != 0 && stageNumber < 44) ep.scrollText("You are now on stage " + stageNumber + ".", 100);  //we let them know what level they are on

        bp.toggleEnabled(true);  //enable input
        while(enemyList.size() > 0) {  //while there are still enemies alive
            String getNum = bp.getNumber();  //get the uSSr input
            bp.toggleEnabled(false);  //make sure the player doesn't change the board while text is running
            if (!getNum.equals("-1")){  //JUST IN CASE THE PLAYER RESETS THE BOARD, ITS NOT A TURN
                if(getNum.equals("reset")) resetButton();  //if the player reset the board
                else {  //otherwise
                    calculateDamage(getNum);  //we calculate the damage dealt to the enemy
                    if(enemyList.size() != 0) takeDamage();  //take dmg
                    bp.setNumBarState(1);  //if the number bar is still not neutral, set number bar as neutral
                    if(lose) return;
                }
            }
            bp.toggleEnabled(true);  //enable input
        }
        stageNumber++;
        bp.updateFloor();
        updateMaxHealth();
    }
    public int initPlayerHealth(){
        int maxHealth = 400;
        if(stageNumber > 0) maxHealth += 100;
        if(stageNumber > 15) maxHealth += 250;
        if(stageNumber > 30) maxHealth += 250;
        if(stageNumber > 45) maxHealth += 500;
        if(stageNumber == 50) maxHealth += 500;
        return maxHealth;
    }
    public void updateMaxHealth(){
        switch(stageNumber){  //every few floors the max health is updated
            case 1:
                bp.updateMaxHealth(100);
                player.maxHealth += 100;
                break;
            case 16:
                bp.updateMaxHealth(250);
                player.maxHealth += 250;
                break;
            case 31:
                bp.updateMaxHealth(250);
                player.maxHealth += 250;
                break;
            case 46:
                bp.updateMaxHealth(500);
                player.maxHealth += 500;
                break;
            case 50:
                bp.updateMaxHealth(500);
                player.maxHealth += 500;
                break;
        }
    }
    public void playMusic(String s,int loopStart){  //plays music
        if(!bgm.getMusic().equals(s)){
            if(!bgm.getMusic().equals("")) bgm.stop();
            bgm.playBGMusic(s,loopStart);
        }
    }
    public void resetButton(){
        int dmg = (player.getMaxHealth()*stageNumber)/250;  //dmg penalty
        bp.updateHealth(dmg);  //update the health
        player.deductHealth(dmg);  //and the health bar
        ep.scrollText("You have reset for "+dmg+" health.", 200);  //inform the user that they have reset
    }
    public void calculateDamage(String number){  //CALCULATES THE DAMAGE GIVEN THE NUMBER USER HAS SELECTED
        BigInteger num = new BigInteger(number);
        int sum = sumOfDigits(number);  //GET SUM OF DIGITS
        ep.scrollText("Sum of digits: " + sum, 500);
        double DMGmultiplier =  1+((number.length()-2)/4.0);
        if(DMGmultiplier > 1) sum = (int)(sum*DMGmultiplier); //DMG MULTIPLIER (MORE NUMBERS == MORE MONIEZ)

        for (int i = 0; i < enemyList.size(); i++) {                                //FOR EVERY LIVING MONSTER ON THE BOARD
            BigInteger bi = BigInteger.valueOf(enemyList.get(i).getPrime());
            BigInteger mod = num.mod(bi);
            if (mod.equals(BigInteger.ZERO)) {                            //IF THE USER'S NUMBER DIVIDES THE MONSTER'S NUMBER
                if(bp.numBarState == 1){
                    bp.setNumBarState(2);
                    if(DMGmultiplier > 1) ep.scrollText("Damage multiplier: 1 + "+(number.length()-2)+" * 0.25 = "+DMGmultiplier, 400);  //SAY THAT THEY HAVE MONIEC
                }
                ep.deductHealth(i,sum);
                ep.scrollText("You have done " + sum + " damage to the enemy.", 400);
            }
            if (enemyList.get(i).getHealth() <= 0) {                           //IF THE MONSTER IS DEADED
                enemyList.remove(i);                                          //REMOVEEEEEEEEEEEEEE IT
                ep.scrollText("You have defeated an enemy!", 400);
                i--;                           //WE NEED TO DO DIS SINCE WE REMOVED SOMETHING AND WE MIGHT SKIP A MONSTER
            }
        }

        if(bp.numBarState == 1) {  //means nothing matched
            bp.setNumBarState(0);
            ep.scrollText("That number is not divisible by any monster's number!", 400); //You did absolutely no damage at all you little dipshit!
        }
        else if(number.indexOf('0') != -1){  //healing time
            int heal = 0;
            for(int i = 0; i < number.length(); i++)  if(number.charAt(i) == '0') heal -= player.maxHealth/12;  //for every 0 in the number, +1/10 max health
            player.deductHealth(heal);  //"deduct" health
            bp.updateHealth(heal);  //update visual bar
            ep.scrollText("You have gained " + (-heal) + " health!", 400);  //texting is not for drinking
        }
    }
    public void takeDamage(){  //this is when the enemy attacks and the player takes damage
        for(int i = 0; i < enemyList.size(); i++){  //for every enemy on board
            int dmg = enemyList.get(i).getDamage();  //get the damage
            player.deductHealth(dmg);
            bp.updateHealth(dmg);
            ep.scrollText("An enemy did " + dmg + " damage to you!", 200);
            if(player.getHealth() <= 0) {  //THEY A LOOOOSER RIPPERINIES
                endGame();
                return;
            }
        }
    }
    public int sumOfDigits(String number){  //returns the sum of all the digits from the inputted number
        int sum = 0;
        for(int i = 0; i < number.length(); i++)  sum += Integer.parseInt(Character.toString(number.charAt(i)));
        return sum;
    }
    public void endGame(){  //rip they are LOSER the GAMEING industry
        //for(int i = enemyList.size() - 1; enemyList.size() > 0; i--)  enemyList.remove(i);  //remove all the enemies
        //game over message
        ep.scrollText("Game over!", 800);
        ep.scrollText("Try again by running this code a second time.",-1);
        lose = true;  //set the lose to TRUE
    }
}
