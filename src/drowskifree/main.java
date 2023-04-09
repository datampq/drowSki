/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drowskifree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.LinkedList;



import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * http://drow.today
 *
 * @author datampq
 */
public final class main extends JFrame {
    
    public Color bg = Color.decode("0xe5e9f0");
    public Color secondary = Color.decode("0x2e3440");
    public Color text = Color.decode("0xffffff");
    public Color primary = Color.decode("0x5e81ac");
    
    public int _highScore = 0;
    public int _tileW = 120;
    public int _tileH = 60;
    public int _tileH2 = 90;
    LinkedList<tileData> graphicsData;
    public Dimension screenSize;
    public BufferedImage pl;
    public JLabel _highScoreLabel;
    public main main;
    public int scaleFacotr = 2;
    public boolean debug = false;
    public int playerWidth;
    public LinkedList<BufferedImage> bgs;
    
    public main() {
        loadScore();
        graphicsData = new LinkedList<>();
        bgs = new LinkedList<>();
        main = this;
        if (loadAssets()) {
            rescaleAssets();
            this.setUndecorated(true);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            FrameDragListener frameDragListener = new FrameDragListener(this);
            this.addMouseListener(frameDragListener);
            this.addMouseMotionListener(frameDragListener);
            this.setLocation(screenSize.width / 2 - 180, screenSize.height / 2 - 120);
            add(content());
            pack();
            setVisible(true);
        } else {
            System.out.println("Error:unable to load assets!");
            System.exit(-1);
        }
    }
    
    private void rescaleAssets() {
        pl = resize(pl, pl.getWidth() * scaleFacotr, pl.getHeight() * scaleFacotr);
        for (int i = 0; i < graphicsData.size(); i++) {
            tileData get = graphicsData.get(i);
            get.graphics = resize(get.graphics, get.graphics.getWidth() * scaleFacotr, get.graphics.getHeight() * scaleFacotr);
        }
        playerWidth = pl.getWidth() / 2;
    }
    
    private JPanel content() {
        JPanel content = new JPanel();
        content.setBackground(bg);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JLabel l = new JLabel("drowSki v0.34b");
        l.setForeground(secondary);
        l.setFont(new Font("Arial", Font.BOLD, 32));
        
        content.add(l);
        btn b = new btn(new Dimension(240, 60), "New Game");
        JLabel l2 = new JLabel("Controls: A,D,Space");
        l2.setForeground(secondary);
        l2.setFont(new Font("Arial", Font.BOLD, 32));
        
        _highScoreLabel = new JLabel("Best run: " + _highScore);
        _highScoreLabel.setForeground(secondary);
        _highScoreLabel.setFont(new Font("Arial", Font.BOLD, 32));
        
        btn b1 = new btn(new Dimension(240, 60), "Exit");
        
        content.add(b);
        content.add(l2);
        content.add(_highScoreLabel);
        content.add(b1);
        
        return content;
    }
    
    public void restartGame() {
        if (game != null) {
            game.dispose();
        }
        game = new game(graphicsData);
    }
    
