package com.example.assign06_6406021631019_ufo;
// ส่วน import ไลบรารีและคลาสต่างๆ
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;
// คลาส GraphicsView สำหรับการวาดและจัดการกับเกม
public class GraphicsView extends View  implements View.OnTouchListener{
    // สร้างตัวแปลต่างๆ
    Paint p1;
    int imageW,imageH;
    CountDownTimer timer1,timer2,UfoBoom;
    boolean StatusGame = false;
    float Width=0,Height=0;
    float spawnX[] = {50,250,450,650,850},spawnY[];
    Random rnd = new Random();
    int score=0 , time=0;
    int Speed = 50,SpeedUnit[];
    int resImage[] = {R.drawable.boom2};
    Bitmap image_boom[] = new Bitmap[resImage.length],image1;
    int HitUFO = -1,Num =0;
    float picy;
    int effect = R.raw.boomef;
    MediaPlayer play;

    // เมธอดสร้าง GraphicsView
    public GraphicsView(Context c) {
        super(c);
        setBackgroundColor(Color.WHITE); // กำหนดสีพื้นหลัง
        p1 = new Paint(); // สร้างอ็อบเจกต์ Paint
        BitmapFactory.Options options = new BitmapFactory.Options();
        image1 = BitmapFactory.decodeResource(getResources(), R.drawable.boom1);
// โหลดรูปภาพที่จะหล่น
        imageW = image1.getWidth(); // กำหนดความกว้างรูป
        imageH = image1.getHeight(); // กำหนดความสูงรูป
        spawnY = new float[5]; // สร้างอาร์เรย์สำหรับตำแหน่ง Y ของ รูป
        SpeedUnit = new int[5]; // สร้างอาร์เรย์สำหรับความเร็วของ รูป
        for (int n = 0; n < SpeedUnit.length; n++) {
            setSpeed(n); // กำหนดความเร็วให้แต่ละ รูป
        }
        for (int n = 0; n < resImage.length; n++) {
            image_boom[n] = BitmapFactory.decodeResource(getResources(), resImage[n]);
// โหลดรูปภาพ Boom
        }
        image1 = BitmapFactory.decodeResource(getResources(), R.drawable.boom1);
// โหลดรูปภาพ รูป
        imageW = image1.getWidth(); // กำหนดความกว้างของ รูป
        imageH = image1.getHeight(); // กำหนดความสูงของ รูป
        spawnY = new float[5]; // สร้างอาร์เรย์สำหรับตำแหน่ง Y ของ รูป
        SpeedUnit = new int[5]; // สร้างอาร์เรย์สำหรับความเร็วของ รูป
        for (int n = 0; n < SpeedUnit.length; n++) {
            setSpeed(n); // กำหนดความเร็วให้แต่ละ รูป
        }
        setOnTouchListener(this); // ตั้งค่า Listener สำหรับการแตะที่หน้าจอ
        timer1 = new CountDownTimer(30000, 1000) { // ตัวจับเวลาสำหรับเกม
            @Override
            public void onTick(long l) {
                time++; // เพิ่มเวลาที่ผ่านไป
                invalidate(); // วาดหน้าจอใหม่
            }
            @Override
            public void onFinish() {
                StatusGame = true; // เกมจบ
                timer2.cancel(); // ยกเลิกการทำงานของตัวจับเวลา
                invalidate(); // วาดหน้าจอใหม่
            }
        };

        timer2 = new CountDownTimer(30000, 50) { // ตัวจับเวลาสำหรับเคลื่อนที่ของ รูป
            @Override
            public void onTick(long l) {
                for (int n = 0; n < spawnY.length; n++) {
                    spawnY[n] += SpeedUnit[n]; // เคลื่อนที่ รูป
                    if (spawnY[n] > Height + image1.getHeight()) {
                        spawnY[n] = 0; // ย้าย รูป กลับไปยังตำแหน่งเริ่มต้น
                    }
                }
                invalidate(); // วาดหน้าจอใหม่
            }
            @Override
            public void onFinish() {
                StatusGame = true; // เกมจบ
                invalidate(); // วาดหน้าจอใหม่
            }
        };
        UfoBoom = new CountDownTimer(200, 10) { // ตัวจับเวลาสำหรับ Boom
            @Override
            public void onTick(long millisUntilFinished) {
                Num = (Num + 1 == resImage.length) ? 0 : Num + 1; // เปลี่ยนภาพของ Boom
                invalidate(); // วาดหน้าจอใหม่
            }
            @Override
            public void onFinish() {
                Num = 0; // กำหนดลำดับของ Boom เป็น 0
                HitUFO = -1; // กำหนดตำแหน่งที่โดน รูป เป็น -1
                invalidate(); // วาดหน้าจอใหม่
            }
        };
        timer1.start(); // เริ่มตัวจับเวลาสำหรับเกม
        timer2.start(); // เริ่มตัวจับเวลาสำหรับเคลื่อนที่ของ UFO
        play = MediaPlayer.create(c, effect); // เล่นเสียงของ Boom
    }

