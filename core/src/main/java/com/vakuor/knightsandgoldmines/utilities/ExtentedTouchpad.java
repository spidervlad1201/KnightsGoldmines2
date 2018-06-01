package com.vakuor.knightsandgoldmines.utilities;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.vakuor.knightsandgoldmines.GameLogic;

public class ExtentedTouchpad extends Touchpad {

    private float touchTime=0;

    @Override
    public void act(float delta) {
        if(isTouched()) touchTime+= GameLogic.deltaTime;
        else touchTime = 0;
        super.act(delta);
    }

    public float getTouchTime() {
        return touchTime;
    }

    public ExtentedTouchpad(float deadzoneRadius, Skin skin) {
        super(deadzoneRadius, skin);
    }

    public ExtentedTouchpad(float deadzoneRadius, Skin skin, String styleName) {
        super(deadzoneRadius, skin, styleName);
    }

    public ExtentedTouchpad(float deadzoneRadius, TouchpadStyle style) {
        super(deadzoneRadius, style);
    }


}
