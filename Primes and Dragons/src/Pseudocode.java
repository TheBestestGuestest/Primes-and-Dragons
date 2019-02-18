//----this is the main frame of the panel.  It will hold everything----//
//public class MainFramePseudo extends JFrame{
    //create a BGMusic object so that we can make sounds

    //public MainFramePseudo(){//MainFrame Constructor
        //name the frame "Primes and Dragons!"
        //exit on close
        //make it not resizeable
        //set the IconImage
        //set everything as visible
    //}
    //public static void main(String[] args){  //main method
        //create MainFrame project
        //run the mainFrame using the run() method
    //}
    //public void run(){
        //play the music, do other stuff to get the game started
    //}
//}





//----this class manages the background music.----//
//class BGMusicPseudo{
    //declare the Clips
    //make the BGMusicPseudo constructor

    //public void playBGMusic(String file, int loopStart){
        //we can use this method to play any music file given any music file

        //catch LineUnavailable Exception
        //catch FileNotFoundException
        //catch UnsupportedAudioFileException
        //catch IOException

        //start the music using the start() method
        //loop the correct parts of the song
        //tell the song to loop continuously
    //}
//}





//----this class will monitor the user's mouse movements to determine which numbers they pick----//
//class GlassPanel extends JPanel implements MouseListener, MouseMotionListener{
    //public GlassPanel(){  //glasspanel constructor
        //set it as transparent
        //add the mouselistener
        //add the mouseMotionListener
    //}
    //public void paintComponent(Graphics g){}
    //public void mouseEntered(MouseEvent e){requestFocus();}
    //public void mouseExited(MouseEvent e){}
    //public void mousePressed(MouseEvent e){}
    //public void mouseReleased(MouseEvent e){}
    //public void mouseClicked(MouseEvent e){}
    //public void mouseDragged(MouseEvent e){}
    //public void mouseMoved(MouseEvent e){}
//}





//----this panel maganges all the user input (AKA orb stuff)----//
//class BoardPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener{

    //declare all the jank variables we need

    //declare/instantiate timer for animations

    //public BoardPanel(){//BoardPanel Constructor
        //set the background as dark grey

        //catch the FileNotFoundException for the fonts that we are using.

        //catch the IOException for the fonts that we are using.

        //catch the FontFormatException for the fonts that we are using.

        //add the MouseListener
        //add the MouseMotionLister

    //}
    //public void paintComponent(Graphics g){
        //super.paintComponent(g);
        //set font
        //display the board with the images we created using g.drawImage()
        //draw the number displayed on the bar, which is offset according to how long it is graphically

        //if we detect that the number is falling
        //start the falling animation, set it as dissapearing
        //if the orb is dissppearing
        //set it to -1 so that it won't show up

        //catch the InterruptedException error.

        //display the gridOfOrbs

        //for all the orbs
        //if the orb is falling and has reached the bottom
        //get the above orb and tell it to fall too
        //if the orb is falling and has not reached the bottom
        //go until it reaches the bottom, and tell the oabove ones to follow it
    //}
    //public void resetBoard(){ // this method will create a totally randomized board
        //for all the rows of the board
        //for all the columns
        //assign each block a random number
        //repaint

    //}
    //public BufferedImage getOrb(int i, int j){//gets the right image for the block based on its assigned numbers
        //there are 2 versions of each orb default is light
        //if the orb is clicked/selected, it will turn dark so that we know that it is selected
        //convert the block's digit into a string

        //get the appropriate image based on the block's digit, and catch the FileIOException

        //return that Image
    //}
    //public void mouseClicked(MouseEvent e){}
    //public void mouseDragged(MouseEvent e){  //determines if the current block is selecteed
        //get the current X coordinate of the mouse
        //get the current Y coordinate of the mouse

        //make a Point object that contains the x and the y

        //if the mouse is in the board AND within 40 pixels of its nearest orb AND it has not been already selected
        //if more than 1 orb has been selected
        //get out the orb we just put in
        //get the previous orb
        //verify that the selected orb is a neighbor of the previous orb
        //update the board by repainting
        //else if there are more than 1 orbs being slected
        //if it matches with the previous orb
        //we treat it as an undo
        //update the panel by repainting
    //}
    //public void mouseMoved(MouseEvent e){}
    //public void actionPerformed(ActionEvent e){  //HOOOO BOY TIMERS
        //if the orbs are disappearing
        //"disable" all listeners so they won't interfere with the animation
        //update by repainting
        //if the orbs are too bright
        //move on to the falling animation
        //reset the brightness settings
        //otherwise make the orbs even BRIGHTER
        //else if the orbs are falling
        //if all the orbs are in a square
        //we calculate its new positions
        //and update the board
        //if the offset is greater than 1 whole square we reset it back to 0
        //otherwise,
        //we increment it by 22
        //update it
        //if there are no mare gaps in the board
        //we stop the animation timer and the falling boolean
        //we also "re-enable" the inputs
    //}
    //public void falling(){ //this method just calculates one step of a board that is falling
        //for every column
        //start on the bottom
        //while not on the VERY top
        //if we are currently on an empty square
        //find the next non-empty square
        //if we couldn't find a non-empty square, we generate a random orb not on display
        //otherwise,
        //we move the non-empty square's value down
        //and then we turn the non-empty square into an empty square
        //increment the index up on row
    //}
    //public boolean hasGap(){}  //checks whether or not the board contains an empty square while also adding empty square into the selected array
