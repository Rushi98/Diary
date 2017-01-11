package go.bits.diary;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static go.bits.diary.R.color.tabBackground;
import static go.bits.diary.R.color.tabUnselected;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    DatabaseHandler db;
    Button tabTopics;
    Button tabScribbles;
    ColorStateList selectedTabTextColor;
    ColorStateList unselectedTabTextColor;
    LinearLayout topicsLL;
    LinearLayout scribblesLL;
    ListView topicsList;
    LinearLayout addTopicBar;
    EditText addTopicText;
    ImageButton addTopicButton;
    List<String> scribbleDates;
    List<String> scribbleDatesUserFriendly;
    String scribblesDate;
    @SuppressLint("SimpleDateFormat") SimpleDateFormat userFriendlyDate = new SimpleDateFormat("d MMM, yyyy");
    ArrayAdapter<String> dateAdapter;
    Spinner scribblesDateSpinner;
    ListView scribblesList;
    LinearLayout scribblesInputBar;
    EditText scribbleText;
    ImageButton doneScribble;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHandler(this);

        tabTopics = (Button) findViewById(R.id.button_topics);
        unselectedTabTextColor = tabTopics.getTextColors();
        View.OnClickListener tabTopicsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTopicList();
            }
        };
        tabTopics.setOnClickListener(tabTopicsListener);

        tabScribbles = (Button) findViewById(R.id.button_scribbles);
        selectedTabTextColor = tabScribbles.getTextColors();
        View.OnClickListener tabScribblesListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScribbles();
            }
        };
        tabScribbles.setOnClickListener(tabScribblesListener);


        /*******************************************************************************************
         *                                      The Scribbles tab                                  *
         ******************************************************************************************/
        scribblesLL = (LinearLayout) findViewById(R.id.rl_scribbles);

        // TODO (2): Restart App at midnight
        scribblesDate = DatabaseHandler.dateFormat.format(new Date());
        Log.d("sd", scribblesDate);
        scribblesDateSpinner = (Spinner) scribblesLL.findViewById(R.id.scribbles_date_spinner);
        scribblesDateSpinner.setOnItemSelectedListener(this);

        scribblesList = (ListView) scribblesLL.findViewById(R.id.list_scribbles);

        scribblesInputBar = (LinearLayout) scribblesLL.findViewById(R.id.scribbles_input_bar);

        scribbleText = (EditText) scribblesInputBar.findViewById(R.id.edit_text_scribbles);

        doneScribble = (ImageButton) scribblesInputBar.findViewById(R.id.button_scribble_done);

        View.OnClickListener doneScribbleListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("what", "clicked");
                db.addNote(scribbleText.getText().toString(), null);
                scribbleText.setText("");
                addTopicText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                onResume();
            }
        };
        doneScribble.setOnClickListener(doneScribbleListener);



        /*******************************************************************************************
         *                                      The Topics tab                                  *
         ******************************************************************************************/
        topicsLL = (LinearLayout) findViewById(R.id.topics);

        topicsList = (ListView) topicsLL.findViewById(R.id.list_topics);

        addTopicBar = (LinearLayout) topicsLL.findViewById(R.id.add_topic_bar);

        addTopicText = (EditText) addTopicBar.findViewById(R.id.edit_topic_name);

        addTopicButton = (ImageButton) addTopicBar.findViewById(R.id.button_add_topic);
        final Toast toast = Toast.makeText(this, "Topic already exists!", Toast.LENGTH_SHORT);
        View.OnClickListener addTopicListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addTopicText.getText().toString().length() != 0) {
                    int status = db.addTopic(addTopicText.getText().toString());
                    if (status == 1) {
                        toast.show();
                    }
                }
                addTopicText.setText("");
                addTopicText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                onResume();
            }
        };
        addTopicButton.setOnClickListener(addTopicListener);
    }

    public void showTopicList(){
        scribblesLL.setVisibility(View.INVISIBLE);
        topicsLL.setVisibility(View.VISIBLE);
        tabTopics.setTextColor(selectedTabTextColor);
        tabScribbles.setTextColor(unselectedTabTextColor);
    }
    public void showScribbles(){
        scribblesLL.setVisibility(View.VISIBLE);
        topicsLL.setVisibility(View.INVISIBLE);
        tabScribbles.setTextColor(selectedTabTextColor);
        tabTopics.setTextColor(unselectedTabTextColor);
    }

    @Override
    protected void onResume(){
        super.onResume();
        /**
         * Scribbles
         */
        scribbleDates = db.getSCRIBBLE_DATES();
        if(!scribbleDates.contains(DatabaseHandler.dateFormat.format(new Date()))){
            db.addToday();
            scribbleDates = db.getSCRIBBLE_DATES();
        }
        Log.d("no", (String.valueOf(scribbleDates.size())));
        scribbleDatesUserFriendly = new ArrayList<>();
        /**
         * Convert Array of dates stored in db to user friendly dates' array
         */
        for (String s:scribbleDates){
            scribbleDatesUserFriendly.add(userFriendlyDate.format(DatabaseHandler.dateFormat.parse(s, new ParsePosition(0))));
        }

        dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, scribbleDatesUserFriendly);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scribblesDateSpinner.setAdapter(dateAdapter);

        List<Note> scribblesToday = db.getScribblesDated(DatabaseHandler.dateFormat.parse(scribblesDate, new ParsePosition(0)));
        Log.d("date", DatabaseHandler.dateFormat.parse(scribblesDate, new ParsePosition(0)).toString());
        NotesAdapter scribblesAdapter = new NotesAdapter(this, scribblesToday);
        scribblesList.setAdapter(scribblesAdapter);

        /**
         * Topic List
         */
        List<String> topics = db.getTOPIC_NAMES();
        TopicListAdapter topicListAdapter = new TopicListAdapter(this, topics);
        topicsList.setAdapter(topicListAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(!Objects.equals(scribblesDate, DatabaseHandler.dateFormat.format(userFriendlyDate.parse(parent.getItemAtPosition(position).toString(), new ParsePosition(0))))) {
            scribblesDate = DatabaseHandler.dateFormat.format(userFriendlyDate.parse(parent.getItemAtPosition(position).toString(), new ParsePosition(0)));
            if (!Objects.equals(scribblesDate, DatabaseHandler.dateFormat.format(new Date()))) {
                scribblesInputBar.setVisibility(View.INVISIBLE);
            } else {
                scribblesInputBar.setVisibility(View.VISIBLE);
            }
            onResume();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        scribblesInputBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop(){
        scribbleDates.remove(DatabaseHandler.dateFormat.format(new Date()));
        for(String s: scribbleDates){
            List<Note> scribbles = db.getScribblesDated(DatabaseHandler.dateFormat.parse(s, new ParsePosition(0)));
            if(scribbles.size() == 0){
                db.removeDate(s);
            }
        }
        super.onStop();
    }
}