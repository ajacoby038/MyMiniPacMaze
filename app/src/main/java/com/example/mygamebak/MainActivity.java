package com.example.mygamebak;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

class MyOnTouchListener implements Button.OnTouchListener {
    DIRECTION direction;
    MainActivity activity;
    ArrowTimer arrowTimer;

    MyOnTouchListener(MainActivity a, DIRECTION d) {
        this.activity = a;
        this.direction = d;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // Get dimensions of screen
        // Get (x,y) of pacman
        // Calculate the max timer value by getting a difference between pacman and border
        // start countdown timer for that amount
        //
        switch (this.direction) {
            case DOWN:
                this.activity.pacman.setImageResource(R.drawable.pacmandown);
                break;
            case UP:
                this.activity.pacman.setImageResource(R.drawable.pacmanup);
                break;
            case LEFT:
                this.activity.pacman.setImageResource(R.drawable.pacmanleft);
                break;
            case RIGHT:
                this.activity.pacman.setImageResource(R.drawable.pacman);
                break;
            default:
                break;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Add logic to calculate the parameters for timer
            this.arrowTimer= new ArrowTimer(50000, 100, direction);
            this.arrowTimer.setTimerListener(this.activity.myEventListener);
            this.arrowTimer.start();
//            Log.i("info", one);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            this.arrowTimer.cancel();
            this.activity.pacman.animate().translationYBy(0);
            this.activity.pacman.animate().translationXBy(0);
            if (this.activity.CheckCollision()) {
                this.activity.bounceBack(this.direction);
            }
        }
        return true;
    }
}

interface  MyEventListener {
    boolean onTick(DIRECTION d);
}


class ArrowTimer extends CountDownTimer {
    private DIRECTION direction;
    private MyEventListener myEventListener;


    ArrowTimer(long millisInFuture, long countDownInterval, DIRECTION d) {
        super(millisInFuture, countDownInterval);
        this.direction = d;
    }
    void setTimerListener(MyEventListener l) {
        myEventListener = l;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        boolean stop_me;
        if (myEventListener.onTick(direction))
            stop_me = true;
        else
            stop_me = false;
        if (stop_me) {
            this.cancel();
        }
    }

    @Override
    public void onFinish() {

    }
}

public class MainActivity extends AppCompatActivity {

    ConstraintLayout startScreen;
    ConstraintLayout gameScreen;
    ConstraintLayout loseScreen;
    ConstraintLayout winScreen;
    Button rightArrow;
    Button leftArrow;
    Button upArrow;
    Button downArrow;
    ImageView pacman;
    ImageView exitWall;
    ImageView[] dots;
    GhostAnimation[] ghosts;
    ImageView[] walls;
    TextView title;
    Button startButton;
    MediaPlayer loseSound;
    MediaPlayer winSound;
    MediaPlayer eatSound;
    MyEventListener myEventListener;

    int dots_remaining;
    boolean ready_to_win;

    static final int STEP = 130;
    static final int NUM_DOTS = 22;
    static final int NUM_WALLS = 26;
    static final int NUM_GHOSTS = 15;

    float[] initialposition;


    private class GhostAnimation {
        ObjectAnimator translateAnimation;
        ImageView resId;
        int duration;
        float distance;
        String direction;

        public GhostAnimation(int duration, float distance, String direction) {
            this.duration = duration;
            this.distance = distance;
            this.direction = direction;
        }

        public GhostAnimation(ImageView resId) {
            this.resId = resId;
        }

        public void initGhostAnimation(int duration, float distance, String direction) {
            this.duration = duration;
            this.distance = distance;
            this.direction = direction;
//            this.translateAnimation = ObjectAnimator.ofFloat(ghosts[0].resId, direction, 0f, distance);
            this.translateAnimation = ObjectAnimator.ofFloat(resId, direction, 0f, distance);
        }

    }
//    public void moveGhost(View view) {
//
//        ghost.animate().translationYBy(700).setDuration(3000);
//
//    }

