package hello;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;


@SpringUI
public class VaadinUI extends UI{

    private final CustomerRepository customerRepository;
    final Grid<Customer> grid;

    public VaadinUI(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.grid = new Grid<>(Customer.class);
    }


    @Override
    protected void init(VaadinRequest request){
        setContent(grid);
        listCustomers();
    }

    private void listCustomers(){
        grid.setItems(customerRepository.findAll());
    }
}
