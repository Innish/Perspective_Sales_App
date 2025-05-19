package uk.co.perspective.app.services;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
import uk.co.perspective.app.entities.Person;
import uk.co.perspective.app.entities.Product;
import uk.co.perspective.app.entities.Project;
import uk.co.perspective.app.entities.ProjectFile;
import uk.co.perspective.app.entities.ProjectPhase;
import uk.co.perspective.app.entities.ProjectTask;
import uk.co.perspective.app.entities.Quote;
import uk.co.perspective.app.entities.QuoteLine;
import uk.co.perspective.app.entities.Task;
import uk.co.perspective.app.entities.User;

public interface EndPoints {

    //Auth & People

    @GET("/api/Auth")
    Call<User> getAuthorization(@Query("UserName") String userName, @Query("Password") String password);

    @GET("/api/Authenticate/AuthenticateUser")
    Call<User> getAuthorizationUsername(@Query("UserName") String userName, @Query("Password") String password);

    @GET("/api/Authenticate/UseToken")
    Call<User> getAuthorizationUseToken(@Query("Token") String token);

    @GET("/api/Person")
    Call<Person> getPerson(@Query("UserID") Integer userID);

    @PUT("/api/User")
    Call<Void> deleteUser(@Query("UserID") Integer userID);

    //Customers

    @GET("/api/customers")
    Call<List<Customer>> getCustomers(@Query("SearchText") String searchText);

    @POST("/api/customers")
    Call<Customer> createNewCustomer(@Body Customer newCustomer);

    @PUT("/api/customers")
    Call<Customer> updateCustomer(@Body Customer updatedCustomer);

    //Contacts

    @GET("/api/Contacts")
    Call<List<Contact>> getContacts();

    @GET("/api/Contacts")
    Call<List<Contact>> getCustomerContacts(@Query("CustomerID") int customerID, @Query("SearchText") String searchText);

    @POST("/api/Contacts")
    Call<Contact> createNewContact(@Body Contact newContact);

    @PUT("/api/Contacts")
    Call<Contact> updateContact(@Body Contact updatedContact);

    //Addresses

    @GET("/api/Address")
    Call<List<Address>> getAddresses();

    @GET("/api/Address")
    Call<List<Address>> getCustomerAddresses(@Query("CustomerID") int customerID, @Query("SearchText") String searchText);

    @POST("/api/Address")
    Call<Address> createNewAddress(@Body Address newAddress);

    @PUT("/api/Address")
    Call<Address> updateAddress(@Body Address updatedAddress);

    //Activity

    @GET("/api/Activity")
    Call<List<Activity>> getActivity();

    @GET("/api/Activity")
    Call<List<Activity>> getCustomerActivity(@Query("CustomerID") int customerID);

    @POST("/api/Activity")
    Call<Activity> createNewActivity(@Body Activity newActivity);

    @PUT("/api/Activity")
    Call<Activity> updateActivity(@Body Activity updatedActivity);

    //Leads

    @GET("/api/lead")
    Call<List<Lead>> getLeads(@Query("PeopleID") int peopleID);

    @POST("/api/lead")
    Call<Lead> createNewLead(@Body Lead newLead);

    @PUT("/api/lead")
    Call<Lead> updateLead(@Body Lead updatedLead);

    //Opportunities

    @GET("/api/opportunity")
    Call<List<Opportunity>> getOpportunities(@Query("PeopleID") int peopleID);

    @POST("/api/opportunity")
    Call<Opportunity> createNewOpportunity(@Body Opportunity newOpportunity);

    @PUT("/api/opportunity")
    Call<Opportunity> updateOpportunity(@Body Opportunity updatedOpportunity);

    //Opportunity Contacts

    @GET("/api/opportunityContacts")
    Call<List<OpportunityContact>> getOpportunityContacts(@Query("OpportunityID") int OpportunityID);

    @POST("/api/opportunityContacts")
    Call<OpportunityContact> createNewOpportunityContact(@Body OpportunityContact newOpportunityContact);

    //Opportunity Quotes

    @GET("/api/opportunityQuotes")
    Call<List<OpportunityQuote>> getOpportunityQuotes(@Query("OpportunityID") int OpportunityID);

    @POST("/api/opportunityQuotes")
    Call<OpportunityQuote> createNewOpportunityQuote(@Body OpportunityQuote newOpportunityQuote);

