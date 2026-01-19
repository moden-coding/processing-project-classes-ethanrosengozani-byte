import processing.core.*;
import java.util.ArrayList;
import java.util.Scanner;

public class App extends PApplet {

    Person person;
    ArrayList<Flower> flowers = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Zombie> zombies = new ArrayList<>();
    ArrayList<Confetti> confettiList = new ArrayList<>();

    // Images
    PImage personImg;
    PImage flowerImg;
    PImage bulletImg;
    PImage ZombieImg;
    PImage juiceImg; 

    int cellSize = 125;

    // Movement Switches
   private boolean isLeft, isRight, isUp, isDown;
   private boolean Fdown;
   private boolean Ydown;
   private boolean Ndown;

    // Game State
    private int gameState = 0; // 0 = Start, 1 = Playing, 2 = Game Over, 3 = Quit Confirm this makes it easier
                       // for me to give the player the ability to quit and for the game to know when
                       // to do things based on where the code says it is at
                       //4 FOR CONFETTI
   private String typingName = "";

    // money
    private int money = 100;

    //Stats
    private int amountoftimesjuice = 0;
    private int amountoftimestakendamage = 0;
    
    // New Score Variables
    private int score = 0;
    private int highscore = 0;
    private int lives = 3; 

    // Array Requirement so we can make the rows using 2d arrays
    private int[] spawnRows;


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
        
        loadHighScore();

        // Initialize Array for spawning 2d array
        int rows = height / cellSize;
        spawnRows = new int[rows];
        for(int i = 0; i < rows; i++){
            spawnRows[i] = i * cellSize + cellSize/2;
        }
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
        
