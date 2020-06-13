package apollo.safety.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.List;

import apollo.safety.Models.Contacts;
import apollo.safety.R;


public class ContactPickerActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 991;
    private List<ContactResult> results = new ArrayList<>();
    DatabaseReference databaseReference;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_picker);
        databaseReference = FirebaseDatabase.getInstance().getReference("contacts");
        new MultiContactPicker.Builder(ContactPickerActivity.this) //Activity/fragment context
                //  .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_SINGLE) //Optional - default: CHOICE_MODE_MULTIPLE
                .handleColor(ContextCompat.getColor(ContactPickerActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(ContactPickerActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .setTitleText("Select 1 Contact") //Optional - default: Select Contacts
                //   .setSelectedContacts("10", "5" / myList) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
                .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                .limitToColumn(LimitColumn.PHONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out) //Optional - default: No animation overrides
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                results = MultiContactPicker.obtainResult(data);
                results.addAll(MultiContactPicker.obtainResult(data));
                String name = results.get(0).getDisplayName();
                String number = results.get(0).getPhoneNumbers().get(0).getNumber();
                if(!TextUtils.isEmpty(name)){
                    String id = databaseReference.push().getKey();
               //     LoginActivity loginActivity = new LoginActivity();
               //     String id = loginActivity.userName();
                    preferences = getSharedPreferences("apollo.safety.USERNAME",MODE_PRIVATE);
                    String username = preferences.getString("Username","");
                    Contacts contacts = new Contacts(name,number,id);
                    databaseReference.child(username).child(id).setValue(contacts);
                    Toast.makeText(this, "1 contacts added", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No contacts selected", Toast.LENGTH_SHORT).show();
            }
        }
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              //  s = String.valueOf(dataSnapshot.getValue(Contacts.class).getName());
              //  Toast.makeText(ContactPickerActivity.this, s + " added to list", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
   //     Toast.makeText(this, "Contacts Added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
