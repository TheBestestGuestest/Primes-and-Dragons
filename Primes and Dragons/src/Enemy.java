/**
 * Created by willi on 4/7/2016.
 */
import java.awt.*;

class Enemy {
    private String name;
    private int primeNumber;
    private int maxHealth;
    private int health;
    private int damage;
    private Image sprite = null;
    private boolean status;
    public Enemy(int pn, int h, int d, String sp, boolean st){  //status: false = dead, true = alive
        primeNumber = pn;
        health = h;
        maxHealth = health;
        sprite = Toolkit.getDefaultToolkit().getImage("resources/sprites/monsters/"+sp+".png");
        damage = d;
        status = st;
    }
    public Image getSprite(){return sprite;}
    public int getPrime(){return primeNumber;}
    public int getHealth(){return health;}
    public int getMaxHealth(){return maxHealth;}
    public int getDamage(){return damage;}
    public void deductHealth(int damaged){
        health -= damaged;
        if(health < 0 )health = 0;
    }
    public void changeStatus(){status = false;}
    public boolean getStatus(){return status;}
}
