package go.bits.diary;


import android.support.annotation.Nullable;

/**
 * Created by rushi on 31/12/16
 */

class Note {
    private String Text;
    private Long ID;
    private String FolderName;
    Note(Long ID, String Text, @Nullable String TOPIC_NAME){
        String FolderName;
        if(TOPIC_NAME != null){
            FolderName = TOPIC_NAME;
        }
        else {
            FolderName = DatabaseHandler.TABLE_NAME_SCRIBBLES;
        }
        this.ID = ID;
        this.Text = Text;
        this.FolderName = FolderName;
    }
    public String getText(){
        return this.Text;
    }
    Long getID(){
        return this.ID;
    }
    String getFolderName(){
        return this.FolderName;
    }
    public void setText(String Text){
        this.Text = Text;
    }
}
