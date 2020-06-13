package apollo.safety.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import apollo.safety.Adapters.ContactsAdapter;
import apollo.safety.Models.Contacts;
import apollo.safety.R;

public class ContactViewerActivity extends AppCompatActivity {

    List<Contacts> contacts;
    RecyclerView recyclerView;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_viewer);
        contacts = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setBackgroundColor(Color.BLACK);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        preferences = getSharedPreferences("apollo.safety.USERNAME",MODE_PRIVATE);
        String username = preferences.getString("Username","");
        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("contacts").child(username);
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot contactsSnapshot : dataSnapshot.getChildren()) {
                        Contacts list = contactsSnapshot.getValue(Contacts.class);
                        if (list != null && list.getNumber() != null && list.getName() != null) {
                            contacts.add(list);
                        }
                    }
                    ContactsAdapter adapter = new ContactsAdapter(contacts, ContactViewerActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            /*    else {
                    TextView textView = findViewById(R.id.textView);
                    textView.setText(R.string.no_contacts);
                    textView.setTextColor(Color.WHITE);
                } */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContactViewerActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
              //  recreate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                recreate();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}