//}





//----this is the "brains" of the program: manages player and enemy stats, as well as stage stuff----//
//public class PrimesAndDragons {
    //public static void main(String[] args) throws Exception{
        //initialize the stage
        //for all the 50 stages
        //start the stage
        //increase the stage number
    //}
    //public void startStage() throws Exception{  //determines which stage you create
        //print the stage number you are on

        //if the stage number is less than 15
        //create an easy stage
        //if the stage number is between 15 and 30
        //create a medium stage
        //if the stage number is between 30 and 45
        //create a hard stage
        //if the stage number is 46
        //activate the final boss
        //if the stage number is 47
        //ACTIVATE THE TRUE FORM OF THE FINAL BOSS
    //}
    //public void beginAction()throws Exception{//starts the actual action
        //show how much health user has left

        //get the number that the user inputs
        //CALCULATE THE DAMAGE DONE TO THE ENEMIES
        //START THE FALLING OF THE ORBS
    //}
    //public void calculateDamage(BigInteger num)throws Exception{ //damage calculation method
        //for all the enemies in the list
        //if the number from the user divides the monster's number
        //deduct the needed heatlh using the sumOfDigits method
        //if the monster's health is less than 0
        //make it 0
        //if the monster is dead
        //remove the enemy from the enemy arraylist

        //for all the enemies in the list
        //calculate the damage that the enemy does to you
        //deduct the calculated health
    //}
    //public void createEasyStage()throws Exception{   //CREATES AN EASY STAGE.
        //there is a 1/2 chance that there is 1 enemy and 1/2 chance that there are 2 enemies
        //generate random numbers that will be assigned to the enemies
    //}
    //public void createMediumStage()throws Exception{   //CREATES A MEDIUM STAGE.
        //there is a 1/2 chance that there are 2 enemies and 1/2 chance that there are 3 enemies
        //generate random numbers that will be assigned to the enemies
    //}
    //public void createHardStage()throws Exception{   //CREATES A HARD STAGE.
        //there is a 1/2 chance that there are 2 enemies and 1/2 chance that there are 3 enemies
        //generate random numbers that will be assigned to the enemies
    //}
    //public void sumOfDigits(BigInteger number) throws Exception{
        //while teh number is not zero
        //add the modulus to the total sum
        //divide the current number by 10.
        //if there are zeroes inside
        //add health to the user
    //}
    //public void endGame(){  //activates if the user dies
        //print game over message
        //breaks program
    //}
//}