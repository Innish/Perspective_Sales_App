package uk.co.perspective.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import uk.co.perspective.app.dao.ActivityDao;
import uk.co.perspective.app.dao.AddressDao;
import uk.co.perspective.app.dao.ContactDao;
import uk.co.perspective.app.dao.CurrencyDao;
import uk.co.perspective.app.dao.CustomerDao;
import uk.co.perspective.app.dao.LeadDao;
import uk.co.perspective.app.dao.OpportunityDao;
import uk.co.perspective.app.dao.OpportunityFileDao;
import uk.co.perspective.app.dao.OpportunityFormDao;
import uk.co.perspective.app.dao.OrderDao;
import uk.co.perspective.app.dao.OrderLineDao;
import uk.co.perspective.app.dao.ProductDao;
import uk.co.perspective.app.dao.ProjectDao;
import uk.co.perspective.app.dao.ProjectFileDao;
import uk.co.perspective.app.dao.ProjectPhaseDao;
import uk.co.perspective.app.dao.QuoteDao;
import uk.co.perspective.app.dao.QuoteLineDao;
import uk.co.perspective.app.dao.TaskDao;
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
import uk.co.perspective.app.entities.OpportunityTask;
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

@Database(entities = {Activity.class, Address.class, Contact.class, Customer.class, Lead.class, Opportunity.class, OpportunityTask.class, OpportunityFile.class, OpportunityForm.class,
        Quote.class, QuoteLine.class, Order.class, OrderLine.class, Task.class, Project.class, ProjectFile.class, OpportunityContact.class, OpportunityQuote.class, Product.class,
        ProjectTask.class, ProjectPhase.class, Currency.class}, version = 26)

public abstract class AppDatabase extends RoomDatabase {

    public abstract ActivityDao activityDao();
    public abstract AddressDao addressDao();
    public abstract ContactDao contactDao();
    public abstract CustomerDao customerDao();
    public abstract LeadDao leadDao();
    public abstract OpportunityDao opportunityDao();
    public abstract QuoteDao quoteDao();
    public abstract QuoteLineDao quoteLineDao();
    public abstract OrderDao orderDao();
    public abstract OrderLineDao orderLineDao();
    public abstract TaskDao taskDao();
    public abstract ProjectDao projectDao();
    public abstract ProjectPhaseDao projectPhaseDao();
    public abstract ProjectFileDao projectImageDao();
    public abstract OpportunityFileDao opportunityFileDao();
    public abstract OpportunityFormDao opportunityFormDao();
    public abstract ProductDao productDao();
    public abstract CurrencyDao currencyDao();
}
