package go.bits.diary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class TopicNotes extends AppCompatActivity {
    String TopicName;
    final DatabaseHandler db = new DatabaseHandler(this);
    LinearLayout topBar;
    TextView topicTitle;
    ImageButton deleteTopicButton;
    ListView topicNotesList;
    LinearLayout inputBar;
    EditText editTextTopicNotes;
    ImageButton doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_notes);

        TopicName = getIntent().getStringExtra("TopicName");

        topBar = (LinearLayout) findViewById(R.id.topic_notes_top_bar);
        topicTitle = (TextView) topBar.findViewById(R.id.topic_notes_title);
        topicTitle.setText(TopicName);
        deleteTopicButton = (ImageButton) topBar.findViewById(R.id.delete_topic_button);
        View.OnClickListener deleteTopicListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.removeTopic(TopicName);
                finish();
            }
        };
        deleteTopicButton.setOnClickListener(deleteTopicListener);

        topicNotesList = (ListView) findViewById(R.id.list_topic_notes);

        inputBar = (LinearLayout) findViewById(R.id.topic_notes_input_bar);
        editTextTopicNotes = (EditText) inputBar.findViewById(R.id.edit_text_topic_notes);
        doneButton = (ImageButton) inputBar.findViewById(R.id.topic_button_done);
        View.OnClickListener doneButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.addNote(editTextTopicNotes.getText().toString(), TopicName);
                editTextTopicNotes.setText("");
                editTextTopicNotes.onEditorAction(EditorInfo.IME_ACTION_DONE);
                onResume();
            }
        };
        doneButton.setOnClickListener(doneButtonListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        List<Note> topicNotes = db.getTopicNotes(TopicName);
        NotesAdapter topicNotesAdapter = new NotesAdapter(this, topicNotes);
        topicNotesList.setAdapter(topicNotesAdapter);
    }
}
