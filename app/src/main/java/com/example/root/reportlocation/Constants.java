package com.example.root.reportlocation;

import java.lang.String; /**
 * Created by qemu on 2/22/2015.
 */
public interface Constants
{
	// Game Activity Intent
	public final int DO_NOT_CONNECT = 0;
	public final int LISTEN_FOR_CONNECT = 1;
	public final int CONNECT_TO_HOST = 2;
	public final String CONNECT_TYPE = "CONNECT_TYPE";
	public final int GET_MAC_ADDR = 3;

	// device list activity
	public final String DEVICE_MAC = "DEVICE_MAC";

	// handler messages - reading
	public final int DISCONNECTED = 0;
	public final int CONNECTED = 1;
	public final int READY = 'r';
	public final int BALLX_READ = 'x';
	public final int BALLY_READ = 'y';
	public final int BALL_ANG_READ = 'a';
	public final int PADDLE_READ = 'p';
	public final int GAME_OVER_READ = 'g';

	// handler messages - writing
	public final int GAME_OVER = -1;
	public final int SEND_GAME_STATE = 0;
	public final int SEND_PADDLE = 1;
	public final int SEND_BALL_ANGLE = 2;

	// GAME LAYOUT CONSTANTS
	public static final float PADDLE_SPACE_FRACT = 100;
	public static final float PADDLE_HALFWIDTH_FRACT = 100;
	public static final float PADDLE_HALFHEIGHT_FRACT = 8;
	public static final float BALL_RADIUS_FRACT = 100;
	public static final int PAINT_COLOR = 0xff666666; // grey

	// GAME FRAMES PER SECOND
	public static final int FPS = 60;

	// GAME OTHER
	public static final int PADDLE_MOVE_ACCEL = 2;
}
