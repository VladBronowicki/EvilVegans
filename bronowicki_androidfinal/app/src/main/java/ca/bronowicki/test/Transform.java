package ca.bronowicki.test;

class Transform
{
    private Vector2f _position;
    private Vector2f _rotation;
    private Vector2f _scale;

    public Transform (){
        _position = new Vector2f();
        _rotation = new Vector2f();
        _scale = new Vector2f();
    }

    public Vector2f getPosition()
    {
        return _position;
    }

    public void setPosition(Vector2f position)
    {
        this._position = position;
    }

    public Vector2f getRotation()
    {
        return _rotation;
    }

    public void setRotation(Vector2f rotation)
    {
        this._rotation = rotation;
    }

    public Vector2f getScale()
    {
        return _scale;
    }

    public void setScale(Vector2f scale)
    {
        this._scale = scale;
    }
}
