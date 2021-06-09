
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ammaar
 */
public class ReadFromSer {

    ObjectInputStream input;

    static ArrayList<Customer> custlist = new ArrayList<Customer>();
    static ArrayList<Supplier> suplist = new ArrayList<Supplier>();
    private int amount;
    private int amount2;

    public void openFile() {
        try {
            input = new ObjectInputStream(new FileInputStream("stakeholder.ser"));
        } catch (IOException ioe) {
            System.out.println("error opening ser file: " + ioe.getMessage());
            System.exit(1);
        }
    }

    public void closeFile() {
        try {
            input.close();
        } catch (IOException ioe) {
            System.out.println("error closing ser file: " + ioe.getMessage());
            System.exit(1);
        }
    }

    public void intoList() {
        try {
            while (true) {
                Object x = input.readObject();
                if (x instanceof Supplier) {
                    suplist.add((Supplier) x);
                } else {
                    custlist.add((Customer) x);
                }
            }
        } catch (EOFException eof) {
            System.out.println("end of file reached");
        } catch (ClassNotFoundException cof) {
            System.out.println("class not found");
        } catch (IOException iof) {
            System.out.println("Inputs not found");
        } finally {
            closeFile();
        }
    }
    public static Comparator<Customer> CustComparator = new Comparator<Customer>() {

        public int compare(Customer c1, Customer c2) {
            String Cust1 = c1.getStHolderId();
            String Cust2 = c2.getStHolderId();

            return Cust1.compareTo(Cust2);
        }
    };
    public static Comparator<Supplier> SupComparator = new Comparator<Supplier>() {

        public int compare(Supplier s1, Supplier s2) {
            String Sup1 = s1.getStHolderId();
            String Sup2 = s2.getStHolderId();

            //ascending order
            return Sup1.compareTo(Sup2);
        }
    };

    public void WriteSupplierToFile() {
        try {
            FileWriter fw = new FileWriter("supplierOutFile.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("========================== SUPPLIERS =====================================" + "\n");
            bw.write(String.format("%-18s%-18s%-18s%-18s\n", "ID", "Name", "Prod Type", "Description"));
            bw.write("==========================================================================" + "\n");
            for (int i = 0; i < suplist.size(); i++) {
                bw.write(String.format("%-18s%-18s%-18s%-18s\n", suplist.get(i).getStHolderId(), suplist.get(i).getName(), suplist.get(i).getProductType(), suplist.get(i).getProductDescription()));

            }
            bw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void WriteCustomerToFile() {
        try {
            FileWriter fw = new FileWriter("customerOutFile.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("============================= CUSTOMERS ===================================" + "\n");
            bw.write(String.format("%-18s%-18s%-18s%-18s%-18s\n", "ID", "Name", "Surname", "Date of Birth", "Age"));
            bw.write("===========================================================================" + "\n");
            for (int i = 0; i < custlist.size(); i++) {
                bw.write(String.format("%-18s%-18s%-18s%-18s%-18s\n", custlist.get(i).getStHolderId(), custlist.get(i).getFirstName(), custlist.get(i).getSurName(), custlist.get(i).getDateOfBirth(), AgeCalc(i)));

            }
            bw.write("Number of customers who can rent: " + amount+"\n");
            bw.write("Number of customers who cannot rent: " + amount2);
            bw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public int AgeCalc(int i) {
        try {
            String d = custlist.get(i).getDateOfBirth();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date g = sdf.parse(d);
            Calendar c = Calendar.getInstance();
            c.setTime(g);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int date = c.get(Calendar.DATE);
            LocalDate l1 = LocalDate.of(year, month, date);
            LocalDate now1 = LocalDate.now();
            Period diff1 = Period.between(l1, now1);
            return diff1.getYears();
        } catch (ParseException e) {
            System.out.println("parse exception");
            return 0;
        }
    }

    public void DateForm() {
        try {
            for (int i = 0; i < custlist.size(); i++) {
                String w = custlist.get(i).getDateOfBirth();
                SimpleDateFormat r = new SimpleDateFormat("yyyy-MM-dd");
                Date q = r.parse(w);
                SimpleDateFormat y = new SimpleDateFormat("dd MMM yyyy");
                custlist.get(i).setDateOfBirth(y.format(q));
            }
        } catch (ParseException e) {
            System.out.println("parse exception");
        }
    }

    public void CanRent() {
        for (int i = 0; i < custlist.size(); i++) {
            if (custlist.get(i).getCanRent()) {
                amount++;
            } else {
                amount2++;
            }
        }
    }

    public static void main(String[] args) {
        ReadFromSer object = new ReadFromSer();
        object.openFile();
        object.intoList();
        Collections.sort(custlist, CustComparator);
        Collections.sort(suplist, SupComparator);
        object.WriteSupplierToFile();
        object.DateForm();
        object.CanRent();
        object.WriteCustomerToFile();

    }
}
