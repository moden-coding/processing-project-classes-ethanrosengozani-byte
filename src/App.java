import processing.core.*;
import java.util.ArrayList;
import java.util.Scanner;

public class App extends PApplet {

    Person person;
    ArrayList<Flower> flowers = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Zombie> zombies = new ArrayList<>();

    // Images
    PImage personImg;
    PImage flowerImg;
    PImage bulletImg;
    PImage ZombieImg;
    PImage juiceImg; 

    int cellSize = 125;

    // Movement Switches
    boolean isLeft, isRight, isUp, isDown;
    boolean Fdown;
    boolean Ydown;
    boolean Ndown;

    // Game State
    int gameState = 0; // 0 = Start, 1 = Playing, 2 = Game Over, 3 = Quit Confirm this makes it easier
                       // for me to give the player the ability to quit and for the game to know when
                       // to do things based on where the code says it is at
    String typingName = "";

    // money
    int money = 100;

    //Stats
    int amountoftimesjuice = 0;
    int amountoftimestakendamage = 0;


    public static void main(String[] args) {
        PApplet.main("App");
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        try {
            // load images chatgpt taught me
            personImg = loadImage("person.png");
            flowerImg = loadImage("flower.png");
            bulletImg = loadImage("bullet.png");
            ZombieImg = loadImage("zombie.png");
            juiceImg = loadImage("juice.png");
            if (personImg != null)
                personImg.resize(80, 80);
            if (flowerImg != null)
                flowerImg.resize(80, 80);
            if (bulletImg != null)
                bulletImg.resize(30, 30);
            if (ZombieImg != null)
                ZombieImg.resize(80, 80);
            if (juiceImg != null)
                juiceImg.resize(50, 50);

        } catch (Exception e) {
            System.out.println("Error loading images");
            System.out.println("check your sprites please ");
        }
        imageMode(CENTER);
        person = new Person(400, 500);
    }

    public void draw() {
        if (gameState == 0) {
            drawStartScreen();// we have draw screen function
        } else if (gameState == 1) {// Playing Screen
            drawGame();// then we have draw game function
        } else if (gameState == 2) {// Game Over Screen
            drawGameOver();// then we have draw game over function
        } else if (gameState == 3) {// Quit Screen
            drawQuitScreen();// then we have draw quit screen function
        }
    }

    public void drawStartScreen() {
        background(255);
        textAlign(CENTER, CENTER);
        textSize(40);
        fill(0);
        text("WELCOME TO GARDEN DEFENSE", width / 2, 150);
        textSize(20);
        text("Type name and press ENTER:", width / 2, 250);

        fill(0, 255, 0);
        textSize(50);
        text(typingName + "_", width / 2, 320);
    }

    public void drawGameOver() {
        background(0);
        fill(255, 0, 0);
        textSize(60);
        textAlign(CENTER, CENTER);
        text("GAME OVER", width / 2, height / 2);
        textSize(30);
        text(person.name + " died!", width / 2, height / 2 + 60);
        textSize(30);
        text(person.name + "Took damage " + amountoftimestakendamage + " times", width / 2, height / 2 + 1);
        text(person.name + "Drank juice " + amountoftimesjuice + " times", width / 2, height / 2 + 1);
    }

    public void drawQuitScreen() {
        background(255, 0, 0);
        fill(255);
        textAlign(CENTER, CENTER);
        textSize(30);
        text("Exiting Game: Thank you for playing!", width / 2, height / 2);
        text("Are you sure you want to quit? (Y/N)", width / 2, height / 2 + 40);
    }

