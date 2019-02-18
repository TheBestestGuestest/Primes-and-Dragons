/**
 * Created by RyanNiu on 4/28/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class StartPanel extends JPanel implements KeyListener, FocusListener, ActionListener{
    private BGMusic bgm = new BGMusic();
    private final double MASTER_SCALE = 1.0;  //scale for the whole thing
    private final int WIDTH = (int)(612 * MASTER_SCALE);
    private final int HEIGHT = (int)(925 * MASTER_SCALE);
    private final int ORB_SCALE = (int)(64*1.05*1.2* MASTER_SCALE);
    private javax.swing.Timer animation = new javax.swing.Timer(1,this);

    private final int[] konamiCode = {KeyEvent.VK_UP,KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT
            ,KeyEvent.VK_B,KeyEvent.VK_A,KeyEvent.VK_ENTER};
    private volatile boolean canKonami = false;
    private ArrayList<Integer> keys = new ArrayList<Integer>();

    private volatile boolean isIntro = true;
    private int primeY = -100;
    private int andX = -100;
    private int dragonsY = HEIGHT;
    private double gravity = 1;

    private int flash = 0;
    private boolean isFlash = false;

    private final int BORDER_MAX = 200;
    private int borderOffset = 0;
    private boolean isShrink = false;
    private int wait = 0;

    private volatile boolean gotInput = false;
    private ArrayList<RandOrb> orbs = new ArrayList<RandOrb>();

    public StartPanel(){
        setBackground(new Color(200,191,231));
        requestFocus();
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        addKeyListener(this);
        addFocusListener(this);
        animation.addActionListener(this);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(!isIntro) {
            //this is for the border
            if(isShrink){
                borderOffset -= 5;
                wait = 0;
            }
            else if(wait >= 50) borderOffset += 5;
            else wait++;

            if(borderOffset <= 0) isShrink = false;
            else if(borderOffset > BORDER_MAX) isShrink = true;

            //this is for the orbs
            for (int i = 0; i < orbs.size(); i++) {
                RandOrb temp = orbs.get(i);
                //System.out.println(temp.y);
                g.drawImage(temp.orb, temp.x, temp.y, ORB_SCALE, ORB_SCALE, this);
                temp.down();
                if (temp.y > HEIGHT || temp.x < -ORB_SCALE || temp.x > WIDTH) {
                    orbs.remove(i);
                    i--;
                }
            }
        }
        //this prints the title
        Image prime = Toolkit.getDefaultToolkit().getImage("resources/board/start_menu/prime.png");
        Image and = Toolkit.getDefaultToolkit().getImage("resources/board/start_menu/and.png");
        Image dragons = Toolkit.getDefaultToolkit().getImage("resources/board/start_menu/dragons.png");
        g.drawImage(prime,45,primeY,(int)(prime.getWidth(this)*MASTER_SCALE),(int)(prime.getHeight(this)*MASTER_SCALE),this);
        g.drawImage(dragons,45,dragonsY,(int)(dragons.getWidth(this)*MASTER_SCALE),(int)(dragons.getHeight(this)*MASTER_SCALE),this);
        g.drawImage(and,andX,200,(int)(and.getWidth(this)*MASTER_SCALE),(int)((and.getHeight(this)+1)*MASTER_SCALE),this);

        //this is for "press start to play"
        Image petp = Toolkit.getDefaultToolkit().getImage("resources/board/start_menu/petp.png");
        if(!isIntro) {
            if(isFlash) g.drawImage(petp, WIDTH / 4, HEIGHT / 2, (int) (petp.getWidth(this) * MASTER_SCALE), (int) (petp.getHeight(this) * MASTER_SCALE), this);
            if (flash >= 100){
                isFlash = !isFlash;
                flash = 0;
            }
            flash++;
        }

        //this is for the border
        int bo = borderOffset/10;
        Image border = Toolkit.getDefaultToolkit().getImage("resources/board/start_menu/border.png");
        g.drawImage(border,-bo,-bo,WIDTH+2*bo,HEIGHT+2*bo,this);
    }
    public void playIntro(){
        requestFocus();
        animation.start();
        while(isIntro){}
        bgm.playBGMusic("resources/music/merga mern.wav",0);
    }
    public boolean getInput(){
        while(!gotInput){requestFocus();}
        bgm.stop();
        return canKonami;
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        keys.add(e.getKeyCode());
        if(keys.size() > 11) keys.remove(0);
        checkKonamiCode();

        if(e.getKeyCode() == KeyEvent.VK_ENTER) gotInput = true;
    }
    public void checkKonamiCode(){
        if(keys.size() == 11){
            for(int i = 0; i < 11; i++){
                if(keys.get(i) != konamiCode[i]) return;
            }
            canKonami = true;
        }
    }
    public void keyReleased(KeyEvent e) {}
    public void focusGained(FocusEvent e) {}
    public void focusLost(FocusEvent e) {}
    public void actionPerformed(ActionEvent e){
        if(isIntro){
            if(dragonsY-gravity <= 250* MASTER_SCALE){
                dragonsY = 250;
                isIntro = false;
            }
            else if(andX+gravity >= 65* MASTER_SCALE){
                andX = 65;
                if(dragonsY == HEIGHT) gravity = 1* MASTER_SCALE;
                dragonsY -= gravity;
            }
            else if(primeY+gravity >= 100* MASTER_SCALE){
                primeY = 100;
                if(andX == 200) gravity = 1* MASTER_SCALE;
                andX += gravity;
            }
            else primeY += gravity;
            gravity *= 1.02 * MASTER_SCALE;
        }
        else if(orbs.size() < 5){
            String num = Integer.toString((int)(Math.random()*10)*2+1);
            if(Integer.parseInt(num) < 10) num = "0"+num;
            Image orb = Toolkit.getDefaultToolkit().getImage("resources/sprites/orbs/orb"+num+".png");
            int x = (int)(Math.random()*(WIDTH-ORB_SCALE)* MASTER_SCALE);
            int y = (int)(Math.random()*(HEIGHT-ORB_SCALE)* MASTER_SCALE);
            int xv = (int)(Math.random()*3)-1;
            boolean doesntOverlap = true;
            for(int i = 0; i < orbs.size(); i++){
                RandOrb temp = orbs.get(i);
                if(x > temp.x-ORB_SCALE && x < temp.x+ORB_SCALE && y > temp.y-ORB_SCALE && y < temp.y+ORB_SCALE) doesntOverlap = false;
            }
            if(doesntOverlap && xv != 0) orbs.add(new RandOrb(orb,x,y,xv));
        }
        repaint();
    }
}

class RandOrb{
    private final double MASTER_SCALE = 1.0;
    private final int UNTIL_BOUNCE = (int)(333 * MASTER_SCALE);
    private int originallY;
    private double gravity = 1* MASTER_SCALE;
    private boolean hasBounced = false;
    private boolean hasHeighted = false;
    public Image orb;
    public int x, y, xVol;
    public int yVol = (int)(2 * MASTER_SCALE);
    public RandOrb(Image numm, int xx, int yy, int xv){
        x = xx;
        y = yy;
        xVol = (int)(xv * MASTER_SCALE);
        originallY = yy;
        orb = numm;
    }
    public void down(){
        if(hasHeighted){
            y += (int)((double)(yVol*gravity));
            gravity *= 1.01* MASTER_SCALE;
        }
        else if(hasBounced && (int)((double)(yVol*gravity)) == 0){
            hasHeighted = true;
        }
        else if(hasBounced){
            y -= (int)((double)(yVol*gravity));
            gravity /= 1.1* MASTER_SCALE;
        }
        else if(y > originallY+UNTIL_BOUNCE) hasBounced = true;
        else{
            y += (int)((double)(yVol*gravity));
            gravity *= 1.01* MASTER_SCALE;
        }
        x += xVol;
    }
}