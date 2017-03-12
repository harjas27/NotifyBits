package in.wolfpack.notifybits;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class Details extends Activity {
    // Array of strings...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        StudentdbHelper s=new StudentdbHelper(Details.this);
        List<String> courses = new ArrayList<String>();
        courses=s.getAllContacts();
        if(courses.size()==0)
        {
            Toast.makeText(this, "No courses in your timetable", Toast.LENGTH_SHORT).show();
        }
        else
        {
            List<String> list = new ArrayList<String>();
            for(int i=0;i<courses.size();i++)
            {
                String r=courses.get(i);
                String[] p=r.split(";");
                String buffer="";
                buffer+=p[0];
                buffer+="-";
                buffer+=p[4];
                buffer+=" : ";
                buffer+=p[1];
                buffer+="(";
                buffer+=p[5];
                buffer+=")";
                list.add(buffer);
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                    R.layout.activity_listview, list);
            ListView listView = (ListView) findViewById(R.id.mobile_list);
            listView.setAdapter(adapter);
        }
    }
}