    public void drawGame() {
        // 1. Draw Background
        background(0, 255, 0);

        // 2. Draw Grid
        stroke(0);
        strokeWeight(1);
        for (int x = 0; x < width; x = x + cellSize) {
            for (int y = 0; y < height; y = y + cellSize) {
                fill(140, 70, 20);
                rect(x, y, cellSize, cellSize);
            }
        }

        if (isLeft)
            person.moveLeft();
        if (isRight)
            person.moveRight();
        if (isUp)
            person.moveUp();
        if (isDown)
            person.moveDown();
        
        // UPDATE COOLDOWN (Reduces invincibility timer AND juice timer)
        person.updateCooldown(); 

        // 4. Flowers
        for (Flower flower : flowers) {
            flower.update(this);
            flower.display(this);
        }
        // Zombies!!
        // ZOMBIE SPAWNING
        if (frameCount % 120 == 0) {// every 2 seconds

            int row = (int) random(0, height / cellSize) * cellSize + cellSize / 2;
            zombies.add(new Zombie(width, row));
        }

        // Remove zombies with health <= 0
        for (int i = zombies.size() - 1; i >= 0; i--) {
            if (zombies.get(i).Zombiehealth <= 0) {
                zombies.remove(i);
            }
        }

        // DRAW & MOVE ZOMBIES
        for (Zombie z : zombies) {
            z.update(); // Move the zombie
            z.display(this); // Draw the zombie
        }

        // 5. Bullets & Collision
        // Loop backwards
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update();
            b.display(this);

            // COLLISION DETECTION
            // first collision logic Gemini taught me the rest i did all by myself with that
            // knoledge           
            // if bullet hits Zombie
            for (int j = zombies.size() - 1; j >= 0; j--) {
                Zombie z = zombies.get(j);
                if (dist(b.x, b.y, z.x, z.y) < 40) {
                    z.Zombiehealth -= 10; // Take damage
                    System.out.println("OUCH! Health: " + z.Zombiehealth);
                    bullets.remove(i); // Delete bullet
                    if (z.Zombiehealth <= 0) {
                        zombies.remove(j);
                        money = money + 100;
                        System.out.println("Money: " + money);

                    }
                    break;
                }
            }

            if (b.x > width) {
                bullets.remove(i);
            }
        }

        // 6. ZOMBIE HITS PERSON (Fixed with Cooldown)
        for (int j = zombies.size() - 1; j >= 0; j--) {
            Zombie z = zombies.get(j);
            // CHECK IF CLOSE AND IF COOLDOWN IS 0
            if (dist(z.x, z.y, person.x, person.y) < 40 && person.damageCooldown == 0) {
                person.health -= 10; // Take damage
                person.damageCooldown = 60; // Set invincibility for 60 frames (1 second)
                System.out.println("OUCH! Health: " + person.health);
                amountoftimestakendamage++;
                
                if (person.health <= 0) {
                    gameState = 2; // Switch to Game Over screen
                }
                break; 
            }
        }