    //Opportunity Files & Forms

    @GET("/api/OpportunityDocument")
    Call<List<OpportunityFile>> getRemoteOpportunityFiles(@Query("OpportunityID") int OpportunityID);

    @Multipart
    @POST("/api/OpportunityDocument")
    Call<Void> postNewOpportunityDocument(@Part MultipartBody.Part DocumentFile, @Part("OpportunityID") RequestBody OpportunityID);

    @PUT("/api/OpportunityForm")
    Call<Void> updateForm(@Body OpportunityForm updatedJobForm);

    @POST("/api/OpportunityForm")
    Call<OpportunityForm> createNewForm(@Body OpportunityForm newForm);

    @GET("/api/OpportunityForm")
    Call<List<OpportunityForm>> getOpportunityForms(@Query("OpportunityID") int OpportunityID);

    //Quotes

    @GET("/api/quote")
    Call<List<Quote>> getQuotes(@Query("PeopleID") int peopleID);

    @POST("/api/quote")
    Call<Quote> createNewQuote(@Body Quote newQuote);

    @PUT("/api/quote")
    Call<Quote> updateQuote(@Body Quote updatedQuote);

    //Quote Lines

    @GET("/api/quoteline")
    Call<List<QuoteLine>> getQuoteLines(@Query("QuoteID") int quoteID);

    @POST("/api/quoteline")
    Call<QuoteLine> createNewQuoteLine(@Body QuoteLine newQuoteLine);

    @PUT("/api/quoteline")
    Call<QuoteLine> updateQuoteLine(@Body QuoteLine updatedQuoteLine);

    //Orders

    @GET("/api/order")
    Call<List<Order>> getOrders(@Query("PeopleID") int peopleID, @Query("SearchText") String searchText);

    @POST("/api/order")
    Call<Order> createNewOrder(@Body Order newOrder);

    @PUT("/api/order")
    Call<Order> updateOrder(@Body Order updatedOrder);

    //Order Lines

    @GET("/api/orderline")
    Call<List<OrderLine>> getOrderLines(@Query("orderID") int orderID);

    @POST("/api/orderline")
    Call<OrderLine> createNewOrderLine(@Body OrderLine newOrderLine);

    @PUT("/api/orderline")
    Call<OrderLine> updateOrderLine(@Body OrderLine updatedOrderLine);

    //Projects

    @GET("/api/project")
    Call<List<Project>> getProjects(@Query("PeopleID") int peopleID);

    @POST("/api/project")
    Call<Project> createNewProject(@Body Project newProject);

    @PUT("/api/project")
    Call<Project> updateProject(@Body Project updatedProject);

    //Project Phases

    @GET("/api/projectPhase")
    Call<List<ProjectPhase>> getProjectPhases(@Query("ProjectID") int ProjectID);

    @POST("/api/projectPhase")
    Call<ProjectPhase> createNewProjectPhase(@Body ProjectPhase newProjectPhase);

    @PUT("/api/projectPhase")
    Call<ProjectPhase> updateProjectPhase(@Body ProjectPhase updatedProjectPhase);

    //Project Tasks

    @GET("/api/projectTasks")
    Call<List<ProjectTask>> getProjectTasks(@Query("ProjectID") int ProjectID);

    @POST("/api/projectTasks")
    Call<ProjectTask> createNewProjectTask(@Body ProjectTask newProjectTask);

    //Project Documents

    @GET("/api/ProjectDocument")
    Call<List<ProjectFile>> getRemoteProjectFiles(@Query("ProjectID") int ProjectID);

    @Multipart
    @POST("/api/ProjectDocument")
    Call<Void> postNewProjectDocument(@Part MultipartBody.Part DocumentFile, @Part("ProjectID") RequestBody projectID);

    //Tasks

    @GET("/api/tasks")
    Call<List<Task>> getTasks(@Query("PeopleID") int peopleID);

    @POST("/api/tasks")
    Call<Task> createNewTask(@Body Task newTask);

    @PUT("/api/tasks")
    Call<Task> updateTask(@Body Task updatedTask);

    //Products

    @GET("/api/products")
    Call<List<Product>> getProducts();

    //Currencies

    @GET("/api/currency")
    Call<List<Currency>> getCurrencies();

}
