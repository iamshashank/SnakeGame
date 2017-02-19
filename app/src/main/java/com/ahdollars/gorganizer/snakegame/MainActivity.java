package com.ahdollars.gorganizer.snakegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import static com.ahdollars.gorganizer.snakegame.R.id.game;

public class MainActivity extends AppCompatActivity implements Runnable,View.OnClickListener{

    SurfaceView gameView;
    ImageButton up,down,left,right;
    ImageView pauseButton;
    TextView scoreBoard;
    SurfaceHolder holder;
    boolean gameOver=false,gamePaused=false;
    Thread gameThread=null;
    int snake[];
    int food;
    float screenHeight,screenWidth;
    float tileHeight,tileWidth;
    int NOB;
    int YINC;
    int D=39,score=0;
    Random random;
    public static final String TAG="CANVAS";
    Handler handler;
    Paint p,f;
    int TAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler=new Handler();
        init();
    }

    private void init() {
        scoreBoard=(TextView)findViewById(R.id.score);
        gameView=(SurfaceView)findViewById(game);
        up=(ImageButton)findViewById(R.id.up);
        down=(ImageButton)findViewById(R.id.down);
        right=(ImageButton)findViewById(R.id.right);
        left=(ImageButton)findViewById(R.id.left);
        pauseButton=(ImageView)findViewById(R.id.icon_pause);
        p=new Paint();
        f=new Paint();
        p.setColor(Color.BLUE);
        f.setColor(Color.RED);
        NOB=45;
        snake=new int[NOB*NOB-1];
        TAIL=6;
        snake[0] = 7;
        snake[1] = 6;
        snake[2] = 5;
        snake[3] = 4;
        snake[4] = 3;
        snake[5] = 2;

        YINC=NOB*(NOB-1);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        right.setOnClickListener(this);
        left.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        holder=gameView.getHolder();
        gameThread=new Thread(this);
        foodGenerator();
        gameThread.start();

    }


    @Override
    public void run() {


        while(true){
            if(!holder.getSurface().isValid()){
                //if the surfaceviiew is not valid then loop
                //if valid then proceed
                continue;
            }
            Canvas canvas=holder.lockCanvas();
            screenHeight=canvas.getHeight();
            screenWidth=canvas.getWidth();
            tileHeight=screenHeight/NOB;
            tileWidth=screenWidth/NOB;
            holder.unlockCanvasAndPost(canvas);
            break;
        }

       // foodGenerator();
        while (!gameOver){


            while (gamePaused){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //this is ouur game loop
            if(!holder.getSurface().isValid()){
                //if the surfaceviiew is not valid then loop
                //if valid then proceed
                continue;
            }



            Canvas canvas=holder.lockCanvas();

            Log.d(TAG, "run: ");
            render(canvas);
            update();

            holder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void foodGenerator(){
        random=new Random(System.currentTimeMillis());
        food=random.nextInt(NOB*NOB);

    }

    public void drawGrid(Canvas canvas){
        Paint p=new Paint();
        p.setColor(Color.GREEN);


        for(int i=1;i<=NOB;i++){
            canvas.drawLine(0,(i-1)*tileHeight,screenWidth,(i-1)*tileHeight,p);
        }
        for(int i=1;i<=NOB;i++){
            canvas.drawLine((i-1)*tileWidth,0,(i-1)*tileWidth,screenHeight,p);

        }
    }

    boolean checkSnakeDied(){
        for(int i=1;i<TAIL;i++){
            if(snake[0]==snake[i]){

                return true;
            }
        }
        return false;
    }

    private void reset() {

        gameOver=true;


    }






    public void render(Canvas canvas){
        canvas.drawColor(Color.BLACK);

        if(checkSnakeDied()){
            f.setTextSize(70f);
            canvas.drawText("GAME OVER",screenWidth/2-100,screenHeight/2-20,f);
            reset();
        }

        //drawGrid(canvas);
        //handlet to update textview
        handler.post(new Runnable() {
            @Override
            public void run() {
                scoreBoard.setText(String.valueOf(score));
            }
        });

        //print food
        canvas.drawCircle((mapX(food)+tileWidth/2)+0.5f,(mapY(food)+tileHeight/2),(Math.min(tileHeight,tileWidth)/2),f);
        //canvas.drawRect(mapX(food),mapY(food),mapX(food)+tileWidth,mapY(food)+tileHeight,f);

        for (int i = 0; i < TAIL; i++) {
            //console.log("INDEX "+(i+1)+" = "+snake[i]);
            float x, y;
            x = mapX(snake[i]);
            y = mapY(snake[i]);
            if(i==0){
                p.setColor(Color.WHITE);
                canvas.drawCircle(((x+x+tileWidth)/2),((y+y+tileHeight)/2),(Math.min(tileHeight,tileWidth)/2+5.0f),p);

            }else{
                p.setColor(Color.BLUE);
                canvas.drawRect(x+0.5f, y+0.5f,x+tileWidth-0.5f, y+tileHeight-0.5f,p);

            }

           // canvas.drawRect(x+0.5f, y+0.5f,x+tileWidth-0.5f, y+tileHeight-0.5f,p);
        }

//        Log.d(TAG, "render: "+tileWidth+" , "+tileHeight);
//        for(int i=1;i<=NOB*NOB;i++){
//            Log.d(TAG, "grid coordinate:: "+(i)+"=  ("+mapX(i)+" , "+mapY(i)+" )");
//        }

    }

    boolean boundCheck(int d) {
        if (d == 39 && snake[0]%NOB==0) {
            snake[0] = snake[0]-(NOB-1);
            return true;
        } else if (d == 40 && Math.ceil((float)snake[0]/NOB)==NOB ) {
            snake[0] = snake[0]-YINC;
            return true;
        } else if (d == 37 && snake[0]%NOB == 1) {
            snake[0] += (NOB-1);
            return true;
        } else if (d == 38 && Math.ceil((float)snake[0]/NOB)==1) {
            snake[0] += YINC;
            return true;
        }
        return false;

    }

    void growSnake(){
        TAIL++;
        snake[TAIL]=snake[TAIL-1];
    }

    public void update(){
        if(food==snake[0]){
            score++;
            //handler to update textview
           growSnake();
            foodGenerator();
        }
        //checkSnakeDied();

        for (int i = TAIL-1; i>0; i--) {
            Log.d(TAG, "snake[" +i+"] = "+snake[i]);
            snake[i] = snake[i-1];
        }

        if(!boundCheck(D)){
            if (D == 39) {
                snake[0] += 1;
            } else if (D == 40) {
                snake[0] += NOB;
            } else if (D == 37) {
                snake[0] += -1;
            } else {
                snake[0] += -NOB;
            }

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.up:
                //38
                if (D!=40)
                D=38;
                break;
            case R.id.down:
                //40
                if (D!=38)
                D=40;
                break;
            case R.id.right:
                //39
                if (D!=37)
                D=39;
                break;
            case R.id.left:
                //37
                if (D!=39)
                D=37;
                break;
            case R.id.icon_pause:
                if(gamePaused)
                    gamePaused=false;
                else
                gamePaused=true;
                break;

        }

    }



    float mapX(int x) {
        if(x%NOB==0)
            return ((screenWidth/tileWidth-1)*tileWidth);

        return ((x%NOB - 1)*tileWidth);
    }
    float mapY(int y) {
        if(y%NOB==0){
            return (((y/NOB)-1)*tileHeight);
        }

        return  (float) (Math.floor(y/NOB) * tileHeight);
    }

    @Override
    protected void onPause() {
        //gameOver=true;
        gamePaused=true;
//        try {
//            gameThread.interrupt();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gamePaused=false;
    }
}