        // 7. Draw Person
        person.display(this);
        money();
        HEALINGJUICE();
    }

    public void keyPressed() {
        if (gameState == 0) {
            if (key == ENTER || key == RETURN) {
                person.name = typingName;
                gameState = 1;
            } else if (key == BACKSPACE) {
                if (typingName.length() > 0) {
                    typingName = typingName.substring(0, typingName.length() - 1);
                }
            } else if (key != CODED) {
                typingName = typingName + key;
            }
        } else if (gameState == 1) {
            if (key == 'A' || key == 'a')
                isLeft = true;
            if (key == 'D' || key == 'd')
                isRight = true;
            if (key == 'W' || key == 'w')
                isUp = true;
            if (key == 'S' || key == 's')
                isDown = true;
            if (key == 'q' || key == 'Q')
                gameState = 3;

            if (key == 'y' || key == 'Y') {
                if (money >= 10) {
                    money = money - 10;
                    FlowersYouCanPlant++;
                    System.out.println("Bought 1 flower. Total inventory: " + FlowersYouCanPlant);
                }
            }

            // BUY HEALING JUICE
            if (key == 'j' || key == 'J') {
                if (person.health < 100 && money >= 100) {
                    money -= 100;
                    person.health = 100; // Restore full health
                    person.juiceTimer = 60; 
                    System.out.println("Juice Drank! Health restored.");
                    amountoftimesjuice++;
                } else if (person.health >= 100) {
                    System.out.println("Health is full! Cannot drink juice.");
                } else if (money < 100) {
                    System.out.println("Not enough money for juice!");
                }
            }

            // PLANTING FLOWERS
            if (key == 'F' || key == 'f') {

                if (FlowersYouCanPlant > 0) {

                    int snapX = (person.x / cellSize) * cellSize + (cellSize / 2);
                    int snapY = (person.y / cellSize) * cellSize + (cellSize / 2);

                    boolean spotIsTaken = false;
                    for (Flower f : flowers) {
                        if (f.x == snapX && f.y == snapY) {
                            spotIsTaken = true;
                            break;
                        }
                    }

                    if (!spotIsTaken) {
                        flowers.add(new Flower(snapX, snapY));
                        FlowersYouCanPlant--;
                        System.out.println("Planted! Remaining: " + FlowersYouCanPlant);
                    } else {
                        System.out.println("Spot taken!");
                    }
                } else {
                    System.out.println("You have no flowers to plant! Press Y to buy.");
                }
            }
        } else if (gameState == 3) {
            if (key == 'y' || key == 'Y')
                exit();
            if (key == 'n' || key == 'N')
                gameState = 1;
        }
        // ... existing movement code ...

        // SECRET CHEAT CODE
        if (key == 'm' || key == 'M') {
            money += 1000;
            System.out.println("Secret cheat activated! Added $1000.");
        }
    }

    public void HEALINGJUICE() {
        if (gameState == 1) {
            // Updated to show hint even if health is just slightly damaged
            if (person.health < 100) {
                textAlign(CENTER, CENTER);
                textSize(30); // made slightly smaller to fit
                fill(255, 0, 0);

                text("Do you want to buy the JUICE? For 100 DOLLA?", width / 2, height / 2 + 100);
                text("Press 'J' to Buy", width / 2, height / 2 + 140);

                // Display the juice image
                if (juiceImg != null) {
                    image(juiceImg, width / 2, height / 2 + 180);
                }
            } else {
                System.out.println("cant display the image check");
            }
        }
    }

    public void keyReleased() {
        if (gameState == 1) {
            if (key == 'A' || key == 'a')
                isLeft = false;
            if (key == 'D' || key == 'd')
                isRight = false;
            if (key == 'W' || key == 'w')
                isUp = false;
            if (key == 'S' || key == 's')
                isDown = false;
            if (key == 'F' || key == 'f')
                Fdown = false;
            if (key == 'Y' || key == 'y')
                Ydown = false;
            if (key == 'N' || key == 'n')
                Ndown = false;
        }

    }

    int FlowersYouCanPlant = 0;

    public void money() {
        if (gameState == 1) {
            textAlign(CENTER, CENTER);
            textSize(20);
            fill(0);

            text("Money: $" + money, width / 2, 30);
            text("Flowers in Inventory: " + FlowersYouCanPlant, width / 2, 55);

            textSize(15);
            text("Press 'Y' to buy flower ($10). Press 'F' to plant.", width / 2, 80);

        }
    }

    // CLASSES

    class Flower {
        int x, y;
        int cooldown = 0;

        public Flower(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void update(App app) {
            if (cooldown > 0)
                cooldown--;
            if (cooldown == 0) {
                app.bullets.add(new Bullet(x, y));
                cooldown = 60;
            }
        }

        // Gemini taught me how to use sprites
        public void display(App app) {
            if (app.flowerImg != null) {
                app.image(app.flowerImg, x, y);
            } else {
                app.rectMode(PConstants.CENTER);
                app.fill(255, 255, 0);
                app.rect(x, y, 50, 50);
                app.rectMode(PConstants.CORNER);
            }
        }
    }

    class Bullet {
        float x, y;
        float speed = 10;

        public Bullet(float startX, float startY) {
            x = startX;
            y = startY;
        }

        public void update() {
            x += speed;
        }

        // Gemini taught me how to use sprites
        public void display(App app) {
            if (app.bulletImg != null) {
                app.image(app.bulletImg, x, y);
            } else {
                app.fill(0);
                app.ellipse(x, y, 10, 10);
            }
        }
    }

    class Person {
        int x, y;
        int speed = 5;
        String name = "";
        int health = 100; // Start with 100 health
        int damageCooldown = 0; 
        int juiceTimer = 0; // <--- NEW VARIABLE FOR HOLDING JUICE

        public Person(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void updateCooldown() {
            if (damageCooldown > 0) {
                damageCooldown--;
            }
            if (juiceTimer > 0) {// COUNTDOWN JUICE TIMER
                juiceTimer--;
            }
        }

        public void moveLeft() {
            x -= speed;
        }

        public void moveRight() {
            x += speed;
        }

        public void moveUp() {
            y -= speed;
        }

        public void moveDown() {
            y += speed;
        }

        // Gemini taught me how to use sprites
        public void display(App app) {
            if (app.personImg != null) {
                app.image(app.personImg, x, y);
            } else {
                app.fill(0, 0, 255);
                app.ellipse(x, y, 50, 50);
            }

            // Draw Name
            app.fill(255);
            app.textAlign(PConstants.CENTER);
            app.textSize(20);
            app.text(name, x, y - 50);

            // Draw Health
            app.fill(0, 255, 0); // Green
            app.text("HP: " + health, x, y - 70);
        }
    }
}

class Zombie {
    int x, y;
    int speed = 2;
    int Zombiehealth = 40;

    public Zombie(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Add update() method to move the zombie leftwards
    public void update() {
        x -= speed;
    }

    public void moveTowards(App.Person p) {
        x++;
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
}