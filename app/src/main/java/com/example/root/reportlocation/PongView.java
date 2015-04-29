package com.example.root.reportlocation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.lang.Math;import java.lang.Override;import java.util.Random;

/**
 * Created by qemu on 2/21/2015.
 */
public class PongView extends View
{
	private final Random random = new Random();

	// to communicate back to game activity
	private Handler mHandler;

	// to redraw frame every 15 milliseconds
	private DrawHandler mDrawHandler = new DrawHandler();

	// booleans that control game initialization
	// this one also defines side of the player paddle
	private boolean isStateController = false; // who does the game logic?
	private boolean isGameRunning = false;

	// game pieces
	private Ball ball;
	private Paddle paddle_player;
	private Paddle paddle_enemy;
	private Paint color;

	// game layout constants
	private float paddle_half_width;
	private float paddle_half_height;
	private float paddle_space;

	private float ball_radius;
	private float ball_speed;
	private float ball_angle;
	private float ASPECT_RATIO;

	public PongView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		color = new Paint();
		color.setColor(Constants.PAINT_COLOR);
	}

	private void newGame()
	{
		ball = new Ball(getWidth()/2, getHeight()/2);

		if (isStateController)
		{
			paddle_player = new Paddle(paddle_half_width+paddle_space, getHeight()/2);
			paddle_enemy = new Paddle(getWidth()-(paddle_half_width+paddle_space), getHeight()/2);
			ball_angle = newBallAngle();
			ball.setVel(ball_angle);
		}
		else
		{
			paddle_enemy = new Paddle(paddle_half_width+paddle_space, getHeight()/2);
			paddle_player = new Paddle(getWidth()-(paddle_half_width+paddle_space), getHeight()/2);
		}
	}

	public float newBallAngle()
	{
		int max = 63;
		int min = -max;
		return (float) ((random.nextInt((max - min) + 1) + min) * .1);
	}

	public void setConstants(Handler wHandler, boolean stateController)
	{
		mHandler = wHandler;
		isStateController = stateController;
	}

	private void setLayoutConstants()
	{
		paddle_half_height = getWidth() / Constants.PADDLE_HALFHEIGHT_FRACT;
		paddle_half_width = getWidth() / Constants.PADDLE_HALFWIDTH_FRACT;
		paddle_space = getWidth() / Constants.PADDLE_SPACE_FRACT;

		ball_radius = getWidth() / Constants.BALL_RADIUS_FRACT;
		ball_speed = ball_radius / 4;

		ASPECT_RATIO = getWidth() / getHeight();
	}

	private void checkBallCollision()
	{
		 if (ball.getLeft() <= 0
			|| ball.getRight() >= getWidth())
		{
			newGame();
			mHandler.obtainMessage(Constants.GAME_OVER).sendToTarget();
		}

		if (ball.getTop() <= 0
			|| ball.getBottom() >= getHeight())
		{
			ball.yDeflect();
		}

		if (ball.getX_vel() < 0)
		{

			if (ball.y >= paddle_player.getTop() &&
				ball.y <= paddle_player.getBottom() &&
				ball.getLeft() <= paddle_space+paddle_half_width*2 &&
				ball.getLeft() >= paddle_space)
			{
				ball.xDeflect();
			}
		}
		else
		{
			if (ball.y >= paddle_enemy.getTop() &&
				ball.y <= paddle_enemy.getBottom() &&
				ball.getRight() >= getWidth() - (paddle_space+paddle_half_width*2) &&
				ball.getRight() <= getWidth() - (paddle_space))
			{
				ball.xDeflect();
			}
		}
	}

	public void update()
	{
		if (getHeight() == 0 || getWidth() == 0)
		{
			mDrawHandler.sleep(1);
			return;
		}

		if (!isGameRunning)
		{
			setLayoutConstants();
			newGame();
			mDrawHandler.sleep(1);
			isGameRunning = true;
			return;
		}

		if (isStateController)
		{
			checkBallCollision();
			mHandler.obtainMessage(Constants.SEND_GAME_STATE).sendToTarget();
		}
		else
		{
			mHandler.obtainMessage(Constants.SEND_PADDLE).sendToTarget();
		}

		mDrawHandler.sleep(1000 / Constants.FPS);
	}

	// getters and setters
	public void setPaddle_playerVelChange(float move_val)
	{
		if (move_val > 0 && paddle_player.getBottom() >= getHeight())
		{
			paddle_player.paddleMove(0);
		}
		else if (move_val < 0 && paddle_player.getTop() <= 0)
		{
			paddle_player.paddleMove(0);
		}
		else
		{
			paddle_player.paddleMove(move_val);
		}
	}

	public void setPaddle_enemyPos(float y_val)
	{
		paddle_enemy.setY(y_val * getHeight());
	}

	public void setBallXRelative(float x_val)
	{
		ball.setX(x_val * getWidth());
	}

	public void setBallYRelative(float y_val)
	{
		ball.setY(y_val * getHeight());
	}

	public void setBallAngle(float angle)
	{
		ball_angle = angle;
		ball.setVel(angle);
	}

	public float getPaddle_playerRelativePos()
	{
		return paddle_player.getY() / getHeight();
	}

	public float getBall_XRelativePos()
	{
		return ball.getX() / getWidth();
	}

	public float getBall_YRelativePos()
	{
		return ball.getY() / getHeight();
	}

	public float getBallAngle()
	{
		return ball_angle;
	}

	public void gameOver()
	{
		newGame();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (!isGameRunning)
		{
			return;
		}

		ball.draw(canvas);
		paddle_enemy.draw(canvas);
		paddle_player.draw(canvas);
	}

	private class DrawHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			PongView.this.update();
			PongView.this.invalidate();
		}

		public void sleep(long time)
		{
			removeMessages(0);
			sendMessageDelayed(obtainMessage(0), time);
		}
	}

	private class Ball
	{
		// x and y coords
		private float x;
		private float y;

		// amount balls move along x and y every frame
		private float x_vel;
		private float y_vel;

		public Ball(float x_, float y_)
		{
			x = x_;
			y = y_;
			x_vel = 0;
			y_vel = 0;
		}

		public void setVel(float ang)
		{
			x_vel = (float) Math.cos(ang) * ball_speed * ASPECT_RATIO;
			y_vel = (float) Math.sin(ang) * ball_speed * ASPECT_RATIO;
		}

		public void setX(float x_val)
		{
			x = x_val;
		}

		public void setY(float y_val)
		{
			y = y_val;
		}

		public float getX()
		{
			return x;
		}

		public float getY()
		{
			return y;
		}

		public float getX_vel()
		{
			return x_vel;
		}

		public float getY_vel()
		{
			return y_vel;
		}

		public float getLeft()
		{
			return x-ball_radius;
		}

		public float getRight()
		{
			return x+ball_radius;
		}

		public float getTop()
		{
			return y-ball_radius;
		}

		public float getBottom()
		{
			return y+ball_radius;
		}

		// when bounces off ceiling/floor
		public void yDeflect()
		{
			y_vel = -y_vel;
		}

		// when bounces off paddle
		public void xDeflect()
		{
			//speed_mult += .1;
			//x_vel *= speed_mult;
			x_vel = -x_vel;
		}

		public void draw(Canvas screen)
		{
			screen.drawCircle(x, y, ball_radius, color);
			x += x_vel;
			y += y_vel;
		}
	}

	private class Paddle
	{
		private float x;
		private float y;
		private float paddle_vel;

		public Paddle(float pos1, float pos2 )
		{
			x = pos1;
			y = pos2;
			paddle_vel = 0;
		}

		public void setY(float newY)
		{
			y = newY;
		}

		public void paddleMove(float tilt_val)
		{
			paddle_vel = tilt_val;
		}

		public float getY()
		{
			return y;
		}

		public float getX()
		{
			return x;
		}

		public float getBottom()
		{
			return y + paddle_half_height;
		}

		public float getTop()
		{
			return y - paddle_half_height;
		}

		public float getLeft()
		{
			return x - paddle_half_width;
		}

		public float getRight()
		{
			return x + paddle_half_width;
		}

		public void draw(Canvas screen)
		{
			screen.drawRect(getLeft(),
				getTop(),
				getRight(),
				getBottom(),
				color);

			y += paddle_vel;
		}
	}
}
