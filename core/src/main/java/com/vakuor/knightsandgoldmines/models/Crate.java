package com.vakuor.knightsandgoldmines.models;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.vakuor.knightsandgoldmines.view.Game;

import static com.vakuor.knightsandgoldmines.view.Game.deltaTime;
import static com.vakuor.knightsandgoldmines.view.Game.myTile;
import static com.vakuor.knightsandgoldmines.view.Game.tileDebug;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class Crate extends Actor {
    public static float WIDTH;
    public static float HEIGHT;
    public static float MAX_VELOCITY = 5f;
    public static float DAMPING = 0.87f;
    public static float thisGRAVITY = -1.5f;

    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();

    public static float hitconst = 20f;

    private boolean grounded = false;
    private boolean died = false;

    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private Array<Rectangle> tiles = new Array<Rectangle>();
    private int startX, startY, endX, endY;


    private TextureAtlas crateTextureAtlas;
    private Animation<TextureRegion> fall;
    private Array<TextureAtlas.AtlasRegion> fallframes;

    public Crate(int x, int y){
        setPosition(x,y);
        crateTextureAtlas = new TextureAtlas("visual/output/Crate/Crate.atlas");
        fallframes =  crateTextureAtlas.findRegions("crate");

        fall = new Animation<TextureRegion>(0, fallframes);

        WIDTH = 1 / 16f * fallframes.get(0).getRegionWidth();
        HEIGHT = 1 / 16f * fallframes.get(0).getRegionHeight();
        velocity.y = thisGRAVITY;

    }

    public void update(){

        //addVelocity(0, thisGRAVITY);
        // multiply by delta time so we know how far we go in this frame
        velocity.scl(deltaTime);
        position.x+=0.25f; position.y+=0.2f;
        WIDTH-=0.5f; HEIGHT-=0.4f;
        // perform collision detection & response, on each axis, separately//todo:вынести эту inere отсюда
        // if the player is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle playerRect = rectPool.obtain();
        playerRect.set( position.x,  position.y,  WIDTH,  HEIGHT);
        if ( velocity.x > 0) {
            startX = endX = (int) ( position.x +  WIDTH +  velocity.x); }
        else {
            startX = endX = (int) ( position.x +  velocity.x);
        }

        startY = (int) (position.y);
        endY = (int) (position.y + HEIGHT);
        getTiles(startX, startY, endX, endY, tiles, tileDebug, myTile);
        playerRect.x += velocity.x;//good without deltaTime
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                if(isGrounded()){
                    if(velocity.x<0)
                        position.x = tile.x + tile.width;
                    else position.x=tile.x-WIDTH;
                }
                velocity.x = 0;
                break;
            }
        }
        playerRect.x = position.x;
        // if the player is moving upwards, check the tiles to the top of its
        // top bounding box edge, otherwise check the ones to the bottom
        if (velocity.y > 0) {
            startY = endY = (int) (position.y + HEIGHT + velocity.y); }
        else {
            startY = endY = (int) (position.y + velocity.y);
        }
        startX = (int) (position.x);
        endX = (int) (position.x + WIDTH);
        getTiles(startX, startY, endX, endY, tiles, tileDebug, myTile);
        playerRect.y += velocity.y;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                // we actually reset the player y-position here
                // so it is just below/above the tile we collided with
                // this removes bouncing :)
                if (velocity.y > 0) {
                    position.y = tile.y - HEIGHT;
                    // we hit a block jumping upwards, let's destroy it!
                    TiledMapTileLayer layer = (TiledMapTileLayer) Game.map.getLayers().get("walls");
                    layer.setCell((int) tile.x, (int) tile.y, null);
                } else {
                    position.y = tile.y + tile.height;
                    died=true;// if we hit the ground, mark us as grounded so we can jump
                }
                velocity.y = 0; break;
            }
        }
        rectPool.free(playerRect);

        // unscale the velocity by the inverse delta time and set
        // the latest position
        position.x-=0.25f; position.y-=0.2f;
        WIDTH+=0.5f; HEIGHT+=0.4f;
        position.add(velocity);
        velocity.scl(1 / Game.deltaTime);
        // Apply damping to the velocity on the x-axis so we don't
        // walk infinitely once a key was pressed
        velocity.x *= DAMPING;
    }




    @Override
    public void act(float deltaTime) {
        if(!died) {
            // If the velocity is < 1, set it to 0 and set state to Standing
            update();
            super.act(deltaTime);
        }
        else System.out.println("CRATEDIED");
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {

        TextureRegion frame = null;

        frame = fall.getKeyFrame(0);

        batch.draw(frame,  position.x-WIDTH/2,  position.y-HEIGHT/2+0.2f, WIDTH*2, HEIGHT*2);


    }
    public void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles, boolean tileDebug, TiledMapTile myTile) {
        TiledMapTileLayer layer = (TiledMapTileLayer) Game.map.getLayers().get("walls");
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

    public void hit(boolean mydir){
        System.out.println("GOCrate");
        died = true;
        //GameLogic.healthLabel.setText(String.valueOf(health));//todo:заменить на enemylabel? (nezya tam iterator, mnogo vragov) // мб лабель над врагами!
    }

    public void addVelocity(float x, float y){//todo: поправить addV о ввсех (нет оси Y и значение max levoe)
        velocity.add(x, y);
        velocity.x = MathUtils.clamp(velocity.x, -MAX_VELOCITY, MAX_VELOCITY);
    }
    public boolean isGrounded(){
        return grounded;
    }
    public void setPosition(float x,float y){
        position.set(x,y);
    }
    public Vector2 getPosition(){
        return position;
    }
}
