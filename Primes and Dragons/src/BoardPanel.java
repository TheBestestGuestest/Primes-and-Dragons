/**
 * Created by RyanNiu on 4/8/2016.
 */
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;

class BoardPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener{
    private JFrame parent = null;  //this is so we can toggleS the glass pane
    private SFX sfx = new SFX();
    private Font f = null;  //this is our font
    private final int X_ORBS = 6;  //number of columns
    private final int Y_ORBS = 5;  //number of rows
    private final double MASTER_SCALE = 1.0;  //scale for the whole thing
    private final double SCALE = 1.05*1.2;  //scale for the orbs
    private final int ORB_X_OFFSET = 46;  //where the orbs locations start (X-coords)
    private final int ORB_Y_OFFSET = 76;  //where the orbs locations start (Y-coords)
    private final int ORB_SCALE = 64;  //basically means the orbs x2
    private final int NUM_Y_OFFSET = (int) (26 * MASTER_SCALE);  //where the string will be placed (X-coords)
    private final int NUM_X_OFFSET = (int) (600 * MASTER_SCALE);  //where the string will be placed (Y-coords)
    private final int SIZE = (int) (ORB_SCALE * SCALE * MASTER_SCALE);  //the size of each orb

    private String number = "";  //the string of the number the user has made
    private volatile boolean gotNumber = false;
    private Stack<Point> selected = new Stack<Point>();  //the orbs the player has selected
    private int[][] boardNums = new int[X_ORBS][Y_ORBS+1];  //the current state of the board

    public volatile int numBarState = 1;  //1 is grey; 2 is green; 0 is red

    private javax.swing.Timer animation = new javax.swing.Timer(1,this);  //ONE TIMER ONLY for animation
    private volatile boolean isDisappearing = false;  //if true, then the disappearing animation will play
    private float scaleFactor = 1.3f;  //this toggles the brightness of the board (only applicable to the disappearing animation)

    private volatile boolean isFalling = false;  //if true, then the falling animation will play
    private int orbOffset = -1;  //this toggles the offset of an orb (only applicable to the falling animation)

    public volatile boolean isUpdatingFloor = false;
    private final int FLOOR_OFFSET_X = (int) (581 * MASTER_SCALE);
    private final int FLOOR_OFFSET_Y = (int) (509 * MASTER_SCALE);
    private final int FLOOR_OFFSET = (int) (70 * MASTER_SCALE);
    private final int FLOOR_HEIGHT = (int) (23 * MASTER_SCALE);
    private final int FLOOR_WIDTH = (int) (25 * MASTER_SCALE);
    private final int FLOOR_TOTAL_HEIGHT = (int) (415 * MASTER_SCALE);
    private int currentFloor = 0;
    private String currentFloorFile = "floors_1-15";
    private int floorOffset = FLOOR_OFFSET;
    private boolean reachedBottom = false;

    private volatile boolean isUpdatingHealth = false;
    private int maxHealth = 400;
    private int currentHealth = 0;
    private int tempCurrHealth = currentHealth;
    private int amount = 0;
    private final int HEALTH_Y_OFFSET = (int) (61 * MASTER_SCALE);
    private final int HEALTH_X_OFFSET = (int) (552 * MASTER_SCALE);

    private boolean enabled = true;

