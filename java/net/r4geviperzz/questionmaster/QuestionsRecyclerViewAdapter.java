package net.r4geviperzz.questionmaster;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder> {

    //This list stores all the questions that need to be displayed in the RecyclerView
    private List<String> dbQuestionsList;
    //This list stores the card colour number for all of the questions that need to be displayed in the RecyclerView
    private List<String> dbQuestionColourNumList;

    //This class variable is used to inflate the layout of each item in the RecyclerView
    private LayoutInflater itemLayoutInflater;

    //This listener is needed so that an OnItemClickListener can be set for each item in the RecyclerView
    private ItemClickListener questionsRecyclerAdapterListenerInterface;
    private Float heightAdjustValue;

    private Map<String, String> cardNumToHexMultiMap = new HashMap<>();

    //The constructor
    public QuestionsRecyclerViewAdapter(Context context, List<String> data, List<String> colourData) {
        this.itemLayoutInflater = LayoutInflater.from(context);
        this.dbQuestionsList = data;
        this.dbQuestionColourNumList = colourData;
        this.heightAdjustValue = TextScale.getFontAdjustHeightValue();

        DBHelper dbHelper = new DBHelper(context);
        setupCardColourHashMap(dbHelper);
    }

    //This method gets the hex colour for each card type
    private void setupCardColourHashMap(DBHelper passedDBHelper){
        List<String> cardColourHexList = passedDBHelper.getCardColourNumAndHex();

        //Adds the card colour and hex value to the multi map with the card colour num being used as the key value
        for (int x = 0; x < cardColourHexList.size(); x = x + 2){
            cardNumToHexMultiMap.put(cardColourHexList.get(x), cardColourHexList.get(x + 1));
        }
    }

    // This method updates the list of questions in the adapter with a new list of questions
    public void updateList(List<String> newQuestions) {
        // Clear the old list of questions in the adapter
        dbQuestionsList.clear();

        // Add all the new questions to the adapter's list
        dbQuestionsList.addAll(newQuestions);

        // Notify the RecyclerView that the data has changed so it can update its display
        notifyDataSetChanged();

        // Remove any extra items from the adapter if the new list is smaller than the old list
        int sizeDiff = dbQuestionsList.size() - newQuestions.size();
        if (sizeDiff > 0) {
            //This line is used to notify the adapter that a range of items has been removed from the RecyclerView
            notifyItemRangeRemoved(newQuestions.size(), sizeDiff);
        }

        // Set the adapter's list to the new list
        this.dbQuestionsList = newQuestions;
    }


    // This method inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = itemLayoutInflater.inflate(R.layout.questions_recycler_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // This method binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Get the question at the current position in the List of questions from the database.
        String question = dbQuestionsList.get(position);
        //Set the text of the TextView in the ViewHolder to the question text.
        holder.myTextView.setText(question);
        //Sets the text size
        holder.myTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (18 * heightAdjustValue));

        String cardHexColour = "";

        //Checks if the dbQuestionColourNumList has more than one element in it, if it has only 1 element then that
        //means that a specific card colour has been chosen in the display dropdown, as such all of the displayed
        //questions will be of the same colour so the card colour number doesn't need to be tracked for each question
        if (dbQuestionColourNumList.size() > 1) {
            //Runs when questions of multiple different colours are being displayed
            cardHexColour = cardNumToHexMultiMap.get(dbQuestionColourNumList.get(position));
        }else{
            //Runs when the questions of a single colour is being displayed
            cardHexColour = cardNumToHexMultiMap.get(dbQuestionColourNumList.get(0));
        }
        holder.myQuestionShape.setColour(Color.parseColor(cardHexColour));
    }

    // This method gets the total number of rows in the RecyclerView so that the adapter knows how many items need to be displayed
    @Override
    public int getItemCount() {
        return dbQuestionsList.size();
    }

    // This class stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        QuestionColourShape myQuestionShape;

        // ViewHolder constructor takes a view as a parameter and initializes the myTextView field and click listener
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.myTextView); // Find the myTextView TextView in the item view
            myQuestionShape = itemView.findViewById(R.id.myQuestionShape);        //Find the question circle in the item view
            itemView.setOnClickListener(this); // Set click listener on the entire item view
        }

        // onClick method is called when an item is clicked and delegates the click event to the click listener object
        @Override
        public void onClick(View view) {
            // Check if click listener is set and call onItemClick method
            if (questionsRecyclerAdapterListenerInterface != null){
                //This line is used to passed the view that was clicked so that the code
                //can tell what item in the RecyclerView has been pressed
                questionsRecyclerAdapterListenerInterface.onItemClick(view, getAdapterPosition());
            }
        }
    }


    // This is a convenience method for getting data at click position
    public String getItem(int id) {
        return dbQuestionsList.get(id);
    }

    // This method allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.questionsRecyclerAdapterListenerInterface = itemClickListener;
    }

    // The parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

