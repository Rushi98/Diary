package go.bits.diary;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rushikesh on 1/1/17
 */

class NotesAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes;
    private List<Long> ids;
    @Override
    public int getCount() {
        return ids.size();
    }

    @Override
    public Object getItem(int i) {
        return notes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return ids.get(i);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v;
        if(convertView == null){
            v = View.inflate(this.context, R.layout.textview_note, null);
        }
        else {
            v = convertView;
        }
        TextView noteTextView = (TextView) v.findViewById(R.id.note_text_view);
        if(notes.get(position) != null){
            noteTextView.setText(notes.get(position).getText());
            final Intent intent = new Intent(this.context,EditNote.class);
            intent.putExtra("NoteID", notes.get(position).getID());
            intent.putExtra("NoteFolder", notes.get(position).getFolderName());

            /**
             * Click to edit note
             */
            View.OnClickListener textViewClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(intent);
                }
            };
            noteTextView.setOnClickListener(textViewClickListener);

            /**
             * Long click to copy note text
             */
            View.OnLongClickListener textViewLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Note text", notes.get(position).getText());
                    clipboard.setPrimaryClip(clip);
                    Toast toast = Toast.makeText(context, "Note Copied", Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
            };
            noteTextView.setOnLongClickListener(textViewLongClickListener);
        }
        return v;
    }

    NotesAdapter(Context context, List<Note> notes){
        this.context = context;
        this.notes = notes;
        List<Long> ids = new ArrayList<>();
        for (Note n:notes) {
            ids.add(n.getID());
        }
        this.ids = ids;
    }
}