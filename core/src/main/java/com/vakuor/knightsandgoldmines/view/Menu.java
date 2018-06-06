package com.vakuor.knightsandgoldmines.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.vakuor.knightsandgoldmines.Main;
import com.vakuor.knightsandgoldmines.models.Crate;
import com.vakuor.knightsandgoldmines.models.Enemy;
import com.vakuor.knightsandgoldmines.models.Player;

public class Menu extends NewScreen {

    private final Main game;
    public static int screenWidth;
    public static int screenHeight;
    private Music menuMusic;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    private Image image;
    private Image img;
    private Skin skin;
    private Skin helpButtonSkin;
    private TextButton textButton;
    private TextButton helpButton;
    private Image helpimg;
    private Texture background;
    private Stage stage;
    private Stage menustage;
    private float aspectRatio;
    private Label outputLabel;
    private boolean allow=true;


    ////////////////////

    public static float deltaTime = 0;
    private Stage uistage;
    private InputMultiplexer multiplexer;
    private TextButton textButtonUP;
    private TextButton textButtonDOWN;
    private TextButton textButtonLEFT;
    private TextButton textButtonRIGHT;

    public static TiledMap map;
    public boolean dire=true;
    private int maxscore=0;
    public static TiledMapTile myTile;
    public static boolean tileDebug = false;
    private static OrthogonalTiledMapRenderer renderer;
    private boolean locals=false;
    //private Viewport viewport;
    public static Player player;
    public static Crate crate;
    private static Enemy enemy;
    public static Array<Enemy> enemies;
    long lastEnemySpawnTime = 5;
    private boolean rest=false;
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private Array<Rectangle> tiles = new Array<Rectangle>();


    private Texture tex;
    private Texture help;
    private Skin textButtonSkin;


    public Menu(final Main game){
        System.out.println("Menu.constructor\n");
        this.game = game;

        camera = new OrthographicCamera();
        //viewport = new ScreenViewport(camera);

        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void show() {


        background = new Texture("KaG/back.png");
        image = new Image(background);
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("visual/music/mainmenutheme.wav"));
        menuMusic.setLooping(true);
        atlas = new TextureAtlas("visual/output/GUI/GUI.atlas");
        skin = new Skin(atlas);
        stage = new Stage();
        menustage = new Stage();
        image.setName("image");
        camera = (OrthographicCamera) stage.getCamera();
        stage.getRoot().addActor(image);

        System.out.println("Menu.show\n");
        Gdx.input.setInputProcessor(stage);

        menuMusic.play();
        System.out.println(menuMusic.getVolume());
        menuMusic.setVolume(menuMusic.getVolume()*4);

        System.out.println(menuMusic.getVolume());

        float scale = background.getHeight()/Gdx.graphics.getHeight();
        camera.setToOrtho(false, Gdx.graphics.getWidth()*scale,Gdx.graphics.getHeight()*scale);//aspect
        camera.position.x=background.getWidth()/2;
        camera.position.y=background.getHeight()/2;
        screenWidth  = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        Main.aspectRatio = (float) screenWidth/screenHeight;
        aspectRatio = (float) Gdx.graphics.getWidth()/(float) Gdx.graphics.getHeight();
        System.out.println(aspectRatio);

        tex = new Texture("menuFon.png");
        help = new Texture("help.png");
        img = new Image(tex);
        helpimg = new Image(help);
        img.setBounds(camera.viewportWidth/2+camera.position.x,-camera.viewportHeight/2+camera.position.y,-camera.viewportWidth/4*aspectRatio/512f*320f,camera.viewportHeight);
        System.out.println(camera.position.x);
        helpimg.setBounds(camera.position.x-camera.viewportWidth/2,-camera.viewportHeight/2+camera.position.y,camera.viewportHeight,camera.viewportHeight);
        //camera.viewportWidth/2+img.getWidth()-img.getWidth()/4+camera.position.x,camera.position.y+img.getWidth()/4,-img.getWidth()/2,-img.getWidth()/2
        helpimg.setVisible(false);
        stage.addActor(img);
        stage.addActor(helpimg);



        textButtonSkin = new Skin();
        textButtonSkin.add("up",new Texture(Gdx.files.internal("visual/input/flatDark/but.png")));
        textButtonSkin.add("down",new Texture(Gdx.files.internal("visual/input/flatDark/but2.png")));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        Drawable up = textButtonSkin.getDrawable("up");
        Drawable down = textButtonSkin.getDrawable("down");
        textButtonStyle.up = up;
        textButtonStyle.down = down;
        textButtonStyle.font = new BitmapFont();
        System.out.println(background.getHeight()/img.getHeight());
        camera.zoom=background.getHeight()/img.getHeight();
        textButton = new TextButton(" ", textButtonStyle);
        textButton.setBounds(camera.viewportWidth/2+img.getWidth()-img.getWidth()/4+camera.position.x,camera.position.y+img.getWidth()/4,-img.getWidth()/2,-img.getWidth()/2);
        textButton.getLabel().setFontScale(5);

        textButton.setPosition(textButton.getX(),textButton.getY()+textButton.getHeight()/1.5f);
        stage.getRoot().addActor(textButton);


        helpButtonSkin = new Skin();
        helpButtonSkin.add("up",new Texture(Gdx.files.internal("visual/input/flatDark/help.png")));
        helpButtonSkin.add("down",new Texture(Gdx.files.internal("visual/input/flatDark/help.png")));
        TextButton.TextButtonStyle helpButtonStyle = new TextButton.TextButtonStyle();
        up = helpButtonSkin.getDrawable("up");
        down = helpButtonSkin.getDrawable("down");
        helpButtonStyle.up = up;
        helpButtonStyle.down = down;
        helpButtonStyle.font = new BitmapFont();
        System.out.println(background.getHeight()/img.getHeight());
        camera.zoom=background.getHeight()/img.getHeight();
        helpButton = new TextButton(" ", helpButtonStyle);
        helpButton.setBounds(camera.viewportWidth/2+img.getWidth()-img.getWidth()/4+camera.position.x,camera.position.y+img.getWidth()/4,-img.getWidth()/2,-img.getWidth()/2);
        helpButton.getLabel().setFontScale(5);
        helpButton.setPosition(helpButton.getX(),helpButton.getY()-helpButton.getHeight()/1.5f);
        stage.getRoot().addActor(helpButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        if(textButton.isPressed()){
            System.out.println("YES");
            game.setScreen(new Game(game));
            dispose();
        }
        if(helpButton.isPressed()){
            if(allow) {
                System.out.println("SOS!");
                helpimg.setVisible(!helpimg.isVisible());
                allow = false;
            }
        }
        else if(!allow){
            allow=true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            camera.zoom-=0.01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            camera.zoom+=0.01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y+=2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y-=2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x+=2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x-=2;
        }
    }

    @Override
    public void resize(int width, int height) {
        aspectRatio = (float) width/height;
        screenHeight = height;
        screenWidth = width;
        game.aspectRatio = (float) screenWidth/screenHeight;
        //viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();

//        game.dispose();
    }

}
