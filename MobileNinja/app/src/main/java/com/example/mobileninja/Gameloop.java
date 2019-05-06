package com.example.mobileninja;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

public class Gameloop extends  Level1 {

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);

        setContentView(new myview(this));
    }


    class myview extends View {

        public myview(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

        }

    }
}




