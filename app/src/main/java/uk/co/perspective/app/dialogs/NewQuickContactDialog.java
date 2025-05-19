package uk.co.perspective.app.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Contact;

public class NewQuickContactDialog extends DialogFragment {

    private View root;

    private int LocalCustomerID;
    private int LocalContactID;
    private String CustomerName;
    private String ContactName;

    private EditText contactName;
    private EditText jobTitle;

    Button okSelected;
    Button cancelSelected;

    NewQuickContactDialog.NewContactListener mListener;

    public interface NewContactListener {
        public void NewContact(int LocalContactID, String ContactName);
    }

    public NewQuickContactDialog() {
        // Required empty public constructor
    }

    public void setListener(NewContactListener listener) {
        mListener = listener;
    }

    public void setCustomer(int ID, String customerName) {
        LocalCustomerID = ID;
        CustomerName = customerName;
    }

    public static NewQuickContactDialog newInstance(NewContactListener mListener, int LocalCustomerID, String CustomerName) {
        NewQuickContactDialog frag = new NewQuickContactDialog();
        Bundle args = new Bundle();
        frag.setListener(mListener);
        frag.setCustomer(LocalCustomerID, CustomerName);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_new_quick_contact, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        contactName = root.findViewById(R.id.contact_name);
        jobTitle = root.findViewById(R.id.job_title);

        //Buttons

        okSelected = root.findViewById(R.id.save);
        cancelSelected = root.findViewById(R.id.cancel);

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!contactName.getText().toString().equals("")) {

                    //Save Contact

                    Contact newContact = new Contact();

                    newContact.setContactID(0);
                    newContact.setCustomerID(0);
                    newContact.setLocalCustomerID(LocalCustomerID);
                    newContact.setCustomerName(CustomerName);

                    newContact.setContactName(contactName.getText().toString());
                    newContact.setSalutation("");
                    newContact.setJobTitle(jobTitle.getText().toString());
                    newContact.setTelephone("");
                    newContact.setMobile("");
                    newContact.setEmail("");
                    newContact.setNotes("");

                    newContact.setIsChanged(false);
                    newContact.setIsNew(true);
                    newContact.setIsArchived(false);

                    Date updatedDate = new Date();

                    SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    newContact.setUpdated(targetDateFormat.format(updatedDate));

                    long ID = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .contactDao()
                            .insert(newContact);

                    mListener.NewContact((int)ID, contactName.getText().toString());

                    dismiss();
                }
                else
                {
                    int pL = contactName.getPaddingLeft();
                    int pT = contactName.getPaddingTop();
                    int pR = contactName.getPaddingRight();
                    int pB = contactName.getPaddingBottom();
                    contactName.setBackgroundResource(R.drawable.text_input_background_required);
                    contactName.setPadding(pL, pT, pR, pB);
                }
            }
        });

        cancelSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

    }

}