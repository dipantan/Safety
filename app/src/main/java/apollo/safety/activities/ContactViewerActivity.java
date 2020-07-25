package apollo.safety.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import apollo.safety.Utils.OnLongClick;

public class ContactViewerActivity extends AppCompatActivity {

    List<Contacts> contacts;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    ContactsAdapter adapter;

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
    protected void onPause() {
        super.onPause();
        contacts.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences("apollo.safety.USERNAME", MODE_PRIVATE);
        String username = preferences.getString("Username", "");
   //     Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
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
                    adapter = new ContactsAdapter(contacts, getApplicationContext(), new OnLongClick() {
                        @Override
                        public void onLongClick(View v, final int position) {
                            final SharedPreferences preferences = ContactViewerActivity.this.getSharedPreferences("apollo.safety.USERNAME", Context.MODE_PRIVATE);
                            final String username = preferences.getString("Username", "");
                            final Contacts ld = contacts.get(position);
                            final String id = ld.getId();
                            //           cardView.setOnLongClickListener(new View.OnLongClickListener() {
                            //                   @Override
                            //                    public boolean onLongClick(View v) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ContactViewerActivity.this);
                            alertDialog.setMessage("Are you sure to delete this contact");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contacts").child(username).child(id);
                                    reference.removeValue();
                                    contacts.remove(position);
                                    adapter.notifyItemRemoved(position);

                                    Toast.makeText(ContactViewerActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                            //        return false;
                            //         }
                            //                 });

                        }
                    });
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
                //       ContactsAdapter adapter = new ContactsAdapter(contacts, ContactViewerActivity.this);
                //      int position = adapter.
                //        adapter.notifyDataSetChanged();
                //  recreate();
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