    public BoardPanel(){
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension((int)(612 * MASTER_SCALE),(int)(547 * MASTER_SCALE)));
        animation.setInitialDelay(0);
        try {  //get the font
            f = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("resources/fonts/ADDLG___.ttf"))).deriveFont((float)(17*MASTER_SCALE));
        }catch(IOException | FontFormatException e){
            e.printStackTrace();
            System.out.println("WHY IS THE FONT SO HARD TO FIND?!");
            System.exit(-5);
        }
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    public void init(int stage){  //this gets our beginning animation
        currentFloor = stage;
        floorOffset = currentFloor*FLOOR_HEIGHT + FLOOR_OFFSET;
        if(currentFloor > 0) maxHealth += 100;
        if(currentFloor > 15){
            currentFloorFile = "floors_16-30";
            floorOffset = (currentFloor-15)*FLOOR_HEIGHT + FLOOR_OFFSET;
            maxHealth += 250;
        }
        if(currentFloor > 30){
            currentFloorFile = "floors_31-45";
            floorOffset = (currentFloor-30)*FLOOR_HEIGHT + FLOOR_OFFSET;
            maxHealth += 250;
        }
        if(currentFloor > 45){
            currentFloorFile = "floors_46-50";
            floorOffset = (currentFloor-45)*FLOOR_HEIGHT*3 + FLOOR_OFFSET;
            maxHealth += 500;
        }
        if(currentFloor == 50) maxHealth += 500;

        animation.start();
        resetBoard(1);
        updateHealth(-maxHealth);
    }
    public void toggleEnabled(boolean state){  //this toggles the state of the enabled
        enabled = state;
        repaint();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setFont(f);  //sets font

        //gets and displays the board
        Image board = Toolkit.getDefaultToolkit().getImage("resources/board/board.png");
        g.drawImage(board,0,0,(int)(board.getWidth(this) * MASTER_SCALE),(int)(board.getHeight(this)*MASTER_SCALE),this);

        //this determines what color the num bar will be
        Color numBarColor = null;
        switch(numBarState){
            case 0:
                numBarColor = new Color(237,28,36);  //red (is not divisible)
                break;
            case 2:
                numBarColor = new Color(36,177,76);  //green (is divisible)
                break;
            default:
                numBarColor = new Color(195,195,195);  //grey (neutral)
                break;
        }
        g.setColor(numBarColor);
        g.fillRect((int)(6*MASTER_SCALE),(int)(6*MASTER_SCALE),(int)(600*MASTER_SCALE),(int)(25*MASTER_SCALE));

        //this draws the number displayed on the bar, which is offset according to how long it is graphically
        g.setColor(Color.BLACK);
        if(!isDisappearing && !isFalling && enabled) number = getNum();
        int numberLength = g.getFontMetrics().stringWidth(number);
        if(!number.equals("-1") && !number.equals("reset")) g.drawString(number, NUM_X_OFFSET - numberLength, NUM_Y_OFFSET);  //don't print if nothing is shown or resetting

        //this is to display the health bar
        g.setColor(getColor(181,203,39));
        g.fillRect((int)(57*MASTER_SCALE),(int)(44*MASTER_SCALE),(int)(498*MASTER_SCALE*currentHealth/maxHealth),(int)(7*MASTER_SCALE));  //top part of health bar
        g.setColor(getColor(34,177,76));
        g.fillRect((int)(57*MASTER_SCALE),(int)((44+7)*MASTER_SCALE),(int)(498*MASTER_SCALE*currentHealth/maxHealth),(int)(12*MASTER_SCALE));  //bottom part of health bar
        g.setColor(new Color(0,0,0));
        if(amount > 0) g.setColor(new Color(150, 0, 0));  //if decreasing health, make it red
        if(amount < 0) g.setColor(new Color(0, 100, 0));  //if increasing health, make it green
        String health = ""+currentHealth+"/"+maxHealth;
        int healthLength = g.getFontMetrics().stringWidth(health);
        if(currentHealth >= 0) g.drawString(health, HEALTH_X_OFFSET - healthLength, HEALTH_Y_OFFSET);  //display health in numbers

        //this is to display the floor bar  // FIXME: 4/24/2016 (flicker issue) AND NOW IT DOESNT SCALE WELL
        Image floor = Toolkit.getDefaultToolkit().getImage("resources/board/floor_bar/"+currentFloorFile+".png");
        g.drawImage(floor,FLOOR_OFFSET_X,FLOOR_OFFSET_Y-floorOffset,FLOOR_OFFSET_X+FLOOR_WIDTH,FLOOR_OFFSET_Y,
                0,FLOOR_TOTAL_HEIGHT-floorOffset,FLOOR_WIDTH,FLOOR_TOTAL_HEIGHT,this);

        //this is all for the orb thing
        //this is for the initial delay to simulate the orbs as "disappearing"
        if(isDisappearing && scaleFactor+scaleFactor > 1000f){
            //turns all the selected orbs into -1's, so they appear blank on display
            Stack<Point> temp = (Stack)selected.clone();
            while(!temp.isEmpty()){
                Point p = temp.pop();
                boardNums[p.x][p.y] = -1;
            }
            try{
                Thread.sleep(80);  //initial delay
            }catch(InterruptedException err){
                err.printStackTrace();
                System.out.println("Sleep is for the weak!");
                System.exit(-1);
            }
        }
        //this displays the grid of orbs
        for (int i = 0; i < X_ORBS; i++) {
            for (int j = 1; j < Y_ORBS+1; j++) {
                BufferedImage orb;
                if(isFalling && boardNums[i][j] == -1) {  //if the current animation is falling and the current orb is empty
                    orb = getOrb(i,j-1);  //we get the orb above it
                    g.drawImage(orb, calcCoords(true,i,false), calcCoords(false,j-2,true), SIZE, SIZE, this);  //and display it "falling down"
                }
                else if(isFalling && j != Y_ORBS){  //else if the current animation is falling and the current orb has NOT reached the bottom
                    if(boardNums[i][j+1] != -1){  //if the orb below it is NOT empty
                        orb = getOrb(i,j);  //we get the orb
                        g.drawImage(orb, calcCoords(true,i,false), calcCoords(false,j-1,false), SIZE, SIZE, this);  //we display it
                    }
                }
                else {  //otherwise
                    orb = getOrb(i,j);  //we get the orb
                    if (selected.contains(new Point(i, j)) && isDisappearing && orb != null) {  //if the current animation is disappearing and the orb has been selected and it DOES exist
                        RescaleOp op = new RescaleOp(scaleFactor, scaleFactor, null);  //update the filter
                        op.filter(orb, orb);  //pass the orb through the filter
                    }
                    if (boardNums[i][j] != -1) g.drawImage(orb, calcCoords(true,i,false), calcCoords(false,j-1,false), SIZE, SIZE, this);  //if the orb has not been deleted, we display it
                }
            }
        }
        //this displays the lines that connect the selected orbs
        if(!isDisappearing && !isFalling){  //make sure no orbs are disappearing or falling
            Stack<Point> temp = (Stack)selected.clone();
            boolean first = true;
            int[] xOffset = {-1, -1, 0, 1, 1, 1, 0, -1};  //to check which neighbors are connected to it
            int[] yOffset = {0, 1, 1, 1, 0, -1, -1, -1};
            if(temp.size() == 1){  //if only one orb is selected, we display the single orange node
                Point p = temp.pop();
                BufferedImage connector = null;
                try {
                    connector = ImageIO.read(new File("resources/sprites/connector/connector00.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Not even 00 is safe.");
                    System.exit(-3);
                }
                g.drawImage(connector,calcCoords(true,p.x,false),calcCoords(false,p.y-1,false),SIZE+2,SIZE+2,this);
            }
            while(temp.size() > 1){  //while there are at least 2 orbs selected
                Point p = temp.pop();  //get out the orb
                int type_to = 0;  //we need lines going to
                int type_from = 0;  //and coming from the orb
                BufferedImage connector_to = null;  //going-to line
                BufferedImage connector_from = null;  //coming-from line
                for (int i = 0; i < xOffset.length; i++) {  //calculate what type of line by its neighbor
                    if (p.x + xOffset[i] == temp.peek().x && p.y + yOffset[i] == temp.peek().y){
                        type_to = (i + 1) * 2 + 1;
                        type_from = 2 + (type_to+6)%16;
                    }
                }
                if (first){  //if this is the first one (the orb the player is currently one), we make it orange
                    type_from--;
                    first = false;
                }
                if(temp.size() == 1) type_to--;//likewise, if this is the player's starting orb, make it orange
                String str_to = Integer.toString(type_to);  //convert the type into a string
                String str_from = Integer.toString(type_from);  //convert the type into a string
                if (type_to < 10) str_to = "0" + str_to;  //the file names are like "08,09,10" so yeah
                if (type_from < 10) str_from = "0" + str_from;  //the file names are like "08,09,10" so yeah
                //get and display the image
                try {
                    connector_to = ImageIO.read(new File("resources/sprites/connector/connector" + str_to + ".png"));
                    connector_from = ImageIO.read(new File("resources/sprites/connector/connector" + str_from + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("So we failed the connect-the-dots test. To:"+str_to+" From: "+str_from);
                    System.exit(-3);
                }
                g.drawImage(connector_to,calcCoords(true,temp.peek().x,false),calcCoords(false,temp.peek().y-1,false),SIZE,SIZE,this);  //draw the
                g.drawImage(connector_from,calcCoords(true,p.x,false),calcCoords(false,p.y-1,false),SIZE,SIZE,this);  //connectors
            }
        }
    }
    //this randomizes the entire board
    public void resetBoard(int animationType){
        switch (animationType) {
            case 0:  //this is for when the player manually resets the board (has the disappearing animation)
                for (int i = 0; i < X_ORBS; i++) {
                    for (int j = 1; j < Y_ORBS+1; j++) {
                        selected.add(new Point(i,j));
                    }
                }
                isDisappearing = true;
                number = "reset";
                break;
            case 1:  //this is for every new game (only falling animation)
                for (int i = 0; i < X_ORBS; i++) {
                    for (int j = 0; j < Y_ORBS+1; j++) {
                        boardNums[i][j] = -1;
                    }
                }
                parent = (JFrame) this.getTopLevelAncestor();
                isFalling = true;
                break;
        }
    }
    //this returns the number to the parent class
    public String getNumber(){
        while(!gotNumber){}  //wait to get number
        gotNumber = false;  //refresh the boolean
        if(number.equals("")) number = "-1";  //if nothing was selected, return -1 to the parent
        return number;
    }
    //this is used by the parent class to change the number bar's color and repaint
    public void setNumBarState(int state){
        numBarState = state;
        repaint();
    }
    //this updates the floor
    public void updateFloor(){
        currentFloor++;
        if(currentFloor != 1) {  //if the floor is not one
            if (currentFloor % 15 == 1) floorOffset--;  //if it is at the top, move the offset down 1
            else floorOffset++;  //otherwise, move the offset up by 1
        }
        isUpdatingFloor = true;  //signal the update
        while(isUpdatingFloor){}  //wait for animation to end
    }
    //this updates the health bar
    public void updateHealth(int dmg){
        amount = dmg;  //set the amount to dmg (-dmg is heal)
        isUpdatingHealth = true;  //signal the update
        while(isUpdatingHealth){}  //wait for the animation to end
    }
    public void updateMaxHealth(int add){
        maxHealth += add;
        repaint();
        try{
            Thread.sleep(500);
        }catch(InterruptedException err){}
        updateHealth(-add);
    }
    //this gets the image of the orb at the given index
    public BufferedImage getOrb(int i, int j){
        int num = boardNums[i][j] * 2 + 1;  //there are 2 versions of each orb default is light
        if (selected.contains(new Point(i, j))) num--;  //if selected, it will go dark
        String str = Integer.toString(num);  //convert the digit into a string
        if (num < 0) return null;  //this means that the number is -1, which means it doesn't exist, so we can return null
        else if (num < 10) str = "0" + str;  //the file names are like "08,09,10" so yeah
        //get and return the image
        BufferedImage orb = null;
        try {
            orb = ImageIO.read(new File("resources/sprites/orbs/orb" + str + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Blorbs: "+str);
            System.exit(-2);
        }
        return orb;
    }
    //this calculates the coordinates of the given orb (with x/y offset)
    public int calcCoords(boolean isX, int index, boolean hasOffset){
        if(hasOffset)  return (int)(((ORB_Y_OFFSET + index*(ORB_SCALE+2))*SCALE+orbOffset)*MASTER_SCALE);
        else if(!isX) return (int)((ORB_Y_OFFSET + index*(ORB_SCALE+2))*SCALE*MASTER_SCALE);
        return (int)((ORB_X_OFFSET + index*(ORB_SCALE+2))*SCALE*MASTER_SCALE);
    }
    //this calculates the color of the health bar (the closer to 0, the more red the bar is, where green at max health)
    public Color getColor(int r, int g, int b){
        r += (int)((1-(double)currentHealth/maxHealth)*(255-r));
        if(r > 255) r = 255;
        g -= (int)((1-(double)currentHealth/maxHealth)*g*0.70);
        return new Color(r,g,b);
    }
    //this gets the number the user has currently created with their selection
    public String getNum(){
        Stack<Point> temp = (Stack)selected.clone();
        String num = "";
        while(!temp.isEmpty()){
            Point p = temp.pop();
            int digit = boardNums[p.x][p.y];
            if(digit < 10) num = digit+num;
        }
        return num;
    }
    //these are the GUI-related methods
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseMoved(MouseEvent e){}
    public void mousePressed(MouseEvent e){
        mouseDragged(e);
    }
    public void mouseReleased(MouseEvent e){  //this means the player has submitted their selection
        if(enabled) {
            //this is for clearing orbs
            if (selected.size() > 1) isDisappearing = true;
            else selected = new Stack<Point>();
        }
        else selected = new Stack<Point>();
        repaint();
    }
    public void mouseDragged(MouseEvent e){  //the user drags to make a selection
        //converts the location into coordinates relative to the board
        int mouseX = (int) (((int) (e.getX() / SCALE / MASTER_SCALE) - ORB_X_OFFSET) / (double) (ORB_SCALE + 2) * 100) ;
        int mouseY = (int) (((int) (e.getY() / SCALE / MASTER_SCALE) - ORB_Y_OFFSET) / (double) (ORB_SCALE + 2) * 100);
        Point coords = new Point(mouseX / 100, mouseY / 100 + 1);  //to get the index
        if (Math.abs(mouseX % 100 - 50) < 40 && Math.abs(mouseY % 100 - 50) < 40 && Math.abs(mouseX - 300) <= 300 && Math.abs(mouseY - 250) <= 250
                && !selected.contains(coords)) {  //if the mouse is in the board AND within 40 pixels of its nearest orb AND it has not been already selected
            selected.push(coords);  //we add our coordinates into the stack
            if (selected.size() > 1) {  //if more than 1 orb has been selected
                selected.pop();  //get out the orb we just put in
                Point first = selected.peek();  //get the previous orb
                //verify that the selected orb is a neighbor of the previous orb
                int[] xOffset = {1, 1, 1, 0, 0, -1, -1, -1};
                int[] yOffset = {1, 0, -1, 1, -1, 1, 0, -1};
                boolean found = false;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (first.equals(new Point(coords.x + xOffset[i], coords.y + yOffset[j]))) {  //if the current orb is one of the previous's orb's 8 neighbors
                            selected.push(coords);  //put the orb in the stack
                            found = true;  //no loop
                            break;  //no loop
                        }
                    }
                    if (found){
                        if(enabled) sfx.playFX("resources/SFX/ding/d"+selected.size()+".wav");
                        break;  //no loop
                    }
                }
            }
            repaint();  //update the board
        }
        else if (selected.size() > 1) {  //else if there are more than 1 orbs currently selected
            if (selected.get(selected.size() - 2).equals(coords)) {  //if it matches with the previous orb
                selected.pop();  //we treat it as an undo
                repaint();  //and update it
            }
        }
    }
    public void actionPerformed(ActionEvent e){  //HOOOO BOY TIMERS
        if(isDisappearing){  //if the orbs are disappearing
            parent.getGlassPane().setVisible(true);  //"disable" all listeners so they won't interfere with the animation
            repaint();  //update
            if(scaleFactor > 1000f){  //if the orbs are too bright
                //move on to the falling animation
                isDisappearing = false;
                scaleFactor = 1.3f;  //reset the brightness settings
                isFalling = true;
            }
            else scaleFactor += scaleFactor;  //otherwise make the orbs even BRIGHTER
        }
        else if(isFalling){  //else if the orbs are falling
            if(orbOffset == 0) {  //if all the orbs are in a square
                falling();  //we calculate its new positions
                repaint();  //and update the board
            }
            if(orbOffset > 66) orbOffset = 0;  //if the offset is greater than 1 whole square we reset it back to 0
            else {  //otherwise
                orbOffset += 22;  //we increment it by 22
                repaint();  //and update it
            }
            if(!hasGap()) {  //if there are no mare gaps in the board
                isFalling = false;  //we stop falling boolean
                parent.getGlassPane().setVisible(false);  //we also "re-enable" the inputs
                if(number.equals("reset") || !number.equals("-1")) gotNumber = true;
            }
        }
        else if(isUpdatingHealth){  //this is for updating health
            parent.getGlassPane().setVisible(true);  //pesky user inputs interrupt the animation
            if(currentHealth - amount/20 <= tempCurrHealth - amount && amount > 0 || currentHealth - amount/20 <= 0 && amount > 0 ||
                    currentHealth - amount/20 >= tempCurrHealth - amount && amount < 0 || currentHealth - amount/20 >= maxHealth && amount < 0 ||
                    amount == 0){  //if the current health is where it needs to be, or it has reached 0 or max health, or there is no change
                currentHealth = tempCurrHealth - amount;  //update the real current health
                if(currentHealth > maxHealth) currentHealth = maxHealth;  //if overshoot, the set back to max
                if(currentHealth < 0) currentHealth = 0;  //if undershoot, set back to 0

                tempCurrHealth = currentHealth;  //reset the temp
                isUpdatingHealth = false;  //update the flag for updating health
                amount = 0;  //reset amount
                parent.getGlassPane().setVisible(false);  //yay USSR input
            }
            else{  //otherwise
                int dmg = amount/20;  //make the health bar always move at a constant rate
                if(dmg == 0){  //if less than 20 dmg
                    if(amount > 0) dmg = 1;  //if dmg, 1 at a time
                    if(amount < 0) dmg = -1;  //if heal, -1 at a time
                }
                currentHealth -= dmg;  //move the health bar
                repaint();  //and repaint
            }
        }
        else if(isUpdatingFloor){  //if we are updating the floor
            parent.getGlassPane().setVisible(true);  //pesky pesky USSR inputs
            boolean isFinalFive = currentFloor > 45;  //this is for jank secret lvls
            boolean isTop = currentFloor%15 == 1 && currentFloor != 1;  //this is when the thing has reached the top
            int floorHeight = floorOffset-FLOOR_OFFSET;  //the height of the floor

            if(floorHeight == (currentFloor%15)*(FLOOR_HEIGHT*3) && isFinalFive && !isTop || floorHeight == (currentFloor%15)*FLOOR_HEIGHT && !isFinalFive && !isTop ||
                    floorHeight == FLOOR_HEIGHT*3 && isFinalFive && isTop && reachedBottom || floorHeight == FLOOR_HEIGHT && !isFinalFive && isTop && reachedBottom ||
                    floorHeight == FLOOR_TOTAL_HEIGHT-FLOOR_OFFSET && !isTop){  //too much case work pl0x ;-;  and also i hafta make it faster rip
                isUpdatingFloor = false;  //we finished
                parent.getGlassPane().setVisible(false);  //hooray russia
                reachedBottom = false;  //update flag
            }
            else{
                if(isTop && !reachedBottom){  //so this is when the guage going down and will go back up again
                    floorOffset -= 15;  //go down
                    if(floorOffset <= FLOOR_OFFSET){  //if we reached the bottom
                        floorOffset = FLOOR_OFFSET;
                        reachedBottom = true;  // ayy lmao
                        if(isFinalFive) currentFloorFile = "floors_"+currentFloor+"-"+(currentFloor+4);  //update the image
                        else currentFloorFile = "floors_"+currentFloor+"-"+(currentFloor+14);  //accordingly
                    }
                }
                else floorOffset++;  //otherwise, we just move it up
            }
            repaint();
        }
    }
    //this method just calculates one step of a board that is falling
    public void falling(){
        for(int i = 0; i < X_ORBS; i++){  //for every column
            int j = Y_ORBS;  //start on the bottom
            while(j >= 0){  //while not on the VERY top
                if(boardNums[i][j] == -1){  //if we are currently on an empty square
                    //find the next non-empty square
                    do{
                        j--;
                        if(j < 0) break;
                    }while(boardNums[i][j] == -1);
                    if(j < 0) boardNums[i][0] = (int)(Math.random()*10);  //if we couldn't find a non-empty square, we generate a random orb not on display
                    else{  //otherwise
                        boardNums[i][j+1] = boardNums[i][j];  //we move the non-empty square's value down
                        boardNums[i][j] = -1;  //and then we turn the non-empty square into an empty square
                    }
                }
                j--;  //increment the index up on row
            }
        }
    }
    //checks whether or not the board contains an empty square while also adding empty square into the selected array
    public boolean hasGap(){
        selected = new Stack<Point>();
        for(int i = 0; i < boardNums.length; i++){
            for(int j = 0; j < boardNums[i].length; j++){
                if(boardNums[i][j] == -1){
                    selected.add(new Point(i,j));
                }
            }
        }
        if(!selected.isEmpty()) return true;
        return false;
    }
}
