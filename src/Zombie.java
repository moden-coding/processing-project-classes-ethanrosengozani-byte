import processing.core.*;

public class Zombie {
    public int x, y;
    private int speed = 1; // CHANGED: speed is now 1 (slower)
    private int Zombiehealth = 40;

    public Zombie(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getter and setter for Zombiehealth
    public int getZombiehealth() {
        return Zombiehealth;
    }

    public void setZombiehealth(int health) {
        this.Zombiehealth = health;
    }

    // Add update() method to move the zombie leftwards
    public void update(Person person) {
        moveTowards(person);
    }

    public void moveTowards(Person p) {
        // CHANGED: Removed the "chasing" logic. 
        // Now it just moves straight left.
        x -= speed; 
    }
    
    // Gemini taught me how to use sprites
    public void display(App app) {
        if (app.ZombieImg != null) {
            app.image(app.ZombieImg, x, y);
        } else {
            app.fill(0, 0, 255);
            app.ellipse(x, y, 50, 50);
        }
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public int getspeed() {
       return speed;
    }
}