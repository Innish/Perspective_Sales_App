package uk.co.perspective.app.services;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.perspective.app.MainActivity;
import uk.co.perspective.app.R;
import uk.co.perspective.app.database.AppDatabase;
import uk.co.perspective.app.database.DatabaseClient;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.entities.Address;
import uk.co.perspective.app.entities.Contact;
import uk.co.perspective.app.entities.Currency;
import uk.co.perspective.app.entities.Customer;
import uk.co.perspective.app.entities.Lead;
import uk.co.perspective.app.entities.Opportunity;
import uk.co.perspective.app.entities.OpportunityContact;
import uk.co.perspective.app.entities.OpportunityFile;
import uk.co.perspective.app.entities.OpportunityForm;
import uk.co.perspective.app.entities.OpportunityQuote;
import uk.co.perspective.app.entities.Order;
import uk.co.perspective.app.entities.OrderLine;
import uk.co.perspective.app.entities.Product;
import uk.co.perspective.app.entities.Project;
import uk.co.perspective.app.entities.ProjectFile;
import uk.co.perspective.app.entities.ProjectPhase;
import uk.co.perspective.app.entities.ProjectTask;
import uk.co.perspective.app.entities.Quote;
import uk.co.perspective.app.entities.QuoteLine;
import uk.co.perspective.app.entities.Task;

public class SyncService extends Worker {

    private static final String CHANNEL_ID = "MBRDY7242119782912";

    final EndPoints endPoint = RetrofitClientInstance.getRetrofitInstance(getApplicationContext()).create(EndPoints.class);

