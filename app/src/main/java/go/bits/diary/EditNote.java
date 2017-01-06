package go.bits.diary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class EditNote extends AppCompatActivity {
    Note note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        final DatabaseHandler db = new DatabaseHandler(this);
        Long ID = getIntent().getLongExtra("NoteID", -1);
        String TopicName = getIntent().getStringExtra("NoteFolder");
        note = db.getNote(TopicName, ID);
        final EditText editText = (EditText) findViewById(R.id.note_edit_text);
        editText.setText(note.getText());
        ImageButton deleteButton = (ImageButton) findViewById(R.id.delete_button);
        ImageButton doneButton = (ImageButton) findViewById(R.id.done_button);
        View.OnClickListener deleteButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.removeNote(note);
                finish();
            }
        };
        deleteButton.setOnClickListener(deleteButtonListener);
        View.OnClickListener doneButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.setText(editText.getText().toString());
                db.updateNote(note);
                finish();
            }
        };
        doneButton.setOnClickListener(doneButtonListener);
    }
}
