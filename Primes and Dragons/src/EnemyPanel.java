import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;


/**
 * Created by RyanNiu on 4/21/2016.
 */
class EnemyPanel extends JPanel implements KeyListener, FocusListener, ActionListener{
    private final double MASTER_SCALE = 1.0;  //scale for the whole thing
    private Font fLarge = null;
    private Font fSmall = null;
    private javax.swing.Timer animation = new javax.swing.Timer(10,this);
    private Image bg = Toolkit.getDefaultToolkit().getImage("resources/board/enemy_board/enemy_board_0.png");

    private final int ENEMY_NUM_X_OFFSET = 139;
    private volatile String input = null;
    private int index = 0;
    private volatile boolean isScrolling = false;
    private volatile boolean gotInput = false;
    private volatile String line1 = "";
    private volatile String line2 = "";
    private int blinkDelay = 0;

    private volatile String speech = null;
    private int index2 = 0;
    private volatile boolean isSpeaking = false;
    private volatile String line3 = "";
    private volatile String line4 = "";
    private volatile String line5 = "";

    private ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
    private final int[][] ENEMY_X_OFFSET = {{217},{102,320},{0,217,434}};

    private volatile int isUpdatingHealth = -1;
    private int tempCurrHealth = 0;
    private int amount = 0;

