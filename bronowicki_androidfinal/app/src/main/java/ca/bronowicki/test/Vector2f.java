package ca.bronowicki.test;

import android.support.annotation.NonNull;

class Vector2f
{
    private float _x;
    private float _y;
    public Vector2f(){
        _x=0.0f;
        _y = 0.0f;
    }

    public Vector2f(float x, float y){
        _x = x;
        _y = y;
    }

    public Vector2f(Vector2f v){
        _x=v.getX();
        _y=v.getY();
    }

    public float getX()
    {
        return _x;
    }

    public float getY()
    {
        return _y;
    }

    public void setX(float x)
    {
        this._x = x;
    }

    public void setY(float y)
    {
        this._y = y;
    }

    @NonNull
    public static Vector2f add(Vector2f v1, Vector2f v2){
        return new Vector2f(v1.getX()+v2.getX(), v1.getY()+v2.getY());
    }

    @NonNull
    public static Vector2f multiply(Vector2f v, float m){
        return new Vector2f(v.getX()*m, v.getY()*m);
    }
}
