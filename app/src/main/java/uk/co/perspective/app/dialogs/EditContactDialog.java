package uk.co.perspective.app.dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Contact;

public class EditContactDialog extends DialogFragment {

    private View root;

    private int ID;

    private int LocalCustomerID;

    private EditText contactName;
    private Spinner salutation;
    private EditText jobTitle;
    private EditText telephone;
    private EditText mobile;
    private EditText email;
    private EditText notes;

    private ConstraintLayout customerLookup;
    private TextView customerLookupName;

    Button makeDefault;
    Button okSelected;
    Button deleteSelected;
    Button cancelSelected;

    private ImageView callTelephone;
    private ImageView callMobile;
    private ImageView sendMessage;

    EditContactDialog.UpdatedContactListener mListener;

    public interface UpdatedContactListener {
        public void ContactUpdated();
        public void DefaultContactUpdated(String ContactName);
    }

    public EditContactDialog() {
        // Required empty public constructor
    }

    public void setListener(UpdatedContactListener listener) {
        mListener = listener;
    }

    public static EditContactDialog newInstance(UpdatedContactListener mListener) {
        EditContactDialog frag = new EditContactDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        frag.setListener(mListener);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ID = getArguments().getInt("ID");
        }
        else
        {
            ID = 0;
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.dialog_edit_contact, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        contactName = root.findViewById(R.id.contact_name);
        salutation = root.findViewById(R.id.salutation);
        jobTitle = root.findViewById(R.id.job_title);
        telephone = root.findViewById(R.id.telephone_number);
        mobile = root.findViewById(R.id.mobile_number);
        email= root.findViewById(R.id.email_address);
        notes = root.findViewById(R.id.notes);

        callTelephone = root.findViewById(R.id.callTelephone);
        callMobile = root.findViewById(R.id.callMobile);
        sendMessage = root.findViewById(R.id.sendMessage);

        //Buttons

        makeDefault = root.findViewById(R.id.makeDefault);
        okSelected = root.findViewById(R.id.save);
        deleteSelected = root.findViewById(R.id.delete);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Salutation

        final ArrayList<String> salutationSpinnerArray =  new ArrayList<>();

        salutationSpinnerArray.add("");
        salutationSpinnerArray.add("Mx");
        salutationSpinnerArray.add("Mr");
        salutationSpinnerArray.add("Mrs");
        salutationSpinnerArray.add("Miss");
        salutationSpinnerArray.add("Dr");
        salutationSpinnerArray.add("Ms");
        salutationSpinnerArray.add("Prof");
        salutationSpinnerArray.add("Rev");
        salutationSpinnerArray.add("Lady");
        salutationSpinnerArray.add("Sir");
        salutationSpinnerArray.add("The Hon");
        salutationSpinnerArray.add("Judge");
        salutationSpinnerArray.add("Lord");

        ArrayAdapter<String> salutationAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_list_item, salutationSpinnerArray);
        salutationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salutation.setAdapter(salutationAdapter);

        //Get Activity

        if (ID != 0) {

            Contact contact = DatabaseClient
                    .getInstance(requireContext())
                    .getAppDatabase()
                    .contactDao()
                    .getContact(ID);

            LocalCustomerID = contact.getLocalCustomerID();

            contactName.setText(contact.getContactName());

            //Set Spinner Values

            int salutationIndex = salutationAdapter.getPosition(contact.getSalutation());
            salutation.setSelection(salutationIndex);

            jobTitle.setText(contact.getJobTitle());
            telephone.setText(contact.getTelephone());
            mobile.setText(contact.getMobile());
            email.setText(contact.getEmail());
            notes.setText(contact.getNotes());

        }

        callTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!telephone.getText().toString().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + telephone.getText().toString()));
                    startActivity(intent);
                }
            }
        });

        callMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mobile.getText().toString().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mobile.getText().toString()));
                    startActivity(intent);
                }
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email.getText().toString()));
                    intent.putExtra(Intent.EXTRA_EMAIL, email.getText().toString());
                    startActivity(intent);
                }
            }
        });

        makeDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LocalCustomerID != 0) {
                    DatabaseClient.getInstance(requireContext()).getAppDatabase()
                            .customerDao()
                            .updateCustomerContactName(contactName.getText().toString(), LocalCustomerID);

                    mListener.DefaultContactUpdated(contactName.getText().toString());
                }
            }
        });

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!contactName.getText().toString().equals("")) {

                    if (ID != 0) {
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                        //update Contact

                        Date updatedDate = new Date();

                        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                .contactDao()
                                .updateContact(contactName.getText().toString(), (String) salutation.getSelectedItem(), jobTitle.getText().toString(), telephone.getText().toString(), mobile.getText().toString(), email.getText().toString(), notes.getText().toString(), targetDateFormat.format(updatedDate), ID);

                        //is this the default contact?

                        mListener.ContactUpdated();

                        dismiss();
                    }
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

        deleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (ID != 0) {

                        Contact contact = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                .contactDao()
                                .getContact(ID);

                        if (contact.getContactID() != 0) {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Archive Contact")
                                    .setMessage("Are you sure you want to archive this contact? It will be removed after the next successful sync")
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                                    .contactDao()
                                                    .deleteContact(ID);

//                                              DatabaseClient.getInstance(requireContext()).getAppDatabase()
//                                                      .addressDao()
//                                                      .archiveAddress(ID);

                                            mListener.ContactUpdated();

                                            dismiss();

                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                        else
                        {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Delete Contact")
                                    .setMessage("Are you sure you want to delete this contact?")
                                    .setIcon(android.R.drawable.ic_delete)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                                    .contactDao()
                                                    .deleteContact(ID);

                                            mListener.ContactUpdated();

                                            dismiss();

                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                    }

            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

    }

}