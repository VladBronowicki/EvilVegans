package ca.bronowicki.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameView extends SurfaceView implements Runnable
{

    volatile boolean playing;

    private Vector2f _windowDimensions;

    private static final long SECOND = 1000;
    private static final long TARGET_FPS = 40;
    private static final long TARGET_DELTATIME = SECOND / TARGET_FPS;
    private long last;
    private float deltaTime;
    private Thread gameThread = null;

    private Entity playerEntity;

    private CopyOnWriteArrayList<Entity> entityList;
    private Entity _lastEntity;

    private byte _lastArrangement;

    private Paint paint;
    private Canvas canvas;
    private Context _context;
    private SurfaceHolder surfaceHolder;

    private Random random;

    private float timer;
    private float hunger;
    private float hungerMax;

    private boolean gameover = false;

    public GameView(Context context)
    {
        super(context);
        timer = 0;
        random = new Random();
        _context = context;
        _windowDimensions = new Vector2f(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels
        );
        hungerMax = _windowDimensions.getX() - 250;//mmm fudge
        hunger = hungerMax;
        playerEntity = new PlayerEntity(context);
        playerEntity.setImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.tiger));
        Vector2f playerSart = new Vector2f(
                _windowDimensions.getX() / 2 - (float) playerEntity.getSize().getX() / 2.0f,
                _windowDimensions.getY() - (float) playerEntity.getSize().getY() + 10.0f//Fudge
        );
        entityList = new CopyOnWriteArrayList<Entity>();
        playerEntity.getTransform().setPosition(playerSart);
        spawnRow();
        surfaceHolder = getHolder();
        paint = new Paint();
    }

    @Override
    public void run()
    {
        last = System.nanoTime();

        while (playing)
        {
            System.gc();
            //to update the frame
            update(deltaTime);

            //to doRender the frame
            render();

            //to control
            cleanup();

        }
    }

    private void update(float deltaTime)
    {
        playerEntity.update(deltaTime);
        if (entityList == null)
        {
            return;
        }

        if (_lastEntity.getTransform().getPosition().getY() > 0)
        {
            spawnRow();
        }

        for (Entity entity : entityList)
        {
            if (entity != null)
            {
                entity.update(deltaTime);
                if (entity.getTransform().getPosition().getY() > _windowDimensions.getY() - playerEntity.getSize().getY() - entity.getSize().getY())
                {
                    if (checkCollision(playerEntity, entity)){
                        entity.destroy();
                        if(entity.getClass() == MeatEntity.class){
                            hunger = (hunger+100 > hungerMax) ?  hungerMax : hunger + 100;
                        }
                        else if(entity.getClass() == VeggieEntity.class){
                            hunger-=50;
                        }else {
                            hunger-=100;
                        }
                    }
                }
                if (entity.getTransform().getPosition().getY() > _windowDimensions.getY() + entity.getSize().getY() + 50)//mmm fudge
                {
                    entity.destroy();
                }
            }
        }
    }

    private boolean checkCollision(Entity entity1, Entity entity2)
    {
        if (entity1 == null || entity2 == null || entity1 == entity2)
        {
            return false;
        }

        float dx = entity1.getTransform().getPosition().getX() - entity2.getTransform().getPosition().getX();
        float dy = entity1.getTransform().getPosition().getY() - entity2.getTransform().getPosition().getY();

        float radii = entity1.getSize().getX() / 2 + entity2.getSize().getX() / 2;
        return ((dx * dx) + (dy * dy) < radii * radii);
    }

    private void render()
    {
        //checking if surface is valid
        if (surfaceHolder.getSurface().isValid())
        {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            canvas.drawColor(Color.GREEN);
            //Debug Text
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(60);

            if(!gameover)
            {

                //Drawing the player
                canvas.drawBitmap(
                        playerEntity.getImage(),
                        playerEntity.getTransform().getPosition().getX(),
                        playerEntity.getTransform().getPosition().getY(),
                        paint);
                if (entityList != null)
                {
                    for (Entity entity : entityList)
                    {
                        if (entity != null)
                        {
                            canvas.drawBitmap(
                                    entity.getImage(),
                                    entity.getTransform().getPosition().getX(),
                                    entity.getTransform().getPosition().getY(),
                                    paint);
                        }
                    }
                }

                canvas.drawText("Score: " + (int) timer, 75, 75, paint);
                canvas.drawText("Hunger: ", 75, 150, paint);
                //Log.d("Hunger", "Value: " + hunger);
                if (hunger > 350)
                {
                    canvas.drawRect(350, 200, hunger, 50, paint);
                    hunger -= deltaTime * 20;
                } else
                {
                    gameover = true;
                    for(Entity entity : entityList){
                        entity.destroy();
                    }
                    SharedPreferences preferences = _context.getSharedPreferences("mysettings",
                        Context.MODE_PRIVATE);
                    if(timer > preferences.getInt("topScore",0))
                    {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("topScore", (int) timer);
                        editor.commit();
                    }
                }
                //Unlocking the canvas
            }
            else
            {
                canvas.drawText("Game Over: " + (int) timer, _windowDimensions.getX()/2 - 250, _windowDimensions.getY()/2, paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void cleanup()
    {

        long time = System.nanoTime();
        deltaTime = (time - last) / (1000.0f * 1000.0f * 1000.0f);//Nano to milli to second
        last = time;
        if(!gameover)
        {
            timer += deltaTime;
        }
        try
        {
            gameThread.sleep((int) TARGET_DELTATIME);
            //dispose
            for (Entity entity : entityList)
            {
                if (entity.getDispose())
                {
                    entityList.remove(entity);
                }
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void pause()
    {
        //when the game is paused
        //setting the variable to false
        playing = false;
        try
        {
            //stopping the thread
            gameThread.join();
        } catch (InterruptedException e)
        {
        }
    }

    public void resume()
    {
        //when the game is resumed
        //starting the thread again
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_UP:
                if(gameover){
                    playing = false;
                    try
                    {
                        //stopping the thread
                        gameThread.join();
                    } catch (InterruptedException e)
                    {
                    }
                    Intent i = new Intent().setClass(getContext(), MainActivity.class);
                    ((Activity) getContext()).startActivity(i);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return true;
    }

    private byte generateRowOrder(byte last)
    {

        byte[] nextRow = new byte[1];
        random.nextBytes(nextRow);
        if (nextRow[0] == 0)
        {
            return generateRowOrder(last);
        }
        byte row = nextRow[0];
        byte check = 0;
        while (row != 0)
        {
            row &= (row - 1);
            check++;
            if (check > 5)
            {
                return generateRowOrder(last);
            }
        }
        byte lastCheck = (byte) (last | nextRow[0]);
        if (lastCheck == (byte)(0xFF))
        {
            return generateRowOrder(last);
        }

        return nextRow[0];
    }

    private void spawnRow()
    {
        if(gameover) return;
        byte newArrangement = generateRowOrder(_lastArrangement);
        byte temp = 1;
        for (int i = 0; i < 8; i++)
        {
            byte t = temp;
            byte l = newArrangement;
            byte b = (byte) (t & l);
            if (b == 0)
            {
            } else
            {
                int r = random.nextInt() % 3;
                switch (r)
                {
                    case 0:
                        _lastEntity = new FruitEntity(_context);
                        _lastEntity.setImage(BitmapFactory.decodeResource(_context.getResources(), R.drawable.apple_c));
                        _lastEntity.getTransform().setPosition(new Vector2f(_lastEntity.getSize().getX() * i, -_lastEntity.getSize().getY() * 2));
                        entityList.add(_lastEntity);
                        break;
                    case 1:
                        _lastEntity = new VeggieEntity(_context);
                        _lastEntity.setImage(BitmapFactory.decodeResource(_context.getResources(), R.drawable.default_entity));

                        _lastEntity.getTransform().setPosition(new Vector2f(_lastEntity.getSize().getX() * i, -_lastEntity.getSize().getY() * 2));
                        entityList.add(_lastEntity);
                        break;
                    case 2:
                        _lastEntity = new MeatEntity(_context);
                        _lastEntity.setImage(BitmapFactory.decodeResource(_context.getResources(), R.drawable.meat2));
                        _lastEntity.getTransform().setPosition(new Vector2f(_lastEntity.getSize().getX() * i, -_lastEntity.getSize().getY() * 2));
                        entityList.add(_lastEntity);
                        break;
                }
            }
            temp = (byte) (temp << 1);
        }
        _lastArrangement = newArrangement;

    }
}