        textSize(20);
        fill(0);
        text("Current High Score: " + highscore, width / 2, 400);
    }

    public void drawGameOver() {
        if (score > highscore) {//if there is a new highscore
            highscore = score;
            saveHighScore();
        }
        
        background(0);
        
        // CONFETTI LOGIC
        if (confettiList.size() < 300) {
            confettiList.add(new Confetti());
        }
        for (Confetti c : confettiList) {
            c.update();
            c.display();
        }
        
        fill(255, 0, 0);
        textSize(60);
        textAlign(CENTER, CENTER);//alignign it to the center
        text("GAME OVER", width / 2, height / 2 - 50);//text saying the game is over
        
        textSize(30);
        fill(255);
        text("Final Score: " + score, width / 2, height / 2 + 10);//text staying your final score
        
        textSize(30);
        fill(255, 215, 0); 
        text("High Score: " + highscore, width / 2, height / 2 + 50);//text saying your high score
        
        fill(255, 0, 0);
        textSize(30);
        text(person.name + " died!", width / 2, height / 2 + 60);
        textSize(30);
        text(person.name + "Took damage " + amountoftimestakendamage + " times", width / 2, height / 2 + 1);//text saying how many times you took damage
        text(person.name + "Drank juice " + amountoftimesjuice + " times", width / 2, height / 2 + 1);//text saying how many times you drank juice
        
        fill(255);
        textSize(20);
        text("Press 'R' to Restart", width / 2, height - 50);
    }

    public void drawQuitScreen() {
        background(255, 0, 0);
        fill(255);
        textAlign(CENTER, CENTER);
        textSize(30);
        text("Exiting Game: Thank you for playing!", width / 2, height / 2);
        text("Are you sure you want to quit? (Y/N)", width / 2, height / 2 + 40);//if you want to quit then it gives you the option
    }

    public void drawGame() {
        // 1. Draw Background
        background(0, 255, 0);

        // 2. Draw Grid
        stroke(0);
        strokeWeight(1);
        for (int x = 0; x < width; x = x + cellSize) {//making the grid
            for (int y = 0; y < height; y = y + cellSize) {
                fill(140, 70, 20);
                rect(x, y, cellSize, cellSize);
            }
        }

        if (isLeft)//using a boolean to make it so we can get the smooth movemnt and in the person class i made the method of moveleft etc
            person.moveLeft();
        if (isRight)
            person.moveRight();
        if (isUp)
            person.moveUp();
        if (isDown)
            person.moveDown();
        
        person.updateCooldown(); 

        // 4. Flowers
        for (Flower flower : flowers) {//display the flowers
            flower.update(this);
            flower.display(this);
        }
        // Zombies!!
        // ZOMBIE SPAWNING
        if (frameCount % 120 == 0) {// every 2 seconds
            int rIndex = (int)random(spawnRows.length); 
            int row = spawnRows[rIndex];
            zombies.add(new Zombie(width, row));
        }

        // Remove zombies with health <= 0
        for (int i = zombies.size() - 1; i >= 0; i--) {
            Zombie z = zombies.get(i);
            
            if (z.getZombiehealth() <= 0) {
                zombies.remove(i);
            } 
            // CHECK IF ZOMBIE REACHED END (LIVES)
            else if (z.x < 0) {
                zombies.remove(i);
                lives--;
                System.out.println("Lost a life! Lives: " + lives);
                if (lives <= 0) {
                     gameState = 2;
                }
            }
        }

        // DRAW & MOVE ZOMBIES
        for (Zombie z : zombies) {
            z.update(person); // Move the zombie, passing the person object
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
                if (dist(b.x, b.y, z.x, z.y) < 40) {//if the distance between the zombie and the bullet is les than 40 pixels we use the distance function
                    z.setZombiehealth(z.getZombiehealth() - 10); // Take damage
                    System.out.println("OUCH! Health: " + z.getZombiehealth());
                    bullets.remove(i); // Delete bullet from array list
                    if (z.getZombiehealth() <= 0) {
                        zombies.remove(j);
                        money = money + 100;
                        score++;
                        System.out.println("Money: " + money);

                    }
                    break;
                }
            }

            if (b.x > width) {
                bullets.remove(i);
            }
        }

        for (int j = zombies.size() - 1; j >= 0; j--) {
            Zombie z = zombies.get(j);//
            // CHECK IF CLOSE AND IF COOLDOWN IS 0
            if (dist(z.x, z.y, person.x, person.y) < 40 && person.damageCooldown == 0) {
                person.decreaseHealth(10); // Take damage
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
        HEALINGJUICE();//if they will do the juice
    }

    public void keyPressed() {
        if (gameState == 0) {
            if (key == ENTER || key == RETURN) {
                person.name = typingName;// Set the player's name
                gameState = 1;// Switch to Game State 1
            } else if (key == BACKSPACE) {// If backspace is pressed then delete the letter
                if (typingName.length() > 0) {
                    typingName = typingName.substring(0, typingName.length() - 1);
                }
            } else if (key != CODED) {// If key is not a special key
                typingName = typingName + key;
            }
        } else if (gameState == 1) {
            if (key == 'A' || key == 'a')//if the key is pressed then the boolean becomes true then the same for the rest
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

                if (FlowersYouCanPlant > 0) {//if you have a flower to plant

                    int snapX = (person.x / cellSize) * cellSize + (cellSize / 2);//put it in the center
                    int snapY = (person.y / cellSize) * cellSize + (cellSize / 2);//also put it in the center

                    boolean spotIsTaken = false;
                    for (Flower f : flowers) {
                        if (f.x == snapX && f.y == snapY) {
                            spotIsTaken = true;//if the spot is taken
                            break;
                        }
                    }

                    if (!spotIsTaken) {//if the spot is free
                        flowers.add(new Flower(snapX, snapY));//add a flower then make it go in the center
                        FlowersYouCanPlant--;
                        System.out.println("Planted! Remaining: " + FlowersYouCanPlant);
                    } else {
                        System.out.println("Spot taken!");
                    }
                } else {
                    System.out.println("You have no flowers to plant! Press Y to buy.");
                }
            }
        } else if (gameState == 2) {
             if (key == 'r' || key == 'R') {
                 // Reset Game Logic and everything for the next game
                 gameState = 1;
                 score = 0;
                 lives = 3; 
                 money = 100;
                 person.health = 100;
                 zombies.clear();
                 bullets.clear();
                 flowers.clear();
                 confettiList.clear();
                 FlowersYouCanPlant = 0;
                 amountoftimesjuice = 0;
                 amountoftimestakendamage = 0;
                 person.x = 400;
                 person.y = 500;
             }
        } else if (gameState == 3) {
            if (key == 'y' || key == 'Y')
                exit();
            if (key == 'n' || key == 'N')
                gameState = 1;
        }

        // SECRET CHEAT CODE
        if (key == 'm' || key == 'M') {
            money += 1000;//adding the money 
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
            text("Score: " + score, width / 2, 10);
            
            fill(255, 0, 0);
            text("Lives: " + lives, width - 60, 30);
            fill(0);
            
            text("Flowers in Inventory: " + FlowersYouCanPlant, width / 2, 55);

            textSize(15);
            text("Press 'Y' to buy flower ($10). Press 'F' to plant.", width / 2, 80);

        }
    }

    // Load High Score
    void loadHighScore() {
        String[] lines = loadStrings("highscore.txt"); 
        if (lines != null && lines.length > 0) {// Check if file is not empty
            try {
                highscore = Integer.parseInt(lines[0]);// Load high score
            } catch (Exception e) {
                highscore = 0;
            }
        }
    }

    void saveHighScore() {//save your high score
        String[] data = { str(highscore) }; 
        saveStrings("highscore.txt", data);
    }

    // Confetti Inner Class
    class Confetti {
        float x, y, speedY;
        int c;// Color

        public Confetti() {
            x = random(width);        // Random X position
            y = random(-500, -50);    // Start above the screen
            speedY = random(2, 5);    // Random falling speed
            c = color(random(255), random(255), random(255)); // Random color
        }

        public void update() {
            y += speedY; // Move down

            // If it falls off the bottom, reset to the top
            if (y > height) {
                y = random(-50, -10);
                x = random(width);
            }
        }

        public void display() {
            noStroke();
            fill(c);
            rect(x, y, 8, 8); // Draw a small square
        }
    }
}