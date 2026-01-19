import processing.core.PConstants;

 class Person {
       public int x, y;
       private int speed = 5;
       public String name = "";
       public int health = 100; // Start with 100 health
       public int damageCooldown = 0;
       public int juiceTimer = 0;

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

        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public int getHealth() {
            return health;
        }
        public int getJuiceTimer() {
            return juiceTimer;
        }
        public int getDamageCooldown() {
            return damageCooldown;
        }
        public String getName(){
            return name;
        }
        public void decreaseHealth(int amount) {
            health -= amount;
        }


        public void damageCooldown() {
            damageCooldown = 60; 
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