    private boolean loadAssets() {
        try {
            //load score;
            File img = new File("tiles.png");
            BufferedImage bufferedImage = ImageIO.read(img);
            
            File imgp = new File("player.png");
            pl = ImageIO.read(imgp);
            //get tiles
            BufferedImage snow = bufferedImage.getSubimage(0, 0, 120, 60);
            graphicsData.add(new tileData(snow, "snow"));
            
            BufferedImage flag = bufferedImage.getSubimage(120, 0, 120, 60);
            graphicsData.add(new tileData(flag, "flag"));
            
            BufferedImage corner = bufferedImage.getSubimage(240, 0, 120, 60);
            graphicsData.add(new tileData(corner, "corner"));
            
            BufferedImage jump = bufferedImage.getSubimage(360, 0, 120, 60);
            graphicsData.add(new tileData(jump, "jump"));
            
            BufferedImage tree = bufferedImage.getSubimage(0, 90, 120, 60);
            graphicsData.add(new tileData(tree, "tree"));
            
            BufferedImage machine = bufferedImage.getSubimage(120, 90, 120, 60);
            graphicsData.add(new tileData(machine, "machine"));
            
            BufferedImage fence = bufferedImage.getSubimage(240, 90, 120, 60);
            graphicsData.add(new tileData(fence, "fence"));
            
            BufferedImage crack = bufferedImage.getSubimage(360, 90, 120, 60);
            graphicsData.add(new tileData(crack, "crack"));

            //load bgs
            File img3 = new File("bgs.png");
            BufferedImage bufferedImage3 = ImageIO.read(img3);
            
            bgs.add(bufferedImage3.getSubimage(0, 0, 405, 210));
            bgs.add(bufferedImage3.getSubimage(405, 0, 405, 210));
            bgs.add(bufferedImage3.getSubimage(810, 0, 405, 210));
            bgs.add(bufferedImage3.getSubimage(1215, 0, 405, 210));
            bgs.add(bufferedImage3.getSubimage(1620, 0, 405, 210));
            
            return true;
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
    }
    public game game;
    
    public class game extends JFrame implements KeyListener {
        
        public LinkedList<tile> tiles;
        public LinkedList<tile> baseData;
        public LinkedList<bgImage> backs;
        public int density = 25;
        public int score = 0;
        public int fps = 30;
        public int speed = 2;
        public int gravity = 9;
        public int jumpSpeed = 20;
        public int baseZ = 0;
        public boolean runs = true;
        public boolean grounded = true;
        public int width = 5;
        public int height = 24;
        public int pointer = 2;
        public boolean paused = false;
        Thread t;

        /**
         *
         * @param data
         */
        public game(LinkedList<tileData> data) {
            tiles = new LinkedList<>();
            backs = new LinkedList<>();
            baseData = new LinkedList<>();
            
            if (!digestSquares(data)) {
                System.out.println("Unable to digest squares!");
            }
            
            this.setUndecorated(true);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            FrameDragListener frameDragListener = new FrameDragListener(this);
            this.addMouseListener(frameDragListener);
            this.addMouseMotionListener(frameDragListener);
            this.setLocation(320, 220);
            add(gameContent());
            addKeyListener(this);
            pack();
            setVisible(true);
            
            t = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        if (screen.ready) {
                            screen.update();
                        }
                        try {
                            Thread.sleep(fps);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            };
            t.start();
            
        }
        public JLabel _score;
        public JPanel canvas;
        public canvas screen;
        public Graphics g;
        
        private JPanel gameContent() {
            JPanel content = new JPanel();
            content.setBackground(secondary);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            JPanel top = new JPanel();
            top.setBackground(secondary);
            top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
            gamebtn b = new gamebtn(new Dimension(220, 60), "Menu");
            gamebtn b1 = new gamebtn(new Dimension(220, 60), "New Game");
            gamebtn b3 = new gamebtn(new Dimension(220, 60), "Pause");
            _score = new JLabel("Score: " + score);
            _score.setForeground(text);
            _score.setFont(new Font("Arial", Font.BOLD, 32));
            _score.setPreferredSize(new Dimension(480, 60));
            top.add(_score);
            top.add(b3);
            top.add(b1);
            top.add(b);
            content.add(top);
            canvas = generateCanvas();
            content.add(canvas);
            return content;
        }
        
        private JPanel generateCanvas() {
            JPanel content = new JPanel();
            content.setBackground(bg);
            screen = new canvas();
            content.add(screen);
            return content;
        }
        
        private boolean digestSquares(LinkedList<tileData> data) {
            for (int i = 0; i < data.size(); i++) {
                tileData get = data.get(i);
                baseData.add(new tile(get.type, new Dimension(get.graphics.getWidth(), get.graphics.getHeight()), get.graphics, 0, 0, 0, 0));
            }
            return true;
        }
        
        @Override
        public void keyTyped(KeyEvent ke) {
            if (runs) {
                char keyCode = ke.getKeyChar();
                if (keyCode == 'p') {
                    paused = !paused;
                }
                if (!game.paused) {
                    switch (keyCode) {
                        case 'a':
                            screen.movePlayerLeft();
                            break;
                        case 'd':
                            screen.movePlayerRight();
                            break;
                        case ' ':
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        
        @Override
        public void keyPressed(KeyEvent ke) {
            char keyCode = ke.getKeyChar();
            if (!game.paused) {
                if (keyCode == ' ') {
                    screen.speedUp();
                }
            }
        }
        
        @Override
        public void keyReleased(KeyEvent ke) {
            char keyCode = ke.getKeyChar();
            if (!game.paused) {
                if (keyCode == ' ') {
                    screen.speedDown();
                    if (!runs) {
                        game.dispose();
                        game = new game(graphicsData);
                    }
                }
            }
        }
        
        public class canvas extends JPanel {
            
            int index = 0;
            int wx = 0;
            int wy = 0;
            public boolean ready = false;
            int prev_index = 3;
            int counter = 0;
            int y = 0;
            int stagex;
            int stagey;
            int playerx;
            int playery;
            int playerbound = -360;
            
            public canvas() {
                setPreferredSize(new Dimension(1280, 720));
                originx = 100;
                wx = 100;
                wy = 0;
                playerx = -360;
                playery = 300;
                int _x = 140;
                int _y = -155;
                for (int i = 0; i < width; i++) {
                    backs.add(new bgImage(bgs.get((int) (Math.random() * bgs.size())), _x, _y));
                    _x += 300;
                    _y += 145;
                    
                }
                for (int j = 0; j < height; j++) {
                    for (int i = 0; i < width; i++) {
                        tile get;
                        if (i == prev_index || i == prev_index + 1 || i == prev_index - 1) {
                            get = baseData.get(0);
                            int name = (int) (Math.random() * 60);
                            if (name < 20 && prev_index > 0) {
                                prev_index--;
                            } else if (prev_index < width - 1 && name > 40) {
                                prev_index++;
                            } else {
                            }
                        } else {
                            get = baseData.get(0);
                        }
                        // public tile(String type, Dimension dim, BufferedImage img, int index, int rotation) {
                        tile nt = new tile(get.type, get.dim, get.img, counter, 0, i, j);
                        nt.x = (i - j) * (nt.img.getWidth() / 2);
                        nt.y = (i + j) * (nt.img.getHeight() / 2);
                        wx = nt.x;
                        wy = nt.y;
                        tiles.add(nt);
                        counter++;
                    }
                }
                ready = true;
            }
            public boolean refill = false;
            int jumpDiff = 1;
            int diffAmount = 0;
            int exponent = 10;
            Polygon p;
            //replace the javafx circle with this:
            public class Circle{
                int x;
                int y;
                int radius;
                public void setCenterX(int x){
                    this.x = x;
                }
                public void setCenterY(int y){
                    this.y = y;
                }
                public void setRadius(int radius){
                    this.radius = radius;
                }
                public boolean intersects(Rectangle2D r){
                    double x1 = r.getX();
                    double y1 = r.getY();
                    double x2 = r.getX() + r.getWidth();
                    double y2 = r.getY() + r.getHeight();
                    return intersects(x1, y1) || intersects(x1, y2) || intersects(x2, y1) || intersects(x2, y2);
                }
                public boolean intersects(double x, double y){
                    double dx = x - this.x;
                    double dy = y - this.y;
                    return dx * dx + dy * dy <= radius * radius;
                }

                public Rectangle2D getBoundsInLocal() {
                    return new Rectangle2D.Double(x - radius, y - radius, radius * 2, radius * 2);
                }
            }
            
            private boolean collisionDebug(tile get) {
                int x1 = originx * 2 - get.x;
                Circle s = new Circle();
                s.setRadius(get.img.getHeight() / 4);
                s.setCenterX(x1);
                s.setCenterY(get.y);
                int x2 = originx * 2 - playerx;
                Circle s2 = new Circle();
                s2.setRadius(pl.getHeight() / 3);
                s2.setCenterX(x2 - pl.getWidth());
                s2.setCenterY(playery);
                return s2.intersects(s.getBoundsInLocal());
                
            }
            
            public void update() {
                if (runs && !paused) {
                    int cc = 0;
                    for (int i = 0; i < backs.size(); i++) {
                        backs.get(i).x -= jumpDiff * 2;
                        backs.get(i).y -= jumpDiff;
                    }
                    if (backs.getFirst().y < -205) {
                        backs.removeFirst();
                        backs.add(new bgImage(bgs.get((int) (Math.random() * bgs.size())), backs.getLast().x + 300, backs.getLast().y + 150));
                    }
                    for (int i = 0; i < tiles.size(); i++) {
                        if (cc > width - 1) {
                            cc = 0;
                        }
                        tile nt = tiles.get(i);
                        if (!paused) {
                            nt.x += jumpDiff * 2;
                            nt.y -= jumpDiff;
                        }
                        if (collisionDebug(nt)) {
                            if (debug && !paused) {
                                nt.selected = true;
                            }
                            if (!nt.type.equals("snow") && !nt.type.equals("jump")) {
                                if (grounded) {
                                    runs = false;
                                    if (_highScore < score) {
                                        _highScore = score;
                                        _highScoreLabel.setText("Best: run: " + _highScore);
                                        saveScore();
                                    }
                                }
                            } else {
                                if (nt.type.equals("jump")) {
                                    screen.jump();
                                }
                            }
                        } else {
                            if (debug && !paused) {
                                nt.selected = false;
                            }
                        }
                        cc++;
                    }
                    if (tiles.get(0).y < -240) {
                        refill = true;
                    }
                    if (refill) {
                        diffAmount++;
                        if (diffAmount > exponent) {
                            exponent = exponent + 20;
                            diffAmount = 0;
                            jumpDiff++;
                            System.out.println(exponent);
                        }
                        score++;
                        _score.setText("Score: " + score);
                        tile last = tiles.get(tiles.size() - width);
                        int origx = last.x;
                        int origy = last.y;
                        for (int i = 0; i < width; i++) {
                            tile get;
                            if (i == prev_index || i == prev_index + 1 || i == prev_index - 1) {
                                get = baseData.get(0);
                                int name = (int) (Math.random() * 60);
                                if (name < 20 && prev_index > 0) {
                                    prev_index--;
                                } else if (prev_index < width - 1 && name > 40) {
                                    prev_index++;
                                } else {
                                }
                            } else {
                                int name = (int) (Math.random() * 100);
                                if (name > density) {
                                    get = baseData.get(0);
                                } else {
                                    get = baseData.get((int) (Math.random() * baseData.size()));
                                }
                            }
                            // public tile(String type, Dimension dim, BufferedImage img, int index, int rotation) {
                            tile nt = new tile(get.type, get.dim, get.img, counter, 0, 0, 0);
                            nt.x = origx + (i - 1) * (nt.img.getWidth() / 2);
                            nt.y = origy + (i + 1) * (nt.img.getHeight() / 2);
                            tiles.addLast(nt);
                            tiles.removeFirst();
                            counter++;
                        }
                        refill = false;
                    }
                }
                repaint();
            }
            public int originx;
            
            public void movePlayerLeft() {
                if (playerbound + (width * tiles.getFirst().width / 3) - 35 > playerx) {
                    playerx += 30;
                    playery += 15;
                }
            }
            
            public void movePlayerRight() {
                if (playerbound - (width * tiles.getFirst().width / 6) < playerx) {
                    playerx -= 30;
                    playery -= 15;
                }
            }
            
            public void jump() {
                if (grounded) {
                    jumpThread = new Thread() {
                        @Override
                        public void run() {
                            grounded = false;
                            for (int i = 0; i < 4; i++) {
                                jumpDiff++;
                                playery -= 8;
                                try {
                                    Thread.sleep(fps * 4);
                                } catch (InterruptedException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                            try {
                                Thread.sleep(fps * 18);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                            for (int i = 0; i < 4; i++) {
                                jumpDiff--;
                                playery += 8;
                                try {
                                    Thread.sleep(fps * 4);
                                } catch (InterruptedException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                            score += 100;
                            grounded = true;
                        }
                    };
                    jumpThread.start();
                }
            }
            Thread jumpThread;
            Thread speedThreadUp;
            Thread speedThreadDown;
            int[] boundsX = {0, 1280, 1280};
            int[] boundsY = {0, 0, 720};
            
            public void speedDown() {
                speedThreadUp = null;
                if (speedThreadDown == null) {
                    speedThreadDown = new Thread() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                jumpDiff--;
                                try {
                                    Thread.sleep(fps * 10);
                                } catch (InterruptedException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                        }
                    };
                    speedThreadDown.start();
                }
            }
            
            public void speedUp() {
                speedThreadDown = null;
                if (speedThreadUp == null) {
                    speedThreadUp = new Thread() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                jumpDiff++;
                                try {
                                    Thread.sleep(fps * 10);
                                } catch (InterruptedException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                        }
                    };
                    speedThreadUp.start();
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(secondary);
                g.fillRect(0, 0, 1280, 720);
                g.setColor(Color.WHITE);
                g.fillPolygon(boundsX, boundsY, 3);
                g.setColor(Color.red);
                if (ready) {
                    for (int i = 0; i < backs.size(); i++) {
                        g.drawImage(backs.get(i).img, backs.get(i).x, backs.get(i).y, this);
                    }
                    for (int i = 0; i < tiles.size(); i++) {
                        tile get = tiles.get(i);
                        if (debug) {
                            if (!get.selected) {
                                g.drawImage(get.img, originx * 2 - get.x, get.y, this);
                            } else {
                                g.drawString("x=" + get.x + " ,y=" + get.y, 20, 700);
                                g.setColor(Color.red);
                                g.drawOval(originx * 2 - get.x, get.y, 10, 10);
                                g.setColor(Color.black);
                                g.drawOval(originx * 2 - get.x + get.width, get.y + get.height, 10, 10);
                            }
                            if (paused) {
                                g.setColor(Color.red);
                                int _xdebug = originx * 2 - get.x;
                                g.drawString("x=" + _xdebug + " ,y=" + get.y, originx * 2 - get.x, get.y);
                                g.setColor(Color.red);
                                g.fillOval(originx * 2 - get.x, get.y, 2, 2);
                                g.setColor(Color.black);
                                g.fillOval(originx * 2 - get.x + get.width, get.y + get.height, 2, 2);
                            }
                        } else {
                            g.drawImage(get.img, originx * 2 - get.x, get.y, this);
                        }
                    }
                    g.drawImage(pl, originx * 2 - playerx, playery, this);
                    if (debug) {
                        g.setColor(Color.blue);
                        int w = originx * 2 - playerx;
                        g.drawString("x=" + w + " ,y=" + playery, w, playery);
                    }
                }
                if (!runs) {
                    g.setColor(Color.red);
                    g.drawString("GAME OVER", 620, 350);
                }
            }
        }
        
    }
    
    private class tileData {
        
        public BufferedImage graphics;
        public String type;
        
        public tileData(BufferedImage icon, String name) {
            type = name;
            graphics = icon;
        }
    }
    
    public class gamebtn extends JPanel implements MouseListener {
        
        private final String action;
        
        public gamebtn(Dimension dim, String title) {
            action = title;
            this.setBackground(primary);
            this.addMouseListener(this);
            setPreferredSize(dim);
            JLabel l = new JLabel(title);
            l.setForeground(text);
            l.setFont(new Font("Arial", Font.BOLD, 24));
            add(l);
        }
        
        @Override
        public void mouseClicked(MouseEvent me) {
            switch (action) {
                case "Menu":
                    game.dispose();
                    main.setVisible(true);
                    break;
                case "Pause":
                    game.paused = !game.paused;
                    break;
                default:
                    if (game != null) {
                        game.dispose();
                    }
                    game = new game(graphicsData);
                    break;
            }
        }
        
        @Override
        public void mousePressed(MouseEvent me) {
        }
        
        @Override
        public void mouseReleased(MouseEvent me) {
        }
        
        @Override
        public void mouseEntered(MouseEvent me) {
            this.setBackground(secondary);
        }
        
        @Override
        public void mouseExited(MouseEvent me) {
            this.setBackground(primary);
        }
    }
    
    public class btn extends JPanel implements MouseListener {
        
        private final String action;
        
        public btn(Dimension dim, String title) {
            action = title;
            this.setBackground(primary);
            this.addMouseListener(this);
            setPreferredSize(dim);
            JLabel l = new JLabel(title);
            l.setForeground(text);
            l.setFont(new Font("Arial", Font.BOLD, 24));
            add(l);
        }
        
        @Override
        public void mouseClicked(MouseEvent me) {
            if (action.equals("Exit")) {
                System.exit(0);
            } else {
                main.setVisible(false);
                if (game != null) {
                    game.dispose();
                }
                game = new game(graphicsData);
            }
        }
        
        @Override
        public void mousePressed(MouseEvent me) {
        }
        
        @Override
        public void mouseReleased(MouseEvent me) {
        }
        
        @Override
        public void mouseEntered(MouseEvent me) {
            this.setBackground(secondary);
        }
        
        @Override
        public void mouseExited(MouseEvent me) {
            this.setBackground(primary);
        }
        
    }
    
    public void loadScore() {
        ObjectInputStream oi = null;
        scoreContainer ss;
        System.out.println("getting score...");
        try {
            FileInputStream fi = new FileInputStream(new File("settings.drow"));
            oi = new ObjectInputStream(fi);
            ss = (scoreContainer) oi.readObject();
            _highScore = ss.score;
            oi.close();
            System.out.println("Score loaded...");
        } catch (IOException | ClassNotFoundException ex) {
            _highScore = 0;
            System.out.println(ex.getMessage());
        }
    }
    
    public void saveScore() {
        try {
            scoreContainer s = new scoreContainer();
            s.score = _highScore;
            try (FileOutputStream f = new FileOutputStream(new File("settings.drow")); ObjectOutputStream o = new ObjectOutputStream(f)) {
                o.writeObject(s);
            }
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        
        return dimg;
    }
    
    public class bgImage {
        
        public int x;
        public int y;
        public int width;
        public int height;
        public BufferedImage img;
        
        public bgImage(BufferedImage i, int _x, int _y) {
            img = i;
            x = _x;
            y = _y;
            width = i.getWidth();
            height = i.getHeight();
        }
    }
    
}
