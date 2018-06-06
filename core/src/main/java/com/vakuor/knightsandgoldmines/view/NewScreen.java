package com.vakuor.knightsandgoldmines.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;

public class NewScreen extends InputAdapter implements Screen {
    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.app.exit();
    }

    @Override
    public void dispose() {

    }
}
