package in.wolfpack.notifybits;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class TimeTable extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        StudentdbHelper s=new StudentdbHelper(TimeTable.this);
        List<String> courses = new ArrayList<String>();
        courses=s.getAllContacts();
        if(courses.size()==0)
        {
            Toast.makeText(this,"empty list",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(int i=0;i<courses.size();i++)
            {
                String r=courses.get(i);
                String[] p=r.split(";");
                String day=p[2];
                String time=p[3];
                String name=p[0];
                String id="textView";
                String week = "XMTWXFS";
                int v = day.length();
                int w = time.length();
                for(int j = 0; j<w ; j++ )
                {
                    id="textView";
                    if(time.charAt(j)!=' ')
                    {
                        for(int k=0;k<v;k++)
                        {
                            id="textView";
                            if(v!=2 && v!=4 && v!=6 && day.charAt(k)!=' ')
                            {
                                id = id + Integer.toString(week.indexOf(day.charAt(k))) + "0" + Character.toString(time.charAt(j));
                                //func call
                                int resID = getResources().getIdentifier(id, "id", getPackageName());
                                TextView t =(TextView)findViewById(resID);
                                t.setText(name);
                                //Toast.makeText(this,id,Toast.LENGTH_LONG).show();
                            }

                            else if(v==2 && day.charAt(k)!=' ' && day.charAt(k)!='h')
                            {
                                id = id + "40" + Character.toString(time.charAt(j));
                                //func call
                                int resID = getResources().getIdentifier(id, "id", getPackageName());
                                TextView t =(TextView)findViewById(resID);
                                t.setText(name);
                            }

                            else if(v==4 && day.charAt(k)!=' ' && day.charAt(k)!='h')
                            {
                                if(day.charAt(k)=='T' && day.charAt(k+1)=='h')
                                {
                                    id = id + "40" + time.charAt(j);
                                    //func call
                                    int resID = getResources().getIdentifier(id, "id", getPackageName());
                                    TextView t =(TextView)findViewById(resID);
                                    t.setText(name);
                                }
                                else
                                {
                                    id = id + Integer.toString(week.indexOf(day.charAt(k))) + "0" + time.charAt(j);
                                    //func call
                                    int resID = getResources().getIdentifier(id, "id", getPackageName());
                                    TextView t =(TextView)findViewById(resID);
                                    t.setText(name);
                                }
                            }


                            else if(v==6 && day.charAt(k)!=' ' && day.charAt(k)!='h')
                            {
                                if(day.charAt(k)=='T' && day.charAt(k+1)=='h')
                                {
                                    id = id + "40" + time.charAt(j);
                                    //func call
                                    int resID = getResources().getIdentifier(id, "id", getPackageName());
                                    TextView t =(TextView)findViewById(resID);
                                    t.setText(name);
                                }
                                else
                                {
                                    id = id + Integer.toString(week.indexOf(day.charAt(k))) + "0" + time.charAt(j);
                                    //func call
                                    int resID = getResources().getIdentifier(id, "id", getPackageName());
                                    TextView t =(TextView)findViewById(resID);
                                    t.setText(name);
                                }
                            }
                        }

                    }

                }
                /*int resID = getResources().getIdentifier(id, "id", getPackageName());
                TextView t =(TextView)findViewById(resID);
                t.setText(name);*/
                //t.setBackgroundColor(Color.RED);
            }
        }
    }
}
