package com.harsh.steelprofile;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.PowerManager;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.harsh.steelprofile.databases.Command;

public class DrawView extends View {
	private static int INVALID_POINTER_ID = -1;    
    
	Paint mPaint = new Paint();
	private ArrayList<Command> mCommands;
	int mWindowWidth;
	int mWindowHeight;
	
	boolean mFirstDraw;
	public Bitmap mScreenBitmap;
	Canvas mBitmapCanvas;
	
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	
	private float mPosX;
	private float mPosY;
	private float mLastTouchX;
	private float mLastTouchY;
	// The "active pointer" is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	private boolean mAnimate;
	
	int mRepeatArrayIndex = -1;
	ArrayList<Integer> mRepeatIndices = new ArrayList<Integer>();
	ArrayList<Integer> mNumRepeats = new ArrayList<Integer>();
	ArrayList<Boolean> mNumRepeatsSet = new ArrayList<Boolean>();
	int mCounter = 0;
	boolean mPenDown = true;
	float mMidX;
	float mMidY;
	float mCurrX;
	float mCurrY;
	float mEndY;
	float mEndX;
	float mAngle;
	float mT;
	float mL;
	float mA;
	float mLx;
	float mLy;
	float cenx;
	float ceny;
	float mCx=0;
	float mCy=0;
	float mSYi=0;
	float mSXi=0;
	float mIA_x=0;
	float mIA_y=0;
	float mIA_xy=0;
	Random randGenerator;
	
	boolean mShowTurtle;
	private Bitmap mTurtle;
	
	PowerManager.WakeLock mWakelock;
	
	@SuppressWarnings("static-access")
	public DrawView(Context context, ArrayList<Command> commands, int winWidth, int winHeight, boolean animate, PowerManager pm) {
		super(context);
				
		initialize();
		
		if (animate) {
			mWakelock = pm.newWakeLock(
		        pm.SCREEN_DIM_WAKE_LOCK, "Animate");
		}
		
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(0);
		mCommands = commands;
		mWindowWidth = winWidth;
		mWindowHeight = winHeight;
		
		mAnimate = animate;
		mFirstDraw = true;
		mShowTurtle = true;
		
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		
		mMidX = (float) mWindowWidth/2;
		mMidY = (float) (mWindowHeight/2) - 50;
		mCurrX = mMidX;
		mCurrY = mMidY;
		mEndY = 0;
		mEndX = 0;
		mAngle = 0;
		mPosX = -mMidX/2;
		mPosY = -mMidY/2;
				
		randGenerator = new Random();
		
		this.setDrawingCacheEnabled(true);
		
		mBitmapCanvas = new Canvas();
		mScreenBitmap = Bitmap.createBitmap(mWindowWidth, mWindowHeight,Bitmap.Config.ARGB_8888);
		mBitmapCanvas.setBitmap(mScreenBitmap);
	}
	
	@SuppressWarnings("static-access")
	public DrawView(Context context, ArrayList<Command> commands, int winWidth, int winHeight, boolean animate, Bitmap bitmap, boolean firstDraw,
			boolean showTurtle, float currX, float currY, float endX, float endY, float angle, float T, float L, float A, float lx, float ly, float cx, float cy, 
			float iSY, float iSX, float IA_y, float IA_x,float IA_xy, int paintColor, boolean penDown, int counter,
			int repeatArrayIndex, ArrayList<Integer> repeatIndices, ArrayList<Integer> numRepeats, ArrayList<Boolean> numRepeatsSet, PowerManager pm) {
		super(context);
		
		initialize();

		if (animate) {
			mWakelock = pm.newWakeLock(
		        pm.SCREEN_DIM_WAKE_LOCK, "Animate");
		}
		
		this.setDrawingCacheEnabled(true);
		randGenerator = new Random();
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		
		mShowTurtle = showTurtle;
		
		mAnimate = animate;
		
		mCommands = commands;
		mWindowWidth = winWidth;
		mWindowHeight = winHeight;
				
		mMidX = (float) mWindowWidth/2;
		mMidY = (float) (mWindowHeight/2) - 50;
		mCurrX = currX;
		mCurrY = currY;
		mEndY = endY;
		mEndX = endX;
		mAngle = angle;
		
		mT=T;
		mL=L;
		mA=A;
		mLx=lx;
		mLy=ly;
		mCx=cx;
		mCy=cy;
		mSYi=iSY;
		mSXi=iSX;
		mIA_x=IA_x;
		mIA_y=IA_y;
		mIA_xy=IA_xy;

		/* flipped for screen rotation */
		mPosX = -mMidY/2;
		mPosY = -mMidX/2;
		
		mPaint.setColor(paintColor);
		mPenDown = penDown;
		
		mCounter = counter;
		mRepeatArrayIndex = repeatArrayIndex;
		mRepeatIndices = repeatIndices;
		mNumRepeats = numRepeats;
		mNumRepeatsSet = numRepeatsSet;
		
		mFirstDraw = firstDraw;
		
		mBitmapCanvas = new Canvas();
		mScreenBitmap = bitmap;
		mBitmapCanvas.setBitmap(mScreenBitmap);
		
		
	}
	
