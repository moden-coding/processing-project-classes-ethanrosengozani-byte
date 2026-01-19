 class Bullet {
       public float x, y;
       private float speed = 10;

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
        public int getX() {
            return (int) x;
        }

        public int getY() {
            return (int) y;
        }
        public float getspeed(){
            return speed;
        
        }
    }