    public SyncService(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int PeopleID = sharedPreferences.getInt("peopleID", 0);

        AppDatabase database = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();

        //Other Contact Operations *************************************************************************

        //Step 1 - Update Contacts From Local Database To Server

        List<Contact> updatedContacts = database
                .contactDao()
                .getChangedContacts();

        for(Contact updatedContact : updatedContacts) {

            if (updatedContact.getContactID() != null) {

                if (updatedContact.getCreatedByDisplayName() == null) {
                    updatedContact.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Contact> updateContactCall = endPoint.updateContact(updatedContact);
                updateContactCall.enqueue(new Callback<Contact>() {

                    @Override
                    public void onResponse(@NonNull Call<Contact> call, @NonNull Response<Contact> response) {

                        if (response.body() != null) {

                            Contact responseContact = ((Contact) response.body());

                            if (responseContact.getId() != 0) {
                                database.contactDao().updateContactIsChanged(responseContact.getId(), 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Contact> call, @NotNull Throwable t) {
                        Log.w("Sync - Update Contact From Local", t.getMessage());
                    }
                });
            }
            else
            {
                //Can't update remote server without a taskID so just mark as done
                database.contactDao().updateContactIsChanged(updatedContact.getId(), 0);
            }
        }

        //Step 2 - Create new Contacts On Server From Local Database

        List<Contact> newContacts = database
                .contactDao()
                .getNewContacts();

        for(Contact newContact : newContacts) {

            if (newContact.getCreatedByDisplayName() != null) {
                if (newContact.getCreatedByDisplayName().equals("")) {
                    newContact.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newContact.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Contact> newContactCall = endPoint.createNewContact(newContact);
            newContactCall.enqueue(new Callback<Contact>() {

                @Override
                public void onResponse(@NonNull Call<Contact> call, @NonNull Response<Contact> response) {

                    if (response.body() != null) {

                        Contact responseContact = ((Contact)response.body());

                        if (responseContact.getContactID() != 0) {
                            database.contactDao().updateContactIsNew(responseContact.getId(), 0, responseContact.getContactID());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Contact> call, @NotNull Throwable t) {
                    Log.w("Sync - New Address", t.getMessage());
                }
            });
        }

        //Other Addresses Operations *************************************************************************

        //Step 1 - Update Addresses From Local Database To Server

        List<Address> updatedAddresses = database
                .addressDao()
                .getChangedAddresses();

        for(Address updatedAddress : updatedAddresses) {

            if (updatedAddress.getAddressID() != null) {

                if (updatedAddress.getCreatedByDisplayName() == null) {
                    updatedAddress.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Address> updateAddressCall = endPoint.updateAddress(updatedAddress);
                updateAddressCall.enqueue(new Callback<Address>() {

                    @Override
                    public void onResponse(@NonNull Call<Address> call, @NonNull Response<Address> response) {

                        if (response.body() != null) {

                            Address responseAddress = ((Address) response.body());

                            if (responseAddress.getId() != 0) {
                                database.addressDao().updateAddressIsChanged(responseAddress.getId(), 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Address> call, @NotNull Throwable t) {
                        Log.w("Sync - Update Address From Local", t.getMessage());
                    }
                });
            }
            else
            {
                //Can't update remote server without a taskID so just mark as done
                database.addressDao().updateAddressIsChanged(updatedAddress.getId(), 0);
            }
        }

        //Step 2 - Create new Addresses On Server From Local Database

        List<Address> newAddresses = database
                .addressDao()
                .getNewAddresses();

        for(Address newAddress : newAddresses) {

            if (newAddress.getCreatedByDisplayName() != null) {
                if (newAddress.getCreatedByDisplayName().equals("")) {
                    newAddress.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newAddress.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Address> newAddressCall = endPoint.createNewAddress(newAddress);
            newAddressCall.enqueue(new Callback<Address>() {

                @Override
                public void onResponse(@NonNull Call<Address> call, @NonNull Response<Address> response) {

                    if (response.body() != null) {

                        Address responseAddress = ((Address)response.body());

                        if (responseAddress.getCustomerID() != 0) {
                            database.addressDao().updateAddressIsNew(responseAddress.getId(), 0, responseAddress.getAddressID());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Address> call, @NotNull Throwable t) {
                    Log.w("Sync - New Address", t.getMessage());
                }
            });
        }

        //Customers *********************************************************************

        //Step 1 - Update Customers From Local Database To Server

        List<Customer> updatedCustomers = database
                .customerDao()
                .getChangedCustomers();

        for(Customer updatedCustomer : updatedCustomers) {

            if (updatedCustomer.getCustomerID() != null) {

                if (updatedCustomer.getCreatedByDisplayName() == null) {
                    updatedCustomer.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Customer> updateCustomerCall = endPoint.updateCustomer(updatedCustomer);
                updateCustomerCall.enqueue(new Callback<Customer>() {

                    @Override
                    public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {

                        if (response.body() != null) {

                            Customer responseCustomer = ((Customer) response.body());

                            if (responseCustomer.getId() != 0) {
                                database.customerDao().updateCustomerIsChanged(responseCustomer.getId(), 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Customer> call, @NotNull Throwable t) {
                        Log.w("Sync - Update Tasks From Local", t.getMessage());
                    }
                });
            }
            else
            {
                //Can't update remote server without a taskID so just mark as done
                database.customerDao().updateCustomerIsChanged(updatedCustomer.getId(), 0);
            }
        }

        //Step 2 - Get Customer List From Server

        Call<List<Customer>> getCustomersCall = endPoint.getCustomers("");
        getCustomersCall.enqueue(new Callback<List<Customer>>() {

            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {

                if (response.body() != null) {

                    Executors.newFixedThreadPool(1).execute(() -> {

                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        for (Customer customer : response.body()) {
                            CreateOrUpdateCustomer(customer);
                        }

                        mainHandler.post(() -> {
                            //Update UI
                        });
                    });

//                    class insertOrUpdateCustomerInBackend extends AsyncTask<Void, Void, Void> {
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//
//                            for (Customer customer : response.body()) {
//                                CreateOrUpdateCustomer(customer);
//                            }
//
//                            return null;
//                        }
//                    }
//
//                    insertOrUpdateCustomerInBackend st = new insertOrUpdateCustomerInBackend();
//                    st.execute();

                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Customer>> call, @NotNull Throwable t) {
                Log.w("Sync - Update Customers From Server", t.getMessage());
            }
        });

        //Get All Contacts (Only getting ones for current customer creates 100's of calls to the API needlessly so just grab them all them sort locally!!

        Call<List<Contact>> getCustomerContactsCall = endPoint.getContacts();
        getCustomerContactsCall.enqueue(new Callback<List<Contact>>() {

            @Override
            public void onResponse(@NonNull Call<List<Contact>> call, @NonNull Response<List<Contact>> response) {

                if (response.body() != null) {

                    for (Contact contact : response.body()) {
                        CreateOrUpdateCustomerContact(contact);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Contact>> call, @NotNull Throwable t) {
                Log.w("Sync - Update Contacts From Server", t.getMessage());
            }
        });

        //Get All Addresses (Only getting ones for current customer creates 100's of calls to the API needlessly so just grab them all them sort locally!!

        Call<List<Address>> getCustomerAddressesCall = endPoint.getAddresses();
        getCustomerAddressesCall.enqueue(new Callback<List<Address>>() {

            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {

                if (response.body() != null) {

                    for (Address address : response.body()) {
                        CreateOrUpdateCustomerAddress(address);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Address>> call, @NotNull Throwable t) {
                Log.w("Sync - Update Addresses From Server", t.getMessage());
            }
        });

        //Get All Activity (Only getting ones for current customer creates 100's of calls to the API needlessly so just grab them all them sort locally!!

        Call<List<Activity>> getActivitiesCall = endPoint.getActivity();
        getActivitiesCall.enqueue(new Callback<List<Activity>>() {

            @Override
            public void onResponse(@NonNull Call<List<Activity>> call, @NonNull Response<List<Activity>> response) {

                if (response.body() != null) {

                    class insertOrUpdateActivityInBackend extends AsyncTask<Void, Void, Void> {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            Executors.newFixedThreadPool(1).execute(() -> {

                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                for (Activity activity : response.body()) {
                                    CreateOrUpdateActivity(activity);
                                }

                                mainHandler.post(() -> {
                                    //Update UI
                                });
                            });

                            return null;
                        }
                    }

                    insertOrUpdateActivityInBackend st = new insertOrUpdateActivityInBackend();
                    st.execute();

                };
            }

            @Override
            public void onFailure(@NotNull Call<List<Activity>> call, @NotNull Throwable t) {
                Log.w("Sync - Activity", t.getMessage());
            }
        });

        //Step 3 - Create new Customers On Server From Local Database

        List<Customer> newCustomers = database
                .customerDao()
                .getNewCustomers();

        for(Customer newCustomer : newCustomers) {

            if (newCustomer.getCreatedByDisplayName() != null) {
                if (newCustomer.getCreatedByDisplayName().equals("")) {
                    newCustomer.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newCustomer.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Customer> newTaskCall = endPoint.createNewCustomer(newCustomer);
            newTaskCall.enqueue(new Callback<Customer>() {

                @Override
                public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {

                    if (response.body() != null) {

                        Customer responseCustomer = ((Customer)response.body());

                        if (responseCustomer.getCustomerID() != 0) {
                            database.customerDao().updateCustomerIsNew(responseCustomer.getId(), 0, responseCustomer.getCustomerID());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Customer> call, @NotNull Throwable t) {
                    Log.w("Sync - New Customer", t.getMessage());
                }
            });
        }

        //Activity

        //Step 1 - Update Opportunities From Local Database To Server

        List<Activity> updatedActivities = database
                .activityDao()
                .getChangedActivities();

        for(Activity updatedActivity : updatedActivities) {

            if (updatedActivity.getJournalEntryID() != null) {

                if (updatedActivity.getCreatedByDisplayName() == null) {
                    updatedActivity.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Activity> updateActivityCall = endPoint.updateActivity(updatedActivity);
                updateActivityCall.enqueue(new Callback<Activity>() {

                    @Override
                    public void onResponse(@NonNull Call<Activity> call, @NonNull Response<Activity> response) {

                        if (response.body() != null) {

                            Activity responseActivity = ((Activity) response.body());

                            if (responseActivity.getId() != 0) {
                                database.activityDao().updateActivityIsChanged(responseActivity.getId(), 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Activity> call, @NotNull Throwable t) {
                        Log.w("Sync - Activity Update Failed", t.getMessage());
                    }
                });
            }
            else
            {
                //Can't update remote server without a taskID so just mark as done
                database.opportunityDao().updateOpportunityIsChanged(updatedActivity.getId(), 0);
            }
        }

        //Step 2 - Create new activities

        List<Activity> newActivities = database
                .activityDao()
                .getNewActivities();

        for(Activity newActivity : newActivities) {

            if (newActivity.getCreatedByDisplayName() != null) {
                if (newActivity.getCreatedByDisplayName().equals("")) {
                    newActivity.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newActivity.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            if (newActivity.getCustomerID() == 0)
            {
                Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomer(newActivity.getLocalCustomerID());

                int CustomerID = 0;

                if (localCustomer != null)
                {
                    CustomerID = localCustomer.getCustomerID();
                }

                newActivity.setCustomerID(CustomerID);
            }

            Call<Activity> newActivityCall = endPoint.createNewActivity(newActivity);
            newActivityCall.enqueue(new Callback<Activity>() {

                @Override
                public void onResponse(@NonNull Call<Activity> call, @NonNull Response<Activity> response) {

                    if (response.body() != null) {

                        Activity responseActivity = ((Activity)response.body());

                        if (responseActivity.getJournalEntryID() != 0) {
                            database.activityDao().updateActivityIsNew(responseActivity.getId(), 0, responseActivity.getJournalEntryID(), responseActivity.getCustomerID());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Activity> call, @NotNull Throwable t) {
                    Log.w("Sync - New Activity", t.getMessage());
                }
            });
        }

        //Create new forms

        List<OpportunityForm> newForms = database
                .opportunityFormDao()
                .getNewOpportunityForms();

        for(OpportunityForm newForm : newForms) {

            Call<OpportunityForm> newFormCall = endPoint.createNewForm(newForm);
            newFormCall.enqueue(new Callback<OpportunityForm>() {

                @Override
                public void onResponse(@NonNull Call<OpportunityForm> call, @NonNull Response<OpportunityForm> response) {

                    if (response.body() != null) {

                        OpportunityForm responseForm = ((OpportunityForm)response.body());

                        if (responseForm.getOpportunityFormID() != 0) {

                            database.opportunityFormDao().updateIsNew(responseForm.getOpportunityID(), false);

                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<OpportunityForm> call, @NotNull Throwable t) {
                    Log.w("Sync - New Form", t.getMessage());
                }
            });
        }

        //Leads *************************************************************************

        //Step 1 - Update Opportunities From Local Database To Server

        List<Lead> updatedLeads = database
                .leadDao()
                .getChangedLeads();

        for(Lead updatedLead : updatedLeads) {

            if (updatedLead.getLeadID() != null) {

                if (updatedLead.getCreatedByDisplayName() == null) {
                    updatedLead.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Lead> updateLeadCall = endPoint.updateLead(updatedLead);
                updateLeadCall.enqueue(new Callback<Lead>() {

                    @Override
                    public void onResponse(@NonNull Call<Lead> call, @NonNull Response<Lead> response) {

                        if (response.body() != null) {

                            Lead responseLead = ((Lead) response.body());

                            if (responseLead.getId() != 0) {
                                database.leadDao().updateLeadIsChanged(responseLead.getId(), 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Lead> call, @NotNull Throwable t) {
                        Log.w("Sync - Leads Update Failed", t.getMessage());
                    }
                });
            }
            else
            {
                //Can't update remote server without a taskID so just mark as done
                database.opportunityDao().updateOpportunityIsChanged(updatedLead.getId(), 0);
            }
        }

        //Step 2 - Get Leads List From Server

        Call<List<Lead>> getLeadsCall = endPoint.getLeads(PeopleID);
        getLeadsCall.enqueue(new Callback<List<Lead>>() {

            @Override
            public void onResponse(@NonNull Call<List<Lead>> call, @NonNull Response<List<Lead>> response) {

                if (response.body() != null) {

                    for (Lead lead : response.body()) {
                        CreateOrUpdateLead(lead);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Lead>> call, @NotNull Throwable t) {
                Log.w("Sync - Leads", t.getMessage());
            }
        });

        //Step 3 - Create new Leads On Server From Local Database

        List<Lead> newLeads = database
                .leadDao()
                .getNewLeads();

        for(Lead newLead : newLeads) {

            if (newLead.getCreatedByDisplayName() != null) {
                if (newLead.getCreatedByDisplayName().equals("")) {
                    newLead.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newLead.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Lead> newAddressCall = endPoint.createNewLead(newLead);
            newAddressCall.enqueue(new Callback<Lead>() {

                @Override
                public void onResponse(@NonNull Call<Lead> call, @NonNull Response<Lead> response) {

                    if (response.body() != null) {

                        Lead responseLead = ((Lead)response.body());

                        if (responseLead.getLeadID() != 0) {
                            database.leadDao().updateLeadIsNew(responseLead.getId(), 0, responseLead.getLeadID());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Lead> call, @NotNull Throwable t) {
                    Log.w("Sync - New Lead", t.getMessage());
                }
            });
        }

        //Quotes *************************************************************************

        //Step 1 - Update (Quote Lines from existing quotes) From Local Database To Server

        List<QuoteLine> updatedQuoteLines = database
                .quoteLineDao()
                .getChangedQuoteLines();

        for(QuoteLine updatedQuoteLine : updatedQuoteLines) {

            Call<QuoteLine> newQuoteLineCall = endPoint.updateQuoteLine(updatedQuoteLine);

            newQuoteLineCall.enqueue(new Callback<QuoteLine>() {

                @Override
                public void onResponse(@NonNull Call<QuoteLine> call, @NonNull Response<QuoteLine> response) {

                    if (response.body() != null)
                    {
                        QuoteLine responseQuoteLine = ((QuoteLine)response.body());

                        if (responseQuoteLine.getQuoteLineID() != 0) {
                            database.quoteLineDao().updateQuoteLineIsChanged(updatedQuoteLine.getId(), 0);
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<QuoteLine> call, @NotNull Throwable t) {
                    Log.w("Sync - Update Quote Line Failed", t.getMessage());
                }
            });
        }

        //Step 2

        Call<List<Quote>> getQuotesCall = endPoint.getQuotes(PeopleID);
        getQuotesCall.enqueue(new Callback<List<Quote>>() {

            @Override
            public void onResponse(@NonNull Call<List<Quote>> call, @NonNull Response<List<Quote>> response) {

                if (response.body() != null) {

                    for (Quote quote : response.body()) {
                        CreateOrUpdateQuote(quote);

                        //Get Quote Lines

                        if (quote.getQuoteID() != null) {

                            Call<List<QuoteLine>> getQuoteLinesCall = endPoint.getQuoteLines(quote.getQuoteID());
                            getQuoteLinesCall.enqueue(new Callback<List<QuoteLine>>() {

                                @Override
                                public void onResponse(@NonNull Call<List<QuoteLine>> call, @NonNull Response<List<QuoteLine>> response) {

                                    if (response.body() != null) {

                                        Executors.newFixedThreadPool(1).execute(() -> {

                                            Handler mainHandler = new Handler(Looper.getMainLooper());

                                            for (QuoteLine quoteLine : response.body()) {
                                                CreateOrUpdateQuoteLine(quoteLine);
                                            }

                                            mainHandler.post(() -> {
                                                //Update UI
                                            });
                                        });

                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<List<QuoteLine>> call, @NotNull Throwable t) {
                                    Log.w("Sync - Quote Lines", t.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Quote>> call, @NotNull Throwable t) {
                Log.w("Sync - Quotes", t.getMessage());
            }
        });

        //Step 3 - Create new Quotes On Server From Local Database

        List<Quote> newQuotes = database
                .quoteDao()
                .getNewQuotes();

        for(Quote newQuote : newQuotes) {

            if (newQuote.getCreatedByDisplayName() != null) {
                if (newQuote.getCreatedByDisplayName().equals("")) {
                    newQuote.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newQuote.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Quote> newQuoteCall = endPoint.createNewQuote(newQuote);
            newQuoteCall.enqueue(new Callback<Quote>() {

                @Override
                public void onResponse(@NonNull Call<Quote> call, @NonNull Response<Quote> response) {

                    if (response.body() != null) {

                        Quote responseQuote = ((Quote)response.body());

                        if (responseQuote.getQuoteID() != 0) {

                            database.quoteDao().updateQuoteIsNew(responseQuote.getId(), 0, responseQuote.getQuoteID(), responseQuote.getReference());

                            //Add Lines

                            List<QuoteLine> newQuoteLines = database
                                    .quoteLineDao()
                                    .getQuoteLinesFromLocalID(newQuote.getId());

                            for(QuoteLine newQuoteLine : newQuoteLines) {

                                newQuoteLine.setQuoteID(responseQuote.getQuoteID());

                                Call<QuoteLine> newQuoteLineCall = endPoint.createNewQuoteLine(newQuoteLine);

                                newQuoteLineCall.enqueue(new Callback<QuoteLine>() {

                                    @Override
                                    public void onResponse(@NonNull Call<QuoteLine> call, @NonNull Response<QuoteLine> response) {

                                        if (response.body() != null)
                                        {
                                            QuoteLine responseQuoteLine = ((QuoteLine)response.body());

                                            if (responseQuoteLine.getQuoteLineID() != 0) {
                                                database.quoteLineDao().updateQuoteLineIsNew(newQuoteLine.getId(), 0, responseQuote.getQuoteID(), responseQuoteLine.getQuoteLineID());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<QuoteLine> call, @NotNull Throwable t) {
                                        Log.w("Sync - Create Quote Line Failed", t.getMessage());
                                    }
                                });
                            }

                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Quote> call, @NotNull Throwable t) {
                    Log.w("Sync - Create New Quote Failed", t.getMessage());
                }
            });
        }

        //Step 4 - Add new quote lines from existing quotes

        //Must be linked to an existing quote

        List<QuoteLine> newQuoteLines = database
                .quoteLineDao()
                .getNewQuoteLines();

        for(QuoteLine newQuoteLine : newQuoteLines) {

            Call<QuoteLine> newQuoteLineCall = endPoint.createNewQuoteLine(newQuoteLine);

            newQuoteLineCall.enqueue(new Callback<QuoteLine>() {

                @Override
                public void onResponse(@NonNull Call<QuoteLine> call, @NonNull Response<QuoteLine> response) {

                    if (response.body() != null)
                    {
                        QuoteLine responseQuoteLine = ((QuoteLine)response.body());

                        if (responseQuoteLine.getQuoteLineID() != 0) {
                            database.quoteLineDao().updateQuoteLineIsNew(newQuoteLine.getId(), 0, responseQuoteLine.getQuoteLineID());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<QuoteLine> call, @NotNull Throwable t) {
                    Log.w("Sync - Create Quote Line Failed", t.getMessage());
                }
            });
        }

        //Orders *************************************************************************

        //Step 1 - Update (Order Lines from existing orders) From Local Database To Server

        List<OrderLine> updatedOrderLines = database
                .orderLineDao()
                .getChangedOrderLines();

        for(OrderLine updatedOrderLine : updatedOrderLines) {

            Call<OrderLine> newOrderLineCall = endPoint.updateOrderLine(updatedOrderLine);

            newOrderLineCall.enqueue(new Callback<OrderLine>() {

                @Override
                public void onResponse(@NonNull Call<OrderLine> call, @NonNull Response<OrderLine> response) {

                    if (response.body() != null)
                    {
                        OrderLine responseOrderLine = ((OrderLine)response.body());

                        if (responseOrderLine.getOrderLineID() != 0) {
                            database.quoteLineDao().updateQuoteLineIsChanged(updatedOrderLine.getId(), 0);
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<OrderLine> call, @NotNull Throwable t) {
                    Log.w("Sync - Update Order Line Failed", t.getMessage());
                }
            });
        }

        //Step 2

        Call<List<Order>> getOrdersCall = endPoint.getOrders(PeopleID, "%%");
        getOrdersCall.enqueue(new Callback<List<Order>>() {

            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {

                if (response.body() != null) {

                    for (Order order : response.body()) {

                        CreateOrUpdateOrder(order);

                        //Get Quote Lines

                        if (order.getOrderID() != null) {

                            Call<List<OrderLine>> getOrderLinesCall = endPoint.getOrderLines(order.getOrderID());
                            getOrderLinesCall.enqueue(new Callback<List<OrderLine>>() {

                                @Override
                                public void onResponse(@NonNull Call<List<OrderLine>> call, @NonNull Response<List<OrderLine>> response) {

                                    if (response.body() != null) {

                                        for (OrderLine orderLine : response.body()) {
                                            CreateOrUpdateOrderLine(orderLine);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<List<OrderLine>> call, @NotNull Throwable t) {
                                    Log.w("Sync - Order Lines", t.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Order>> call, @NotNull Throwable t) {
                Log.w("Sync - Orders", t.getMessage());
            }
        });

        //Step 3 - Create new Orders On Server From Local Database

        List<Order> newOrders = database
                .orderDao()
                .getNewOrders();

        for(Order newOrder : newOrders) {

            if (newOrder.getCreatedByDisplayName() != null) {
                if (newOrder.getCreatedByDisplayName().equals("")) {
                    newOrder.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newOrder.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Order> newOrderCall = endPoint.createNewOrder(newOrder);
            newOrderCall.enqueue(new Callback<Order>() {

                @Override
                public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {

                    if (response.body() != null) {

                        Order responseOrder = ((Order)response.body());

                        if (responseOrder.getOrderID() != 0) {

                            database.orderDao().updateOrderIsNew(responseOrder.getId(), 0, responseOrder.getOrderID(), responseOrder.getReference());

                            //Add Lines

                            List<OrderLine> newOrderLines = database
                                    .orderLineDao()
                                    .getOrderLinesFromLocalID(newOrder.getId());

                            for(OrderLine newOrderLine : newOrderLines) {


                                newOrderLine.setOrderID(responseOrder.getOrderID());

                                Call<OrderLine> newOrderLineCall = endPoint.createNewOrderLine(newOrderLine);

                                newOrderLineCall.enqueue(new Callback<OrderLine>() {

                                    @Override
                                    public void onResponse(@NonNull Call<OrderLine> call, @NonNull Response<OrderLine> response) {

                                        if (response.body() != null)
                                        {
                                            OrderLine responseOrderLine = ((OrderLine)response.body());

                                            if (responseOrderLine.getOrderLineID() != 0) {
                                                database.orderLineDao().updateOrderLineIsNew(newOrderLine.getId(), 0, responseOrder.getOrderID(), responseOrderLine.getOrderLineID());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<OrderLine> call, @NotNull Throwable t) {
                                        Log.w("Sync - Create Order Line Failed", t.getMessage());
                                    }
                                });
                            }

                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Order> call, @NotNull Throwable t) {
                    Log.w("Sync - Create New Order Failed", t.getMessage());
                }
            });
        }

        //Step 4 - Add new order lines from existing orders

        //Must be linked to an existing order

        List<OrderLine> newOrderLines = database
                .orderLineDao()
                .getNewOrderLines();

        for(OrderLine newOrderLine : newOrderLines) {

            Call<OrderLine> newOrderLineCall = endPoint.createNewOrderLine(newOrderLine);

            newOrderLineCall.enqueue(new Callback<OrderLine>() {

                @Override
                public void onResponse(@NonNull Call<OrderLine> call, @NonNull Response<OrderLine> response) {

                    if (response.body() != null)
                    {
                        OrderLine responseOrderLine = ((OrderLine)response.body());

                        if (responseOrderLine.getOrderLineID() != 0) {
                            database.orderLineDao().updateOrderLineIsNew(newOrderLine.getId(), 0, responseOrderLine.getOrderLineID());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<OrderLine> call, @NotNull Throwable t) {
                    Log.w("Sync - Create Order Line Failed", t.getMessage());
                }
            });
        }

        //Opportunities *************************************************************************

        //Step 1 - Update Opportunities From Local Database To Server

        List<Opportunity> updatedOpportunities = database
                .opportunityDao()
                .getChangedOpportunities();

        for(Opportunity updatedOpportunity : updatedOpportunities) {

            if (updatedOpportunity.getOpportunityID() != null) {

                if (updatedOpportunity.getCreatedByDisplayName() == null) {
                    updatedOpportunity.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Opportunity> updateOpportunityCall = endPoint.updateOpportunity(updatedOpportunity);
                updateOpportunityCall.enqueue(new Callback<Opportunity>() {

                    @Override
                    public void onResponse(@NonNull Call<Opportunity> call, @NonNull Response<Opportunity> response) {

                        if (response.body() != null) {

                            Opportunity responseOpportunity = ((Opportunity) response.body());

                            if (responseOpportunity.getId() != 0) {
                                database.opportunityDao().updateOpportunityIsChanged(responseOpportunity.getId(), 0);

                                //Create Quotes Links to Opportunity if needed

                                List<OpportunityQuote> newOpportunityQuotes = database
                                        .opportunityDao()
                                        .getOpportunityQuotesByLocalOpportunityID(responseOpportunity.getId());

                                for(OpportunityQuote newOpportunityQuote : newOpportunityQuotes) {

                                    newOpportunityQuote.setOpportunityID(responseOpportunity.getOpportunityID());

                                    int QuoteID = 0;

                                    if (newOpportunityQuote.getQuoteID() != null)
                                    {
                                        QuoteID = newOpportunityQuote.getQuoteID();
                                    }
                                    else
                                    {
                                        Quote quote = database
                                                .quoteDao()
                                                .getQuote(newOpportunityQuote.getLocalQuoteID());

                                        if (quote.getQuoteID() != null)
                                        {
                                            QuoteID = quote.getQuoteID();
                                            newOpportunityQuote.setQuoteID(QuoteID);
                                        }
                                    }

                                    if (QuoteID != 0) //The quote must of been created on the remote server before we can link it.
                                    {

                                        Call<OpportunityQuote> newOpportunityQuoteCall = endPoint.createNewOpportunityQuote(newOpportunityQuote);

                                        int finalQuoteID = QuoteID;

                                        newOpportunityQuoteCall.enqueue(new Callback<OpportunityQuote>() {

                                            @Override
                                            public void onResponse(@NonNull Call<OpportunityQuote> call, @NonNull Response<OpportunityQuote> response) {

                                                if (response.body() != null)
                                                {
                                                    database.opportunityDao().updateOpportunityQuote(newOpportunityQuote.getId(), finalQuoteID, responseOpportunity.getOpportunityID());
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NotNull Call<OpportunityQuote> call, @NotNull Throwable t) {
                                                Log.w("Sync - Create Opportunity Quote Failed", t.getMessage());
                                            }
                                        });
                                    }
                                }

                                //Create Contacts Links to Opportunity if needed

                                List<OpportunityContact> newOpportunityContacts = database
                                        .opportunityDao()
                                        .getOpportunityContactsByLocalOpportunityID(responseOpportunity.getId());

                                for(OpportunityContact newOpportunityContact : newOpportunityContacts) {

                                    newOpportunityContact.setOpportunityID(responseOpportunity.getOpportunityID());

                                    int ContactID = 0;

                                    if (newOpportunityContact.getContactID() != null)
                                    {
                                        ContactID = newOpportunityContact.getContactID();
                                    }
                                    else
                                    {
                                        Contact contact = database
                                                .contactDao()
                                                .getContact(newOpportunityContact.getLocalContactID());

                                        if (contact.getContactID() != null)
                                        {
                                            ContactID = contact.getContactID();
                                            newOpportunityContact.setContactID(ContactID);
                                        }
                                    }

                                    if (ContactID != 0) //The quote must of been created on the remote server before we can link it.
                                    {
                                        Call<OpportunityContact> newOpportunityContactCall = endPoint.createNewOpportunityContact(newOpportunityContact);

                                        int finalContactID = ContactID;

                                        newOpportunityContactCall.enqueue(new Callback<OpportunityContact>() {

                                            @Override
                                            public void onResponse(@NonNull Call<OpportunityContact> call, @NonNull Response<OpportunityContact> response) {

                                                if (response.body() != null)
                                                {
                                                    database.opportunityDao().updateOpportunityContact(newOpportunityContact.getId(), finalContactID, responseOpportunity.getOpportunityID());
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NotNull Call<OpportunityContact> call, @NotNull Throwable t) {
                                                Log.w("Sync - Create opportunity contact failed", t.getMessage());
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Opportunity> call, @NotNull Throwable t) {
                        Log.w("Sync - Tasks", t.getMessage());
                    }
                });
            }

            else
            {
                //Can't update remote server without a taskID so just mark as done
                database.opportunityDao().updateOpportunityIsChanged(updatedOpportunity.getId(), 0);
            }
        }

        //Send Back Files

        //Create Project Documents (if New)

        String Dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

        File pathToPicture = new File(Dir);

        List<OpportunityFile> newDocuments = database
                .opportunityFileDao()
                .getNewOpportunityFiles();

        for (OpportunityFile newDocument : newDocuments) {

            File Picture = null;

            if (newDocument.getFilepath() != null) {
                if (!newDocument.getFilepath().equals("")) {
                    Picture = new File(newDocument.getFilename());
                } else {
                    Picture = new File(pathToPicture, newDocument.getFilepath());
                }
            } else {
                assert false;
                Picture = new File(pathToPicture, newDocument.getFilepath());
            }

            //Get Project ID

            Opportunity localOpportunity = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .opportunityDao()
                    .getOpportunity(newDocument.getLocalOpportunityID());

            int remoteOpportunityID = 0;

            if (localOpportunity != null) {
                remoteOpportunityID = localOpportunity.getOpportunityID();
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(getFileType(newDocument.getFilename())), Picture);
            MultipartBody.Part body = MultipartBody.Part.createFormData("DocumentFile", Picture.getName(), requestFile);

            //MultipartBody.Part jobID = MultipartBody.Part.createFormData("JobID", Integer.toString(TheJobImage.getJobID()), requestFile);
            RequestBody opportunityID = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(remoteOpportunityID));

            final int fileID = newDocument.getId();

            Call<Void> newFileCall = endPoint.postNewOpportunityDocument(body, opportunityID);
            newFileCall.enqueue(new Callback<Void>() {

                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                    if (response.isSuccessful()) {
                        database.opportunityFileDao().updateIsNew(fileID, false);
                    } else {
                        Log.w("Sync - New Opportunity File Upload Failed", response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {

                    Log.w("Sync - New Opportunity File Upload Failed", t.getMessage());
                }
            });
        }

        //Step 2 - Get Opportunities List From Server

        Call<List<Opportunity>> getOpportunitiesCall = endPoint.getOpportunities(PeopleID);
        getOpportunitiesCall.enqueue(new Callback<List<Opportunity>>() {

            @Override
            public void onResponse(@NonNull Call<List<Opportunity>> call, @NonNull Response<List<Opportunity>> response) {

                if (response.body() != null) {

                    for (Opportunity opportunity : response.body()) {
                        CreateOrUpdateOpportunity(opportunity);

                        //Get Linked Contacts

                        Call<List<OpportunityContact>> getOpportunityContactsCall = endPoint.getOpportunityContacts(opportunity.getOpportunityID());
                        getOpportunityContactsCall.enqueue(new Callback<List<OpportunityContact>>() {

                            @Override
                            public void onResponse(@NonNull Call<List<OpportunityContact>> call, @NonNull Response<List<OpportunityContact>> response) {

                                if (response.body() != null) {

                                    for (OpportunityContact opportunityContact : response.body()) {
                                        CreateOpportunityContact(opportunityContact);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<List<OpportunityContact>> call, @NotNull Throwable t) {
                                Log.w("Sync - Opportunity Contacts", t.getMessage());
                            }
                        });

                        //Get Linked Quotes

                        Call<List<OpportunityQuote>> getOpportunityQuotesCall = endPoint.getOpportunityQuotes(opportunity.getOpportunityID());
                        getOpportunityQuotesCall.enqueue(new Callback<List<OpportunityQuote>>() {

                            @Override
                            public void onResponse(@NonNull Call<List<OpportunityQuote>> call, @NonNull Response<List<OpportunityQuote>> response) {

                                if (response.body() != null) {

                                    for (OpportunityQuote opportunityQuote : response.body()) {
                                        CreateOpportunityQuote(opportunityQuote);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<List<OpportunityQuote>> call, @NotNull Throwable t) {
                                Log.w("Sync - Opportunity Quotes", t.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Opportunity>> call, @NotNull Throwable t) {
                Log.w("Sync - Opportunities", t.getMessage());
            }
        });

        //Step 3 - Create new Opportunities On Server From Local Database

        List<Opportunity> newOpportunities = database
                .opportunityDao()
                .getNewOpportunities();

        for(Opportunity newOpportunity : newOpportunities) {

            if (newOpportunity.getCreatedByDisplayName() != null) {
                if (newOpportunity.getCreatedByDisplayName().equals("")) {
                    newOpportunity.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newOpportunity.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Opportunity> newOpportunityCall = endPoint.createNewOpportunity(newOpportunity);
            newOpportunityCall.enqueue(new Callback<Opportunity>() {

                @Override
                public void onResponse(@NonNull Call<Opportunity> call, @NonNull Response<Opportunity> response) {

                    if (response.body() != null) {

                        Opportunity responseOpportunity = ((Opportunity)response.body());

                        if (responseOpportunity.getOpportunityID() != 0) {

                            database.opportunityDao().updateOpportunityIsNew(responseOpportunity.getId(), 0, responseOpportunity.getOpportunityID());

                            //Create Quotes Links to Opportunity if needed

                            List<OpportunityQuote> newOpportunityQuotes = database
                                    .opportunityDao()
                                    .getOpportunityQuotesByLocalOpportunityID(responseOpportunity.getId());

                            for(OpportunityQuote newOpportunityQuote : newOpportunityQuotes) {

                                newOpportunityQuote.setOpportunityID(responseOpportunity.getOpportunityID());

                                int QuoteID = 0;

                                if (newOpportunityQuote.getQuoteID() != null)
                                {
                                    QuoteID = newOpportunityQuote.getQuoteID();
                                }
                                else
                                {
                                    Quote quote = database
                                            .quoteDao()
                                            .getQuote(newOpportunityQuote.getLocalQuoteID());

                                    if (quote.getQuoteID() != null)
                                    {
                                        QuoteID = quote.getQuoteID();
                                        newOpportunityQuote.setQuoteID(QuoteID);
                                    }
                                }

                                if (QuoteID != 0) //The quote must of been created on the remote server before we can link it.
                                {

                                    Call<OpportunityQuote> newOpportunityQuoteCall = endPoint.createNewOpportunityQuote(newOpportunityQuote);

                                    int finalQuoteID = QuoteID;

                                    newOpportunityQuoteCall.enqueue(new Callback<OpportunityQuote>() {

                                        @Override
                                        public void onResponse(@NonNull Call<OpportunityQuote> call, @NonNull Response<OpportunityQuote> response) {

                                            if (response.body() != null)
                                            {
                                                database.opportunityDao().updateOpportunityQuote(newOpportunityQuote.getId(), finalQuoteID, responseOpportunity.getOpportunityID());
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<OpportunityQuote> call, @NotNull Throwable t) {
                                            Log.w("Sync - Create Opportunity Quote Failed", t.getMessage());
                                        }
                                    });
                                }
                            }

                            //Create Contacts Links to Opportunity if needed

                            List<OpportunityContact> newOpportunityContacts = database
                                    .opportunityDao()
                                    .getOpportunityContactsByLocalOpportunityID(responseOpportunity.getId());

                            for(OpportunityContact newOpportunityContact : newOpportunityContacts) {

                                newOpportunityContact.setOpportunityID(responseOpportunity.getOpportunityID());

                                int ContactID = 0;

                                if (newOpportunityContact.getContactID() != null)
                                {
                                    ContactID = newOpportunityContact.getContactID();
                                }
                                else
                                {
                                    Contact contact = database
                                            .contactDao()
                                            .getContact(newOpportunityContact.getLocalContactID());

                                    if (contact.getContactID() != null)
                                    {
                                        ContactID = contact.getContactID();
                                        newOpportunityContact.setContactID(ContactID);
                                    }
                                }

                                if (ContactID != 0) //The quote must of been created on the remote server before we can link it.
                                {

                                    Call<OpportunityContact> newOpportunityContactCall = endPoint.createNewOpportunityContact(newOpportunityContact);

                                    int finalContactID = ContactID;

                                    newOpportunityContactCall.enqueue(new Callback<OpportunityContact>() {

                                        @Override
                                        public void onResponse(@NonNull Call<OpportunityContact> call, @NonNull Response<OpportunityContact> response) {

                                            if (response.body() != null)
                                            {
                                                database.opportunityDao().updateOpportunityContact(newOpportunityContact.getId(), finalContactID, responseOpportunity.getOpportunityID());
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<OpportunityContact> call, @NotNull Throwable t) {
                                            Log.w("Sync - Create Opportunity contact failed", t.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Opportunity> call, @NotNull Throwable t) {
                    Log.w("Sync - New Opportunity", t.getMessage());
                }
            });
        }

        //Products ******************************************************************************

        //Step 2 - Get Products List From Server

        Call<List<Product>> getProductsCall = endPoint.getProducts();
        getProductsCall.enqueue(new Callback<List<Product>>() {

            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {

                if (response.body() != null) {

                    for (Product product : response.body()) {
                        CreateOrUpdateProduct(product);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                Log.w("Sync - Update Products From Server", t.getMessage());
            }
        });

        //Tasks *************************************************************************

        //Step 1 - Update Tasks From Local Database To Server

        List<Task> updatedTasks = database
                .taskDao()
                .getChangedTasks();

        for(Task updatedTask : updatedTasks) {

            if (updatedTask.getTaskID() != null) {

                if (updatedTask.getCreatedByDisplayName() == null) {
                    updatedTask.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Task> updateTaskCall = endPoint.updateTask(updatedTask);
                updateTaskCall.enqueue(new Callback<Task>() {

                    @Override
                    public void onResponse(@NonNull Call<Task> call, @NonNull Response<Task> response) {

                        if (response.body() != null) {

                            Task responseTask = ((Task) response.body());

                            if (responseTask.getId() != 0) {
                                database.taskDao().updateTaskIsChanged(responseTask.getId(), 0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Task> call, @NotNull Throwable t) {
                        Log.w("Sync - Tasks", t.getMessage());
                    }
                });
            }
            else
            {
                //Can't update remote server without a taskID so just mark as done
                database.taskDao().updateTaskIsChanged(updatedTask.getId(), 0);
            }
        }

        //Step 2 - Get Task List From Server

        Call<List<Task>> getTasksCall = endPoint.getTasks(PeopleID);
        getTasksCall.enqueue(new Callback<List<Task>>() {

            @Override
            public void onResponse(@NonNull Call<List<Task>> call, @NonNull Response<List<Task>> response) {

                if (response.body() != null) {

                    for (Task task : response.body()) {
                        CreateOrUpdateTask(task);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Task>> call, @NotNull Throwable t) {
                Log.w("Sync - Tasks", t.getMessage());
            }
        });

        //Step 3 - Create new Tasks On Server From Local Database

        List<Task> newTasks = database
                .taskDao()
                .getNewTasks();

        for(Task newTask : newTasks) {

            if (newTask.getCreatedByDisplayName() != null) {
                if (newTask.getCreatedByDisplayName().equals("")) {
                    newTask.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            }
            else
            {
                newTask.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Task> newTaskCall = endPoint.createNewTask(newTask);
            newTaskCall.enqueue(new Callback<Task>() {

                @Override
                public void onResponse(@NonNull Call<Task> call, @NonNull Response<Task> response) {

                    if (response.body() != null) {

                        Task responseTask = ((Task)response.body());

                        if (responseTask.getTaskID() != 0) {

                            database.taskDao().updateTaskIsNew(responseTask.getId(), 0, responseTask.getTaskID());

                            //Create Task Links to phases in projects if needed

                            ProjectTask newProjectTask = database
                                    .projectDao()
                                    .getProjectTaskByLocalTaskID(responseTask.getId());

                            if (newProjectTask != null)
                            {
                                //Get Phase

                                int PhaseID = 0;

                                ProjectPhase projectPhase = database
                                        .projectPhaseDao()
                                        .getProjectPhase(newProjectTask.getLocalTaskPhaseID());

                                if (projectPhase.getPhaseID() != null)
                                {
                                    PhaseID = projectPhase.getPhaseID();
                                }

                                int ProjectID = 0;

                                Project project = database
                                        .projectDao()
                                        .getProject(newProjectTask.getLocalProjectID());

                                if (projectPhase.getProjectID() != null)
                                {
                                    ProjectID = projectPhase.getProjectID();
                                }

                                //Get Project

                                newProjectTask.setTaskID(responseTask.getTaskID());
                                newProjectTask.setPhaseID(PhaseID);
                                newProjectTask.setProjectID(ProjectID);

                                Call<ProjectTask> newProjectTaskCall = endPoint.createNewProjectTask(newProjectTask);

                                int finalPhaseID = PhaseID;
                                int finalProjectID = ProjectID;

                                newProjectTaskCall.enqueue(new Callback<ProjectTask>() {

                                    @Override
                                    public void onResponse(@NonNull Call<ProjectTask> call, @NonNull Response<ProjectTask> response) {

                                        if (response.body() != null)
                                        {
                                            database.projectDao().updateProjectTask(newProjectTask.getId(), responseTask.getTaskID(), finalPhaseID, finalProjectID);
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<ProjectTask> call, @NotNull Throwable t) {
                                        Log.w("Sync - Update Project From Local Failed", t.getMessage());
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Task> call, @NotNull Throwable t) {
                    Log.w("Sync - Tasks", t.getMessage());
                }
            });
        }

        //Projects *************************************************************************

        //Step 1 - Update Projects From Local Database To Server

        List<Project> updatedProjects = database
                .projectDao()
                .getChangedProjects();

        for(Project updatedProject : updatedProjects) {

            if (updatedProject.getProjectID() != null) {

                if (updatedProject.getCreatedByDisplayName() == null) {
                    updatedProject.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }

                Call<Project> updateProjectCall = endPoint.updateProject(updatedProject);
                updateProjectCall.enqueue(new Callback<Project>() {

                    @Override
                    public void onResponse(@NonNull Call<Project> call, @NonNull Response<Project> response) {

                        if (response.body() != null) {

                            Project responseProject = ((Project) response.body());

                            if (responseProject.getId() != 0) {
                                database.projectDao().updateProjectIsChanged(responseProject.getId(), 0);
                            }

                            //Create Task Links

                            List<ProjectTask> newProjectTasks = database
                                    .projectDao()
                                    .getAllNewProjectTasks(updatedProject.getId());

                            for(ProjectTask newProjectTask : newProjectTasks) {

                                //get Values of inserted Items

                                Task task = database
                                        .taskDao()
                                        .getTask(newProjectTask.getLocalTaskID());

                                if (task != null)
                                {
                                    if (task.getTaskID() != null)
                                    {
                                        newProjectTask.setTaskID(task.getTaskID());
                                        newProjectTask.setPhaseID(newProjectTask.getPhaseID());
                                        newProjectTask.setProjectID(updatedProject.getProjectID());

                                        Call<ProjectTask> newProjectTaskCall = endPoint.createNewProjectTask(newProjectTask);
                                        newProjectTaskCall.enqueue(new Callback<ProjectTask>() {

                                            @Override
                                            public void onResponse(@NonNull Call<ProjectTask> call, @NonNull Response<ProjectTask> response) {

                                                if (response.body() != null)
                                                {
                                                    database.projectDao().updateProjectTask(newProjectTask.getId(), task.getTaskID(), newProjectTask.getPhaseID(), responseProject.getProjectID());
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NotNull Call<ProjectTask> call, @NotNull Throwable t) {
                                                Log.w("Sync - Update Project From Local Failed", t.getMessage());
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Project> call, @NotNull Throwable t) {
                        Log.w("Sync - Update Project From Local Failed", t.getMessage());
                    }
                });
            }
            else
            {
                //Can't update remote server without a project ID so just mark as done
                database.projectDao().updateProjectIsChanged(updatedProject.getId(), 0);
            }

        }

        //Step 2 - Get Projects From Server

        Call<List<Project>> getProjectsCall = endPoint.getProjects(PeopleID);
        getProjectsCall.enqueue(new Callback<List<Project>>() {

            @Override
            public void onResponse(@NonNull Call<List<Project>> call, @NonNull Response<List<Project>> response) {

                if (response.body() != null) {

                    for (Project project : response.body()) {
                        CreateOrUpdateProject(project);

                        //Get Project Phases

                        Call<List<ProjectPhase>> getProjectPhasesCall = endPoint.getProjectPhases(project.getProjectID());
                        getProjectPhasesCall.enqueue(new Callback<List<ProjectPhase>>() {

                            @Override
                            public void onResponse(@NonNull Call<List<ProjectPhase>> call, @NonNull Response<List<ProjectPhase>> response) {

                                if (response.body() != null) {

                                    for (ProjectPhase projectPhase : response.body()) {
                                        CreateOrUpdateProjectPhase(projectPhase);
                                    }
                                }

                                //Get Project Tasks, if get phases worked!

                                Call<List<ProjectTask>> getProjectTasksCall = endPoint.getProjectTasks(project.getProjectID());
                                getProjectTasksCall.enqueue(new Callback<List<ProjectTask>>() {

                                    @Override
                                    public void onResponse(@NonNull Call<List<ProjectTask>> call, @NonNull Response<List<ProjectTask>> response) {

                                        if (response.body() != null) {

                                            for (ProjectTask projectTask : response.body()) {
                                                CreateProjectTask(projectTask);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<List<ProjectTask>> call, @NotNull Throwable t) {
                                        Log.w("Sync - Project Task", t.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NotNull Call<List<ProjectPhase>> call, @NotNull Throwable t) {
                                Log.w("Sync - Project Phase", t.getMessage());
                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Project>> call, @NotNull Throwable t) {
                Log.w("Sync - Projects", t.getMessage());
            }
        });

        //Step 3 - Create new projects

        List<Project> newProjects = database
                .projectDao()
                .getNewProjects();

        for(Project newProject : newProjects) {

            if (newProject.getCreatedByDisplayName().equals(""))
            {
                newProject.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            Call<Project> newProjectCall = endPoint.createNewProject(newProject);
            newProjectCall.enqueue(new Callback<Project>() {

                @Override
                public void onResponse(@NonNull Call<Project> call, @NonNull Response<Project> response) {

                    if (response.body() != null) {

                        Project responseProject = ((Project)response.body());

                        if (responseProject.getProjectID() != 0) {
                            database.projectDao().updateProjectIsNew(responseProject.getId(), 0, responseProject.getProjectID());
                        }

                        //Create Phases and link tasks

                        List<ProjectPhase> newProjectPhases = database
                                .projectPhaseDao()
                                .getAllProjectPhases(newProject.getId());

                        for(ProjectPhase newProjectPhase : newProjectPhases) {

                            if (newProjectPhase.getCreatedByDisplayName() != null) {
                                if (newProjectPhase.getCreatedByDisplayName().equals("")) {
                                    newProjectPhase.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                                }
                            }
                            else
                            {
                                newProjectPhase.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                            }

                            newProjectPhase.setProjectID(responseProject.getProjectID());

                            Call<ProjectPhase> newProjectPhaseCall = endPoint.createNewProjectPhase(newProjectPhase);
                            newProjectPhaseCall.enqueue(new Callback<ProjectPhase>() {

                                @Override
                                public void onResponse(@NonNull Call<ProjectPhase> call, @NonNull Response<ProjectPhase> response) {

                                    if (response.body() != null) {

                                        ProjectPhase responsePhaseProject = ((ProjectPhase) response.body());

                                        if (responsePhaseProject.getPhaseID() != 0) {
                                            database.projectPhaseDao().updateProjectPhaseIsNew(responsePhaseProject.getId(), 0, responsePhaseProject.getPhaseID());
                                        }

                                        //Create Task Links

                                        List<ProjectTask> newProjectTasks = database
                                                .projectDao()
                                                .getAllProjectTasks(newProject.getId());

                                        for(ProjectTask newProjectTask : newProjectTasks) {

                                            //get Values of inserted Items

                                            Task task = database
                                                    .taskDao()
                                                    .getTask(newProjectTask.getLocalTaskID());

                                            if (task != null)
                                            {
                                                if (task.getTaskID() != null)
                                                {
                                                    newProjectTask.setTaskID(task.getTaskID());
                                                    newProjectTask.setPhaseID(responsePhaseProject.getPhaseID());
                                                    newProjectTask.setProjectID(responseProject.getProjectID());

                                                    Call<ProjectTask> newProjectTaskCall = endPoint.createNewProjectTask(newProjectTask);
                                                    newProjectTaskCall.enqueue(new Callback<ProjectTask>() {

                                                        @Override
                                                        public void onResponse(@NonNull Call<ProjectTask> call, @NonNull Response<ProjectTask> response) {

                                                            if (response.body() != null)
                                                            {
                                                                database.projectDao().updateProjectTask(newProjectTask.getId(), task.getTaskID(), responsePhaseProject.getPhaseID(), responseProject.getProjectID());
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(@NotNull Call<ProjectTask> call, @NotNull Throwable t) {
                                                            Log.w("Sync - Update Project From Local Failed", t.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                    }

                                }

                                @Override
                                public void onFailure(@NotNull Call<ProjectPhase> call, @NotNull Throwable t) {
                                    Log.w("Sync - New Project Phases Failed", t.getMessage());
                                }
                            });
                        }

                    }
                }

                @Override
                public void onFailure(@NotNull Call<Project> call, @NotNull Throwable t) {
                    Log.w("Sync - New Projects Failed", t.getMessage());
                }
            });
        }

        //Create new phases and task joins

        List<ProjectPhase> newProjectPhases = database
                .projectPhaseDao()
                .getNewProjectPhases();

        for(ProjectPhase newProjectPhase : newProjectPhases) {

            if (newProjectPhase.getCreatedByDisplayName() != null) {
                if (newProjectPhase.getCreatedByDisplayName().equals("")) {
                    newProjectPhase.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
                }
            } else {
                newProjectPhase.setCreatedByDisplayName(sharedPreferences.getString("display_name", ""));
            }

            //Make sure it has a project ID

            Project project = database
                    .projectDao()
                    .getProject(newProjectPhase.getLocalProjectID());

            if (project != null) {

                if (project.getProjectID() != null)
                {
                    newProjectPhase.setProjectID(project.getProjectID());

                    Call<ProjectPhase> newProjectPhaseCall = endPoint.createNewProjectPhase(newProjectPhase);
                    newProjectPhaseCall.enqueue(new Callback<ProjectPhase>() {

                        @Override
                        public void onResponse(@NonNull Call<ProjectPhase> call, @NonNull Response<ProjectPhase> response) {

                            if (response.body() != null) {

                                ProjectPhase responsePhaseProject = ((ProjectPhase) response.body());

                                if (responsePhaseProject.getPhaseID() != 0) {
                                    database.projectPhaseDao().updateProjectPhaseIsNew(responsePhaseProject.getId(), 0, responsePhaseProject.getPhaseID());
                                }

                                //Create Task Links

                                List<ProjectTask> newProjectTasks = database
                                        .projectDao()
                                        .getAllProjectTasks(project.getProjectID());

                                for(ProjectTask newProjectTask : newProjectTasks) {

                                    //get Values of inserted Items

                                    Task task = database
                                            .taskDao()
                                            .getTask(newProjectTask.getLocalTaskID());

                                    if (task != null)
                                    {
                                        if (task.getTaskID() != null)
                                        {
                                            newProjectTask.setTaskID(task.getTaskID());
                                            newProjectTask.setPhaseID(responsePhaseProject.getPhaseID());
                                            newProjectTask.setProjectID(project.getProjectID());

                                            Call<ProjectTask> newProjectTaskCall = endPoint.createNewProjectTask(newProjectTask);
                                            newProjectTaskCall.enqueue(new Callback<ProjectTask>() {

                                                @Override
                                                public void onResponse(@NonNull Call<ProjectTask> call, @NonNull Response<ProjectTask> response) {

                                                    if (response.body() != null)
                                                    {
                                                        database.projectDao().updateProjectTask(newProjectTask.getId(), task.getTaskID(), responsePhaseProject.getPhaseID(), project.getProjectID());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NotNull Call<ProjectTask> call, @NotNull Throwable t) {
                                                    Log.w("Sync - Update Project From Local Failed", t.getMessage());
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ProjectPhase> call, @NotNull Throwable t) {
                            Log.w("Sync - New Project Phases Failed", t.getMessage());
                        }
                    });
                }
            }
        }

        //Create Project Documents (if New)

        List<ProjectFile> newProjectDocuments = database
                .projectImageDao()
                .getNewProjectFiles();

        for(ProjectFile newDocument : newProjectDocuments) {

            File Picture = null;

            if (newDocument.getFilepath() != null) {
                if (!newDocument.getFilepath().equals("")) {
                    Picture = new File(newDocument.getFilename());
                } else {
                    Picture = new File(pathToPicture, newDocument.getFilepath());
                }
            }
            else
            {
                assert false;
                Picture = new File(pathToPicture, newDocument.getFilepath());
            }

            //Get Project ID

            Project localProject = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectDao()
                    .getProject(newDocument.getLocalProjectID());

            int remoteProjectID = 0;

            if (localProject != null)
            {
                remoteProjectID = localProject.getProjectID();
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(getFileType(newDocument.getFilename())), Picture);
            MultipartBody.Part body = MultipartBody.Part.createFormData("DocumentFile", Picture.getName(), requestFile);

            //MultipartBody.Part jobID = MultipartBody.Part.createFormData("JobID", Integer.toString(TheJobImage.getJobID()), requestFile);
            RequestBody projectID = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(remoteProjectID));

            final int fileID = newDocument.getId();

            Call<Void> newContactCall = endPoint.postNewProjectDocument(body, projectID);
            newContactCall.enqueue(new Callback<Void>() {

                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                    if (response.isSuccessful()) {
                        database.projectImageDao().updateIsNew(fileID, false);
                    }
                    else
                    {
                        Log.w("Sync - New File Upload Failed", response.message());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {

                    Log.w("Sync - New File Upload Failed", t.getMessage());
                }
            });
        }

        //Currencies

        Call<List<Currency>> getCurrenciesCall = endPoint.getCurrencies();
        getCurrenciesCall.enqueue(new Callback<List<Currency>>() {

            @Override
            public void onResponse(@NonNull Call<List<Currency>> call, @NonNull Response<List<Currency>> response) {

                if (response.body() != null) {

                    for (Currency currency : response.body()) {
                        CreateOrUpdateCurrency(currency);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Currency>> call, @NotNull Throwable t) {
                Log.w("Sync - Get currencies from server failed", t.getMessage());
            }
        });

        //End of Sync

        return Result.success();
    }

    public void CreateOrUpdateCustomer(Customer customer)
    {
            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().customerDao().getCustomerCount(customer.getCustomerID()) + DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().customerDao().getCustomerCount(customer.getCustomerName());

            if (Count > 0) {

                //Only update is it's newer

                Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomerByCustomerID(customer.getCustomerID());

                if (localCustomer != null) {

                    Date sourceConvertedDate = new Date();
                    Date localConvertedDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    String sourceDateString;
                    String localDateString;

                    if (customer.getUpdated() != null) {
                        sourceDateString = customer.getUpdated();

                        try {
                            sourceConvertedDate = dateFormat.parse(sourceDateString);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    } else {
                        sourceDateString = sourceConvertedDate.toString();
                    }

                    if (localCustomer.getUpdated() != null) {
                        localDateString = localCustomer.getUpdated();

                        try {
                            localConvertedDate = dateFormat.parse(localDateString);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    } else {
                        localDateString = localConvertedDate.toString();
                    }

                    if (localConvertedDate != null) {
                        assert sourceConvertedDate != null;
                        if (sourceConvertedDate.after(localConvertedDate)) {
                            if (customer.getCustomerID() != 0) {
                                DatabaseClient.getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .customerDao()
                                        .updateCustomerByCustomerID(customer.getCustomerID(), customer.getCustomerReference(), customer.getCustomerName(), customer.getCustomerStatus(), customer.getParentCustomerName(), customer.getContactName(), customer.getGeneralTelephone(), customer.getGeneralEmail(), customer.getNotes(), customer.getUpdated());
                            }
                        }
                    }
                }
            } else //Insert
            {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                Customer newCustomer = new Customer();

                newCustomer.setCustomerID(customer.getCustomerID());
                newCustomer.setCustomerName(customer.getCustomerName());
                newCustomer.setCustomerReference(customer.getCustomerReference());
                newCustomer.setCustomerStatus(customer.getCustomerStatus());
                newCustomer.setParentCustomerName(customer.getParentCustomerName());
                newCustomer.setContactName(customer.getContactName());
                newCustomer.setGeneralTelephone(customer.getGeneralTelephone());
                newCustomer.setGeneralEmail(customer.getGeneralEmail());
                newCustomer.setNotes(customer.getNotes());
                newCustomer.setIsChanged(false);
                newCustomer.setIsNew(false);

                if (customer.getUpdated() != null) {
                    newCustomer.setUpdated(customer.getUpdated());
                } else {
                    Date created = new Date();
                    newCustomer.setUpdated(dateFormat.format(created));
                }

                long id = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .insert(newCustomer);
            }


    }



    public void CreateOrUpdateCustomerAddress(Address address)
    {


            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().addressDao().getAddressCount(address.getCustomerID(),  address.getAddressID());

            if (Count > 0) {

                //Only update is it's newer

                Address localAddress = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .addressDao()
                        .getAddressByAddressID(address.getAddressID());

                if (localAddress != null) {

                    Date sourceConvertedDate = new Date();
                    Date localConvertedDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    String sourceDateString;
                    String localDateString;

                    if (address.getUpdated() != null) {
                        sourceDateString = address.getUpdated();

                        try {
                            sourceConvertedDate = dateFormat.parse(sourceDateString);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    } else {
                        sourceDateString = sourceConvertedDate.toString();

                    }

                    if (localAddress.getUpdated() != null) {
                        localDateString = localAddress.getUpdated();

                        try {
                            localConvertedDate = dateFormat.parse(localDateString);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    } else {
                        localDateString = localConvertedDate.toString();
                    }

                    if (localConvertedDate != null) {
                        assert sourceConvertedDate != null;
                        if (sourceConvertedDate.after(localConvertedDate)) {
                            if(address.getAddressID() != 0) {
                                DatabaseClient.getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .addressDao()
                                        .updateAddressByAddressID(address.getAddressID(), address.getAddressType(), address.getAddress(), address.getUpdated());
                            }
                        }
                    }
                }
            }
            else //Insert
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomerByCustomerID(address.getCustomerID());

                int localCustomerID = 0;

                if (localCustomer != null) {
                    localCustomerID = localCustomer.getId();
                }

                Address newAddress = new Address();

                newAddress.setLocalCustomerID(localCustomerID);
                newAddress.setAddressID(address.getAddressID());
                newAddress.setCustomerID(address.getCustomerID());
                newAddress.setAddressType(address.getAddressType());
                newAddress.setAddress(address.getAddress());
                newAddress.setIsChanged(false);
                newAddress.setIsNew(false);

                if (newAddress.getUpdated() != null) {
                    newAddress.setUpdated(address.getUpdated());
                } else {
                    Date created = new Date();
                    newAddress.setUpdated(dateFormat.format(created));
                }

                long id = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .addressDao()
                        .insert(newAddress);
            }


    }


    public void CreateOrUpdateCustomerContact(Contact contact)
    {
            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().getContactCount(contact.getCustomerName(), contact.getContactName()) + DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().contactDao().getContactCount(contact.getCustomerID(), contact.getContactName());

            if (Count > 0) {

                //Only update is it's newer

                Contact localContact = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .contactDao()
                        .getContactByContactID(contact.getContactID());

                if (localContact != null) {

                    Date sourceConvertedDate = new Date();
                    Date localConvertedDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    String sourceDateString;
                    String localDateString;

                    if (contact.getUpdated() != null) {
                        sourceDateString = contact.getUpdated();
                    } else {
                        sourceDateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(sourceConvertedDate);
                    }

                    if (localContact.getUpdated() != null) {
                        localDateString = localContact.getUpdated();
                    } else {
                        localDateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(localConvertedDate);
                    }

                    try {
                        sourceConvertedDate = dateFormat.parse(sourceDateString);
                        localConvertedDate = dateFormat.parse(localDateString);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    if (localConvertedDate != null) {
                        assert sourceConvertedDate != null;
                        if (sourceConvertedDate.after(localConvertedDate)) {
                            if(contact.getContactID() != 0) {
                                DatabaseClient.getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .contactDao()
                                        .updateContactByContactID(contact.getContactID(), contact.getContactName(), contact.getSalutation(), contact.getJobTitle(), contact.getTelephone(), contact.getMobile(), contact.getEmail(), contact.getNotes(), contact.getUpdated());
                            }
                        }
                    }
                }
            }
            else //Insert
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomerByCustomerID(contact.getCustomerID());

                int localCustomerID = 0;

                if (localCustomer != null)
                {
                    localCustomerID = localCustomer.getId();
                }

                Contact newContact = new Contact();

                newContact.setLocalCustomerID(localCustomerID);
                newContact.setContactID(contact.getContactID());
                newContact.setCustomerID(contact.getCustomerID());
                newContact.setContactName(contact.getContactName());
                newContact.setCustomerName(contact.getCustomerName());
                newContact.setSalutation(contact.getSalutation());
                newContact.setJobTitle(contact.getJobTitle());
                newContact.setTelephone(contact.getTelephone());
                newContact.setMobile(contact.getMobile());
                newContact.setEmail(contact.getEmail());
                newContact.setNotes(contact.getNotes());
                newContact.setUpdated(contact.getUpdated());
                newContact.setIsChanged(false);
                newContact.setIsNew(false);
                newContact.setIsArchived(false);

                if (newContact.getUpdated() != null) {
                    newContact.setUpdated(contact.getUpdated());
                }
                else
                {
                    Date created = new Date();
                    newContact.setUpdated(dateFormat.format(created));
                }

                long id = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .contactDao()
                        .insert(newContact);
            }

    }

    public void CreateOrUpdateActivity(Activity activity)
    {


            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().activityDao().getActivityCount(activity.getJournalEntryID()) + DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().activityDao().getActivityCount(activity.getSubject(), activity.getNotes());

            if (Count > 0) {

                //Only update is it's newer

                Activity localActivity = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .activityDao()
                        .getActivityByJournalEntryID(activity.getJournalEntryID());

                if (localActivity != null) {

                    Date sourceConvertedDate = new Date();
                    Date localConvertedDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    String sourceDateString;
                    String localDateString;

                    if (activity.getUpdated() != null) {
                        sourceDateString = activity.getUpdated();
                    } else {
                        sourceDateString = sourceConvertedDate.toString();
                    }

                    if (localActivity.getUpdated() != null) {
                        localDateString = localActivity.getUpdated();
                    } else {
                        localDateString = localConvertedDate.toString();
                    }

                    try {
                        sourceConvertedDate = dateFormat.parse(sourceDateString);
                        localConvertedDate = dateFormat.parse(localDateString);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    if (localConvertedDate != null) {
                        assert sourceConvertedDate != null;
                        if (sourceConvertedDate.after(localConvertedDate)) {
                            if(activity.getJournalEntryID() != 0) {
                                DatabaseClient.getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .activityDao()
                                        .updateActivityByJournalEntryID(activity.getJournalEntryID(), activity.getJournalEntryType(), activity.getStartDate(), activity.getEndDate(), activity.getSubject(), activity.getNotes(), activity.getUpdated());
                            }
                        }
                    }
                }
            }
            else //Insert
            {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                Activity newActivity = new Activity();

                Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomerByCustomerID(activity.getCustomerID());

                int localCustomerID = 0;

                if (localCustomer != null)
                {
                    localCustomerID = localCustomer.getId();
                }

                newActivity.setJournalEntryID(activity.getJournalEntryID());
                newActivity.setCustomerID(activity.getCustomerID());
                newActivity.setJournalEntryType(activity.getJournalEntryType());
                newActivity.setSubject(activity.getSubject());
                newActivity.setStartDate(activity.getStartDate());
                newActivity.setEndDate(activity.getEndDate());
                newActivity.setLocalCustomerID(localCustomerID);
                newActivity.setCreatedByDisplayName(activity.getCreatedByDisplayName());
                newActivity.setNotes(activity.getNotes());
                newActivity.setIsChanged(false);
                newActivity.setIsNew(false);

                if (newActivity.getUpdated() != null) {
                    newActivity.setUpdated(activity.getUpdated());
                }
                else
                {
                    Date created = new Date();
                    newActivity.setUpdated(dateFormat.format(created));
                }

                long id = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .activityDao()
                        .insert(newActivity);
            }


    }


    public void CreateOrUpdateProduct(Product product)
    {

            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().productDao().getProductCount(product.getProductID()) + DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().productDao().getProductCount(product.getPartNumber());

            if (Count > 0) {

                //Only update is it's newer

                Product localProduct = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .productDao()
                    .getProductByProductID(product.getProductID());

                if (localProduct != null) {

                    if (product != localProduct)
                    {
                        DatabaseClient.getInstance(getApplicationContext())
                            .getAppDatabase()
                            .productDao()
                            .updateProductByProductID(product.getProductID(), product.getPartNumber(), product.getDescription(), product.getCostPrice(), product.getSalePrice(), product.getTaxRate());
                    }

                }
            }
            else //Insert
            {
                Product newProduct = new Product();

                newProduct.setProductID(product.getProductID());
                newProduct.setDescription(product.getDescription());
                newProduct.setPartNumber(product.getPartNumber());
                newProduct.setCostPrice(product.getCostPrice());
                newProduct.setSalePrice(product.getSalePrice());
                newProduct.setTaxRate(product.getTaxRate());

                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .productDao()
                        .insert(newProduct);
            }


    }



    public void CreateOpportunityContact(OpportunityContact opportunityContact)
    {
        if (!OpportunityContactExists(opportunityContact.getOpportunityID(), opportunityContact.getContactID()))
        {
            OpportunityContact newOpportunityContact = new OpportunityContact();

            newOpportunityContact.setOpportunityID(opportunityContact.getOpportunityID());
            newOpportunityContact.setContactID(opportunityContact.getContactID());

            //Get Local ID's

            Opportunity localOpportunity = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .opportunityDao()
                    .getOpportunityByOpportunityID(opportunityContact.getOpportunityID());

            int localOpportunityID = 0;

            if (localOpportunity != null)
            {
                localOpportunityID = localOpportunity.getId();
            }

            //Contact

            Contact localContact = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .contactDao()
                    .getContactByContactID(opportunityContact.getContactID());

            int localContactID = 0;

            if (localContact != null)
            {
                localContactID = localContact.getId();
            }

            newOpportunityContact.setLocalOpportunityID(localOpportunityID);
            newOpportunityContact.setLocalContactID(localContactID);

            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .opportunityDao()
                    .insert(newOpportunityContact);
        }
        else
        {
            //update contact ID's if contacts hadn't loaded on initial sync

            Contact localContact = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .contactDao()
                    .getContactByContactID(opportunityContact.getContactID());

            int localContactID = 0;

            if (localContact != null)
            {
                localContactID = localContact.getId();
            }

            if (localContactID != 0) {

                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .opportunityDao()
                        .updateOpportunityContactLocalID(opportunityContact.getOpportunityID(), opportunityContact.getContactID(), localContactID);
            }
        }
    }

    public boolean OpportunityContactExists(int opportunityID, int contactID) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().opportunityDao().getOpportunityContactCount(opportunityID, contactID);
        return Count > 0;
    }

    public void CreateOpportunityQuote(OpportunityQuote opportunityQuote)
    {
        if (!OpportunityQuoteExists(opportunityQuote.getOpportunityID(), opportunityQuote.getQuoteID()))
        {
            OpportunityQuote newOpportunityQuote = new OpportunityQuote();

            newOpportunityQuote.setOpportunityID(opportunityQuote.getOpportunityID());
            newOpportunityQuote.setQuoteID(opportunityQuote.getQuoteID());

            //Get Local ID's

            Opportunity localOpportunity = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .opportunityDao()
                    .getOpportunityByOpportunityID(opportunityQuote.getOpportunityID());

            int localOpportunityID = 0;

            if (localOpportunity != null)
            {
                localOpportunityID = localOpportunity.getId();
            }

            //Quote

            Quote localQuote = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .quoteDao()
                    .getQuoteByQuoteID(opportunityQuote.getQuoteID());

            int localQuoteID = 0;

            if (localQuote != null)
            {
                localQuoteID = localQuote.getId();
            }

            newOpportunityQuote.setLocalOpportunityID(localOpportunityID);
            newOpportunityQuote.setLocalQuoteID(localQuoteID);

            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .opportunityDao()
                    .insert(newOpportunityQuote);
        }
        else
        {
            //update quote ID's if quotes hadn't loaded on initial sync

            Quote localQuote = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .quoteDao()
                    .getQuoteByQuoteID(opportunityQuote.getQuoteID());

            int localQuoteID = 0;

            if (localQuote != null)
            {
                localQuoteID = localQuote.getId();
            }

            if (localQuoteID != 0) {

                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .opportunityDao()
                        .updateOpportunityQuoteLocalID(opportunityQuote.getOpportunityID(), opportunityQuote.getQuoteID(), localQuoteID);
            }
        }
    }

    public boolean OpportunityQuoteExists(int opportunityID, int quoteID) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().opportunityDao().getOpportunityQuoteCount(opportunityID, quoteID);
        return Count > 0;
    }

    public void CreateProjectTask(ProjectTask projectTask)
    {
        if (!ProjectTaskExists(projectTask.getProjectID(), projectTask.getTaskID()))
        {
            ProjectTask newProjectTask = new ProjectTask();

            newProjectTask.setProjectID(projectTask.getProjectID());
            newProjectTask.setTaskID(projectTask.getTaskID());
            newProjectTask.setPhaseID(projectTask.getPhaseID());
            //Get Local ID's

            Project localProject = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectDao()
                    .getProjectByProjectID(projectTask.getProjectID());

            int localProjectID = 0;

            if (localProject != null)
            {
                localProjectID = localProject.getId();
            }

            //Phase

            ProjectPhase localProjectPhase = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectPhaseDao()
                    .getProjectPhaseByPhaseID(projectTask.getPhaseID());

            int localTaskPhaseID = 0;

            if (localProjectPhase != null)
            {
                localTaskPhaseID = localProjectPhase.getId();
            }

            //Task

            Task localTask = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .taskDao()
                    .getTaskByTaskID(projectTask.getTaskID());

            int localTaskID = 0;

            if (localTask != null)
            {
                localTaskID = localTask.getId();
            }

            newProjectTask.setLocalProjectID(localProjectID);
            newProjectTask.setLocalTaskID(localTaskID);
            newProjectTask.setLocalTaskPhaseID(localTaskPhaseID);

            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectDao()
                    .insert(newProjectTask);
        }
    }

    public boolean ProjectTaskExists(int projectID, int taskID) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().projectDao().getProjectTaskCount(projectID, taskID);
        return Count > 0;
    }

    public void CreateOrUpdateTask(Task task)
    {
        if (TaskExists(task.getTaskID(), task.getSubject(), task.getNotes(), task.getDueDate())) {

            //Only update is it's newer

            Task localTask = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .taskDao()
                    .getTaskByTaskID(task.getTaskID());

            if (localTask != null) {

                Date sourceConvertedDate = new Date();
                Date localConvertedDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                String sourceDateString;
                String localDateString;

                if (task.getUpdated() != null) {
                    sourceDateString = task.getUpdated();
                } else {
                    sourceDateString = sourceConvertedDate.toString();
                }

                if (localTask.getUpdated() != null) {
                    localDateString = localTask.getUpdated();
                } else {
                    localDateString = localConvertedDate.toString();
                }

                try {
                    sourceConvertedDate = dateFormat.parse(sourceDateString);
                    localConvertedDate = dateFormat.parse(localDateString);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (localConvertedDate != null) {
                    assert sourceConvertedDate != null;
                    if (sourceConvertedDate.after(localConvertedDate)) {
                        if(task.getTaskID() != 0) {
                            DatabaseClient.getInstance(getApplicationContext())
                                    .getAppDatabase()
                                    .taskDao()
                                    .updateTaskByTaskID(task.getTaskID(), task.getSubject(), task.getCustomerID(), task.getCustomerName(), task.getDueDate(), task.getNotes(), task.getUpdated());
                        }
                    }
                }
            }
        }
        else //Insert
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

            Task newTask = new Task();

            //Get local customer ID

            Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .customerDao()
                    .getCustomerByCustomerID(task.getCustomerID());

            if (localCustomer != null)
            {
                newTask.setLocalCustomerID(localCustomer.getId());
            }
            else
            {
                newTask.setLocalCustomerID(0);
            }

            newTask.setTaskID(task.getTaskID());
            newTask.setSubject(task.getSubject());
            newTask.setNotes(task.getNotes());
            newTask.setDueDate(task.getDueDate());
            newTask.setCustomerID(task.getCustomerID());
            newTask.setCustomerName(task.getCustomerName());
            newTask.setComplete(task.getComplete());
            newTask.setCreatedByDisplayName(task.getCreatedByDisplayName());
            newTask.setIsChanged(false);
            newTask.setIsNew(false);

            if (task.getUpdated() != null) {
                newTask.setUpdated(task.getUpdated());
            }
            else
            {
                Date created = new Date();
                newTask.setUpdated(dateFormat.format(created));
            }

            long id = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .taskDao()
                    .insert(newTask);

            CreateNotifcation((int)id, newTask.getSubject());
        }
    }

    public boolean TaskExists(int taskID, String subject, String notes, String dueDate) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().taskDao().getTaskCount(taskID) + DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().taskDao().getTaskCount(subject, notes, dueDate);
        return Count > 0;
    }

    public void CreateOrUpdateLead(Lead lead)
    {
        if (LeadExists(lead.getLeadID(), lead.getSubject(), lead.getNotes())) {

            //Only update is it's newer

            Lead localLead = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .leadDao()
                    .getLeadByLeadID(lead.getLeadID());

            if (localLead != null) {

                Date sourceConvertedDate = new Date();
                Date localConvertedDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                String sourceDateString;
                String localDateString;

                if (lead.getUpdated() != null) {
                    sourceDateString = lead.getUpdated();
                } else {
                    sourceDateString = sourceConvertedDate.toString();
                }

                if (localLead.getUpdated() != null) {
                    localDateString = localLead.getUpdated();
                } else {
                    localDateString = localConvertedDate.toString();
                }

                try {
                    sourceConvertedDate = dateFormat.parse(sourceDateString);
                    localConvertedDate = dateFormat.parse(localDateString);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (localConvertedDate != null) {
                    assert sourceConvertedDate != null;
                    if (sourceConvertedDate.after(localConvertedDate)) {
                        if(lead.getLeadID() != 0) {
                            DatabaseClient.getInstance(getApplicationContext())
                                    .getAppDatabase()
                                    .leadDao()
                                    .updateLeadByLeadID(lead.getSubject(), lead.getRating().toString(), lead.getStatus(), lead.getValue().toString(), lead.getNotes(), lead.getUpdated(), lead.getLeadID());
                        }
                    }
                }
            }
        }
        else //Insert
        {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

            Lead newLead = new Lead();

            //Get local customer ID

            Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .customerDao()
                    .getCustomerByCustomerID(lead.getCustomerID());

            if (localCustomer != null)
            {
                newLead.setLocalCustomerID(localCustomer.getId());
            }
            else
            {
                newLead.setLocalCustomerID(0);
            }

            newLead.setLeadID(lead.getLeadID());
            newLead.setCustomerID(lead.getCustomerID());
            newLead.setCustomerName(lead.getCustomerName());
            newLead.setContactID(lead.getContactID());
            newLead.setContactName(lead.getContactName());
            newLead.setSubject(lead.getSubject());
            newLead.setStatus(lead.getStatus());
            newLead.setRating(lead.getRating());
            newLead.setValue(lead.getValue());
            newLead.setTelephone(lead.getTelephone());
            newLead.setEmail(lead.getEmail());
            newLead.setNotes(lead.getNotes());
            newLead.setCreatedByDisplayName(lead.getCreatedByDisplayName());
            newLead.setIsChanged(false);
            newLead.setIsNew(false);

            if (lead.getUpdated() != null) {
                newLead.setUpdated(lead.getUpdated());
            }
            else
            {
                Date created = new Date();
                newLead.setUpdated(dateFormat.format(created));
            }

            long id = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .leadDao()
                    .insert(newLead);
        }
    }

    public boolean LeadExists(int taskID, String subject, String notes) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().leadDao().getLeadCount(taskID) +
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().leadDao().getLeadCount(subject, notes);
        return Count > 0;
    }

    public void CreateOrUpdateOpportunity(Opportunity opportunity)
    {


            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().opportunityDao().getOpportunityCount(opportunity.getOpportunityID()) +
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().opportunityDao().getOpportunityCount(opportunity.getSubject(), opportunity.getNotes());

            if (Count > 0) {

                //Only update is it's newer

                Opportunity localOpportunity = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .opportunityDao()
                        .getOpportunityByOpportunityID(opportunity.getOpportunityID());

                if (localOpportunity != null) {

                    Date sourceConvertedDate = new Date();
                    Date localConvertedDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    String sourceDateString;
                    String localDateString;

                    if (opportunity.getUpdated() != null) {
                        sourceDateString = opportunity.getUpdated();
                    } else {
                        sourceDateString = sourceConvertedDate.toString();
                    }

                    if (localOpportunity.getUpdated() != null) {
                        localDateString = localOpportunity.getUpdated();
                    } else {
                        localDateString = localConvertedDate.toString();
                    }

                    try {
                        sourceConvertedDate = dateFormat.parse(sourceDateString);
                        localConvertedDate = dateFormat.parse(localDateString);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    if (localConvertedDate != null) {
                        assert sourceConvertedDate != null;
                        if (sourceConvertedDate.after(localConvertedDate)) {
                            if(opportunity.getOpportunityID() != 0) {
                                DatabaseClient.getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .opportunityDao()
                                        .updateOpportunityByOpportunityID(opportunity.getSubject(), opportunity.getRating().toString(), opportunity.getStatus(), opportunity.getValue().toString(), opportunity.getProbability(), opportunity.getDetails(), opportunity.getTargetDate(), opportunity.getNotes(), opportunity.getUpdated(), opportunity.getOpportunityID());
                            }
                        }
                    }
                }
            }
            else //Insert
            {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                Opportunity newOpportunity = new Opportunity();

                //Get local customer ID

                Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomerByCustomerID(opportunity.getCustomerID());

                if (localCustomer != null)
                {
                    newOpportunity.setLocalCustomerID(localCustomer.getId());
                }
                else
                {
                    newOpportunity.setLocalCustomerID(0);
                }

                newOpportunity.setOpportunityID(opportunity.getOpportunityID());
                newOpportunity.setCustomerID(opportunity.getCustomerID());
                newOpportunity.setCustomerName(opportunity.getCustomerName());
                newOpportunity.setContactID(opportunity.getContactID());
                newOpportunity.setContactName(opportunity.getContactName());
                newOpportunity.setSubject(opportunity.getSubject());
                newOpportunity.setStatus(opportunity.getStatus());
                newOpportunity.setDetails(opportunity.getDetails());
                newOpportunity.setProbability(opportunity.getProbability());
                newOpportunity.setTargetDate(opportunity.getTargetDate());
                newOpportunity.setRating(opportunity.getRating());
                newOpportunity.setValue(opportunity.getValue());
                newOpportunity.setTelephone(opportunity.getTelephone());
                newOpportunity.setEmail(opportunity.getEmail());
                newOpportunity.setNotes(opportunity.getNotes());
                newOpportunity.setCreatedByDisplayName(opportunity.getCreatedByDisplayName());
                newOpportunity.setIsChanged(false);
                newOpportunity.setIsNew(false);

                if (opportunity.getUpdated() != null) {
                    newOpportunity.setUpdated(opportunity.getUpdated());
                }
                else
                {
                    Date created = new Date();
                    newOpportunity.setUpdated(dateFormat.format(created));
                }

                long id = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .opportunityDao()
                        .insert(newOpportunity);
            }


    }

    public void CreateOrUpdateQuote(Quote quote)
    {


            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().quoteDao().getQuoteCount(quote.getQuoteID()) +
                        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().quoteDao().getQuoteCount(quote.getSubject(), quote.getNotes());

            if (Count > 0) {

                //Only update if it's newer

                Quote localQuote = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .quoteDao()
                        .getQuoteByQuoteID(quote.getQuoteID());

                if (localQuote != null) {

                    Date sourceConvertedDate = new Date();
                    Date localConvertedDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    String sourceDateString;
                    String localDateString;

                    if (quote.getUpdated() != null) {
                        sourceDateString = quote.getUpdated();
                    } else {
                        sourceDateString = sourceConvertedDate.toString();
                    }

                    if (localQuote.getUpdated() != null) {
                        localDateString = localQuote.getUpdated();
                    } else {
                        localDateString = localConvertedDate.toString();
                    }

                    try {
                        sourceConvertedDate = dateFormat.parse(sourceDateString);
                        localConvertedDate = dateFormat.parse(localDateString);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    if (localConvertedDate != null) {
                        assert sourceConvertedDate != null;
                        if (sourceConvertedDate.after(localConvertedDate)) {
                            if(quote.getQuoteID() != 0) {
                                DatabaseClient.getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .quoteDao()
                                        .updateQuoteByQuoteID(quote.getSubject(), quote.getReference(), quote.getStatus(), quote.getTargetDate(), quote.getNotes(), quote.getUpdated(), quote.getQuoteID());
                            }
                        }
                    }
                }
            }
            else //Insert
            {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                Quote newQuote = new Quote();

                //Get local customer ID

                Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .customerDao()
                        .getCustomerByCustomerID(quote.getCustomerID());

                if (localCustomer != null)
                {
                    newQuote.setLocalCustomerID(localCustomer.getId());
                }
                else
                {
                    newQuote.setLocalCustomerID(0);
                }

                newQuote.setReference(quote.getReference());
                newQuote.setQuoteID(quote.getQuoteID());
                newQuote.setOpportunityID(quote.getOpportunityID());
                newQuote.setCustomerID(quote.getCustomerID());
                newQuote.setCustomerName(quote.getCustomerName());
                newQuote.setContactID(quote.getContactID());
                newQuote.setContactName(quote.getContactName());
                newQuote.setSubject(quote.getSubject());
                newQuote.setQuoteType("Quote");
                newQuote.setStatus(quote.getStatus());
                newQuote.setTargetDate(quote.getTargetDate());
                newQuote.setCurrency(quote.getCurrency());
                newQuote.setExchangeRate(quote.getExchangeRate());
                newQuote.setNotes(quote.getNotes());
                newQuote.setCreatedByDisplayName(quote.getCreatedByDisplayName());
                newQuote.setIsChanged(false);
                newQuote.setIsNew(false);

                if (quote.getUpdated() != null) {
                    newQuote.setUpdated(quote.getUpdated());
                }
                else
                {
                    Date created = new Date();
                    newQuote.setUpdated(dateFormat.format(created));
                }

                long id = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .quoteDao()
                        .insert(newQuote);
            }


    }

    public void CreateOrUpdateQuoteLine(QuoteLine quoteLine)
    {
            int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().quoteLineDao().getQuoteLineCount(quoteLine.getQuoteLineID()) +
                    DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().quoteLineDao().getQuoteLineCount(quoteLine.getPartNumber(), quoteLine.getDescription(), quoteLine.getQuoteID());

            if (Count > 0) {

                //Only update is it's newer

                QuoteLine localQuoteLine = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .quoteLineDao()
                        .getQuoteLineByQuoteLineID(quoteLine.getQuoteLineID());

                if (localQuoteLine != null) {

                    Date sourceConvertedDate = new Date();
                    Date localConvertedDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                    String sourceDateString;
                    String localDateString;

                    if (quoteLine.getUpdated() != null) {
                        sourceDateString = quoteLine.getUpdated();
                    } else {
                        sourceDateString = sourceConvertedDate.toString();
                    }

                    if (localQuoteLine.getUpdated() != null) {
                        localDateString = localQuoteLine.getUpdated();
                    } else {
                        localDateString = localConvertedDate.toString();
                    }

                    try {
                        sourceConvertedDate = dateFormat.parse(sourceDateString);
                        localConvertedDate = dateFormat.parse(localDateString);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    if (localConvertedDate != null) {
                        assert sourceConvertedDate != null;
                        if (sourceConvertedDate.after(localConvertedDate)) {
                            if(quoteLine.getQuoteLineID() != 0) {
                                DatabaseClient.getInstance(getApplicationContext())
                                        .getAppDatabase()
                                        .quoteLineDao()
                                        .updateQuoteLineByQuoteLineID(quoteLine.getPartNumber(), quoteLine.getDescription(), quoteLine.getQuantity(), quoteLine.getValue(), quoteLine.getCostPrice(), quoteLine.getDiscount(), quoteLine.getTaxRate(), quoteLine.getNotes(), quoteLine.getUpdated(), quoteLine.getQuoteLineID());
                            }
                        }
                    }
                }
            }
            else //Insert
            {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                QuoteLine newQuoteLine = new QuoteLine();

                /*Get the local quote ID*/

                Quote localQuote = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .quoteDao()
                        .getQuoteByQuoteID(quoteLine.getQuoteID());

                if (localQuote != null)
                {
                    newQuoteLine.setLocalQuoteID(localQuote.getId());
                }
                else
                {
                    newQuoteLine.setLocalQuoteID(0);
                }

                newQuoteLine.setQuoteLineID(quoteLine.getQuoteLineID());
                newQuoteLine.setQuoteID(quoteLine.getQuoteID());
                newQuoteLine.setPartNumber(quoteLine.getPartNumber());
                newQuoteLine.setDescription(quoteLine.getDescription());
                newQuoteLine.setValue(quoteLine.getValue());
                newQuoteLine.setQuantity(quoteLine.getQuantity());
                newQuoteLine.setCostPrice(quoteLine.getCostPrice());
                newQuoteLine.setDiscount(quoteLine.getDiscount());
                newQuoteLine.setTaxRate(quoteLine.getTaxRate());
                newQuoteLine.setCreatedByDisplayName(quoteLine.getCreatedByDisplayName());
                newQuoteLine.setNotes(quoteLine.getNotes());
                newQuoteLine.setIsChanged(false);
                newQuoteLine.setIsNew(false);

                if (quoteLine.getUpdated() != null) {
                    newQuoteLine.setUpdated(quoteLine.getUpdated());
                }
                else
                {
                    Date created = new Date();
                    newQuoteLine.setUpdated(dateFormat.format(created));
                }

                long id = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .quoteLineDao()
                        .insert(newQuoteLine);

            }

    }

    public void CreateOrUpdateOrder(Order order)
    {
        if (OrderExists(order.getOrderID(), order.getSubject(), order.getNotes())) {

            //Only update if it's newer

            Order localOrder = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .orderDao()
                    .getOrderByOrderID(order.getOrderID());

            if (localOrder != null) {

                Date sourceConvertedDate = new Date();
                Date localConvertedDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                String sourceDateString;
                String localDateString;

                if (order.getUpdated() != null) {
                    sourceDateString = order.getUpdated();
                } else {
                    sourceDateString = sourceConvertedDate.toString();
                }

                if (localOrder.getUpdated() != null) {
                    localDateString = localOrder.getUpdated();
                } else {
                    localDateString = localConvertedDate.toString();
                }

                try {
                    sourceConvertedDate = dateFormat.parse(sourceDateString);
                    localConvertedDate = dateFormat.parse(localDateString);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (localConvertedDate != null) {
                    assert sourceConvertedDate != null;
                    if (sourceConvertedDate.after(localConvertedDate)) {
                        if(order.getOrderID() != 0) {
                            DatabaseClient.getInstance(getApplicationContext())
                                    .getAppDatabase()
                                    .orderDao()
                                    .updateOrderByOrderID(order.getSubject(), order.getReference(), order.getStatus(), order.getClosingDate(), order.getNotes(), order.getUpdated(), order.getOrderID());
                        }
                    }
                }
            }
        }
        else //Insert
        {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

            Order newOrder = new Order();

            //Get local customer ID

            Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .customerDao()
                    .getCustomerByCustomerID(order.getCustomerID());

            if (localCustomer != null)
            {
                newOrder.setLocalCustomerID(localCustomer.getId());
            }
            else
            {
                newOrder.setLocalCustomerID(0);
            }

            newOrder.setReference(order.getReference());
            newOrder.setOrderID(order.getOrderID());
            newOrder.setOpportunityID(order.getOpportunityID());
            newOrder.setCustomerID(order.getCustomerID());
            newOrder.setCustomerName(order.getCustomerName());
            newOrder.setContactID(order.getContactID());
            newOrder.setContactName(order.getContactName());
            newOrder.setSubject(order.getSubject());
            newOrder.setStatus(order.getStatus());
            newOrder.setClosingDate(order.getClosingDate());
            newOrder.setCurrency(order.getCurrency());
            newOrder.setExchangeRate(order.getExchangeRate());
            newOrder.setNotes(order.getNotes());
            newOrder.setCreatedByDisplayName(order.getCreatedByDisplayName());
            newOrder.setIsChanged(false);
            newOrder.setIsNew(false);

            if (order.getUpdated() != null) {
                newOrder.setUpdated(order.getUpdated());
            }
            else
            {
                Date created = new Date();
                newOrder.setUpdated(dateFormat.format(created));
            }

            long id = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .orderDao()
                    .insert(newOrder);
        }
    }

    public boolean OrderExists(int orderID, String subject, String notes) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().orderDao().getOrderCount(orderID) +
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().orderDao().getOrderCount(subject, notes);
        return Count > 0;
    }

    public void CreateOrUpdateOrderLine(OrderLine orderLine)
    {
        if (OrderLineExists(orderLine.getOrderLineID(), orderLine.getOrderID(), orderLine.getPartNumber(), orderLine.getDescription())) {

            //Only update is it's newer

            OrderLine localOrderLine = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .orderLineDao()
                    .getOrderLineByOrderLineID(orderLine.getOrderLineID());

            if (localOrderLine != null) {

                Date sourceConvertedDate = new Date();
                Date localConvertedDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                String sourceDateString;
                String localDateString;

                if (orderLine.getUpdated() != null) {
                    sourceDateString = orderLine.getUpdated();
                } else {
                    sourceDateString = sourceConvertedDate.toString();
                }

                if (localOrderLine.getUpdated() != null) {
                    localDateString = localOrderLine.getUpdated();
                } else {
                    localDateString = localConvertedDate.toString();
                }

                try {
                    sourceConvertedDate = dateFormat.parse(sourceDateString);
                    localConvertedDate = dateFormat.parse(localDateString);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (localConvertedDate != null) {
                    assert sourceConvertedDate != null;
                    if (sourceConvertedDate.after(localConvertedDate)) {
                        if(orderLine.getOrderLineID() != 0) {
                            DatabaseClient.getInstance(getApplicationContext())
                                    .getAppDatabase()
                                    .orderLineDao()
                                    .updateOrderLineByOrderLineID(orderLine.getPartNumber(), orderLine.getDescription(), orderLine.getQuantity(), orderLine.getValue(), orderLine.getCostPrice(), orderLine.getDiscount(), orderLine.getTaxRate(), orderLine.getNotes(), orderLine.getUpdated(), orderLine.getOrderLineID());
                        }
                    }
                }
            }
        }
        else //Insert
        {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

            OrderLine newOrderLine = new OrderLine();

            /*Get the local quote ID*/

            Order localOrder = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .orderDao()
                    .getOrderByOrderID(orderLine.getOrderID());

            if (localOrder != null)
            {
                newOrderLine.setLocalOrderID(localOrder.getId());
            }
            else
            {
                newOrderLine.setLocalOrderID(0);
            }

            newOrderLine.setOrderLineID(orderLine.getOrderLineID());
            newOrderLine.setOrderID(orderLine.getOrderID());
            newOrderLine.setPartNumber(orderLine.getPartNumber());
            newOrderLine.setDescription(orderLine.getDescription());
            newOrderLine.setValue(orderLine.getValue());
            newOrderLine.setQuantity(orderLine.getQuantity());
            newOrderLine.setCostPrice(orderLine.getCostPrice());
            newOrderLine.setDiscount(orderLine.getDiscount());
            newOrderLine.setTaxRate(orderLine.getTaxRate());
            newOrderLine.setCreatedByDisplayName(orderLine.getCreatedByDisplayName());
            newOrderLine.setNotes(orderLine.getNotes());
            newOrderLine.setIsChanged(false);
            newOrderLine.setIsNew(false);

            if (orderLine.getUpdated() != null) {
                newOrderLine.setUpdated(orderLine.getUpdated());
            }
            else
            {
                Date created = new Date();
                newOrderLine.setUpdated(dateFormat.format(created));
            }

            long id = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .orderLineDao()
                    .insert(newOrderLine);

        }
    }

    public boolean OrderLineExists(int orderLineID, int orderID, String partNumber, String description) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().orderLineDao().getOrderLineCount(orderLineID) +
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().orderLineDao().getOrderLineCount(partNumber, description, orderID);
        return Count > 0;
    }

    public void CreateOrUpdateProject(Project project)
    {
        if (ProjectExists(project.getProjectID(), project.getProjectName(), project.getStartDate())) {

            //Only update is it's newer

            Project localProject = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectDao()
                    .getProjectByProjectID(project.getProjectID());

            if (localProject != null) {

                Date sourceConvertedDate = new Date();
                Date localConvertedDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                String sourceDateString;
                String localDateString;

                if (project.getUpdated() != null) {
                    sourceDateString = project.getUpdated();
                } else {
                    sourceDateString = sourceConvertedDate.toString();
                }

                if (localProject.getUpdated() != null) {
                    localDateString = localProject.getUpdated();
                } else {
                    localDateString = localConvertedDate.toString();
                }

                try {
                    sourceConvertedDate = dateFormat.parse(sourceDateString);
                    localConvertedDate = dateFormat.parse(localDateString);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (localConvertedDate != null) {
                    assert sourceConvertedDate != null;
                    if (sourceConvertedDate.after(localConvertedDate)) {
                        if(project.getProjectID() != 0) {
                            DatabaseClient.getInstance(getApplicationContext())
                                .getAppDatabase()
                                .projectDao()
                                .updateProjectByProjectID(project.getProjectName(), project.getStatus(), project.getReference(), project.getDetails(), project.getStartDate(), project.getEndDate(), project.getNotes(), project.getUpdated(), project.getProjectID());
                        }
                    }
                }
            }
        }
        else //Insert
        {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

            Project newProject = new Project();

            //Get local customer ID

            Customer localCustomer = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .customerDao()
                    .getCustomerByCustomerID(project.getCustomerID());

            if (localCustomer != null)
            {
                newProject.setLocalCustomerID(localCustomer.getId());
            }
            else
            {
                newProject.setLocalCustomerID(0);
            }

            newProject.setProjectID(project.getProjectID());
            newProject.setProjectName(project.getProjectName());
            newProject.setStatus(project.getStatus());
            newProject.setDetails(project.getDetails());
            newProject.setCustomerID(project.getCustomerID());
            newProject.setCustomerName(project.getCustomerName());
            newProject.setReference(project.getReference());
            newProject.setStartDate(project.getStartDate());
            newProject.setEndDate(project.getEndDate());
            newProject.setNotes(project.getNotes());
            newProject.setCreatedByDisplayName(project.getCreatedByDisplayName());
            newProject.setIsChanged(false);
            newProject.setIsNew(false);

            if (project.getUpdated() != null) {
                newProject.setUpdated(project.getUpdated());
            }
            else
            {
                Date created = new Date();
                newProject.setUpdated(dateFormat.format(created));
            }

            long id = DatabaseClient.getInstance(getApplicationContext())
                .getAppDatabase()
                .projectDao()
                .insert(newProject);
        }
    }

    public boolean ProjectExists(int projectID, String projectName, String startDate) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().projectDao().getProjectCount(projectID) +
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().projectDao().getProjectCount(projectName, startDate);
        return Count > 0;
    }

    public void CreateOrUpdateProjectPhase(ProjectPhase projectPhase)
    {
        if (ProjectPhaseExists(projectPhase.getPhaseID(), projectPhase.getPhaseName(), projectPhase.getDescription(), projectPhase.getStartDate(), projectPhase.getEndDate())) {

            //Only update is it's newer

            ProjectPhase localProjectPhase = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectPhaseDao()
                    .getProjectPhaseByPhaseID(projectPhase.getPhaseID());

            if (localProjectPhase != null) {

                Date sourceConvertedDate = new Date();
                Date localConvertedDate = new Date();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

                String sourceDateString;
                String localDateString;

                if (projectPhase.getUpdated() != null) {
                    sourceDateString = projectPhase.getUpdated();
                } else {
                    sourceDateString = sourceConvertedDate.toString();
                }

                if (localProjectPhase.getUpdated() != null) {
                    localDateString = localProjectPhase.getUpdated();
                } else {
                    localDateString = localConvertedDate.toString();
                }

                try {
                    sourceConvertedDate = dateFormat.parse(sourceDateString);
                    localConvertedDate = dateFormat.parse(localDateString);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (localConvertedDate != null) {
                    assert sourceConvertedDate != null;
                    if (sourceConvertedDate.after(localConvertedDate)) {
                        if(projectPhase.getPhaseID() != 0) {
                            DatabaseClient.getInstance(getApplicationContext())
                                    .getAppDatabase()
                                    .projectPhaseDao()
                                    .updateProjectPhaseByPhaseID(projectPhase.getPhaseName(), projectPhase.getDescription(), projectPhase.getStartDate(), projectPhase.getEndDate(), projectPhase.getUpdated(), projectPhase.getPhaseID());
                        }
                    }
                }
            }
        }
        else //Insert
        {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.UK);

            ProjectPhase newProjectPhase = new ProjectPhase();

            //Get local project ID

            Project localProject = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectDao()
                    .getProjectByProjectID(projectPhase.getProjectID());

            if (localProject != null)
            {
                newProjectPhase.setLocalProjectID(localProject.getId());
            }
            else
            {
                newProjectPhase.setLocalProjectID(0);
            }

            newProjectPhase.setProjectID(projectPhase.getProjectID());
            newProjectPhase.setPhaseID(projectPhase.getPhaseID());
            newProjectPhase.setPhaseName(projectPhase.getPhaseName());
            newProjectPhase.setDescription(projectPhase.getDescription());
            newProjectPhase.setStartDate(projectPhase.getStartDate());
            newProjectPhase.setEndDate(projectPhase.getEndDate());
            newProjectPhase.setCreatedByDisplayName(projectPhase.getCreatedByDisplayName());
            newProjectPhase.setIsChanged(false);
            newProjectPhase.setIsNew(false);

            if (newProjectPhase.getUpdated() != null) {
                newProjectPhase.setUpdated(projectPhase.getUpdated());
            }
            else
            {
                Date created = new Date();
                newProjectPhase.setUpdated(dateFormat.format(created));
            }

            long id = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .projectPhaseDao()
                    .insert(newProjectPhase);
        }
    }

    public boolean ProjectPhaseExists(int phaseID, String phaseName, String descrption, String startDate, String endDate) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().projectPhaseDao().getProjectPhaseCount(phaseID) +
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().projectPhaseDao().getProjectPhaseCount(phaseName, descrption, startDate, endDate);
        return Count > 0;
    }

    public void CreateOrUpdateCurrency(Currency currency)
    {
        if (CurrencyExists(currency.getCurrencyID(), currency.getCurrencyName())) {

            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .currencyDao()
                    .updateCurrencyByCurrencyID(currency.getCurrencyID(), currency.getCurrencyName(), currency.getISOSymbol(), currency.getShortSymbol(), currency.getRate());

        }
        else //Insert
        {
            Currency newCurrency = new Currency();

            newCurrency.setCurrencyID(currency.getCurrencyID());
            newCurrency.setCurrencyName(currency.getCurrencyName());
            newCurrency.setISOSymbol(currency.getISOSymbol());
            newCurrency.setShortSymbol(currency.getShortSymbol());
            newCurrency.setRate(currency.getRate());

            long id = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .currencyDao()
                    .insert(newCurrency);
        }
    }

    public boolean CurrencyExists(int currencyID, String currencyName) {
        int Count = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().currencyDao().getCurrencyCount(currencyID) +
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().currencyDao().getCurrencyCount(currencyName);
        return Count > 0;
    }

    public String getFileType(String fileName)
    {
        String mimeType = "application/*";

        switch (fileName.substring(fileName.indexOf(".")))
        {
            case ".jpg":
                mimeType = "image/jpeg";
                break;

            case ".jpeg":
                mimeType = "image/jpeg";
                break;

            case ".png":
                mimeType = "image/png";
                break;

            case ".gif":
                mimeType = "image/gif";
                break;

            case ".doc":
                mimeType = "application/msword";
                break;

            case ".docx":
                mimeType = "application/msword";
                break;

            case ".xls":
                mimeType = "application/vnd.ms-excel";
                break;

            case ".xlsx":
                mimeType = "application/vnd.ms-excel";
                break;

            case ".ppt":
                mimeType = "application/mspowerpoint";
                break;

            case ".pptx":
                mimeType = "application/mspowerpoint";
                break;

            case ".pdf":
                mimeType = "application/pdf";
                break;

            default:
                mimeType = "application/*";
                break;
        }

        return mimeType;
    }

    public void CreateNotifcation(int ID, String Description)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo3)
                .setContentTitle("New Task Assignment")
                .setContentText(Description + " - Click to see more details")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify((int)(Math.random() * 999 + 1), builder.build());
    }
}