    void initGhostAnimations() {
        ghosts[0].initGhostAnimation(2000, 270f, "translationY");
        ghosts[1].initGhostAnimation(5000, 1050f, "translationX");
        ghosts[2].initGhostAnimation(3000, 120f, "translationY");
        ghosts[3].initGhostAnimation(3000, -710f, "translationX");
        ghosts[4].initGhostAnimation(5000, 1450f, "translationX");
        ghosts[5].initGhostAnimation(3000, -120f, "translationY");
        ghosts[6].initGhostAnimation(3000, 130f, "translationY");
        ghosts[7].initGhostAnimation(3000, -280f, "translationY");
        ghosts[8].initGhostAnimation(3000, 600f, "translationX");
        ghosts[9].initGhostAnimation(3000, -600f, "translationX");
        ghosts[10].initGhostAnimation(4000, -330f, "translationX");
        ghosts[11].initGhostAnimation(4000, 550f, "translationY");
        ghosts[12].initGhostAnimation(3000, 270f, "translationY");
        ghosts[13].initGhostAnimation(3000, -270f, "translationY");
        ghosts[14].initGhostAnimation(3000, 600f, "translationX");
//        ghosts[15].initGhostAnimation(3000, -600f, "translationX");
//        for (GhostAnimation ghost: ghosts) {
//            ghost.initGhostAnimation(3000, 700f, "translationY");
//        }
    }

    ImageView[] getDots() {
        int[] res = new int[] { R.id.d1, R.id.d2, R.id.d3, R.id.d4, R.id.d5, R.id.d6, R.id.d7, R.id.d8, R.id.d9, R.id.d10, R.id.d11, R.id.d12, R.id.d13, R.id.d14, R.id.d15, R.id.d16, R.id.d17, R.id.d18, R.id.d19, R.id.d20, R.id.d21, R.id.d22 };
        ImageView[] dots = new ImageView[NUM_DOTS];
        for (int i = 0; i < NUM_DOTS; i++ ) {
            dots[i] = (ImageView) findViewById(res[i]);
        }
        return dots;
    }

    ImageView[] getWalls() {
        int[] res = new int[] { R.id.w1, R.id.w2, R.id.w3, R.id.w4, R.id.w5, R.id.w6, R.id.w7, R.id.w8, R.id.w9, R.id.w10, R.id.w11, R.id.w12, R.id.w13, R.id.w14, R.id.w15, R.id.w16, R.id.w17, R.id.w18, R.id.w19, R.id.w20, R.id.w21, R.id.w22, R.id.w24, R.id.w25, R.id.w26, R.id.w27};
        ImageView[] walls = new ImageView[NUM_WALLS];
        for (int i = 0; i < NUM_WALLS; i++ ) {
            walls[i] = (ImageView) findViewById(res[i]);
        }
        return walls;
    }

//    ImageView[] getGhosts() {
//        int[] res = new int[] { R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6, R.id.g7, R.id.g8, R.id.g9, R.id.g10, R.id.g11, R.id.g12, R.id.g13, R.id.g14, R.id.g15, R.id.g16};
//        ImageView[] ghosts = new ImageView[NUM_GHOSTS];
//        for (int i = 0; i < NUM_GHOSTS; i++ ) {
//            ghosts[i] = (ImageView) findViewById(res[i]);
//        }
//        return ghosts;
//    }

    GhostAnimation[] getGhosts() {
        int[] res = new int[] { R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6, R.id.g7, R.id.g8, R.id.g9, R.id.g10, R.id.g11, R.id.g12, R.id.g13, R.id.g14, R.id.g15};
        GhostAnimation[] ghosts = new GhostAnimation[NUM_GHOSTS];
        for (int i = 0; i < NUM_GHOSTS; i++ ) {
            ghosts[i] = new GhostAnimation((ImageView) findViewById(res[i]));
        }
        return ghosts;
    }

    void bounceBack(DIRECTION d) {
        boolean moveX = true;
        int reverse = -1;

        switch (d) {
            case DOWN:
                moveX = false;
                break;
            case UP:
                moveX = false;
                reverse = 1;
                break;
            case LEFT:
                reverse = 1;
                break;
            case RIGHT:
                break;
            default:
                break;
        }
        final int step = reverse * 20;
        if (moveX) {
            pacman.animate().translationXBy(step);

        } else {
            pacman.animate().translationYBy(step);
        }
    }

