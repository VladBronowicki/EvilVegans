package ca.bronowicki.test;

import android.support.annotation.NonNull;

class Vector2i
{
    private int _x;
    private int _y;
    public Vector2i(){
        _x = _y = 0;
    }

    public Vector2i(int x, int y){
        _x = x;
        _y = y;
    }

    public Vector2i(Vector2i v){
        _x=v.getX();
        _y=v.getY();
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public void setX(int x)
    {
        this._x = x;
    }

    public void setY(int y)
    {
        this._y = y;
    }

    @NonNull
    public static Vector2f add(Vector2i v1, Vector2i v2){
        return new Vector2f(v1.getX()+v2.getX(), v1.getY()+v2.getY());
    }

}
