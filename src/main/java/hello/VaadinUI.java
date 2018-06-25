package hello;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import org.springframework.util.StringUtils;


@SpringUI
public class VaadinUI extends UI{

    private final CustomerRepository customerRepository;
    private final CustomerEditor customerEditor;
    final Grid<Customer> grid;
    final TextField filter;
    private final Button addNewBtn;

    public VaadinUI(CustomerRepository customerRepository, CustomerEditor customerEditor) {
        this.customerRepository = customerRepository;
        this.customerEditor = customerEditor;
        this.grid = new Grid<>(Customer.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request){
        // Build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid, customerEditor);
        setContent(mainLayout);

        grid.setHeight(300, Unit.PIXELS);
        grid.setColumns("id", "firstName", "lastName");

        filter.setPlaceholder("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            customerEditor.editCustomer(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> customerEditor.editCustomer(new Customer("", "")));

        // Listen changes made by the editor, refresh data from backend
        customerEditor.setChangeHandler(() -> {
            customerEditor.setVisible(false);
            listCustomers(filter.getValue());
        });

        // Initialize listing
        listCustomers(null);
    }

    private void listCustomers(String filterText){
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(customerRepository.findAll());
        }
        else {
            grid.setItems(customerRepository.findByLastNameStartsWithIgnoreCase(filterText));
        }
    }
}
