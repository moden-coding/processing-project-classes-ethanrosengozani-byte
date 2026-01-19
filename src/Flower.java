import processing.core.PConstants;

public class Flower {
       public int x, y;
       private int cooldown = 0;

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

  