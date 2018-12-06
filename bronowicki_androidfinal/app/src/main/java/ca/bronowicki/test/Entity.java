package ca.bronowicki.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class Entity
{
    protected Context _context;

    public Entity(Context context){
        _context = context;
    }

    public Context getContext(){
        return _context;
    }

    abstract void initialize(Context context);
    abstract void update(float deltaTime);
    abstract void destroy();
    abstract Transform getTransform();
    abstract Vector2f getVelocity();
    abstract void setVelocity(Vector2f velocity);
    abstract Bitmap getImage();
    abstract void setImage(Bitmap image);
    abstract void setSize(Vector2i size);
    abstract Vector2i getSize();

    protected boolean dispose;

    public boolean getDispose(){
        return dispose;
    }

}