    public EnemyPanel(){
        setPreferredSize(new Dimension((int)(612 * MASTER_SCALE),(int)(378 * MASTER_SCALE)));
        try {  //get the font
            fLarge = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("resources/fonts/ADDLG___.TTF"))).deriveFont((float)(12*MASTER_SCALE));
            fSmall = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("resources/fonts/ADDLG___.TTF"))).deriveFont((float)(10*MASTER_SCALE));
        }catch(IOException | FontFormatException e){
            e.printStackTrace();
            System.out.println("WHY IS THE FONT SO HARD TO FIND?!");
            System.exit(-5);
        }
        addKeyListener(this);
        addFocusListener(this);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setFont(fLarge);
        //this draws the background
        g.drawImage(bg,0,0,(int)(bg.getWidth(this) * MASTER_SCALE),(int)(bg.getHeight(this)*MASTER_SCALE),this);

        //this is for the "terminal" text
        g.setColor(Color.WHITE);
        g.drawString(line1,10,345);
        if(isScrolling || blinkDelay >= 40) g.drawString(line2+"|",10,370);
        else g.drawString(line2,10,370);
        //this is for dialogue
        g.drawString(line3,10,20);
        g.drawString(line4,10,55);
        g.drawString(line5,10,90);

        g.setFont(fSmall);
        //this draws the enemies
        if(enemyList.size() > 0) {
            Image enemyHealth = Toolkit.getDefaultToolkit().getImage("resources/sprites/monsters/enemy_health.png");
            for (int i = 0; i < enemyList.size(); i++) {      //spaces out monsters accordingly
                Image nmeSprite = enemyList.get(i).getSprite();
                int xCoord = ENEMY_X_OFFSET[enemyList.size()-1][i];
                int yCoord = 280;
                if(enemyList.size()%2 == 2) yCoord = 100;  //for now

                g.drawImage(enemyHealth,xCoord,yCoord,(int)(enemyHealth.getWidth(this) * MASTER_SCALE),(int)(enemyHealth.getHeight(this)*MASTER_SCALE),this);
                g.drawImage(nmeSprite, xCoord + 22, 144, 134, 134, this);

                //this draws the number of the enemy
                g.setColor(Color.BLACK);
                String num = ""+enemyList.get(i).getPrime();
                int x = ENEMY_NUM_X_OFFSET - g.getFontMetrics().stringWidth(num)/2;
                g.drawString(""+enemyList.get(i).getPrime(), xCoord + x, yCoord + fSmall.getSize() + 15);//writes the monster's number on it.

                //this draws the healthbar
                Enemy nme = enemyList.get(i);
                int currentHealth = nme.getHealth();
                int maxHealth = nme.getMaxHealth();
                g.setColor(getColor(i,181,203,39));
                g.fillRect(xCoord + 18,yCoord+12,(int)(100.0*currentHealth/maxHealth),6);  //top part of health bar
                g.setColor(getColor(i,34,177,76));
                g.fillRect(xCoord + 18,yCoord+12+6,(int)(100.0*currentHealth/maxHealth),12);  //bottom part of health bar
                g.setColor(new Color(0,0,0));
                if(isUpdatingHealth == i){
                    if (amount > 0) g.setColor(new Color(150, 0, 0));  //if decreasing health, make it red
                    if (amount < 0) g.setColor(new Color(0, 100, 0));  //if increasing health, make it green
                }
                String health = ""+currentHealth+"/"+maxHealth;
                int healthLength = g.getFontMetrics().stringWidth(health)/2;
                if(currentHealth >= 0) g.drawString(health, xCoord + 69 - healthLength, yCoord + fSmall.getSize() + 15);  //display health in numbers
            }
        }
    }
    public void init(){
        animation.start();  //start the timer
        requestFocus();  //grab keyboard focus
    }
    public void scrollText(String text, int wait){
        line1 = line2;  //move the line up
        line2 = "";  //for a new line
        index = 0;  //reset the index
        input = text;  //grab the input

        isScrolling = true;  //set the animation signal
        while(isScrolling){}  //wait for animation to end
        if(wait == -1) while(!gotInput){}  //if we need to request user input, we wait for it
        else {  //otherwise
            try{
                Thread.sleep(wait);  //we have a delay
            }catch(InterruptedException err){}
        }
        repaint();  //and then we update
    }
    public void scrollDialogue(String dia, int wait){
        line3 = line4;  //move the line up
        line4 = line5;  //move the line up
        line5 = "";  //for a new line
        index2 = 0;  //reset the index
        speech = dia;  //grab the input

        isSpeaking = true;  //set the animation signal
        while(isSpeaking){}  //wait for animation to end
        if(wait == -1) while(!gotInput){}  //if we need to request user input, we wait for it
        else {  //otherwise
            try{
                Thread.sleep(wait);  //we have a delay
            }catch(InterruptedException err){}
        }
        repaint();  //and then we update
    }
    public void setEnemyList(ArrayList<Enemy> e){enemyList = e;}
    public void setBackgroundFile(String s){bg = Toolkit.getDefaultToolkit().getImage("resources/board/enemy_board/enemy_board_"+s+".png");}
    public void deductHealth(int i, int sum){
        amount = sum;
        isUpdatingHealth = i;
        tempCurrHealth = enemyList.get(isUpdatingHealth).getHealth();
        while(isUpdatingHealth != -1){}
    }
    public Color getColor(int i, int r, int g, int b){
        Enemy nme = enemyList.get(i);
        int currentHealth = nme.getHealth();
        int maxHealth = nme.getMaxHealth();

        r += (int)((1-(double)currentHealth/maxHealth)*(255-r));
        if(r > 255) r = 255;
        g -= (int)((1-(double)currentHealth/maxHealth)*g*0.70);
        return new Color(r,g,b);
    }
    public void keyTyped(KeyEvent e) {keyPressed(e);}
    public void keyPressed(KeyEvent e) {gotInput = true;}  //pressing makes input go BOOM
    public void keyReleased(KeyEvent e) {gotInput = false;}  //releasing doesn't do that
    public void focusGained(FocusEvent e) {}
    public void focusLost(FocusEvent e) {requestFocus();}
    public void actionPerformed(ActionEvent e){
        if(isScrolling){  //if text is moving
            line2 = input.substring(0,index);  //add a letter
            index++;
            repaint(); //and update the panel
            if(index > input.length()) isScrolling = false;  //if we done with da string, we stop
        }
        else {  //otherwise, just blink the cursor for a while
            blinkDelay++;
            if (blinkDelay >= 80) {
                blinkDelay = 0;
            }
            repaint();
        }

        if(isSpeaking){  //if speech is moving
            line5 = speech.substring(0,index2);  //add a letter
            index2++;
            repaint(); //and update the panel
            if(index2 > speech.length()) isSpeaking = false;  //if we done with da string, we stop
        }

        if(isUpdatingHealth != -1){  //this is for updating health
            Enemy nme = enemyList.get(isUpdatingHealth);
            int currentHealth = nme.getHealth();
            int maxHealth = nme.getMaxHealth();
            if(currentHealth <= tempCurrHealth - amount && amount > 0 || currentHealth <= 0 && amount > 0 ||
                    currentHealth >= tempCurrHealth - amount && amount < 0 || currentHealth >= maxHealth && amount < 0 ||
                    amount == 0){  //if the current health is where it needs to be, or it has reached 0 or max health, or there is no change
                currentHealth = tempCurrHealth - amount;  //update the real current health
                if(currentHealth > maxHealth) currentHealth = maxHealth;  //if overshoot, the set back to max
                if(currentHealth < 0) currentHealth = 0;  //if undershoot, set back to 0

                tempCurrHealth = 0;  //reset the temp
                isUpdatingHealth = -1;  //update the flag for updating health
                amount = 0;  //reset amount
            }
            else{  //otherwise
                int dmg = 1;
                if(Math.abs(amount/100) > 0)dmg = amount/100;
                nme.deductHealth(dmg);  //move the health bar
                repaint();  //and repaint
            }
        }
    }
}
