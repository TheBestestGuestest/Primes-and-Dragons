/**
 * Created by willi on 4/16/2016.
 */
public class Player {
    public int maxHealth = 0;
    private int health;
    public Player(int h){
        maxHealth = h;
        health = maxHealth;
    }
    public int getHealth(){return health;}
    public int getMaxHealth(){return maxHealth;}
    public void deductHealth(int dam){
        health -= dam;
        if(health > maxHealth) health = maxHealth;
        if(health < 0) health = 0;
    }
}
