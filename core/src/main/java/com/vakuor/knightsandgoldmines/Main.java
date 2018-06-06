package com.vakuor.knightsandgoldmines;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vakuor.knightsandgoldmines.view.Menu;

public class Main extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public AssetManager manager;
    public static int fps;
    private int frames;
    public static float aspectRatio;

    long javaHeap;//отладка
    long nativeHeap;//отладка

    @Override
    public void create () {
        System.out.println("Main.create\n");
        fps = 60;
        manager = new AssetManager();
        this.setScreen(new Menu(this));
    }

    @Override
    public void render () {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
        super.render();
        frames = Gdx.graphics.getFramesPerSecond();
        if(frames>0 && frames < Integer.MAX_VALUE)
            fps = frames;
        javaHeap = Gdx.app.getJavaHeap();//потребление памяти Java//отладка
        nativeHeap = Gdx.app.getNativeHeap();//нативной heap памяти//отладка
    }
    @Override
    public void dispose () {

    }
}
