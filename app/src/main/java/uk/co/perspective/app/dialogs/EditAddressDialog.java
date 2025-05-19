package uk.co.perspective.app.dialogs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import uk.co.perspective.app.R;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Address;

public class EditAddressDialog extends DialogFragment {

    private View root;

    private int ID;

    private int LocalCustomerID;

    private Spinner addressType;
    private EditText address;

    Button okSelected;
    Button deleteSelected;
    Button cancelSelected;

    EditAddressDialog.UpdatedAddressListener mListener;

    public interface UpdatedAddressListener {
        public void AddressUpdated();
    }

    public EditAddressDialog() {
        // Required empty public constructor
    }

    public void setListener(UpdatedAddressListener listener) {
        mListener = listener;
    }

    public static EditAddressDialog newInstance(UpdatedAddressListener mListener) {
        EditAddressDialog frag = new EditAddressDialog();
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

        root = inflater.inflate(R.layout.dialog_edit_address, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        root.setBackgroundResource(R.drawable.dialog_rounded);

        addressType = root.findViewById(R.id.address_type);
        address = root.findViewById(R.id.address);

        okSelected = root.findViewById(R.id.save);
        deleteSelected = root.findViewById(R.id.delete);
        cancelSelected = root.findViewById(R.id.cancel);

        //Setup Spinners

        //Salutation

        final ArrayList<String> addressTypeSpinnerArray =  new ArrayList<>();

        addressTypeSpinnerArray.add("Primary");
        addressTypeSpinnerArray.add("Billing Address");
        addressTypeSpinnerArray.add("Delivery Address");
        addressTypeSpinnerArray.add("Departmental");
        addressTypeSpinnerArray.add("Other");

        ArrayAdapter<String> salutationAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_list_item, addressTypeSpinnerArray);
        salutationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressType.setAdapter(salutationAdapter);

        //Get Activity

        if (ID != 0) {

            Address theAddress = DatabaseClient
                    .getInstance(requireContext())
                    .getAppDatabase()
                    .addressDao()
                    .getAddress(ID);

            LocalCustomerID = theAddress.getLocalCustomerID();

            //Set Spinner Values

            int addressTypeIndex = salutationAdapter.getPosition(theAddress.getAddressType());
            addressType.setSelection(addressTypeIndex);

            address.setText(theAddress.getAddress().replaceAll("\\r\\r", "\n").replaceAll("\\r", "\n").replaceAll("\\n\\n", "\n"));
        }

        okSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!address.getText().toString().equals("")) {

                    if (ID != 0) {
                        SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                        //update Contact

                        Date updatedDate = new Date();

                        DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                .addressDao()
                                .updateAddress((String)addressType.getSelectedItem(), address.getText().toString(), targetDateFormat.format(updatedDate), ID);

                        //is this the default contact?

                        mListener.AddressUpdated();

                        dismiss();
                    }
                }
                else
                {
                    int pL = address.getPaddingLeft();
                    int pT = address.getPaddingTop();
                    int pR = address.getPaddingRight();
                    int pB = address.getPaddingBottom();
                    address.setBackgroundResource(R.drawable.text_input_background_required);
                    address.setPadding(pL, pT, pR, pB);
                }
            }
        });

        deleteSelected.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (!address.getText().toString().equals("")) {
                      if (ID != 0) {

                          Address address = DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                  .addressDao()
                                  .getAddress(ID);

                          if (address.getAddressID() != 0) {
                              new AlertDialog.Builder(requireContext())
                                      .setTitle("Archive Address")
                                      .setMessage("Are you sure you want to archive this address? It will be removed after the next successful sync")
                                      .setIcon(android.R.drawable.ic_delete)
                                      .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                          public void onClick(DialogInterface dialog, int whichButton) {

                                              DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                                      .addressDao()
                                                      .deleteAddress(ID);

//                                              DatabaseClient.getInstance(requireContext()).getAppDatabase()
//                                                      .addressDao()
//                                                      .archiveAddress(ID);

                                              mListener.AddressUpdated();

                                              dismiss();

                                          }
                                      })
                                      .setNegativeButton(android.R.string.no, null).show();
                          }
                          else
                          {
                              new AlertDialog.Builder(requireContext())
                                      .setTitle("Delete Address")
                                      .setMessage("Are you sure you want to delete this address?")
                                      .setIcon(android.R.drawable.ic_delete)
                                      .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                          public void onClick(DialogInterface dialog, int whichButton) {

                                              DatabaseClient.getInstance(requireContext()).getAppDatabase()
                                                      .addressDao()
                                                      .deleteAddress(ID);

                                              mListener.AddressUpdated();

                                              dismiss();

                                          }
                                      })
                                      .setNegativeButton(android.R.string.no, null).show();
                          }
                      }
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