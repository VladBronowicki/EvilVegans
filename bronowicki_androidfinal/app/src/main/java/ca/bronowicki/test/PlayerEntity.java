package ca.bronowicki.test;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

public class PlayerEntity extends Entity implements SensorEventListener
{
    private Vector2f _windowDimensions;
    private Vector2f _velocity;
    private Transform _transform;
    private final float SPEED = 100.0f;
    private Bitmap _bitmap;
    private SensorManager _sensorManager;
    private Sensor _sensorAccelerometer;

    private float[] _accelerometerData = new float[3];


    public PlayerEntity(Context context)
    {
        super(context);
        initialize(context);
    }

    @Override
    public void initialize(Context context)
    {
        _windowDimensions = new Vector2f(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels
        );
        _context = context;
        _velocity = new Vector2f();
        _transform = new Transform();
        if (context == null) return;
        _sensorManager = (SensorManager) _context.getSystemService(
                Context.SENSOR_SERVICE);
        _sensorManager.registerListener(this,
                _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        _sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        _sensorAccelerometer = _sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void update(float deltaTime)
    {
        Vector2f newPosition = new Vector2f(
                _transform.getPosition().getX() + (_velocity.getX() * deltaTime),
                _transform.getPosition().getY() + (_velocity.getY() * deltaTime));
        _transform.setPosition(newPosition);
    }

    @Override
    public void destroy()
    {
        if (_sensorAccelerometer != null)
        {
            _sensorManager.registerListener(this, _sensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
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
        _bitmap = Bitmap.createScaledBitmap(image, (int)(_windowDimensions.getX()/9), (int)(_windowDimensions.getX()/9), false);
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


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        int sensorType = event.sensor.getType();
        switch (sensorType)
        {
            case Sensor.TYPE_ACCELEROMETER:
                _accelerometerData = event.values.clone();
                break;
            default:
                return;
        }
        if (getTransform().getPosition().getX() >= _windowDimensions.getX() - _bitmap.getWidth() && getVelocity().getX() >= 0)
        {
            getTransform().getPosition().setX(_windowDimensions.getX() - _bitmap.getWidth());
            getVelocity().setX(0);
        }
        if (getTransform().getPosition().getX() <= 0 && getVelocity().getX() <= 0)
        {
            getTransform().getPosition().setX(0);
            getVelocity().setX(0);
        }
        if(getTransform().getPosition().getX() < _windowDimensions.getX() - _bitmap.getWidth() || getTransform().getPosition().getX() > 0)
        {
            getVelocity().setX(_accelerometerData[0] * -SPEED);
            getVelocity().setY(0.0f);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
