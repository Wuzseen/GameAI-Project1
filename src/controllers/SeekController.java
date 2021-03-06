package controllers;

import engine.Car;
import utility.*;
import engine.Game;
import engine.GameObject;

public class SeekController extends Controller {
	GameObject _tar;
	double steer = 0;
	double throttle = 0;
	double brake = 0;
	
	double steerVal = 5;
	double maxThrottle = 2;
	double maxAcceleration = 200;
	double distForMax = 200;
	double breakDistance = 20;
	
	double currentAlpha = 0;
	double targetAlpha = 0;
	
	double lastDelta = 0;
	
	public SeekController(GameObject target)
	{
		this._tar = target;
	}
	
	Vector2 Seek(GameObject character, GameObject E) {
        Vector2 thisPosition = character.position();
        Vector2 targetPosition = E.position();
        Vector2 D = targetPosition.subtract(thisPosition);
        Vector2 dnorm = D.normal(); // direction we want to steer towards
        
        return dnorm.scalarMultiply(maxAcceleration * MathUtil.InverseLerp(targetPosition.distance(thisPosition), 0, distForMax));
	}
	
    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        Vector2 seek = Seek(subject, this._tar); // A
        
        double distance = subject.position().distance(this._tar.position());
        if(distance < breakDistance) {
        	this.brake += .1 * delta_t;
        	this.throttle = 0;
        } else if(distance >= breakDistance) {
        	this.throttle = maxThrottle * MathUtil.InverseLerp(distance, 0, this.distForMax);
        }
        
        // Convert Rad 2 Deg for mental help!
        targetAlpha = seek.alpha() * 180 / Math.PI;
        currentAlpha = subject.getAngle() * 180 / Math.PI;
        
        double alphaDelta = targetAlpha - currentAlpha; // if 0 -> moving in correction direction
        if(alphaDelta < -180) {
        	alphaDelta += 360;
        }
        if(alphaDelta > 180) {
        	alphaDelta -= 360;
        }
        if(Math.abs(alphaDelta) < 1) { // close enough...
        	this.steer = 0;
        }
        else if(alphaDelta > 0) {
        	if(this.steer < 0) {
        		this.steer = 0;
        	}
        	this.steer += steerVal * delta_t;
        }
        else {
        	if(this.steer > 0) {
        		this.steer = 0;
        	}
        	this.steer -= steerVal * delta_t;
        }
        
        lastDelta = alphaDelta;
        controlVariables[VARIABLE_STEERING] = this.steer;
        controlVariables[VARIABLE_THROTTLE] = this.throttle;
        controlVariables[VARIABLE_BRAKE] = this.brake;
    }
}
