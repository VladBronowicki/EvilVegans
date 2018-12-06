package ca.bronowicki.test;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

public class MeatEntity extends Entity
{

    private Vector2f _windowDimensions;
    private Vector2f _velocity;
    private Transform _transform;
    private final float SPEED = 100.0f;
    private Bitmap _bitmap;

    public MeatEntity(Context context){

        super(context);
        _context = context;
        initialize(context);
    }

    @Override
    void initialize(Context context)
    {
        _windowDimensions = new Vector2f(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels
        );
        _windowDimensions = new Vector2f(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels
        );
        _velocity = new Vector2f(0,150.0f);
        _transform = new Transform();
        _context = context;
    }

    @Override
    void update(float deltaTime)
    {
        Vector2f newPosition = new Vector2f(
                _transform.getPosition().getX() + (_velocity.getX() * deltaTime),
                _transform.getPosition().getY() + (_velocity.getY() * deltaTime));
        _transform.setPosition(newPosition);
    }

    @Override
    void destroy()
    {
        dispose = true;
    }

    @Override
    public Transform getTransform()
    {
        return _transform;
    }

    @Override
    public Vector2f getVelocity()
    {
        return _velocity;
    }

    @Override
    public void setVelocity(Vector2f velocity)
    {
        _velocity = new Vector2f(velocity);
    }

    @Override
    public Bitmap getImage()
    {
        return _bitmap;
    }

    @Override
    public void setImage(Bitmap image)
    {
        _bitmap = Bitmap.createScaledBitmap(image, (int)(_windowDimensions.getX()/8), (int)(_windowDimensions.getX()/8), false);
    }

    @Override
    public void setSize(Vector2i size)
    {
        _bitmap = Bitmap.createScaledBitmap(_bitmap, size.getX(), size.getY(), false);
    }

    @Override
    public Vector2i getSize()
    {
        return new Vector2i(_bitmap.getWidth(), _bitmap.getHeight());
    }

}