    public boolean CheckCollision() {
        // Location holder
        final int[] loc = new int[2];

        pacman.getLocationInWindow(loc);
        final Rect rc1 = new Rect(loc[0], loc[1],
                loc[0] + pacman.getWidth(), loc[1] + pacman.getHeight());

        for (ImageView dot: dots) {
            dot.getLocationInWindow(loc);
            final Rect rc2 = new Rect(loc[0], loc[1],loc[0] + dot.getWidth(), loc[1] + dot.getHeight());
            if (Rect.intersects(rc1,rc2)) {
                if (dot.getVisibility() == View.VISIBLE) {
                    Log.i("info", "collide:");
                    eatSound.start();
                    dots_remaining--;
                    Log.i("info", "dots remaining = " + dots_remaining);
                    dot.setVisibility(View.INVISIBLE);

                    if (dots_remaining == 0 && exitWall.getVisibility() == View.INVISIBLE) {
//                        exitWall.setScaleX(0.01f);
//                        exitWall.setScaleY(0.01f);
//                        exitWall.setVisibility(View.VISIBLE);
//                        exitWall.animate().scaleX(1f).scaleY(1f).setDuration(2000);
                        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
                        animation.setDuration(1000); //1 second duration for each animation cycle
                        animation.setInterpolator(new LinearInterpolator());
                        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
                        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
                        exitWall.startAnimation(animation); //to start animation
                        exitWall.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        exitWall.getLocationInWindow(loc);
        final Rect rc2 = new Rect(loc[0], loc[1],loc[0] + exitWall.getWidth(), loc[1] + exitWall.getHeight());
        if (Rect.intersects(rc1,rc2)) {
            Log.i("info", "at Exit:");
            if (exitWall.getVisibility() == View.VISIBLE) {
                ready_to_win = true;
            }
        }

        for(ImageView wall: walls) {
            wall.getLocationInWindow(loc);
            final Rect rc3 = new Rect(loc[0], loc[1],loc[0] + wall.getWidth(), loc[1] + wall.getHeight());

            if (Rect.intersects(rc1,rc3)) {
                return true;
            }
        }

        return false;
    }

    void startGhosts() {
        for (GhostAnimation ghost: ghosts) {
            ghost.translateAnimation.setRepeatCount(ValueAnimator.INFINITE);
            ghost.translateAnimation.setRepeatMode(ValueAnimator.REVERSE);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(ghost.duration);
            set.play(ghost.translateAnimation);
            set.start();

            ghost.translateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    boolean lost = false;
                    final int[] loc = new int[2];
                    pacman.getLocationInWindow(loc);
                    final Rect p = new Rect(loc[0], loc[1],
                            loc[0] + pacman.getWidth(), loc[1] + pacman.getHeight());
                    for (GhostAnimation ghost: ghosts) {
                        ghost.resId.getLocationOnScreen(loc);
                        final Rect g = new Rect(loc[0], loc[1], loc[0] + ghost.resId.getWidth(), loc[1] + ghost.resId.getHeight());

                        if (Rect.intersects(p, g)) {
                            lost = true;
                        }
                    }
                    if (lost) {
                        for (GhostAnimation ghost: ghosts) {
                            ghost.translateAnimation.setRepeatCount(0);
                        }
                    }
                     if (lost) {
                        pacman.setVisibility(View.INVISIBLE);
                        gameScreen.setVisibility(View.INVISIBLE);
                        loseScreen.setScaleX(0.01f);
                        loseScreen.setScaleY(0.01f);
                        loseScreen.setVisibility(View.VISIBLE);
                        loseScreen.animate().scaleX(1f).scaleY(1f).setDuration(2000);
                        loseSound.start();
                    }
                }
            });
        }
    }

    void doWin() {
        pacman.animate().translationXBy(STEP*100);
        for (GhostAnimation ghost: ghosts) {
//            ghost.translateAnimation.setRepeatCount(0);
        }
        gameScreen.setVisibility(View.INVISIBLE);
        winScreen.setVisibility(View.VISIBLE);
        winScreen.setScaleX(0.05f);
        winScreen.setScaleY(0.05f);
        winScreen.animate().scaleX(1f).scaleY(1f).rotation(3600).setDuration(2000);
        winSound.start();
    }

    public void start(View view) {

        initialposition = new float[2];
        initialposition[0] = pacman.getX();
        initialposition[1] = pacman.getY();
        startGhosts();
        retry(view);
    }

    public void retry(View view) {

        loseScreen.setVisibility(View.INVISIBLE);
        loseScreen.setScaleX(0.01f);
        loseScreen.setScaleY(0.01f);

        winScreen.setVisibility(View.INVISIBLE);
        winScreen.setScaleX(0.01f);
        winScreen.setScaleY(0.01f);

        startScreen.setVisibility(View.INVISIBLE);
        gameScreen.setVisibility(View.VISIBLE);
        exitWall.setVisibility(View.INVISIBLE);
        exitWall.clearAnimation();

//        dots_remaining = NUM_DOTS;
        dots_remaining = 1;
        ready_to_win = false;

        pacman.setX(initialposition[0]);
        pacman.setY(initialposition[1]);
        pacman.setImageResource(R.drawable.pacman);
        pacman.setVisibility(View.VISIBLE);

        for (ImageView dot: dots) {
            dot.setVisibility(View.INVISIBLE);
        }

        dots[20].setVisibility(View.VISIBLE);

        for (GhostAnimation ghost: ghosts) {
            ghost.translateAnimation.setRepeatCount(ValueAnimator.INFINITE);
            ghost.translateAnimation.start();
        }

        Log.i("info", " Pacman: " + this.pacman.getX() + " Y: " + this.pacman.getY());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getSupportActionBar().hide();

        startScreen = findViewById(R.id.startScreen);
        gameScreen = findViewById(R.id.gameScreen);
        loseScreen = findViewById(R.id.loseScreen);
        winScreen = findViewById(R.id.winScreen);
        rightArrow = findViewById(R.id.rightArrow);
        leftArrow = findViewById(R.id.leftArrow);
        upArrow = findViewById(R.id.upArrow);
        downArrow = findViewById(R.id.downArrow);
        pacman = findViewById(R.id.pacman);
        dots = getDots();
        ghosts = getGhosts();
        walls = getWalls();
        exitWall = findViewById(R.id.exitWall);



        loseSound = MediaPlayer.create(this, R.raw.whawhawhasoundeffect);
        winSound = MediaPlayer.create(this, R.raw.fanfare);
        eatSound = MediaPlayer.create(this, R.raw.eat);
        title = findViewById(R.id.title);
        startButton = findViewById(R.id.startButton);
        dots_remaining = NUM_DOTS;
        ready_to_win = false;

        initGhostAnimations();

        startScreen.setVisibility(View.VISIBLE);
        gameScreen.setVisibility(View.INVISIBLE);
        loseScreen.setVisibility(View.INVISIBLE);
        winScreen.setVisibility(View.INVISIBLE);


        startScreen.setScaleX(0.05f);
        startScreen.setScaleY(0.05f);
        startScreen.animate().scaleX(1f).scaleY(1f).rotation(3600).setDuration(2000);

        loseScreen.setScaleX(0.01f);
        loseScreen.setScaleY(0.01f);

        myEventListener = new MyEventListener() {
            @Override
            public boolean onTick(DIRECTION d) {
                boolean moveX = true;
                int reverse = -1;

                switch (d) {
                    case DOWN:
//                this.pacman.animate().translationYBy(130);
                        moveX = false;
                        break;
                    case UP:
//                this.pacman.animate().translationYBy(-130);
                        moveX = false;
                        reverse = 1;
                        break;
                    case LEFT:
//                this.pacman.animate().translationXBy(-130);
                        reverse = 1;
                        break;
                    case RIGHT:
//                this.pacman.animate().translationXBy(130);
                        break;
                    default:
                        break;
                }

                boolean collision = CheckCollision();

                if (ready_to_win) {
                    doWin();
                } else {
                    if (!collision) {
                        if (moveX) {
                            pacman.animate().translationXBy(-1 * reverse * STEP);
                        } else {
                            pacman.animate().translationYBy(-1 * reverse * STEP);
                        }
                    } else {
                        bounceBack(d);
                    }
                }
                return collision;
            }
        };

        upArrow.setOnTouchListener(new MyOnTouchListener(this, DIRECTION.UP));
        downArrow.setOnTouchListener(new MyOnTouchListener(this, DIRECTION.DOWN));
        leftArrow.setOnTouchListener(new MyOnTouchListener(this, DIRECTION.LEFT));
        rightArrow.setOnTouchListener(new MyOnTouchListener(this, DIRECTION.RIGHT));

    }
}


