import java.util.ArrayList;

/**
 * Created by willi on 4/21/2016.
 */
public class StageCreation {
    private int[] lvl1 = {3,4,5,6,8,9,10,12,20,25,50};
    private int[] lvl2 = {5,6,8,9,10,11,12,14,15,20,22,24,25,30,32,40,50};
    private int[] lvl3 = {7,10,11,12,14,15,16,18,21,22,26,27,30,32,35,40,45,60};
    private String[] s1 = {"tree","burger","fire1","dragon1","dragon2","potato1","tv1"};
    private String[] s2 = {"tree","burger","taco","rice","fire1","fire2","dragon2","dragon3","dragon4","potato2","tv1","tv2"};
    private String[] s3 = {"burger","taco","rice","fire2","fire3","dragon4","dragon5","potato3","tv2","tv3"};
    public void createStage(ArrayList<Enemy> enemyList, int state){            //CREATES AN EASY STAGE.  NOTHING TO SEE HERE, MOVE ALONG
        int max = 2;
        int min = 1;
        int randNum = (int)(Math.random()*(max-min+1)+min);
        switch(state){
            //tutorial stage
            case 0:
                enemyList.add(new Enemy(2,500,20,"tv1",true));
                break;
            //easy stage (1-15)
            case 1:
                for(int i = 0; i < randNum; i++){enemyList.add(new Enemy(genRandNum(lvl1),800/randNum,100/randNum,genRandSprite(s1),true));}
                break;
            //medium stage (16-30)
            case 2:
                max = 3;
                randNum = (int)(Math.random()*(max-min+1)+min);
                for(int i = 0; i < randNum; i++){enemyList.add(new Enemy(genRandNum(lvl2),1200/randNum,90/randNum,genRandSprite(s2),true));}
                break;
            //hard stage (31-45)
            case 3:
                max = 3;
                min = 2;
                randNum = (int)(Math.random()*(max-min+1)+min);
                for(int i = 0; i < randNum; i++){enemyList.add(new Enemy(genRandNum(lvl3),1500/randNum,120/randNum,genRandSprite(s3),true));}
                break;
            //boss 46
            case 4:  //duo opposites
                enemyList.add(new Enemy(11,75,500,"dat_boi",true));
                enemyList.add(new Enemy(13,375,100,"dat_boi",true));
                break;
            //boss 47  (damage recipricol to health)
            case 5:
                enemyList.add(new Enemy(16,2000,50,"dat_boi",true));
                break;
            //boss 48
            case 6:
                enemyList.add(new Enemy(6,1500,100,"dat_boi",true));
                enemyList.add(new Enemy(12,1500,100,"dat_boi",true));
                enemyList.add(new Enemy(20,1500,100,"dat_boi",true));

                break;
            //boss 49 (dat boi)
            case 7:
                enemyList.add(new Enemy(3,5000,420,"dat_boi",true));
                break;
            //frazier stage 1
            case 8:
                enemyList.add(new Enemy(17,100,300,"frazier",true));
                break;
            //frazier stage 2
            case 9:
                enemyList.add(new Enemy(45,9001,450,"frazier_final_form",true));
                break;
            //frazier stage 3
            case 10:
                enemyList.add(new Enemy(2,9999,666,"frazier_final_form",true));
                break;
        }
    }
    public int genRandNum(int[] i){
        return i[(int)(Math.random() * i.length)];
    }
    public String genRandSprite(String[] s){
        return s[(int)(Math.random() * s.length)];
    }
}
