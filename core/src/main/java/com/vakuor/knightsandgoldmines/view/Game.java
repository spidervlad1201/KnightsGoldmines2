package com.vakuor.knightsandgoldmines.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.vakuor.knightsandgoldmines.Main;
import com.vakuor.knightsandgoldmines.models.Crate;
import com.vakuor.knightsandgoldmines.models.Enemy;
import com.vakuor.knightsandgoldmines.models.Player;
import com.vakuor.knightsandgoldmines.utilities.ExtentedTouchpad;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings({"WeakerAccess", "AccessStaticViaInstance"})
public class Game extends NewScreen {

    final Main game;

    public static float deltaTime = 0;
    private static float aspectRatio;
    private Stage stage, uistage;
    private InputMultiplexer multiplexer;
    private TextButton textButtonUP;
    private TextButton textButtonDOWN;
    private TextButton textButtonLEFT;
    private TextButton textButtonRIGHT;

    public static TiledMap map;
    public boolean dire=true;
    private Image img;
    private int maxscore=0;
    public static TiledMapTile myTile;
    public static boolean tileDebug = false;
    private static OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private boolean locals=false;
    //private Viewport viewport;
    public static Player player;
    public static Crate crate;
    private Texture tex;
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

    public static final float GRAVITY = -2.5f;

    private boolean debug = false;
    private boolean oldtouch = false;
    private ShapeRenderer debugRenderer;

    public static ExtentedTouchpad touchpad;
    public static ExtentedTouchpad touchpadR;
    private Slider sliderZoom;
    private float zoomVal = 1.25f;
    private float localdeltapos=0;
    private boolean localbool;

    public static Label healthLabel;
    public static Label scoreLabel;
    public static Label sco;
    public static Label sco2;

    public static Sound kickSound;
    public static Sound swordSound;
    public static Sound screamSound;
    public static Sound spawnSound;
    public static Sound thudSound;
    /////////////////////////////////////////////////////

    private int startX, startY, endX, endY;

