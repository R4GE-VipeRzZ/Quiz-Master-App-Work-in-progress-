package net.r4geviperzz.questionmaster;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import java.util.List;

public class BoardPage extends Activity {
    private DBHelper dbHelper = new DBHelper(BoardPage.this);
    private GridLayout gridLayout;
    private ImageView counterImgPlayer1;
    private int numColumns = 13;
    private int numRows = 12;
    private String idOfBoard = null;
    private int testCurrentPos = 1;
    private int posWinPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_page);

        idOfBoard = getIntent().getStringExtra("boardId");
        posWinPosition = dbHelper.getBoardTotalSpaces(idOfBoard);

        Button askQuestionBtn = findViewById(R.id.askQuestionBtn);

        askQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testCurrentPos != posWinPosition) {
                    testCurrentPos++;

                    List<String> gridPositions = dbHelper.getPosGridLocations(testCurrentPos, idOfBoard);
                    Log.e("boardGridPos", gridPositions.toString() + " Position = " + Integer.toString(testCurrentPos));
                    movePlayer1Counter(Integer.parseInt(gridPositions.get(1)),Integer.parseInt(gridPositions.get(0)));
                }else{
                    Log.e("Win", "You have won!");
                }
            }
        });

        gridLayout = findViewById(R.id.boardPgGrid);
        gridLayout.setColumnCount(numColumns);
        gridLayout.setRowCount(numRows);

        // Get the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // Get the screen density
        float density = getResources().getDisplayMetrics().density;

        // Calculate the margin size in pixels (10dp)
        int marginSize = (int) (10 * density);

        // Subtract the total margin width from the screen width
        int availableWidth = screenWidth - (2 * marginSize);

        // Set the width of each cell to fit the screen
        int cellSize = availableWidth / numColumns;

        // Create the checkerboard pattern
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                TextView cell = new TextView(this);
                cell.setWidth(cellSize);
                cell.setHeight(cellSize);
                cell.setGravity(Gravity.CENTER);
                gridLayout.addView(cell);
            }
        }

        // Set the margins of the grid layout
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) gridLayout.getLayoutParams();
        layoutParams.setMargins(marginSize, marginSize, marginSize, marginSize);
        gridLayout.setLayoutParams(layoutParams);

        // Create the image view for the drawable
        counterImgPlayer1 = new ImageView(this);

        // Load the drawable
        Drawable player1Drawable = getResources().getDrawable(R.drawable.blue_counter_1);

        // Set the new drawable on the ImageView
        counterImgPlayer1.setImageDrawable(player1Drawable);

        // Create the layout parameters for the image view
        GridLayout.LayoutParams player1CounterParams = new GridLayout.LayoutParams();
        player1CounterParams.height = cellSize;
        player1CounterParams.width = cellSize;

        //Used to get the start position on the board for the counter
        List<String> gridPositions = dbHelper.getPosGridLocations(1, idOfBoard);

        player1CounterParams.rowSpec = GridLayout.spec(Integer.parseInt(gridPositions.get(1)), 1);
        player1CounterParams.columnSpec = GridLayout.spec(Integer.parseInt(gridPositions.get(0)), 1);

        // Add the image view to the grid
        gridLayout.addView(counterImgPlayer1, player1CounterParams);



        // Create the image view for the drawable
        ImageView playBoardImg = new ImageView(this);

        // Load the drawable
        String boardDrawableName = dbHelper.getBoardImgById(idOfBoard);
        int boardImgResId = getResources().getIdentifier(boardDrawableName, "drawable", getPackageName());
        Drawable playBoardDrawable= getResources().getDrawable(boardImgResId);

        // Set the new drawable on the ImageView
        playBoardImg.setImageDrawable(playBoardDrawable);

        // Create the layout parameters for the image view
        GridLayout.LayoutParams playBoardParams = new GridLayout.LayoutParams();
        playBoardParams.height = (cellSize * numRows);
        playBoardParams.width = (cellSize * numColumns);
        playBoardParams.rowSpec = GridLayout.spec(0, numRows);
        playBoardParams.columnSpec = GridLayout.spec(0, numColumns);

        //Sets the elevation of the board image to a negative to ensure that it is always behind all other views in the grid
        ViewCompat.setElevation(playBoardImg, -1);
        // Add the image view to the grid
        gridLayout.addView(playBoardImg, playBoardParams);
    }

    public void movePlayer1Counter(int row, int column) {
        // Get the layout parameters of the blue counter
        GridLayout.LayoutParams params = (GridLayout.LayoutParams) counterImgPlayer1.getLayoutParams();

        // Update the row and column specs of the layout parameters
        params.rowSpec = GridLayout.spec(row, 1);
        params.columnSpec = GridLayout.spec(column, 1);

        // Set the updated layout parameters on the blue counter
        counterImgPlayer1.setLayoutParams(params);
    }
}