	private void initialize() {
		mTurtle = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	}
	
	public void cleanUp() {
		mScreenBitmap.recycle();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int pointerIndex;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
        	
        	mLastTouchX = event.getX();
        	mLastTouchY = event.getY();
        	
        	// Save the ID of this pointer
            mActivePointerId = event.getPointerId(0);
            break;

        case MotionEvent.ACTION_MOVE:        
        	// Find the index of the active pointer and fetch its position
            pointerIndex = event.findPointerIndex(mActivePointerId);
            
        	final float x = event.getX();
            final float y = event.getY();
            
        	final float dx = event.getX() - mLastTouchX;
        	final float dy = event.getY() - mLastTouchY;
        	
        	mPosX += dx;
        	mPosY += dy;
        	
        	mLastTouchX = x;
        	mLastTouchY = y;
        	
        	invalidate();
        	
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            //The second finger has been placed on the screen and so we need to set the mode to ZOOM
            break;

        case MotionEvent.ACTION_UP:
            //All fingers are off the screen and so we're neither dragging nor zooming.
        	mActivePointerId = INVALID_POINTER_ID;
            break;
        case MotionEvent.ACTION_CANCEL:
        	mActivePointerId = INVALID_POINTER_ID;
        	break;
        case MotionEvent.ACTION_POINTER_UP:
            //The second finger is off the screen and so we're back to dragging.
        	
        	// Extract the index of the pointer that left the touch sensor
            pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            final int pointerId = event.getPointerId(pointerIndex);
            if (pointerId == mActivePointerId) {
                // This was our active pointer going up. Choose a new
                // active pointer and adjust accordingly.
                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                mLastTouchX = event.getX(newPointerIndex);
                mLastTouchY = event.getY(newPointerIndex);
                mActivePointerId = event.getPointerId(newPointerIndex);
            }
        	
            break;
		}
		
		mScaleDetector.onTouchEvent(event);
		
	    return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.save();
	    canvas.scale(mScaleFactor, mScaleFactor);
	    canvas.translate(mPosX, mPosY);	    
	    
	    // We're done drawing
	    if(mCounter >= mCommands.size()) {
			mFirstDraw = false;
			mShowTurtle = false;
	    }
	    
	    if (mFirstDraw) {
			
	    	if (mAnimate) {
	    		mCounter = executeCommand(mBitmapCanvas, mCounter);
	    		invalidate();
	    		mCounter++;
	    		mWakelock.acquire(3000);
	    	} else {
	    		for(mCounter = 0; mCounter < mCommands.size(); mCounter++) {
	    			mCounter = executeCommand(mBitmapCanvas, mCounter);
	    		}
	    	}
	    }
	    
	    canvas.drawBitmap(mScreenBitmap, 0, 0, mPaint);
	    
	    if(mShowTurtle && mAnimate) {
	    	canvas.drawBitmap(mTurtle, mCurrX - (mTurtle.getWidth() / 2), mCurrY - mTurtle.getHeight(), mPaint);
	    }
	    