    public Game(final Main game){
        System.out.println("Menu.constructor\n");
        this.game = game;
        camera = new OrthographicCamera();
        //viewport = new ScreenViewport(camera);
        swordSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/swordBlow.wav"));
        kickSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/Kick.ogg"));
        screamSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/man_scream.ogg"));
        spawnSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/spawn.ogg"));
        thudSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/thud.ogg"));
        camera.setToOrtho(false, 800, 480);

    }
    @Override
    public void show() {

        // load the map, set the unit scale to 1/12 (1 unit == 12 pixels)
        map = new TmxMapLoader().load("logical/maps/Map/leve3.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / 12f);
        myTile = map.getTileSets().getTile(20);
        stage = new Stage();
        uistage = new Stage();
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(uistage);
        player = new Player(50,20);
        //crate = new Crate(20,30);
        //enemy = new Enemy(25,25);
        enemies = new Array<Enemy>();
        spawnEnemy();

        //enemies.add(enemy);
        aspectRatio = (float) Gdx.graphics.getWidth()/(float) Gdx.graphics.getHeight();
        //viewport = new ScreenViewport();
        camera = (OrthographicCamera) stage.getCamera();
        camera.setToOrtho(false, 20f*aspectRatio, 20f);

        createTouches();
        addToStages();
        camera.update();
        debugRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        deltaTime = Gdx.graphics.getDeltaTime();

        if(maxscore<player.score){maxscore=player.score;}

        if(rest&&isTouched(0.0f, 1f)){
            player.died=false;rest=false;locals=false;
            player.setHealth(4);
            player.score=0;
            lastEnemySpawnTime=5;
            show();
        }

        scoreLabel.setText("Score: "+String.valueOf(player.score));

        if(TimeUtils.millis() - lastEnemySpawnTime > 10000) {spawnEnemy();//dire=!dire;
        }
        updateControls();
        // update the koala (process input, collision detection, position update)
        //enemy.move(enemy.position.x<player.position.x);
        //enemy.update();
        //player.update();


        // let the camera follow the koala, x-axis only
        camera.position.x = player.getPosition().x+player.WIDTH/2;
        camera.position.y = player.getPosition().y+player.HEIGHT;
        camera.update();

        // set the TiledMapRenderer view based on what the
        // camera sees, and render the map
        renderer.setView(camera);
        renderer.render();

        stage.act(deltaTime); uistage.act(deltaTime);
        stage.draw();
        //if(!player.died)
        uistage.draw();


        // render debug rectangles
        if (debug) renderDebug();
        if(player.died && !locals){
            tex = new Texture("border.png");
            img = new Image(tex);
            img.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
            locals=true;
            uistage.addActor(img);
            Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.BLACK);
            if(maxscore<player.score){maxscore=player.score;}
            sco = new Label("null",labelStyle);
            sco.setSize(player.WIDTH, Player.HEIGHT*1.7f);
            sco.setFontScale(Gdx.graphics.getHeight()/400f*aspectRatio);
            System.out.println(Gdx.graphics.getHeight()/400f*aspectRatio);
            sco.setPosition(Gdx.graphics.getHeight()/3f,(Gdx.graphics.getHeight()/2-Gdx.graphics.getHeight()*(aspectRatio/20f)), Align.left);
            sco.setText("Your score is: "+String.valueOf(player.score));
            uistage.addActor(sco);
            rest=true;

            sco2 = new Label("null",labelStyle);
            sco2.setSize(Player.WIDTH, Player.HEIGHT);
            sco2.setFontScale(Gdx.graphics.getHeight()/400f*aspectRatio);
            sco2.setPosition(Gdx.graphics.getHeight()/3f,(Gdx.graphics.getHeight()/2+Gdx.graphics.getHeight()*(aspectRatio/20f)), Align.left);
            sco2.setText("Highest score is: "+String.valueOf(maxscore));
            uistage.addActor(sco2);

//
//            healthLabel.setFontScale(Gdx.graphics.getHeight()/400f*aspectRatio);
//            healthLabel.setPosition(Gdx.graphics.getHeight()/20f,Gdx.graphics.getHeight()/2+Gdx.graphics.getHeight()*(aspectRatio/20f), Align.left);

        }
        if(player.died && locals)
        {
            img.setPosition(0,0);

        }
    }
    private void updateControls(){


        if(touchpadR.getKnobPercentX()!=0){
            player.attack(enemies);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            player.setPosition(player.getPosition().x,player.getPosition().y+10);
            player.velocity.y=0;
        }

        if(touchpad.getKnobPercentX()!=0) player.move(touchpad.getKnobPercentX());
        else

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || (isTouched(0, 0.25f)&&oldtouch)) {
            player.move(false);
        }
        else

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || (isTouched(0.25f, 0.5f)&&oldtouch)) {
            player.move(true);
        }
        else player.ismoving=false;
        if(touchpad.getKnobPercentY()>0.4 && player.isGrounded()) player.jump();


        if (sliderZoom.isDragging()){
            camera.zoom = sliderZoom.getValue()/zoomVal;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            oldtouch = !oldtouch;
            touchpad.setVisible(!oldtouch);

        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){{
            uistage.clear();show();}
//
//            player.setPosition(20, 20);
//            enemy.setPosition(25,25);
//            player.addVelocity(-player.velocity.x,-player.velocity.y);
//            enemy.addVelocity(-enemy.velocity.x,-enemy.velocity.y);
//            map = new TmxMapLoader().load("logical/maps/Map/level1.tmx");
//            renderer = new OrthogonalTiledMapRenderer(map, 1 / 12f);


        }
        // check input and apply to velocity & state
        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE)|| (isTouched(0.5f, 1)&&oldtouch)) && player.isGrounded()) {
            player.jump();
        }



        if (Gdx.input.isKeyJustPressed(Input.Keys.B))
            debug = !debug;

    }
    public static void removeEnemy(Enemy enemy){
        System.out.println(enemies.size);
        enemies.get(enemies.indexOf(enemy,true)).setPosition(0,0);
        enemies.get(enemies.indexOf(enemy,true)).remove();
    }
    private void spawnEnemy() {
        Enemy enemy;
        if(dire) {
            enemy = new Enemy(25,25);
        }
        else {
            enemy = new Enemy(5*25,25);
        }
        //enemy.MAX_VELOCITY= MathUtils.random(1, 8);
        enemies.add(enemy);

        stage.getRoot().addActor(enemy);
        lastEnemySpawnTime = TimeUtils.millis();
    }
    private void createTouches(){

        //touchpadskin
        Skin touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground",new Texture(Gdx.files.internal("visual/input/flatDark/flatDark_10.png")));
        touchpadSkin.add("touchKnob",new Texture(Gdx.files.internal("visual/input/flatDark/flatDark_00.png")));

        ExtentedTouchpad.TouchpadStyle touchpadStyle = new ExtentedTouchpad.TouchpadStyle();
        Drawable touchBackground = touchpadSkin.getDrawable("touchBackground");
        Drawable touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        touchpad = new ExtentedTouchpad(10, touchpadStyle);
        float touchScale = 0.2f;
        touchpad.setBounds(15,15,Gdx.graphics.getWidth()* touchScale,Gdx.graphics.getHeight()*aspectRatio* touchScale);
        touchpadStyle.knob.setMinHeight(touchpad.getHeight()*0.5f);
        touchpadStyle.knob.setMinWidth(touchpad.getWidth()*0.5f);

        touchpadR = new ExtentedTouchpad(10, touchpadStyle);
        touchpadR.setBounds(Gdx.graphics.getWidth()-Gdx.graphics.getWidth()* touchScale -15,15,Gdx.graphics.getWidth()* touchScale,Gdx.graphics.getHeight()*aspectRatio* touchScale);

        Skin textButtonSkin = new Skin();
        textButtonSkin.add("up",new Texture(Gdx.files.internal("visual/input/flatDark/but.png")));
        textButtonSkin.add("down",new Texture(Gdx.files.internal("visual/input/flatDark/but2.png")));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        Drawable up = textButtonSkin.getDrawable("up");
        Drawable down = textButtonSkin.getDrawable("down");
        textButtonStyle.up = up;
        textButtonStyle.down = down;
        textButtonStyle.font = new BitmapFont();
//        textButtonLEFT = new TextButton(" ", textButtonStyle);
//        textButtonLEFT.setBounds(Gdx.graphics.getWidth()-Gdx.graphics.getWidth()* touchScale -15,15,(Gdx.graphics.getWidth()* touchScale)/2,Gdx.graphics.getHeight()*aspectRatio* touchScale);

//        textButton.isPressed();


        float aspectScale;

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();

        int pixheight= (int) (20*aspectRatio);

        Pixmap pixmap = new Pixmap(100, (int) (pixheight/2.5f), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        sliderStyle.background = drawable;

        pixmap = new Pixmap(0, (int) (pixheight/2f), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        sliderStyle.knob = drawable;

        pixmap = new Pixmap(100, (int)(pixheight/8f), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));

        sliderStyle.knobBefore = drawable;

        sliderZoom = new Slider(0.5f,2,0.01f,false, sliderStyle);
        float zoomScale = 0.5f;
        sliderZoom.setBounds(Gdx.graphics.getWidth()/2-Gdx.graphics.getWidth()* zoomScale /2,Gdx.graphics.getHeight()-pixheight,Gdx.graphics.getWidth()* zoomScale,pixheight);//y:  Gdx.graphics.getHeight()-15-(pixheight)/2
        sliderZoom.setValue(1f*zoomVal);
        camera.zoom=sliderZoom.getValue()/zoomVal;
        pixmap.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(),Color.BLACK);
        healthLabel = new Label("null",labelStyle);
        healthLabel.setSize(player.WIDTH, Player.HEIGHT);

        healthLabel.setFontScale(Gdx.graphics.getHeight()/400f*aspectRatio);
        healthLabel.setPosition(Gdx.graphics.getHeight()/20f,Gdx.graphics.getHeight()-Gdx.graphics.getHeight()*(aspectRatio/20f), Align.left);
        healthLabel.setText("HP: "+String.valueOf(player.getHealth()));


        scoreLabel = new Label("null",labelStyle);
        scoreLabel.setSize(player.WIDTH, Player.HEIGHT*1.7f);
        scoreLabel.setFontScale(Gdx.graphics.getHeight()/400f*aspectRatio);
        scoreLabel.setPosition(Gdx.graphics.getHeight()/20f,Gdx.graphics.getHeight()-Gdx.graphics.getHeight()*(aspectRatio/20f)*2, Align.left);
        scoreLabel.setText(String.valueOf(player.score));
    }
    private void addToStages(){
//        stage.getRoot().addActor(enemy);
        stage.getRoot().addActor(player);
        //stage.getRoot().addActor(crate);
        uistage.getRoot().addActor(touchpad);
        if(textButtonLEFT!=null)uistage.getRoot().addActor(textButtonLEFT);
        uistage.getRoot().addActor(touchpadR);
        uistage.getRoot().addActor(sliderZoom);
        uistage.getRoot().addActor(healthLabel);
        uistage.getRoot().addActor(scoreLabel);
        Gdx.input.setInputProcessor(multiplexer);
//        enemy.setPosition(25, 25);
//        player.setPosition(20, 20);
    }

    private boolean isTouched(float startX, float endX) {
        // Check for touch inputs between startX and endX
        // startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
        for (int i = 0; i < 2; i++) {
            float x = Gdx.input.getX(i) / (float) Gdx.graphics.getWidth();
            if (Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
                return true;
            }
        }
        return false;
    }

    public void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                    if(tileDebug)cell.setTile(myTile);
                }
            }
        }
    }

    private void renderDebug() {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(player.position.x, player.position.y, Player.WIDTH, Player.HEIGHT);
        debugRenderer.rect(enemy.position.x, enemy.position.y, Enemy.WIDTH, Enemy.HEIGHT);

        debugRenderer.setColor(Color.YELLOW);
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
        for (int y = 0; y <= layer.getHeight(); y++) {
            for (int x = 0; x <= layer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (camera.frustum.boundsInFrustum(x + 0.5f, y + 0.5f, 0, 1, 1, 0))
                        debugRenderer.rect(x, y, 1, 1);
                }
            }
        }
        debugRenderer.end();
    }

    @Override
    public void resize(int width, int height) {

        aspectRatio = (float) Gdx.graphics.getWidth()/(float) Gdx.graphics.getHeight();
        //viewport.update(width,height);
        System.out.println("resize");
    }

    @Override
    public void resume() {
        swordSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/swordBlow.wav"));
        kickSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/Kick.ogg"));
        screamSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/man_scream.ogg"));
        spawnSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/spawn.ogg"));
        thudSound = Gdx.audio.newSound(Gdx.files.internal("visual/sounds/thud.ogg"));

        map = new TmxMapLoader().load("logical/maps/Map/leve3.tmx");
        if(player.died)
        tex = new Texture("border.png");
        super.resume();
    }

    @Override
    public void pause() {
        Gdx.app.exit();
        System.out.println("pause");
    }


    @Override
    public void dispose() {
        super.dispose();
    }
}