    // เมธอดสำหรับกำหนดความเร็วของ รูป
    private void setSpeed(int n) {
        SpeedUnit[n] = 1 + rnd.nextInt(Speed); // ความเร็วของรูป จะเป็นสุ่มในช่วง 1 ถึง Speed
    }
    // เมธอด onTouch สำหรับจัดการเหตุการณ์การแตะที่หน้าจอ
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (StatusGame) { // ถ้าเกมจบ
            StatusGame = false; // เริ่มเกมใหม่
            timer1.start(); // เริ่มตัวจับเวลาสำหรับเกม
            timer2.start(); // เริ่มตัวจับเวลาสำหรับเคลื่อนที่ของ รูป
            score = 0; // กำหนดคะแนนเริ่มต้น
            time = 0; // กำหนดเวลาเริ่มต้น
            for (int n = 0; n < spawnY.length; n++)
                spawnY[n] = 0; // กำหนดตำแหน่งเริ่มต้นของ รูป
            invalidate(); // วาดหน้าจอใหม่
        } else { // ถ้าเกมกำลังเล่น
            float x = event.getX(); // ตำแหน่ง X ที่แตะ
            float y = event.getY(); // ตำแหน่ง Y ที่แตะ
            if (Boom(x, y)) { // ถ้ามีการโดน UFO
                score++; // เพิ่มคะแนน
                play.start(); // เล่นเสียงของ รูป
                invalidate(); // วาดหน้าจอใหม่
            }
        }
        return true; // ส่งค่าเป็น true เพื่อบอกว่ามีการจัดการกับเหตุการณ์แตะ
    }


    // เมธอดที่ใช้ในการตรวจสอบการโดน รูป
    private boolean Boom(float x, float y) {
        for (int n = 0; n < spawnX.length; n++) {
            if (x > spawnX[n] && x < spawnX[n] + image1.getWidth()) {
                if (y > spawnY[n] && y < spawnY[n] + image1.getHeight()) {
                    NewSpawn(n, y); // สร้าง UFO ใหม่
                    return true; // บอกว่ามีการโดน รูป
                }
            }
        }
        return false; // บอกว่าไม่มีการโดน รูป
    }

    // เมธอดที่ใช้ในการสร้าง รูป ใหม่
    private void NewSpawn(int n, float y) {
        spawnY[n] = 0; // กำหนดตำแหน่ง Y ของ รูป
        HitUFO = n; // บอกว่าโดน รูป ที่ตำแหน่ง n
        UfoBoom.start(); // เริ่มตัวจับเวลาสำหรับ Boom
        picy = y; // กำหนดตำแหน่ง Y ของ Boom
        setSpeed(n); // กำหนดความเร็วใหม่ให้ รูป
        invalidate(); // วาดหน้าจอใหม่
    }
    // เมธอด onDraw เมื่อต้องการวาดบน Canvas
    @Override
    protected void onDraw(Canvas canvas) {
        Width = getWidth(); // ความกว้างของหน้าจอ
        Height = getHeight(); // ความสูงของหน้าจอ
        if(StatusGame){// ถ้าเกมจบ
            p1.setColor(Color.RED); //กำหนดการวาด
            p1.setTextSize(60);
            p1.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Time Out!",Width/2,Height/2-100,p1);
// วาดข้อความ "Time Out!"
            canvas.drawText("Press for play again or back to exit",Width/2,Height/2,p1);

// วาดข้อความ "Press for play again or back to exit"
        }else{ // ถ้าเกมกำลังเล่น
            p1.setColor(Color.BLACK);
            p1.setTextSize(60);
            p1.setTextAlign(Paint.Align.CENTER);
            p1.setStrokeWidth(3);
            canvas.drawText("Score : " + score, 130, 60, p1);
            p1.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Time : " + time, Width-50, 60, p1);
            for(int n =0;n< spawnY.length;n++){
                if(HitUFO == n){// ถ้ากดโดน
                    canvas.drawBitmap(image_boom[Num], spawnX[n], picy, null);
// วาด Boom ที่ตำแหน่งที่โดน
                }else{
                    canvas.drawBitmap(image1, spawnX[n], spawnY[n], null);
// วาดที่ตำแหน่งปัจจุบัน
                }
            }
        }
    }
}




