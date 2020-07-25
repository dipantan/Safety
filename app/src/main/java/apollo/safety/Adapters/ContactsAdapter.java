package apollo.safety.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import apollo.safety.Models.Contacts;
import apollo.safety.R;
import apollo.safety.Utils.OnLongClick;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private List<Contacts> contacts;
    private Context context;
    private OnLongClick onLongClick;

    public ContactsAdapter(List<Contacts> contacts, Context context, OnLongClick onLongClick) {
        this.contacts = contacts;
        this.context = context;
        this.onLongClick = onLongClick;
    }


    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.list_contacts, parent, false);
        final ContactsViewHolder holder = new ContactsViewHolder(view);
      //  holder.setIsRecyclable(false);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongClick.onLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position) {

        final Contacts ld = contacts.get(position);
        holder.name.setText(ld.getName());        //set name
        holder.number.setText(ld.getNumber());   //set number


  /*      final SharedPreferences preferences = context.getSharedPreferences("apollo.safety.USERNAME", Context.MODE_PRIVATE);
        final String username = preferences.getString("Username", "");
        final String id = ld.getId();
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("Are you sure to delete this contact");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contacts").child(username).child(id);
                        reference.removeValue();
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
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
                return false;
            }
        });      */
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
     //   CardView cardView;

        ContactsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameContact);
            number = itemView.findViewById(R.id.numContact);
        //    cardView = itemView.findViewById(R.id.cardView);
        }
    }

}