		canvas.restore();
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        mScaleFactor *= detector.getScaleFactor();

	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 3.0f));
	        return true;
	    }
	}
	
	@SuppressWarnings("incomplete-switch")
	public int executeCommand(Canvas canvas, int counter) {
		String[] split;
		KeypadButton cmd = null;		

		split = mCommands.get(counter).get_commandString().split(" ");
		cmd = KeypadButton.fromString(split[0]);
	
		if(cmd != null) {
			switch (cmd) {
			case FD:
					mEndX = (float) (mCurrX + ( Integer.parseInt(split[1]) * FloatMath.sin(mAngle) ));
					mEndY = (float) (mCurrY + -( Integer.parseInt(split[1]) * FloatMath.cos(mAngle) ));
				
				if(mPenDown){
					if (mT == 0)
						mT = 1;
					canvas.drawLine(mCurrX, mCurrY, mEndX, mEndY, mPaint);
					cenx = 0;
					ceny = 0;
					mLx = mCurrX - mEndX;
					mLy = mCurrY - mEndY;
					cenx = (mCurrX + mEndX)/2;
					ceny = (mCurrY + mEndY)/2;
					mL = FloatMath.sqrt(mLx*mLx + mLy*mLy);
					mA += mL * mT;
					mCy += (mL * mT * ceny);
					mCx += (mL * mT * cenx);
					mSYi +=  cenx * (mL*mT);
					mSXi +=  ceny * (mL*mT);
					mIA_x += (((mEndY - mCurrY)*(mEndY - mCurrY))/12 + ceny*ceny)*(mL*mT);
					mIA_y += (((mEndX - mCurrX)*(mEndX - mCurrX))/12 + cenx*cenx)*(mL*mT);
					mIA_xy += (((mEndX - mCurrX)*(mEndY - mCurrY))/12 + cenx*ceny)*(mL*mT);
				}
				
				mCurrX = mEndX;
				mCurrY = mEndY;
				
                break;
                
			case BK:
					mEndX = (float) (mCurrX + -( Integer.parseInt(split[1]) * FloatMath.sin(mAngle) ));
					mEndY = (float) (mCurrY + ( Integer.parseInt(split[1]) * FloatMath.cos(mAngle) ));
				
				if(mPenDown){
					canvas.drawLine(mCurrX, mCurrY, mEndX, mEndY, mPaint);
					cenx = 0;
					ceny = 0;
					mLx = mCurrX - mEndX;
					mLy = mCurrY - mEndY;
					cenx = (mCurrX + mEndX)/2;
					ceny = (mCurrY + mEndY)/2;
					mL = FloatMath.sqrt(mLx*mLx + mLy*mLy);
					mA += mL * mT;
					mCy += (mL * mT * ceny);
					mCx += (mL * mT * cenx);
					mSYi +=  cenx * (mL*mT);
					mSXi +=  ceny * (mL*mT);
					mIA_x += (((mEndY - mCurrY)*(mEndY - mCurrY))/12 + ceny*ceny)*(mL*mT);
					mIA_y += (((mEndX - mCurrX)*(mEndX - mCurrX))/12 + cenx*cenx)*(mL*mT);
					mIA_xy += (((mEndX - mCurrX)*(mEndY - mCurrY))/12 + cenx*ceny)*(mL*mT);
				}
				
				mCurrX = mEndX;
				mCurrY = mEndY;
				
				break;

			case LT:
					mAngle -= ( Integer.parseInt(split[1]) * (Math.PI / 180) );
				break;

			case RT:
					mAngle += ( Integer.parseInt(split[1]) * (Math.PI / 180) );
				break;
				
			case DIR:
					mAngle = (float) ( Integer.parseInt(split[1]) * (Math.PI / 180) );
				break;
				
			case PU:
				mPenDown = false;
				break;
			
			case PD:
				mPenDown = true;
				break;
				
			case SPC:
				switch(KeypadButton.fromString(split[1])){
				case WHT:
                    mPaint.setColor(Color.WHITE);
                    break;
				case RED:
                    mPaint.setColor(Color.RED);
                    break;
				case GRN:
                    mPaint.setColor(Color.GREEN);
                    break;
				case BLU:
                    mPaint.setColor(Color.CYAN);
                    break;
				case PUR:
                    mPaint.setColor(Color.MAGENTA);
                    break;
				case YLW:
                    mPaint.setColor(Color.YELLOW);
                    break;
				}
                break;
				
			case POS:
				break;	
			
			case HOM:
				mEndX = mMidX;
				mEndY = mMidY;
				
				if(mPenDown){
					canvas.drawLine(mCurrX, mCurrY, mEndX, mEndY, mPaint);
				}
				
				mCurrX = mEndX;
				mCurrY = mEndY;
				break;
				
			case PT:
					mPaint.setStrokeWidth(Integer.parseInt(split[1]));
					mT = Integer.parseInt(split[1]);
				break;
				
			case RPT:
				mRepeatArrayIndex++;
				
				if(mNumRepeatsSet.size() < (mRepeatArrayIndex + 1)){
					mNumRepeats.add(mRepeatArrayIndex, Integer.parseInt(split[1]));
					mNumRepeatsSet.add(mRepeatArrayIndex, true);
					mRepeatIndices.add(mRepeatArrayIndex, counter);
				}
				else if(!mNumRepeatsSet.get(mRepeatArrayIndex)){
					mNumRepeats.set(mRepeatArrayIndex, Integer.parseInt(split[1]));
					mNumRepeatsSet.set(mRepeatArrayIndex, true);
					mRepeatIndices.set(mRepeatArrayIndex, counter);
				}

				if(mNumRepeats.get(mRepeatArrayIndex) > 0)
					mNumRepeats.set(mRepeatArrayIndex, (mNumRepeats.get(mRepeatArrayIndex))-1);

				break;
				
			case BRKT:
				if(mNumRepeats.get(mRepeatArrayIndex) > 0)
					counter = mRepeatIndices.get(mRepeatArrayIndex)-1;
				else
					mNumRepeatsSet.set(mRepeatArrayIndex, false);
				
				mRepeatArrayIndex--;
				break;
			}
		}
		
		return counter;
	}
}
