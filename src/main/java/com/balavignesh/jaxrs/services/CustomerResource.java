/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balavignesh.jaxrs.services;

import com.balavignesh.jaxrs.domain.Customer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author BalaVignesh
 */
@Path("/customers")
public class CustomerResource {
    
    private Map<Integer, Customer> customerDB =
                            new ConcurrentHashMap<Integer, Customer>();
   private AtomicInteger idCounter = new AtomicInteger();
   
   @POST
   @Consumes("application/xml")
   public Response createCustomer(InputStream is) {
      Customer customer = readCustomer(is);
      customer.setId(idCounter.incrementAndGet());
      customerDB.put(customer.getId(), customer);
      System.out.println("Created customer " + customer.getId());
      return Response.created(URI.create("/customers/"
                                   + customer.getId())).build();
   }
   @GET
   @Path("{id}")
   @Produces("application/xml")
   public StreamingOutput getCustomer(@PathParam("id") int id) {
      final Customer customer = customerDB.get(id);
      if (customer == null) {
         throw new WebApplicationException(
                                          Response.Status.NOT_FOUND);
      }
      return new StreamingOutput() {
         public void write(OutputStream outputStream)
                       throws IOException, WebApplicationException {
            outputCustomer(new PrintStream(outputStream), customer);
         }
      };
   }
   
   @GET
   @Produces("application/xml")
   public StreamingOutput getAllCustomers() {
       return new StreamingOutput() {
         public void write(OutputStream outputStream)
                       throws IOException, WebApplicationException {
            outputAllCustomer(outputStream);
         }

           
      };
   }
   private void outputAllCustomer(OutputStream os) throws IOException {
       PrintStream writer = new PrintStream(os);
        writer.println("<customers>");
        if (customerDB == null || customerDB.isEmpty()) {
         throw new WebApplicationException(
                                          Response.Status.NO_CONTENT);
      }
       customerDB.values().stream().forEach(customer -> {
           try {
               outputCustomer(writer,customer);
           } catch (IOException ex) {
               Logger.getLogger(CustomerResource.class.getName()).log(Level.SEVERE, null, ex);
           }
       });
        writer.println("</customers>");
   
   }
   
   @PUT
   @Path("{id}")
   @Consumes("application/xml")
   public void updateCustomer(@PathParam("id") int id,
                               InputStream is) {
      Customer update = readCustomer(is);
      Customer current = customerDB.get(id);
      if (current == null)
        throw new WebApplicationException(Response.Status.NOT_FOUND);

      current.setFirstName(update.getFirstName());
      current.setLastName(update.getLastName());
      current.setStreet(update.getStreet());
      current.setState(update.getState());
      current.setZip(update.getZip());
      current.setCountry(update.getCountry());
   }
   
    protected void outputCustomer(PrintStream writer, Customer cust)
                                                     throws IOException {  
      writer.println("<customer id=\"" + cust.getId() + "\">");
      writer.println("   <first-name>" + cust.getFirstName()
                      + "</first-name>");
      writer.println("   <last-name>" + cust.getLastName()
                      + "</last-name>");
      writer.println("   <street>" + cust.getStreet() + "</street>");
      writer.println("   <city>" + cust.getCity() + "</city>");
      writer.println("   <state>" + cust.getState() + "</state>");
      writer.println("   <zip>" + cust.getZip() + "</zip>");
      writer.println("   <country>" + cust.getCountry() + "</country>");
      writer.println("</customer>");
   }
    
    protected Customer readCustomer(InputStream is) {
      try {
         DocumentBuilder builder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder();
          Document doc = builder.parse(is);
         Element root = doc.getDocumentElement();
         Customer cust = new Customer();
         if (root.getAttribute("id") != null
                && !root.getAttribute("id").trim().equals("")) {
            cust.setId(Integer.valueOf(root.getAttribute("id")));
         }
         NodeList nodes = root.getChildNodes();
         for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.getTagName().equals("first-name")) {
               cust.setFirstName(element.getTextContent());
            }
            else if (element.getTagName().equals("last-name")) {
               cust.setLastName(element.getTextContent());
            }
            else if (element.getTagName().equals("street")) {
               cust.setStreet(element.getTextContent());
            }
            else if (element.getTagName().equals("city")) {
               cust.setCity(element.getTextContent());
            }
            else if (element.getTagName().equals("state")) {
               cust.setState(element.getTextContent());
            }
            else if (element.getTagName().equals("zip")) {
               cust.setZip(element.getTextContent());
            }
            else if (element.getTagName().equals("country")) {
               cust.setCountry(element.getTextContent());
            }
         }
         return cust;
      }
      catch (Exception e) {
         throw new WebApplicationException(e,
                       Response.Status.BAD_REQUEST);
      }
   }

}
